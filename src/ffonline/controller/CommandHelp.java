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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author thefa
 */
public class CommandHelp {
    private static final List<HelpData> SERVER_COMMANDS = new ArrayList<>();
    
    static{
        SERVER_COMMANDS.addAll(List.of(
            new HelpData("ooc", List.of(), "ooc <message>", "Send a global chat message (out-of-character)"),
            new HelpData("who", List.of(), "who", "List currently logged-in users"),
            new HelpData("logout", List.of(), "logout", "Disconnect from the server"),
            new HelpData("status", List.of("score"), "status [<character name|index>]", "Show party or character stats")
        ));
    }
    
    public Optional<HelpData> findHelp(String topic){
        for(HelpData cmd : SERVER_COMMANDS){
            if(cmd.name().equals(topic)) return Optional.of(cmd);
            if(cmd.aliases().contains(topic)) return Optional.of(cmd);
        }
        
        return Optional.empty();
    }
    
    public record HelpData(
        String name,
        List<String> aliases,
        String Usage,
        String Description
    ){}
}
