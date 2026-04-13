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
import java.util.Random;

/**
 *
 * @author thefa
 */
public abstract class Battler {
    private String name;
    private final EnumSet<StatusAilment> statuses;
    private EnumSet<Element> elementalOffense;
    private EnumSet<Element> elementalResistances;
    private EnumSet<Element> elementalWeaknesses;
    
    protected Random rng;

    /*
     * Battle stats
     */
    private int hp, maxHp;
    private int damage; // Attack
    private int absorb; // Defense
    private int evadeChance; // Evasion
    private int hitChance;   // Accuracy
    private int critChance;
    private int magicDefense;

    private int hitMultiplier = 1; // Affects number of hits per turn

    public Battler(){
        statuses = EnumSet.noneOf(StatusAilment.class);
        elementalOffense = EnumSet.noneOf(Element.class);
        elementalResistances = EnumSet.noneOf(Element.class);
        elementalWeaknesses = EnumSet.noneOf(Element.class);
        rng = new Random();
    }
    
    public Battler(long rngSeed){
        statuses = EnumSet.noneOf(StatusAilment.class);
        elementalOffense = EnumSet.noneOf(Element.class);
        elementalResistances = EnumSet.noneOf(Element.class);
        elementalWeaknesses = EnumSet.noneOf(Element.class);
        rng = new Random(rngSeed);
    }
    
    public Battler(int hp, int damage, int hitChance, int evadeChance, int magicDefense){
        this.hp = maxHp = hp&0xffff;
        this.damage = damage&0xff;
        this.hitChance = hitChance&0xff;
        this.evadeChance = evadeChance&0xff;
        this.magicDefense = magicDefense&0xff;
        
        statuses = EnumSet.noneOf(StatusAilment.class);
        elementalOffense = EnumSet.noneOf(Element.class);
        elementalResistances = EnumSet.noneOf(Element.class);
        elementalWeaknesses = EnumSet.noneOf(Element.class);
        rng = new Random();
        
        name = "null";
        absorb = 0;
    }

    /*
     * Status ailment management
     */

    public boolean hasStatus(StatusAilment status){
        return statuses.contains(status);
    }

    public boolean canReceiveStatus(StatusAilment status){
        return isAlive();
    }
    
    public boolean isAlive(){
        return (!hasStatus(StatusAilment.DEAD) && !hasStatus(StatusAilment.PETRIFIED));
    }

    public void addStatus(StatusAilment status){
        if(!canReceiveStatus(status)) return;
        if(status == StatusAilment.DEAD) hp = 0;
        statuses.add(status);
    }

    public void removeStatus(StatusAilment status){
        if(status == StatusAilment.DEAD) hp = 1;
        statuses.remove(status);
    } 
    
    /**
     * Removes temporary battle effects
     */
    public abstract void battleEnd();


    public void increaseHitMultiplier(){
        hitMultiplier++;
    }

    public void decreaseHitMultiplier(){
        hitMultiplier = Math.max(hitMultiplier-1, 0);
    }
    
    public void resetHitMultiplier(){
        hitMultiplier = 1;
    }

    public abstract int getBaseHitsPerTurn();
    
    public int getHitsPerTurn(){
        return getBaseHitsPerTurn()*hitMultiplier;
    }
    
    /*
     * Getters and setters
     */

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public EnumSet<StatusAilment> getStatuses(){
        return EnumSet.copyOf(statuses);
    }

    public void setElementalOffense(EnumSet<Element> elementalOffense){
        this.elementalOffense = EnumSet.copyOf(elementalOffense);
    }

    public EnumSet<Element> getElementalOffense(){
        return EnumSet.copyOf(elementalOffense);
    }

    public void setElementalResistances(EnumSet<Element> elementalResistances){
        this.elementalResistances = EnumSet.copyOf(elementalResistances);
    }

    public EnumSet<Element> getElementalResistances(){
        return EnumSet.copyOf(elementalResistances);
    }

    public void setElementalWeaknesses(EnumSet<Element> elementalWeaknesses){
        this.elementalWeaknesses = EnumSet.copyOf(elementalWeaknesses);
    }

    public EnumSet<Element> getElementalWeaknesses(){
        return EnumSet.copyOf(elementalWeaknesses);
    }
    
    public int getMaxHp(){
        return maxHp;
    }

    public void setMaxHp(int maxHp){
        this.maxHp = maxHp&0xffff;
        setHp(hp);
    }

    public int getHp(){
        return hp;
    }

    public void setHp(int hp){
        if(
            hasStatus(StatusAilment.DEAD) ||
            hasStatus(StatusAilment.PETRIFIED)
        ) return;
        if(hp <= 0){
            addStatus(StatusAilment.DEAD);
            return;
        }
        this.hp = Math.min(hp&0xffff, maxHp);
    }

    public void offsetHp(int offset){
        setHp(hp + offset);
    }

    public int getDamage(){
        return damage;
    }

    public void setDamage(int damage){
        this.damage = damage&0xff;
    }

    public int getAbsorb(){
        return absorb;
    }

    public void setAbsorb(int absorb){
        this.absorb = absorb&0xff;
    }

    public int getEvadeChance(){
        return evadeChance;
    }

    public void setEvadeChance(int evadeChance){
        this.evadeChance = evadeChance&0xff;
    }

    public int getHitChance(){
        return hitChance;
    }

    public void setHitChance(int hitChance){
        this.hitChance = hitChance&0xff;
    }

    public int getHitMultiplier(){
        return hitMultiplier;
    }
    
    public int getMagicDefense(){
        return magicDefense;
    }

    public void setMagicDefense(int magicDefense){
        this.magicDefense = magicDefense&0xff;
    }

    public int getCritChance() {
        return critChance;
    }

    public void setCritChance(int critChance) {
        this.critChance = critChance&0xff;
    }
}
