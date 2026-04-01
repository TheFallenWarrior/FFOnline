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
package ffonline.model.command;

import ffonline.model.Battler;
import ffonline.model.BattlerGroup;
import ffonline.model.PlayerCharacter;
import ffonline.model.StatusAilment;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author thefa
 */
public class RunCommand extends Command {
    private final static Map<Integer, StatusAilment> statusBitmasks = new HashMap<>();
    public RunCommand(BattlerGroup allies, Battler actor){
        super(allies, null, actor, null, null, CommandTarget.ACTOR);
        
        statusBitmasks.put(0x01, StatusAilment.DEAD);
        statusBitmasks.put(0x02, StatusAilment.PETRIFIED);
        statusBitmasks.put(0x04, StatusAilment.POISONED);
        statusBitmasks.put(0x08, StatusAilment.BLIND);
        statusBitmasks.put(0x10, StatusAilment.PARALYZED);
        statusBitmasks.put(0x20, StatusAilment.ASLEEP);
        statusBitmasks.put(0x40, StatusAilment.SILENCED);
        statusBitmasks.put(0x80, StatusAilment.CONFUSED);
}
    
    /**
     * Original formula for deciding if running is successful
     * @param value The value to be used instead of {@code charActor}'s level
     * @param charActor The {@code PlayerCharacter} attempting to run
     * @return {@code true} if {@code charActor}'s luck > 0..({@code value}+15); {@code false} otherwise
     */
    private boolean evaluateRun(int value, PlayerCharacter charActor){
        return (charActor.getLuck() > rng.nextInt(0, (value&0xff) + 16));
    }
    
    /**
     * Get original status encoding from {@code EnumSet<StatusAilment>}
     * @param statuses EnumSet-encoded status set
     * @return bit-mask-encoded status set
     */
    private int unmaskStatus(EnumSet<StatusAilment> statuses){
        int accumulator = 0;
        for(var entry : statusBitmasks.entrySet()){
            if(statuses.contains(entry.getValue())){
                accumulator |= entry.getKey();
            }
        }
        
        return accumulator;
    }
    
    @Override
    public CommandResult execute(){
        int charIndex = -1;
        boolean success;
        PlayerCharacter charActor;
        
        if(actor instanceof PlayerCharacter character)
            charActor = character;
        else // Enemies always succeed from running
            return new CommandResult(actor, true);
        
        for(int i=0;i<allies.size();i++){
            if(allies.get(i) == actor){
                charIndex = i;
                break;
            }
        }
        
        // INTENTIONAL: Running was meant to use the character's level against their luck
        //  but it's bugged, and loads special values according to the character index.
        
        // Default to the correct behavior if unexpected party size
        if(allies.size() != 4) success = evaluateRun(charActor.getLevel(), charActor);
        
        switch(charIndex){
            // The first character uses the third character's status byte
            case 0 -> {
                success = evaluateRun(
                    unmaskStatus(allies.get(2).getStatuses()),
                    charActor
                );
            }
            
            // The second character uses the fourth character's status byte
            case 1 -> {
                success = evaluateRun(
                    unmaskStatus(allies.get(3).getStatuses()),
                    charActor
                );
            }
            
            // The forth character uses the ones digit of their HP
            case 3 -> {
                String hpStr = ""+actor.getHp();
                int ones = hpStr.charAt(hpStr.length()-1);
                
                // Convert from ASCII encoding to FF's string encoding
                ones = (ones-0x30) + 0x80;
                success = evaluateRun(ones, charActor);
            }
            
            // Default to the correct behavior if invalid character index
            // NOTE: the third character should instead use the identifier of who acts third
            //  in the current round (0..8 or 128..131) but that's terribly out of scope from
            //  this method.
            default -> { success = evaluateRun(charActor.getLevel(), charActor); }
        }
        
        return new CommandResult(actor, success);
    }
}
