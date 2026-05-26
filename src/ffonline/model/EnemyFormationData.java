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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.jackson.databind.JsonNode;

/**
 *
 * @author thefa
 */
public class EnemyFormationData {
    private static final Logger LOGGER = Logger.getLogger(EnemyFormationData.class.getName());
    public static final String JSON_PATH = "json/formation.json";
    
    private final int formationId;
    private final int[] enemies;
    private final int surpriseFactor;
    private final boolean isUnrunnable;
    
    private final int[] enemyMinCountA;
    private final int[] enemyMaxCountA;
    private final int[] enemyMinCountB;
    private final int[] enemyMaxCountB;
    
    public EnemyFormationData(
        int formationId,
        int[] enemies,
        int surpriseFactor,
        boolean isUnrunnable,
        int[] enemyMinCountA,
        int[] enemyMaxCountA,
        int[] enemyMinCountB,
        int[] enemyMaxCountB
    ){
        this.formationId = formationId;
        
        if(enemies.length != 4){
            LOGGER.log(Level.WARNING, "Unexpected enemy formation size {0} in formation {1}", new Object[]{enemies.length, formationId});
            this.enemies = new int[]{1, 1, 1, 1};
        } else this.enemies = enemies;
        
        this.surpriseFactor = surpriseFactor&0xff;
        this.isUnrunnable = isUnrunnable;
        
        this.enemyMinCountA = new int[]{0, 0, 0, 0};
        this.enemyMaxCountA = new int[]{0, 0, 0, 0};     
        if(enemyMinCountA.length != 4)
            LOGGER.log(Level.WARNING, "Unexpected min enemy formation size {0} in formation {1} A", new Object[]{enemyMinCountA.length, formationId});
        else if(enemyMaxCountA.length != 4)
            LOGGER.log(Level.WARNING, "Unexpected max enemy formation size {0} in formation {1} A", new Object[]{enemyMaxCountA.length, formationId});
        else for(int i=0;i<4;i++){
            this.enemyMaxCountA[i] = enemyMaxCountA[i];
            
            if(enemyMinCountA[i] > enemyMaxCountA[i]){
                LOGGER.log(Level.WARNING, "Min enemy count is bigger than max enemy count in formation {0} A",  formationId);
                this.enemyMinCountA[i] = enemyMaxCountA[i];
            } else this.enemyMinCountA[i] = enemyMinCountA[i];
        }
        
        this.enemyMinCountB = new int[]{0, 0};
        this.enemyMaxCountB = new int[]{0, 0};
        if(enemyMinCountB.length != 2)
            LOGGER.log(Level.WARNING, "Unexpected min enemy formation size {0} in formation {1} B", new Object[]{enemyMinCountB.length, formationId});
        else if(enemyMaxCountB.length != 2)
            LOGGER.log(Level.WARNING, "Unexpected max enemy formation size {0} in formation {1} B", new Object[]{enemyMaxCountB.length, formationId});
        else for(int i=0;i<2;i++){
            this.enemyMaxCountB[i] = enemyMaxCountB[i];
            
            if(enemyMinCountB[i] > enemyMaxCountB[i]){
                LOGGER.log(Level.WARNING, "Min enemy count is bigger than max enemy count in formation {0} B",  formationId);
                this.enemyMinCountB[i] = enemyMaxCountB[i];
            } else this.enemyMinCountB[i] = enemyMinCountB[i];
        }
    }
    
    
    public static EnemyFormationData buildFromJson(JsonNode node){
        int formationId = node.path("formationId").asInt(0);
        int surpriseFactor = node.path("surpriseFactor").asInt(0);
        boolean isUnrunnable = node.path("isUnrunnable").asBoolean(false);
        
        int[] enemies = JsonLoader.parseIntArray(node.path("enemies"), "enemies", JSON_PATH);
        int[] enemyMinCountA = JsonLoader.parseIntArray(node.path("enemyMinCountA"), "enemyMinCountA", JSON_PATH);
        int[] enemyMaxCountA = JsonLoader.parseIntArray(node.path("enemyMaxCountA"), "enemyMaxCountA", JSON_PATH);
        int[] enemyMinCountB = JsonLoader.parseIntArray(node.path("enemyMinCountB"), "enemyMinCountB", JSON_PATH);
        int[] enemyMaxCountB = JsonLoader.parseIntArray(node.path("enemyMaxCountB"), "enemyMaxCountB", JSON_PATH);

        return new EnemyFormationData(
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
        return Arrays.stream(enemies).boxed().toList();
    }

    public int getSurpriseFactor(){
        return surpriseFactor;
    }

    public boolean isUnrunnable(){
        return isUnrunnable;
    }

    public List<Integer> getEnemyMinCountA(){
        return Arrays.stream(enemyMinCountA).boxed().toList();
    }

    public List<Integer> getEnemyMaxCountA(){
        return Arrays.stream(enemyMaxCountA).boxed().toList();
    }

    public List<Integer> getEnemyMinCountB(){
        return Arrays.stream(enemyMinCountB).boxed().toList();
    }

    public List<Integer> getEnemyMaxCountB(){
        return Arrays.stream(enemyMaxCountB).boxed().toList();
    }
}
