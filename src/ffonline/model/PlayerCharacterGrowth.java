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
package ffonline.model;

import ffonline.JsonLoader;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;

/**
 *
 * @author thefa
 */
public class PlayerCharacterGrowth{
    public static final String JSON_PATH = "json/growth.json";
    private static final Logger LOGGER = Logger.getLogger(Enemy.class.getName());
    
    public static Optional<CharacterJob> getPromotion(CharacterJob job){
        JsonNode jobRoot = JsonLoader.getGrowth().path(job.name());
        
        // This catches promoted jobs not having promotions of their own
        Optional<String> promOpt = jobRoot.path("promotion").asStringOpt();
        
        if(promOpt.isPresent()){
            CharacterJob promoted;
            try{
                promoted = CharacterJob.valueOf(promOpt.get());
                return Optional.of(promoted);
            } catch(IllegalArgumentException e){
                LOGGER.log(Level.SEVERE, "Unknown job found in JSON:{0}", promOpt.get());
            }
        }
        
        return Optional.empty();
    }
    
    public static int getExpForLevelUp(int currentLevel){
        if(currentLevel <= 0 || currentLevel >= 50) return 0;
        
        JsonNode expRoot = JsonLoader.getGrowth().path("exp");
        
        try{
            JsonNode expNode = expRoot.get(currentLevel);
            
            if(expNode != null)
                return expRoot.get(currentLevel).asInt();
        } catch(JacksonException e){
            LOGGER.log(Level.SEVERE, "Critical error retrieving EXP from JSON", e);
        }
        
        return 0;
    }
    
    private enum MpMode{
        NONE,    // No MP growth
        LIMITED, // Limited MP growth (KNIGHT, NINJA)
        FULL     // New MP values read from JSON
    }
    
    public class StatGrowth{
        private final int hitChance;
        private final int magicDefense;
        private final int strength;
        private final int agility;
        private final int intelligence;
        private final int vitality;
        private final int luck;

        private final List<Integer> mp;
        
        // Whether the character should have the extra 20-25 HP increase
        private final boolean hpBonus;
        
        public StatGrowth(
            int hitChance,
            int magicDefense,
            int strength,
            int agility,
            int intelligence,
            int vitality,
            int luck,
            boolean hpBonus,
            List<Integer> mp
                
        ){
            this.hitChance = hitChance;
            this.magicDefense = magicDefense;
            this.strength = strength;
            this.agility = agility;
            this.intelligence = intelligence;
            this.vitality = vitality;
            this.luck = luck;
            this.hpBonus = hpBonus;
            this.mp = Collections.unmodifiableList(mp);
        }
        
        public int getHitChance(){
            return hitChance;
        }

        public int getMagicDefense(){
            return magicDefense;
        }

        public int getStrength(){
            return strength;
        }

        public int getAgility(){
            return agility;
        }

        public int getIntelligence(){
            return intelligence;
        }

        public int getVitality(){
            return vitality;
        }

        public int getLuck(){
            return luck;
        }

        public boolean isHpBonus(){
            return hpBonus;
        }
        
        public List<Integer> getMp(){
            return mp;
        }
    }
}
