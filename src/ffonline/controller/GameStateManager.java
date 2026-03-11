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

import ffonline.JsonLoader;
import ffonline.model.PlayerCharacter;
import ffonline.model.PlayerParty;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author thefa
 */
public class GameStateManager {
    private final PlayerParty party = new PlayerParty();
    
    private final BufferedReader in;
    private final PrintWriter out;
    
    public GameStateManager(BufferedReader in, PrintWriter out){
        this.in = in;
        this.out = out;
        
        // Placeholder party of 4 fighters
        party.add(JsonLoader.getPlayerCharacter(0).get());
        party.add(JsonLoader.getPlayerCharacter(0).get());
        party.add(JsonLoader.getPlayerCharacter(0).get());
        party.add(JsonLoader.getPlayerCharacter(0).get());
    }
    
    public void runGameCommand(String command){
        ParsedCommand parseComm = new ParsedCommand(command, 1);
        
        switch(parseComm.getVerb()){
            case "status" -> statusCommand(parseComm.getArgs());
            
            case "score" -> statusCommand(parseComm.getArgs());
            
            case "" -> {}
                
            default -> out.println("Unknown command: \""+parseComm.getVerb()+"\"");
        }
    }
    
    private Optional<PlayerCharacter> resolveCharacter(String token){
        Optional<PlayerCharacter> charOpt = party.getFromName(token);
        if(charOpt.isPresent()) return charOpt;
        
        try{
            int charIndex = Integer.parseInt(token)-1;
            return party.getOptional(charIndex);
        } catch(NumberFormatException e){
            return Optional.empty();
        }
    }
    
    private void statusCommand(List<String> args){
        switch(args.size()){
            case 0 -> out.print(Presentation.partyStats(party));
            
            case 1 -> {
                Optional<PlayerCharacter> charOpt = resolveCharacter(args.getFirst());
                if(charOpt.isPresent()) out.print(Presentation.characterStats(charOpt.get()));
                else out.println("Error: '"+args.getFirst()+"' isn't a valid character name or character index.");
            }
        }
        out.flush();
    }
}
