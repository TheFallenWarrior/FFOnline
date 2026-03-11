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

import ffonline.model.Battler;
import ffonline.model.PlayerCharacter;
import ffonline.model.PlayerParty;
import ffonline.model.StatusAilment;

/**
 *
 * @author thefa
 */
public class Presentation {
    /**
     * Returns the name of the most severe status afflicting a character
     * @param battler the Battler to get the status ailment from
     * @return a String with the name of the first status ailment in battler.statuses, or "OK" if there are none
     */
    private static String statusAilment(Battler battler){
        for(StatusAilment status : battler.getStatuses()){
            return status.displayName();
        }
        
        return "OK";
    }
    
    public static String characterStats(PlayerCharacter character){
        return String.format(
            "%s - %s - LEV %d\n\n" +
            " EXP. POINTS\t%d\n" +
            " FOR LEV UP\t%d\n\n" +
            " STR.\t%d\n" +
            " AGL.\t%d\n" +
            " INT.\t%d\n" +
            " VIT.\t%d\n" +
            " LUCK\t%d\n\n" +
            " DAMAGE\t%d\n" +
            " HIT%%\t%d\n" +
            " ABSORB\t%d\n" +
            " EVADE%%\t%d\n\n",
            character.getName(),
            character.getJob().displayName(),
            character.getLevel(),
            character.getExp(),
            -1, // Placeholder
            character.getStrength(),
            character.getAgility(),
            character.getIntelligence(),
            character.getVitality(),
            character.getLuck(),
            character.getDamage(),
            character.getHitChance(),
            character.getAbsorb(),
            character.getEvadeChance()
        );
    }
    
    public static String partyStats(PlayerParty party){
        StringBuilder str = new StringBuilder(" NAME\tHP\tSTATUS\tJOB\n");
        for(PlayerCharacter member : party){
            str = str.append(String.format(
                " %s\t%d\t%s\t%s\n",
                member.getName(),
                member.getHp(),
                statusAilment(member),
                member.getJob().displayName()
            ));
        }
        return str.toString();
    }
}
