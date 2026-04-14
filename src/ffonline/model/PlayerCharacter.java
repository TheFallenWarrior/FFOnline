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
import java.util.EnumMap;
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
public class PlayerCharacter extends Battler {
    private static final int MAX_INVENTORY = 4;
    private static final int MAGIC_MAX_INVENTORY = 3;
    private static final int STARTING_MP = 2;
    
    private static final Logger LOGGER = Logger.getLogger(PlayerCharacter.class.getName());
    public static final String JSON_PATH = "json/job.json";
    
    private int level;
    private int exp;
    
    private int strength;
    private int agility;
    private int intelligence;
    private int vitality;
    private int luck;
    
    private int baseHitChance;
    private int baseHitsPerTurn;
    
    private CharacterJob job;

    private final int[] mp = new int[Magic.MAGIC_LEVELS];
    private final int[] maxMp = new int[Magic.MAGIC_LEVELS];
    
    private final Inventory<Armor> armorInventory = new Inventory<>(MAX_INVENTORY);
    private final EnumMap<ArmorType, Armor> equippedArmors = new EnumMap<>(ArmorType.class);

    private final Inventory<Weapon> weaponInventory = new Inventory<>(MAX_INVENTORY);
    private Weapon equippedWeapon;
    
    private final Inventory<Magic>[] magicInventory = new Inventory[Magic.MAGIC_LEVELS];

    public PlayerCharacter(
            int hp,
            int strength,
            int agility,
            int intelligence,
            int vitality,
            int luck,
            int damage,
            int hitChance,
            int evadeChance,
            int magicDefense,
            CharacterJob job
    ){
        super(hp&0xff, damage, hitChance, evadeChance, magicDefense);
        this.strength = strength&0xff;
        this.agility = agility&0xff;
        this.intelligence = intelligence&0xff;
        this.vitality = vitality&0xff;
        this.luck = luck&0xff;
        this.job = job;
        
        this.level = 1;
        
        if(
            job == CharacterJob.RED_MAGE ||
            job == CharacterJob.WHITE_MAGE ||
            job == CharacterJob.BLACK_MAGE
        ) mp[0] = STARTING_MP;
        
        for(int i=0;i<Magic.MAGIC_LEVELS;i++)
            magicInventory[i] = new Inventory<>(MAGIC_MAX_INVENTORY);
    }
    
    public static PlayerCharacter buildFromJson(JsonNode node){
        int hp = node.path("hp").asInt(0);
        int strength = node.path("strength").asInt(0);
        int agility = node.path("agility").asInt(0);
        int intelligence = node.path("intelligence").asInt(0);
        int vitality = node.path("vitality").asInt(0);
        int luck = node.path("luck").asInt(0);
        int damage = node.path("damage").asInt(0);
        int hitChance = node.path("hitChance").asInt(0);
        int evadeChance = node.path("evadeChance").asInt(0);
        int magicDefense = node.path("magicDefense").asInt(0);

        Optional<String> optJob = node.path("job").asStringOpt();
        CharacterJob resolvedJob = CharacterJob.FIGHTER;
        try{
            resolvedJob = CharacterJob.valueOf(optJob.orElse("Non-coercible value"));
        } catch(IllegalArgumentException e){
            LOGGER.log(Level.WARNING, "Unknown job found in JSON: {0}", optJob.orElse("Non-coercible value"));
        }
        
        return new PlayerCharacter(
            hp,
            strength,
            agility,
            intelligence,
            vitality,
            luck,
            damage,
            hitChance,
            evadeChance,
            magicDefense,
            resolvedJob
        );
    }
    
    @Override
    public boolean canReceiveStatus(StatusAilment status){
        return (status != StatusAilment.CONFUSED && super.canReceiveStatus(status));
    }

    @Override
    public void battleEnd(){
        // Remove temporary status ailments
        removeStatus(StatusAilment.BLIND);
        removeStatus(StatusAilment.PARALYZED);
        removeStatus(StatusAilment.ASLEEP);
        removeStatus(StatusAilment.SILENCED);
        
        // Clear stat buffs/debuffs
        resetHitMultiplier();
        updateStats();
    }

    @Override
    public int getBaseHitsPerTurn(){
        return baseHitsPerTurn;
    }
    
    public boolean receiveArmor(int armorIndex){
        Optional<Armor> opt = JsonLoader.getArmor(armorIndex);
        return opt.map(this::receiveArmor).orElse(false);
    }
    
    public boolean receiveArmor(Armor armor){
        if(armor == null) return false;
        return armorInventory.add(armor);
    }
    
    public Optional<Armor> dropArmor(int inventoryIndex){
        if(
            inventoryIndex < 0 ||
            inventoryIndex >= armorInventory.size()
        ) return Optional.empty();
        
        Armor toDrop = armorInventory.get(inventoryIndex);
        
        if(equippedArmors.get(toDrop.getType()) == toDrop){
            equippedArmors.remove(toDrop.getType());
            updateStats();
        }
        
        return Optional.of(armorInventory.remove(inventoryIndex));
    }
    
    public boolean equipArmor(int inventoryIndex){
        if(
            inventoryIndex < 0 ||
            inventoryIndex >= armorInventory.size()
        ) return false;
        
        Armor toEquip = armorInventory.get(inventoryIndex);
        
        if(toEquip.isEquippable(job)){
            equippedArmors.put(toEquip.getType(), toEquip);
            updateStats();
            return true;
        }
        return false;
    }
    
    public boolean unequipArmor(int inventoryIndex){
        if(
            inventoryIndex < 0 ||
            inventoryIndex >= armorInventory.size()
        ) return false;
        
        Armor toUnequip = armorInventory.get(inventoryIndex);
        
        if(equippedArmors.get(toUnequip.getType()) != toUnequip) return false;
        equippedArmors.remove(toUnequip.getType());
        updateStats();
        return true;
    }
    
    public boolean receiveWeapon(int weaponIndex){
        Optional<Weapon> opt = JsonLoader.getWeapon(weaponIndex);
        return opt.map(this::receiveWeapon).orElse(false);
    }
    
    public boolean receiveWeapon(Weapon weapon){
        if(weapon == null) return false;
        return weaponInventory.add(weapon);
    }
    
    public Optional<Weapon> dropWeapon(int inventoryIndex){
        if(
            inventoryIndex < 0 ||
            inventoryIndex >= weaponInventory.size()
        ) return Optional.empty();
        
        if(weaponInventory.get(inventoryIndex) == equippedWeapon)
            unequipWeapon();
        
        return Optional.of(weaponInventory.remove(inventoryIndex));
    }
    
    public boolean equipWeapon(int inventoryIndex){
        if(
            inventoryIndex < 0 ||
            inventoryIndex >= weaponInventory.size()
        ) return false;
        
        if(weaponInventory.get(inventoryIndex).isEquippable(job)){
            equippedWeapon = weaponInventory.get(inventoryIndex);
            updateStats();
            return true;
        }
        return false;
    }
    
    public void unequipWeapon(){
        equippedWeapon = null;
        updateStats();
    }
    
    public boolean receiveMagic(Magic magic){
        if(
            magic == null ||
            magic.getLevel() <= 0 ||
            magic.getLevel() > Magic.MAGIC_LEVELS ||
            !magic.isEquippable(job)
        ) return false;
        
        return magicInventory[magic.getLevel()-1].add(magic);
    }
    
    public void updateStats(){
        updateStats(false);
    }

    public void updateStats(boolean blackBeltBugEnable){
        // Weapon stats
        if(equippedWeapon != null){
            // INTENTIONAL: Due to a bug in FF1, weapon elemental affinities are never loaded and have no effect
            // INTENTIONAL: Another bug causes characters to be weak to the element their weapon is strong against,
            //  but because of the bug described above, it has no effect
            // INTENTIONAL: The weapon ID is used as the critical chance for physical attacks
            setDamage(strength/2 + equippedWeapon.getDamage());
            setHitChance(baseHitChance + equippedWeapon.getHitChance());
            setCritChance(equippedWeapon.getWeaponId());
        } else{
            setDamage(strength/2);
            setHitChance(baseHitChance);
            setCritChance(0);
        }
        
        baseHitsPerTurn = (1+(getHitChance()/32));
        
        // Armor stats
        int totalAbsorb = 0;
        int totalWeight= 0;
        EnumSet<Element> totalResistances = EnumSet.noneOf(Element.class);
        for(Armor i : equippedArmors.values()){
            if(i == null) continue;
            totalAbsorb += i.getAbsorb();
            totalWeight += i.getWeight();
            totalResistances.addAll(i.getElementalResistances());
        }
        setAbsorb(totalAbsorb);
        setEvadeChance(48 + agility - totalWeight);
        setElementalResistances(totalResistances);
        
        // Black Belt-specific behavior
        if(job == CharacterJob.BLACK_BELT || job == CharacterJob.MASTER){
            if(equippedWeapon == null){
                setDamage(level*2);
                setCritChance(level*2);
                baseHitsPerTurn *= 2;
            } else{
                setDamage(1 + getDamage());
            }
                        
            // INTENTIONAL: The original Black Belt level-up bug is replicated:
            //  on level-up, the game mistakenly checks whether a weapon is equipped
            //  to apply the absorb bonus. When changing equipment, it works as
            //  intended by checking whether any armor is worn.
            if(
                !blackBeltBugEnable && totalAbsorb == 0 ||
                blackBeltBugEnable && equippedWeapon == null
            ) setAbsorb(level);
        }
    }
    
        public boolean shouldLevelUp(){
        int nextExp = CharacterProgression.getExpForLevelUp(level);
        
        return (nextExp != 0 && exp >= nextExp);
    }
    
    public Optional<CharacterProgression.StatGrowth> levelUp(){
        // INTENTIONAL: A character only levels up once at a time, even if they have
        //  enough EXP to go up multiple levels.
        if(!shouldLevelUp()) return Optional.empty();
        
        var growthOpt = CharacterProgression.getGrowth(job, ++level, rng);
        if(growthOpt.isEmpty()) return growthOpt;
        var growth = growthOpt.get();
        
        setBaseHitChance(baseHitChance + growth.getHitChance());
        offsetMagicDefense(growth.getMagicDefense());
        
        setStrength(strength + growth.getStrength());
        setAgility(agility + growth.getAgility());
        setIntelligence(intelligence + growth.getIntelligence());
        setVitality(vitality + growth.getVitality());
        setLuck(luck + growth.getLuck());
        
        offsetMaxHp(vitality/4);
        // Optionally add 20..25 to max HP
        if(growth.isHpBonus()) offsetMaxHp(20 + rng.nextInt(0, 6));
        
        // MP handling
        if(growth.getMp().size() != maxMp.length){
            LOGGER.log(
                Level.WARNING,
                "MP length mismatch: {0} vs {1}",
                new Object[]{growth.getMp().size(), maxMp.length}
            );
        } else{
            for(int i=0;i<maxMp.length;i++){
                maxMp[i] += growth.getMp().get(i);
            }
        }
        
        // Enable the Black Belt bug
        updateStats(true);
        
        return growthOpt;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level&0xff;
    }

    public int getExp() { 
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength&0xff;
    }

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility&0xff;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence&0xff;
    }

    public int getVitality() {
        return vitality;
    }

    public void setVitality(int vitality) {
        this.vitality = vitality&0xff;
    }

    public CharacterJob getJob() {
        return job;
    }
    
    public void setJob(CharacterJob job){
        this.job = job;
    }

    public int getBaseHitChance() {
        return baseHitChance;
    }

    public void setBaseHitChance(int baseHitChance) {
        this.baseHitChance = baseHitChance&0xff;
    }

    public int getLuck() {
        return luck;
    }

    public void setLuck(int luck) {
        this.luck = luck&0xff;
    }
    
    public List<Magic> getMagicInventory(int level){
        if(level < 0 || level >= Magic.MAGIC_LEVELS){
            LOGGER.log(Level.WARNING, "Tried to read invalid magic inventory level: {0}", level);
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(magicInventory[level]);
    }
    
    /**
     * @return A flat {@code List} with all spells the character knows
     */
    public List<Magic> getMagicInventory(){
        List<Magic> magicList = new ArrayList<>();
        for(int i=0;i<Magic.MAGIC_LEVELS;i++){
            magicList.addAll(magicInventory[i]);
        }
        return Collections.unmodifiableList(magicList);
    }
    
    public List<Integer> getMp(){
        List<Integer> mpList = new ArrayList<>();
        for(int mpSlot : mp){
            mpList.add(mpSlot);
        }
        return Collections.unmodifiableList(mpList);
    }
    
    public List<Integer> getMaxMp(){
        List<Integer> mpList = new ArrayList<>();
        for(int mpSlot : maxMp){
            mpList.add(mpSlot);
        }
        return Collections.unmodifiableList(mpList);
    }
}
