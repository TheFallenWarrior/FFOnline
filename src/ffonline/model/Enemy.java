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
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.jackson.databind.JsonNode;

/**
 *
 * @author thefa
 */
public class Enemy extends Battler{
    private static final int MAGIC_MAX_INVENTORY = 8;
    private static final int SKILL_MAX_INVENTORY = 4;
    
    private static final Logger LOGGER = Logger.getLogger(Enemy.class.getName());
    public static final String JSON_PATH = "json/enemy.json";
    
    private final int exp;
    private final int gil;
    
    private final int morale;
    private final int baseHitsPerTurn;
    private final EnumSet<EnemyType> enemyTypes;
    private final EnumSet<StatusAilment> attackStatuses; // Status that can be inflicted by normal attacks
    
    private final Inventory<Magic> magicInventory = new Inventory<>(MAGIC_MAX_INVENTORY);
    private final int magicChance;
    
    private final Inventory<Magic> skillInventory = new Inventory<>(SKILL_MAX_INVENTORY);
    private final int skillChance;
    
    public Enemy(
        String name,
        int exp,
        int gil,
        int hp,
        int morale,
        List<Magic> magicInventory,
        int magicChance,
        List<Magic> skillInventory,
        int skillChance,
        int evadeChance,
        int absorb,
        int baseHitsPerTurn,
        int hitChance,
        int damage,
        int critChance,
        EnumSet<Element> elementalOffense,
        EnumSet<StatusAilment> attackStatuses,
        EnumSet<EnemyType> enemyTypes,
        int magicDefense,
        EnumSet<Element> elementalWeaknesses,
        EnumSet<Element> elementalResistances
    ){
        super(
            hp,
            damage,
            absorb,
            hitChance,
            critChance,
            evadeChance,
            magicDefense,
            elementalOffense,
            elementalWeaknesses,
            elementalResistances
        );
        setName(name);
        this.exp = exp&0xffff;
        this.gil = gil&0xffff;
        this.morale = morale&0xff;
        this.magicInventory.addAll(magicInventory);
        this.magicChance = magicChance&0xff;
        this.skillInventory.addAll(skillInventory);
        this.skillChance = skillChance&0xff;
        this.baseHitsPerTurn = baseHitsPerTurn&0xff;
        this.attackStatuses = EnumSet.copyOf(attackStatuses);
        this.enemyTypes = EnumSet.copyOf(enemyTypes);
    }
    
    public static Enemy buildFromJson(JsonNode node){
        String name = node.path("name").asString("Non-coercible value");
        int exp = node.path("exp").asInt(0);
        int gil = node.path("gil").asInt(0);
        int hp = node.path("hp").asInt(0);
        int morale = node.path("morale").asInt(0);
        
        List<Magic> magicInventory = new ArrayList<>();
        if(node.path("magicInventory").isArray()){
            for(JsonNode spellNode : node.path("magicInventory")){
                Optional<Magic> optSpell = Magic.getBySpellId(spellNode.asInt(0));
                if(optSpell.isPresent()) magicInventory.add(optSpell.get());
                else LOGGER.log(Level.WARNING, "Invalid spellId {0} found in JSON", spellNode.asInt(0));
            }
        }
        int magicChance = node.path("magicChance").asInt(0);
        
        List<Magic> skillInventory = new ArrayList<>();
        if(node.path("skillInventory").isArray()){
            for(JsonNode spellNode : node.path("skillInventory")){
                Optional<Magic> optSpell = Magic.getBySpellId(spellNode.asInt(0));
                if(optSpell.isPresent()) skillInventory.add(optSpell.get());
                else LOGGER.log(Level.WARNING, "Invalid spellId {0} found in JSON", spellNode.asInt(0));
            }
        }
        int skillChance = node.path("skillChance").asInt(0);
        
        int evadeChance = node.path("evadeChance").asInt(0);
        int absorb = node.path("absorb").asInt(0);
        int baseHitsPerTurn = node.path("baseHitsPerTurn").asInt(0);
        int hitChance = node.path("hitChance").asInt(0);
        int damage = node.path("damage").asInt(0);
        int critChance = node.path("critChance").asInt(0);
        int magicDefense = node.path("magicDefense").asInt(0);

        EnumSet<Element> elementalOffense = JsonLoader.parseEnumSet(node.path("elementalOffense"), Element.class, "element");
        EnumSet<StatusAilment> attackStatuses = JsonLoader.parseEnumSet(node.path("attackStatuses"), StatusAilment.class, "status ailment");
        EnumSet<EnemyType> enemyTypes = JsonLoader.parseEnumSet(node.path("enemyTypes"), EnemyType.class, "enemy type");
        EnumSet<Element> elementalWeaknesses = JsonLoader.parseEnumSet(node.path("elementalWeaknesses"), Element.class, "element");
        EnumSet<Element> elementalResistances = JsonLoader.parseEnumSet(node.path("elementalResistances"), Element.class, "element");
        
        return new Enemy(
            name,
            exp,
            gil,
            hp,
            morale,
            magicInventory,
            magicChance,
            skillInventory,
            skillChance,
            evadeChance,
            absorb,
            baseHitsPerTurn,
            hitChance,
            damage,
            critChance,
            elementalOffense,
            attackStatuses,
            enemyTypes,
            magicDefense,
            elementalWeaknesses,
            elementalResistances
        );
    }
    
    @Override
    public boolean canReceiveStatus(StatusAilment status){
        return (status != StatusAilment.POISONED && super.canReceiveStatus(status));
    }

    @Override
    public void battleEnd(){
        setHp(0);
    }

    public int getExp() {
        return exp;
    }

    public int getGil() {
        return gil;
    }

    public int getMorale() {
        return morale;
    }

    public EnumSet<EnemyType> getEnemyTypes() {
        return enemyTypes;
    }

    public EnumSet<StatusAilment> getAttackStatuses() {
        return attackStatuses;
    }

    public List<Magic> getMagicInventory(){
        return Collections.unmodifiableList(magicInventory);
    }
    
    public int getMagicChance() {
        return magicChance;
    }
    
    public List<Magic> getSkillInventory(){
        return Collections.unmodifiableList(skillInventory);
    }

    public int getSkillChance() {
        return skillChance;
    }
    
    @Override
    public int getBaseHitsPerTurn(){
        return baseHitsPerTurn;
    }
}
