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
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 *
 * @author thefa
 */
public class RunCommand extends Command {
    private final static Map<StatusAilment, Integer> statusBitmasks = new EnumMap<>(StatusAilment.class);
    
    static {
        statusBitmasks.put(StatusAilment.DEAD, 0x01);
        statusBitmasks.put(StatusAilment.PETRIFIED, 0x02);
        statusBitmasks.put(StatusAilment.POISONED, 0x04);
        statusBitmasks.put(StatusAilment.BLIND, 0x08);
        statusBitmasks.put(StatusAilment.PARALYZED, 0x10);
        statusBitmasks.put(StatusAilment.ASLEEP, 0x20);
        statusBitmasks.put(StatusAilment.SILENCED, 0x40);
        statusBitmasks.put(StatusAilment.CONFUSED, 0x80);
    }
    
    public RunCommand(BattlerGroup allies, Battler actor){
        super(allies, null, actor, null, null, CommandTarget.ACTOR);
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
    private int maskStatus(EnumSet<StatusAilment> statuses){
        int accumulator = 0;
        for(var entry : statusBitmasks.entrySet()){
            if(statuses.contains(entry.getKey())){
                accumulator |= entry.getValue();
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
        else // Enemies always succeed at running
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
        if(allies.size() != 4){
            success = evaluateRun(charActor.getLevel(), charActor);
            return new CommandResult(actor, success);
        }
        
        switch(charIndex){
            // The first character uses the third character's status byte
            case 0 -> {
                success = evaluateRun(
                    maskStatus(allies.get(2).getStatuses()),
                    charActor
                );
            }
            
            // The second character uses the fourth character's status byte
            case 1 -> {
                success = evaluateRun(
                    maskStatus(allies.get(3).getStatuses()),
                    charActor
                );
            }
            
            // The fourth character uses the ones digit of their HP
            case 3 -> {
                // Convert raw digit to FF's string encoding
                int ones = (actor.getHp()%10) + 0x80;
                success = evaluateRun(ones, charActor);
            }
            
            // Default to the correct behavior if invalid character index
            // NOTE: the third character should instead use the identifier of who acts third
            //  in the current round (0..8 or 128..131) but that value is not accessible from
            //  this method.
            default -> { success = evaluateRun(charActor.getLevel(), charActor); }
        }
        
        return new CommandResult(actor, success);
    }
}
