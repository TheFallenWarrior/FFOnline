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
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author thefa
 */
public class ParsedCommand {
    private final String verb;
    private final List<String> args = new ArrayList<>();
    private final String rest;
    
    public ParsedCommand(String command, int argsLength){
        command = command.strip();
        
        if(command.equals("")){
            verb = "";
            rest = "";
            return;
        }
        
        List<String> tokens = new ArrayList<>();
        
        Pattern pattern = Pattern.compile("\"([^\"]*)\"|\\S+");
        Matcher matcher = pattern.matcher(command);
        
        while(matcher.find()){
            if(matcher.group(1) != null) tokens.add(matcher.group(1)); // Quoted token
            else tokens.add(matcher.group()); // Unquoted token
        }
        
        verb = tokens.getFirst().toLowerCase();
        
        int argsEnd = Math.min(1+argsLength, tokens.size());
        args.addAll(tokens.subList(1, argsEnd));
        
        if(argsEnd < tokens.size()){
            StringBuilder restBuilder = new StringBuilder();
            for(int i=argsEnd;i<tokens.size();i++){
                if(i > argsEnd) restBuilder.append(" ");
                restBuilder.append(tokens.get(i));
            }
            rest = restBuilder.toString();
        } else{
            rest = "";
        }
    }
    
    public ParsedCommand reparse(int argsLength){
        StringBuilder command = new StringBuilder(verb);
        for(String arg : args){
            command.append(" ").append(arg);
        }
        command.append(" ").append(rest);
        
        return new ParsedCommand(command.toString(), argsLength);
    }

    public String getVerb() {
        return verb;
    }

    public List<String> getArgs() {
        return Collections.unmodifiableList(args);
    }

    public String getRest() {
        return rest;
    }
}
