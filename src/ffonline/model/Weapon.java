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

import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.jackson.databind.JsonNode;

/**
 *
 * @author thefa
 */
public class Weapon extends Item {
    private static final Logger LOGGER = Logger.getLogger(Weapon.class.getName());
    public static final String JSON_PATH = "json/weapon.json";
    
    private final int hitChance;
    private final int critChance; // Weapon index number, 1-based
    private final int damage;
    private final int spellId;
    private final EnumSet<Element> attackElements;
    private final EnumSet<EnemyType> enemyTypes; // The enemy types the weapon is strong against
    
    public Weapon(JsonNode node){
        super(node);
        this.hitChance = node.path("hitChance").asInt(0);
        this.critChance = node.path("critChance").asInt(0);
        this.damage = node.path("damage").asInt(0);
        this.spellId = node.path("spellId").asInt(0);
        
        this.attackElements = EnumSet.noneOf(Element.class);
        JsonNode attackElem = node.path("attackElements");
        if(attackElem.isArray()){
            for(JsonNode i : attackElem){
                try{
                    attackElements.add(Element.valueOf(i.asString("Nothing")));
                } catch(IllegalArgumentException e){
                    LOGGER.log(Level.WARNING, "Unknown element found in JSON: {0}", i.asString());
                }
            }
        }
        
        this.enemyTypes = EnumSet.noneOf(EnemyType.class);
         JsonNode enTypes = node.path("enemyTypes");
        if(attackElem.isArray()){
            for(JsonNode i : enTypes){
                try{
                    enemyTypes.add(EnemyType.valueOf(i.asString("Nothing")));
                } catch(IllegalArgumentException e){
                    LOGGER.log(Level.WARNING, "Unknown enemy type found in JSON: {0}", i.asString());
                }
            }
        }
    }
    
    public int getHitChance() {
        return hitChance;
    }
    
    public int getCritChance() {
        return critChance;
    }

    public int getDamage() {
        return damage;
    }

    public int getSpellId() {
        return spellId;
    }

    public EnumSet<Element> getAttackElements() {
        return attackElements.clone();
    }

    public EnumSet<EnemyType> getEnemyTypes() {
        return enemyTypes.clone();
    }
}
