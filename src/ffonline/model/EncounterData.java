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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.jackson.databind.JsonNode;

/**
 *
 * @author thefa
 */
public class EncounterData {
    public static final int FORMATION_SIZE_A = 4;
    public static final int FORMATION_SIZE_B = 2;
    
    private static final Logger LOGGER = Logger.getLogger(EncounterData.class.getName());
    public static final String JSON_PATH = "json/encounter.json";
    
    private final int formationId;
    private final List<Integer> enemies;
    private final int surpriseFactor;
    private final boolean isUnrunnable;
    
    private final List<Integer> enemyMinCountA;
    private final List<Integer> enemyMaxCountA;
    private final List<Integer> enemyMinCountB;
    private final List<Integer> enemyMaxCountB;
    
    public EncounterData(
        int formationId,
        List<Integer> enemies,
        int surpriseFactor,
        boolean isUnrunnable,
        List<Integer> enemyMinCountA,
        List<Integer> enemyMaxCountA,
        List<Integer> enemyMinCountB,
        List<Integer> enemyMaxCountB
    ){
        this.formationId = formationId;
        
        if(enemies.size() != FORMATION_SIZE_A){
            LOGGER.log(Level.WARNING, "Unexpected enemy formation size {0} in formation {1}", new Object[]{enemies.size(), formationId});
            this.enemies = List.of(1, 1, 1, 1);
        } else this.enemies = List.copyOf(enemies);
        
        this.surpriseFactor = surpriseFactor&0xff;
        this.isUnrunnable = isUnrunnable;
        
        this.enemyMinCountA = new ArrayList<>(Arrays.asList(0, 0, 0, 0));
        this.enemyMaxCountA = new ArrayList<>(Arrays.asList(0, 0, 0, 0));
        if(enemyMinCountA.size() != FORMATION_SIZE_A)
            LOGGER.log(Level.WARNING, "Unexpected min enemy formation size {0} in formation {1} A", new Object[]{enemyMinCountA.size(), formationId});
        else if(enemyMaxCountA.size() != FORMATION_SIZE_A)
            LOGGER.log(Level.WARNING, "Unexpected max enemy formation size {0} in formation {1} A", new Object[]{enemyMaxCountA.size(), formationId});
        else for(int i=0;i<4;i++){
            this.enemyMaxCountA.set(i, enemyMaxCountA.get(i));
            
            if(enemyMinCountA.get(i) > enemyMaxCountA.get(i)){
                LOGGER.log(Level.WARNING, "Min enemy count is bigger than max enemy count in formation {0} A",  formationId);
                this.enemyMinCountA.set(i, enemyMaxCountA.get(i));
            } this.enemyMinCountA.set(i, enemyMinCountA.get(i));
        }
        
        this.enemyMinCountB = new ArrayList<>(Arrays.asList(0, 0));
        this.enemyMaxCountB = new ArrayList<>(Arrays.asList(0, 0));
        if(enemyMinCountB.size() != FORMATION_SIZE_B)
            LOGGER.log(Level.WARNING, "Unexpected min enemy formation size {0} in formation {1} B", new Object[]{enemyMinCountB.size(), formationId});
        else if(enemyMaxCountB.size() != FORMATION_SIZE_B)
            LOGGER.log(Level.WARNING, "Unexpected max enemy formation size {0} in formation {1} B", new Object[]{enemyMaxCountB.size(), formationId});
        else for(int i=0;i<2;i++){
            this.enemyMaxCountB.set(i, enemyMaxCountB.get(i));
            
            if(enemyMinCountB.get(i) > enemyMaxCountB.get(i)){
                LOGGER.log(Level.WARNING, "Min enemy count is bigger than max enemy count in formation {0} B",  formationId);
                this.enemyMinCountB.set(i, enemyMaxCountB.get(i));
            } this.enemyMinCountB.set(i, enemyMinCountB.get(i));
        }
    }
    
    
    public static EncounterData buildFromJson(JsonNode node){
        int formationId = node.path("formationId").asInt(0);
        int surpriseFactor = node.path("surpriseFactor").asInt(0);
        boolean isUnrunnable = node.path("isUnrunnable").asBoolean(false);
        
        List<Integer> enemies = JsonLoader.parseIntArray(node.path("enemies"), "enemies", JSON_PATH);
        List<Integer> enemyMinCountA = JsonLoader.parseIntArray(node.path("enemyMinCountA"), "enemyMinCountA", JSON_PATH);
        List<Integer> enemyMaxCountA = JsonLoader.parseIntArray(node.path("enemyMaxCountA"), "enemyMaxCountA", JSON_PATH);
        List<Integer> enemyMinCountB = JsonLoader.parseIntArray(node.path("enemyMinCountB"), "enemyMinCountB", JSON_PATH);
        List<Integer> enemyMaxCountB = JsonLoader.parseIntArray(node.path("enemyMaxCountB"), "enemyMaxCountB", JSON_PATH);

        return new EncounterData(
            formationId,
            enemies,
            surpriseFactor,
            isUnrunnable,
            enemyMinCountA,
            enemyMaxCountA,
            enemyMinCountB,
            enemyMaxCountB
        );
    }

    public int getFormationId(){
        return formationId;
    }

    public List<Integer> getEnemies(){
        return Collections.unmodifiableList(enemies);
    }

    public int getSurpriseFactor(){
        return surpriseFactor;
    }

    public boolean isUnrunnable(){
        return isUnrunnable;
    }

    public List<Integer> getEnemyMinCountA(){
        return Collections.unmodifiableList(enemyMinCountA);
    }

    public List<Integer> getEnemyMaxCountA(){
        return Collections.unmodifiableList(enemyMaxCountA);
    }

    public List<Integer> getEnemyMinCountB(){
        return Collections.unmodifiableList(enemyMinCountB);
    }

    public List<Integer> getEnemyMaxCountB(){
        return Collections.unmodifiableList(enemyMaxCountB);
    }
}
