/*
 * The MIT License
 *
 * Copyright 2026 TheFallenWarrior.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ffonline.controller;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wraps a {@link Socket} with telnet protocol handling and manual line
 * buffering.
 *
 * <p>
 * Upon construction, sends IAC WILL SUPPRESS-GO-AHEAD and IAC DO
 * SUPPRESS-GO-AHEAD to initiate half-duplex mode negotiation with the client.
 * Subsequent telnet control sequences received from the client are handled
 * transparently during {@link #readLine()}.
 *
 * <p>
 * Telnet protocol references used:
 * <ul>
 * <li>RFC 854 — Telnet Protocol Specification</li>
 * <li>RFC 858 — Telnet Suppress Go Ahead Option</li>
 * </ul>
 *
 * @author thefa
 */
public class TelnetConnection implements Closeable{
    private static final Logger LOGGER = Logger.getLogger(ServerController.class.getName());
    /**
     * Interpret As Command — marks the start of a telnet control sequence.
     */
    private static final int IAC = 255;

    /**
     * Don't — refuse an offered option.
     */
    private static final int DONT = 254;

    /**
     * Do — request that the remote party enable an option.
     */
    private static final int DO = 253;

    /**
     * Won't — refuse to enable an option.
     */
    private static final int WONT = 252;

    /**
     * Will — offer to enable an option.
     */
    private static final int WILL = 251;

    // Two-byte commands (IAC <cmd>, no option byte follows)
    /**
     * No Operation.
     */
    private static final int NOP = 241;

    /**
     * Are You There — client health-check; we respond with NOP.
     */
    private static final int AYT = 246;

    /**
     * Erase Character — delete the last character from the line buffer.
     */
    private static final int EC = 247;

    /**
     * Erase Line — wipe the entire line buffer.
     */
    private static final int EL = 248;

    /**
     * Suppress Go-Ahead option code.
     */
    private static final int OPT_SGA = 3;

    private final Socket socket;
    private final InputStream in;
    private final OutputStream out;

    // Manual line buffer — holds raw (non-IAC) bytes received so far on the
    // current line.  We use a List<Byte> so that EC can remove the last element
    // cheaply without keeping a separate length counter.
    private final List<Byte> lineBuffer = new ArrayList<>();


    /**
     * Creates a {@code TelnetConnection} that takes ownership of {@code socket}.
     * <p>
     * Immediately sends IAC WILL SUPPRESS-GO-AHEAD and IAC DO SUPPRESS-GO-AHEAD
     * so that clients that understand telnet will disable Go-Ahead signals and
     * allow character-at-a-time input/output without synchronization overhead.
     * @param socket an accepted, connected {@link Socket}
     * @throws IOException if obtaining the socket streams or sending the
     * initial negotiation bytes fails
     */
    public TelnetConnection(Socket socket) throws IOException{
        this.socket = socket;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();

        // Announce: we will suppress go-ahead, and ask the client to do the same.
        sendRaw(new byte[]{
            (byte)IAC, (byte)WILL, (byte)OPT_SGA,
            (byte)IAC, (byte)DO, (byte)OPT_SGA
        });
    }

    /**
     * Blocks until a complete line is available, then returns it as a UTF-8
     * string with the line terminator stripped.
     * <p>
     * Data is read one byte at a time so that IAC sequences can be intercepted
     * and acted on inline. Only printable data bytes (i.e. bytes outside of IAC
     * sequences) are accumulated in the line buffer; the line is complete when
     * {@code '\n'} is encountered (handles both bare {@code LF} and the
     * telnet-standard {@code CR LF} pair).
     * @return the decoded line, or {@code null} if the connection was closed by
     * the remote end before a line terminator was seen
     * @throws IOException if an I/O error occurs
     */
    public String readLine() throws IOException{
        while(true){
            int b = in.read();

            if(b == -1){
                // Connection closed — return whatever is buffered, or null.
                if(lineBuffer.isEmpty()){
                    return null;
                }
                return drainBuffer();
            }

            if(b == IAC){
                handleIac();
                continue;
            }

            if(b == '\n'){
                // End of line.  Strip a trailing '\r' that was buffered from a
                // CR LF pair (telnet line endings are CR LF per RFC 854 §3.3.1).
                if(!lineBuffer.isEmpty() && lineBuffer.getLast() == '\r'){
                    lineBuffer.removeLast();
                }
                return drainBuffer();
            }

            lineBuffer.add((byte)b);
        }
    }

    /**
     * Writes {@code text} to the socket and flushes.
     * <p>
     * Any lone {@code '\n'} is expanded into {@code "\r\n"} before sending, so
     * multi-line string built with bare LFs are transmitted correctly per RFC
     * 854 §3.3.1.
     * @param text the string to send; encoded as UTF-8
     * @throws IOException if an I/O error occurs
     */
    public void print(String text) throws IOException {
        byte[] raw = text.getBytes(StandardCharsets.UTF_8);
 
        // Count lone '\n's so we can allocate the expanded buffer in one pass.
        int extra = 0;
        for (int i = 0; i < raw.length; i++) {
            if (raw[i] == '\n' && (i == 0 || raw[i - 1] != '\r')) extra++;
        }
 
        if (extra == 0) {
            sendRaw(raw);
            return;
        }
 
        byte[] expanded = new byte[raw.length + extra];
        int dst = 0;
        for (int i = 0; i < raw.length; i++) {
            if (raw[i] == '\n' && (i == 0 || raw[i - 1] != '\r')) {
                expanded[dst++] = '\r';
            }
            expanded[dst++] = raw[i];
        }
        sendRaw(expanded);
    }


    /**
     * Writes {@code text} followed by {@code "\r\n"} to the socket and flushes.
     * <p>
     * Any lone {@code '\n'} in {@code text} is expanded into {@code "\r\n"} before
     * sending, so multi-line string built with bare LFs are transmitted correctly per RFC
     * 854 §3.3.1.
     * @param text the string to send; encoded as UTF-8
     * @throws IOException if an I/O error occurs
     */
    public void println(String text) throws IOException{
        print(text + "\r\n");
    }
    
    /**
     * Writes {@code text} followed by {@code "\r\n"} to the socket and flushes.
     * This method does not propagate IOExceptions, logging the error instead.
     * <p>
     * Any lone {@code '\n'} in {@code text} text is expanded into {@code "\r\n"} before
     * sending, so multi-line string built with bare LFs are transmitted correctly per RFC
     * 854 §3.3.1.
     * @param text the string to send; encoded as UTF-8
     */
    public void safePrintln(String text){
        try{
            println(text);
        } catch(IOException e){
            LOGGER.log(
                Level.WARNING,
                "IOException occurred when writing to {0}",
                socket.getRemoteSocketAddress()
            );
        }
    }

    /**
     * Returns {@code true} if the underlying socket is closed.
     * @return whether the socket is closed
     */
    public boolean isClosed(){
        return socket.isClosed();
    }

    /**
     * Closes the underlying socket.
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException{
        socket.close();
    }

    /**
     * Returns the remote socket address, suitable for logging.
     * @return the remote {@link SocketAddress}
     */
    public SocketAddress getRemoteSocketAddress(){
        return socket.getRemoteSocketAddress();
    }

    /**
     * Called immediately after the leading {@code IAC} byte has been consumed.
     * Reads enough additional bytes to fully consume the control sequence and
     * performs the appropriate action.
     *
     * @throws IOException if an I/O error occurs
     */
    private void handleIac() throws IOException{
        int cmd = in.read();
        if(cmd == -1){
            return;
        }

        switch(cmd){
            // Two-byte commands

            case NOP -> {
                // No operation
            }

            case AYT -> {
                // Respond with NOP so the client knows we are alive
                sendRaw(new byte[]{(byte)IAC, (byte)NOP});
            }

            case EC -> {
                // Erase the last character from the line buffer
                if(!lineBuffer.isEmpty()){
                    lineBuffer.removeLast();
                }
            }

            case EL -> {
                // Erase the entire line buffer
                lineBuffer.clear();
            }

            // Three-byte option-negotiation commands (IAC + cmd + option byte)
            case WILL -> {
                int option = in.read();
                if(option == -1){
                    return;
                }

                if(option != OPT_SGA){
                    // Refuse all other WILL offers with DON'T
                    sendRaw(new byte[]{(byte)IAC, (byte)DONT, (byte)option});
                }
            }

            case DO -> {
                int option = in.read();
                if(option == -1){
                    return;
                }

                if(option != OPT_SGA){
                    // Refuse all other DO requests with WON'T
                    sendRaw(new byte[]{(byte)IAC, (byte)WONT, (byte)option});
                }
            }

            case DONT, WONT -> {
                // Client is refusing one of our offers. Consume the option byte
                //  and ignore.
                in.read();
            }

            default -> {
                // All other IAC sequences are silently ignored
            }
        }
    }

    /**
     * Converts the line buffer contents to a UTF-8 string, clears the buffer,
     * and returns the string.
     */
    private String drainBuffer(){
        byte[] bytes = new byte[lineBuffer.size()];
        for(int i = 0; i < bytes.length; i++){
            bytes[i] = lineBuffer.get(i);
        }
        lineBuffer.clear();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Writes {@code bytes} to the output stream and flushes.
     *
     * @param bytes the raw bytes to send
     * @throws IOException if an I/O error occurs
     */
    private void sendRaw(byte[] bytes) throws IOException{
        out.write(bytes);
        out.flush();
    }
}
