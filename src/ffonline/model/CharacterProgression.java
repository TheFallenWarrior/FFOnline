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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;

/**
 *
 * @author thefa
 */
public class CharacterProgression{
    public static final String JSON_PATH = "json/growth.json";
    private static final Logger LOGGER = Logger.getLogger(CharacterProgression.class.getName());
    
    public static Optional<CharacterJob> getPromotion(JsonNode jobRoot, CharacterJob job){
        // This catches promoted jobs not having promotions of their own
        Optional<String> promOpt = jobRoot.path("promotion").asStringOpt();
        
        if(promOpt.isPresent()){
            CharacterJob promoted;
            try{
                promoted = CharacterJob.valueOf(promOpt.get());
                return Optional.of(promoted);
            } catch(IllegalArgumentException e){
                LOGGER.log(Level.WARNING, "Unknown job found in JSON:{0}", promOpt.get());
            }
        }
        
        return Optional.empty();
    }
    
    public static int getExpForLevelUp(JsonNode expRoot, int currentLevel){
        if(currentLevel <= 0 || currentLevel >= 50) return 0;
        
        OptionalInt optExp = expRoot.path(currentLevel).asIntOpt();
        
        if(optExp.isPresent()) return optExp.getAsInt();
        
        LOGGER.log(Level.WARNING, "Couldn't retrieve EXP from JSON index {0}", currentLevel);
        return 0;
    }
    
    public static Optional<StatGrowth> getGrowth(JsonNode jobRoot, CharacterJob job, int newLevel, Random rng){
        if(newLevel <= 1 || newLevel > 50) return Optional.empty();

        JsonNode growthRoot = JsonLoader.getGrowth();

        // Resolve the base-class node for fields that promoted classes cannot override.
        // A promoted-class entry omits "saivl"; scan for whichever base class promotes into this job.
        JsonNode baseRoot = jobRoot;
        if(!jobRoot.has("saivl")){
            for(CharacterJob candidate : CharacterJob.values()){
                JsonNode candidateRoot = growthRoot.path(candidate.name());
                Optional<String> promOpt = candidateRoot.path("promotion").asStringOpt();
                if(promOpt.isPresent() && promOpt.get().equals(job.name())){
                    baseRoot = candidateRoot;
                    break;
                }
            }
        }

        // hitChance always comes from the base class
        int hitChance = baseRoot.path("hitChance").asInt(0);

        // magicDefense: use the promoted-class override when present, else fall back to base class
        int magicDefense = (
            jobRoot.has("magicDefense") ?
            jobRoot.path("magicDefense").asInt(0) :
            baseRoot.path("magicDefense").asInt(0)
        );

        // Stat growth resolution (+SAIVL notation), always from the base class
        int[] saivl = new int[]{0, 0, 0, 0, 0, 0}; // [HP, Str, Agl, Int, Vit, Luck]
        String saivlStr = baseRoot.path("saivl").path(newLevel-1).asString("");

        if(saivlStr.contains("+")) saivl[0] = 1;
        if(saivlStr.contains("S")) saivl[1] = 1;
        if(saivlStr.contains("A")) saivl[2] = 1;
        if(saivlStr.contains("I")) saivl[3] = 1;
        if(saivlStr.contains("V")) saivl[4] = 1;
        if(saivlStr.contains("L")) saivl[5] = 1;

        // Stats not marked for growth still have a 25% chance of increasing (except HP)
        for(int i=1;i<6;i++){
            if(saivl[i] != 0) continue;
            if(rng.nextInt(0, 4) == 0) saivl[i] = 1;
        }

        // MP resolution; use the promoted-class mpMode override when present, else fall back to base class
        List<Integer> mp = new ArrayList<>();
        Optional<String> optMode = jobRoot.path("mpMode").asStringOpt();
        if(!optMode.isPresent()){
            optMode = baseRoot.path("mpMode").asStringOpt();
        }

        MpMode mpMode = MpMode.NONE;
        try{
            mpMode = MpMode.valueOf(optMode.orElse("Non-coercible value"));
        } catch(IllegalArgumentException e){
            LOGGER.log(Level.WARNING, "Unknown MP mode found in JSON:{0}", optMode.orElse("Non-coercible value"));
        }
        
        switch(mpMode){
            case MpMode.NONE -> {
                for(int i=0; i < Magic.MAGIC_LEVELS; i++) mp.add(0);
            }
            
            case MpMode.LIMITED -> {
                // Starting at level 15, gain a charge for the 3 lower spell levels every
                //  odd level.
                // NOTE: Characters with limited MP growth should have their max MPs capped
                //  at 4, but we ignore this, since we can't see the character's max MP
                //  from this method.
                if(newLevel > 15 && newLevel%2 == 1){
                    int i = 0;
                    for(; i < 3; i++) mp.add(1);
                    for(; i < Magic.MAGIC_LEVELS; i++) mp.add(0);
                }
                else for(int i=0; i < Magic.MAGIC_LEVELS; i++) mp.add(0);
            }
            
            case MpMode.FULL -> {
                JsonNode mpNode = jobRoot.path("mp");
                
                String[] mpStr = mpNode.path(newLevel).asString("0/0/0/0/0/0/0/0").split("/");
                for(String str : mpStr){
                    try{
                        mp.add(Integer.valueOf(str));
                    } catch(NumberFormatException e){
                        mp.add(0);
                        LOGGER.log(Level.WARNING, "Non-numeric MP in MP string: {0}", mpStr);
                    }
                }
            }
        }
        
        return Optional.of(new StatGrowth(
                hitChance,
                magicDefense,
                saivl[0] == 1,
                saivl[1],
                saivl[2],
                saivl[3],
                saivl[4],
                saivl[5],
                mp
        ));
    }
    
    public static Optional<StatGrowth> getGrowth(CharacterJob job, int newLevel, Random rng){
        JsonNode jobRoot = JsonLoader.getGrowth().path(job.name());
        
        return getGrowth(jobRoot, job, newLevel, rng);
    }
    
    public static Optional<CharacterJob> getPromotion(CharacterJob job){
        JsonNode jobRoot = JsonLoader.getGrowth().path(job.name());
        
        return getPromotion(jobRoot, job);
    }
    
    public static int getExpForLevelUp(int currentLevel){
        JsonNode expRoot = JsonLoader.getGrowth().path("exp");
        
        return getExpForLevelUp(expRoot, currentLevel);
    }
    
    private enum MpMode{
        NONE,    // No MP growth
        LIMITED, // Limited MP growth (KNIGHT, NINJA)
        FULL     // MP increase read directly from JSON
    }
    
    /**
     * DTO for stat increases. With the exception of hpBonus, its fields are
     * designed to be added (accumulated) to character stats upon level up.
     */
    public static class StatGrowth{
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
            boolean hpBonus,
            int strength,
            int agility,
            int intelligence,
            int vitality,
            int luck,
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
