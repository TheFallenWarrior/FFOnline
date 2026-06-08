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
import java.util.List;

/**
 *
 * @author thefa
 */
public class Presentation {
    
    private Presentation(){}
    
    /**
     * Returns the name of the most severe status afflicting a character, assuming statuses are ordered by severity
     * @param battler the {@code Battler} to get the status ailment from
     * @return a string with the name of the first status ailment in {@code battler.statuses}, or "OK" if there are none
     */
    private static String statusAilment(Battler battler){
        for(StatusAilment status : battler.getStatuses()){
            return status.displayName();
        }
        
        return "OK";
    }
    
    /**
     * Converts an integer to a String using Final Fantasy's int to string algorithm
     * @param value the number to be converted, treated as a 24-bit unsigned int
     * @return a left-aligned, 6-digit wide string representation of {@code value}
     */
    private static String formatNumber6Digits(int value){
        // INTENTIONAL: This method returns wrong values if input is bigger than 999999, replicating
        //  observed behavior from FF1
        
        int tmp = value & 0xffffff;
        char[] buf = new char[6];
        
        // Powers of 10
        int[] p10 = {100000, 10000, 1000, 100};

        // Calculate digits 0 through 3 (100000s to 1000s)
        for(int d = 0; d < 4; d++){
            // Discard upper 8 bits for digits 2 and 3
            if(d >= 2) tmp &= 0xffff;

            int digit = 0;
            // Walk through multiples of p10[d] to find the correct digit
            for(int x=8;x>=0;x--){
                if(tmp >= (x+1) * p10[d]){
                    tmp -= (x+1) * p10[d];
                    digit = x + 1;
                    break;
                }
            }
            buf[d] = (char)('0' + digit);
        }

        // Calculate digits 4 and 5 (tens and ones)
        tmp &= 0xff; // Discard middle 8 bits
        int tens = tmp/10;
        int ones = tmp%10;

        // Since the tens digit can be bigger than 9 and display incorrectly, simulate Final Fantasy's string encoding
        // May go up to P5 (255)
        buf[4] = (char)(tens <= 9 ? '0' + tens : 'A' + tens-10);
        buf[5] = (char)('0' + ones);

        // Replace leading '0's with spaces, never trimming the ones digit
        for(int i = 0; i < 5; i++){
            if(buf[i] == '0') buf[i] = ' ';
            else break;
        }

        return new String(buf);
    }
    
    public static String loginMessage(String username){
        return "LIGHT WARRIOR "+username+"'s journey begins..";
    }
    
    public static String logoutMessage(String username){
        return "LIGHT WARRIOR "+username+" disappeared into the void.";
    }
    
    public static String sayMessage(String username, String message){
        return username+" says, \""+message+"\"";
    }
    
    public static String helpMessage(List<String> commands){
        StringBuilder str = new StringBuilder("Available commands listed below. Type 'help <command>' for more information.\n");
        for(String command : commands){
            str.append(String.format(" %s\n", command));
        }
        return str.toString();
    }
    
    public static String helpMessage(CommandHelp.HelpData helpData){
        StringBuilder str = new StringBuilder(String.format(
            "%s command help:\n" +
            " Usage: %s \n" +
            " Description: %s\n",
            helpData.name().toUpperCase(),
            helpData.usage(),
            helpData.description()
        ));
        
        if(!helpData.aliases().isEmpty()){
            str.append(" Aliases: ");
            for(String alias : helpData.aliases()){
                str.append(String.format(
                    "%s%s",
                    alias,
                    (alias.equals(helpData.aliases().getLast()) ? "" : ", ")
                ));
            }
            str.append("\n");
        }
        return str.toString();
    }
    
    public static String characterStats(PlayerCharacter character){
        return String.format(
            "%s - %s - LEV %2d\n\n" +
            " EXP. POINTS\t%s\n" +
            " FOR LEV UP\t%s\n\n" +
            " STR.\t%2d\tDAMAGE\t%2d\n" +
            " AGL.\t%2d\tHIT%%\t%2d\n" +
            " INT.\t%2d\tABSORB\t%2d\n" +
            " VIT.\t%2d\tEVADE%%\t%2d\n" +
            " LUCK\t%2d\n",
            character.getName(),
            character.getJob().displayName(),
            character.getLevel(),
            formatNumber6Digits(character.getExp()),
            formatNumber6Digits(character.getExpForNextLevel()),
            character.getStrength(),
            character.getDamage(),
            character.getAgility(),
            character.getHitChance(),
            character.getIntelligence(),
            character.getAbsorb(),
            character.getVitality(),
            character.getEvadeChance(),
            character.getLuck()
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
