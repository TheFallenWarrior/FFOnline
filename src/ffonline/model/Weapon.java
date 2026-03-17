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
import java.util.EnumSet;
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
    private final EnumSet<Element> elementalOffense;
    private final EnumSet<EnemyType> enemyTypes; // The enemy types the weapon is strong against
    private final EnumSet<CharacterJob> equippable;
    
    public Weapon(
        String name,
        int shopId,
        int price,
        int hitChance,
        int critChance,
        int damage,
        int spellId,
        EnumSet<Element> elementalOffense,
        EnumSet<EnemyType> enemyTypes,
        EnumSet<CharacterJob> equippable
    ){
        super(name, shopId, price);
        this.hitChance = hitChance&0xff;
        this.critChance = critChance&0xff;
        this.damage = damage&0xff;
        this.spellId = spellId&0xff;
        this.elementalOffense = EnumSet.copyOf(elementalOffense);
        this.enemyTypes = EnumSet.copyOf(enemyTypes);
        this.equippable = EnumSet.copyOf(equippable);
    }
    
    public static Weapon buildFromJson(JsonNode node){
        String name = node.path("name").asString("Non-coercible value");
        int shopId = node.path("shopId").asInt(0);
        int price = node.path("price").asInt(0);
        
        int hitChance = node.path("hitChance").asInt(0);
        int critChance = node.path("critChance").asInt(0);
        int damage = node.path("damage").asInt(0);
        int spellId = node.path("spellId").asInt(0);
        
        EnumSet<Element> elementalOffense = JsonLoader.parseEnumSet(node.path("elementalOffense"), Element.class, "element");
        EnumSet<EnemyType> enemyTypes = JsonLoader.parseEnumSet(node.path("enemyTypes"), EnemyType.class, "enemy type");
        EnumSet<CharacterJob> equippable = JsonLoader.parseEnumSet(node.path("equippable"), CharacterJob.class, "job");
        
        return new Weapon(
            name,
            shopId,
            price,
            hitChance,
            critChance,
            damage,
            spellId,
            elementalOffense,
            enemyTypes,
            equippable
        );
    }
    
    public boolean isEquippable(CharacterJob job){
        return equippable.contains(job);
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

    public EnumSet<Element> getElementalOffense() {
        return EnumSet.copyOf(elementalOffense);
    }

    public EnumSet<EnemyType> getEnemyTypes() {
        return EnumSet.copyOf(enemyTypes);
    }
    
    public EnumSet<CharacterJob> getEquippable(){
        return EnumSet.copyOf(equippable);
    }
}
