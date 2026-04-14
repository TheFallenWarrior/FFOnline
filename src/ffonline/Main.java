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
package ffonline;

import ffonline.controller.ServerController;

/**
 *
 * @author thefa
 */
public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int port = 4080, maxClients = 50;
        boolean isQuiet = false;
        for(String arg : args){
            if (arg.startsWith("-p")){
                try {
                    port = Integer.parseInt(arg.substring(2));
                }catch(NumberFormatException e){
                    System.err.println("Error: Failed to parse port number.\n");
                    printUsageAndExit(1);
                }
                if(port < 0 || port > 0xffff){
                    System.err.println("Error: Port number out of range.");
                    System.exit(1);
                }
            }
            else if(arg.startsWith("-c")){
                try {
                    maxClients = Integer.parseInt(arg.substring(2));
                }catch(NumberFormatException e){
                    System.err.println("Error: Failed to parse maximum number of clients.\n");
                    printUsageAndExit(1);
                }
                if(maxClients < 1){
                    System.err.println("Error: Maximum number of clients must be positive.");
                    System.exit(1);
                }
            }
            else if(arg.equals("-q")) isQuiet = true;
            else if(arg.equals("-h") || arg.equals("--help")) printUsageAndExit(0);
            else printUsageAndExit(1);
        }
        
        ServerController controller = new ServerController(port, maxClients, isQuiet);
        controller.listen();
    }

    private static void printUsageAndExit(int status){
        System.out.print(
            " Usage: FFOnline <options>\n" +
            " Options:\n" +
            " \t-c<clients>\tDefine the maximum number of concurrent clients (Default: 50)\n" +
            " \t-h\t\tShow this message and exit\n" +
            " \t-p<port>\tDefine the port to run on (Default: 4080)\n" +
            " \t-q\t\tQuiet; only log errors\n\n"
        );
        System.exit(status);
    }
}
