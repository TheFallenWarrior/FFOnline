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

import ffonline.model.PlayerCharacter;
import ffonline.model.PlayerParty;

/**
 *
 * @author thefa
 */
public class Presentation {
    public static String characterStatus(PlayerCharacter character){
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
            character.getJob().toString(),
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
    
    public static String partyStatus(PlayerParty party){
        String str = " NAME\tHP\tSTATUS\tJOB\n";
        for(PlayerCharacter member : party){
            str = str.concat(String.format(
                " %s\t%d\t%s\t%s\n",
                member.getName(),
                member.getHp(),
                "Sad", // Placeholder
                (
                    member.getJob().toString().length() < 8 ?
                    member.getJob().toString(): 
                    member.getJob().toString().substring(0, 8)
                )
            ));
        }
        return str;
    }
}
