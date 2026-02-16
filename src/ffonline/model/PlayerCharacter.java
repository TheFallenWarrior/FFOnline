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
import java.util.Optional;

/**
 *
 * @author thefa
 */
public class PlayerCharacter extends Battler {
    private static final int MAX_INVENTORY = 4;
    
    private int level;
    private int exp;
    
    private int strength;
    private int agility;
    private int intelligence;
    private int vitality;
    
    private CharacterJob job;
    
    private final ArrayList<Armor> armorInventory = new ArrayList<>();
    private final ArrayList<Weapon> weaponInventory = new ArrayList<>();
    private Weapon equippedWeapon;

    @Override
    public boolean canReceiveStatus(StatusAilment status){
        return (status != StatusAilment.CONFUSED && super.canReceiveStatus(status));
    }
    
    @Override
    public boolean rollCrit(){ return true; }

    @Override
    public void battleEnd(){

    }

    @Override
    public int getBaseHitsPerTurn(){
        return 1+(getHitChance()/32);
    }
    
    public boolean receiveWeapon(int weaponIndex){
        if(weaponInventory.size() >= MAX_INVENTORY)
            return false;

        Optional<Weapon> opt = JsonLoader.getWeapon(weaponIndex);
        if(opt.isEmpty()) return false;
        else{
            weaponInventory.add(opt.get());
            return true;
        }
    }
    
    public boolean receiveWeapon(Weapon weapon){
        if(weapon == null || weaponInventory.size() >= MAX_INVENTORY)
            return false;
        weaponInventory.add(weapon);
        return true;
    }
    
    public Optional<Weapon> dropWeapon(int inventoryIndex){
        if(
            weaponInventory.isEmpty() ||
            inventoryIndex < 0 ||
            inventoryIndex > weaponInventory.size()
        ) return Optional.empty();
        
        if(weaponInventory.get(inventoryIndex) == equippedWeapon)
            unequipWeapon();
        
        return Optional.of(weaponInventory.remove(inventoryIndex));
    }
    
    public boolean equipWeapon(int inventoryIndex){
        if(
            weaponInventory.isEmpty() ||
            inventoryIndex < 0 ||
            inventoryIndex > weaponInventory.size()
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
    
    public void updateStats(){}

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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
        this.strength = strength;
    }

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getVitality() {
        return vitality;
    }

    public void setVitality(int vitality) {
        this.vitality = vitality;
    }

    public CharacterJob getJob() {
        return job;
    }
    
    public void setJob(CharacterJob job){
        this.job = job;
    }
}
