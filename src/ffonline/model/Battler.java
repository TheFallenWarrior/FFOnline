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
    private EnumSet<StatusAilment> statuses;
    private EnumSet<Element> attackElements;
    private EnumSet<Element> elementalResistances;
    private EnumSet<Element> elementalWeaknesses;
    
    protected Random rng;

    /*
     * Battle stats
     */
    private int hp;
    private int damage; // Attack
    private int absorb; // Defense
    private int evadeChance; // Evasion
    private int hitChance;   // Accuracy
    private int magicAbsorb;

    private int hitMultiplier = 1; // Affects number of hit per turn

    public Battler(){
        statuses = EnumSet.noneOf(StatusAilment.class);
        rng = new Random();
    }
    
    public Battler(long rngSeed){
        statuses = EnumSet.noneOf(StatusAilment.class);
        rng = new Random(rngSeed);
    }

    /*
     * Status ailment management
     */

    public boolean hasStatus(StatusAilment status){
        return statuses.contains(status);
    }

    public void addStatus(StatusAilment status){
        if(
            (status == StatusAilment.POISONED && this instanceof Enemy) ||
            (status == StatusAilment.CONFUSED && this instanceof PlayerCharacter) ||
            hasStatus(StatusAilment.DEAD) || hasStatus(StatusAilment.PETRIFIED)
        ) return;
        if(status == StatusAilment.DEAD) hp = 0;
        statuses.add(status);
    }

    public void removeStatus(StatusAilment status){
        if(status == StatusAilment.DEAD) hp = 1;
        statuses.remove(status);
    }

    /*
     * Battle methods
     */

    public AttackResult attack(Battler target){
        int totalDamage = 0, successfulHits = 0;
        boolean isCritical = false;

        for(int i=0;i<getHitsPerTurn();i++){
            // Calculate hit/miss
            int baseHitChance = 168;
            if(hasStatus(StatusAilment.BLIND))        baseHitChance -= 40;
            if(target.hasStatus(StatusAilment.BLIND)) baseHitChance += 40;

            int finalHitChance;
            if(target.hasStatus(StatusAilment.ASLEEP) || target.hasStatus(StatusAilment.PARALYZED))
                finalHitChance = baseHitChance;
            else finalHitChance = Math.min(baseHitChance + hitChance, 255) - target.getEvadeChance();

            // A hit roll of 200 is an automatic miss; 0 is an automatic hit
            int hitRoll = rng.nextInt(0, 201);
            if((hitRoll > finalHitChance && hitRoll != 0) || hitRoll == 200) continue; // Attack missed

            // Calculate damage
            // Due to a bug in the original game, elements and enemy types have no
            // effect in damage output. 
            int baseDamage = damage + rng.nextInt(0, 1+damage);
            if(rollCrit()){
                baseDamage *= 2;
                isCritical = true;
            }
            if(target.hasStatus(StatusAilment.ASLEEP) || target.hasStatus(StatusAilment.PARALYZED))
                baseDamage = (baseDamage*5) / 4;

            int attackDamage = Math.max(baseDamage - target.absorb, 1);

            totalDamage += attackDamage;
            successfulHits++;

            target.offsetHp(-attackDamage);
        }
        
        return new AttackResult(totalDamage, successfulHits, isCritical);
    }

    public abstract boolean rollCrit();

    /**
     * Removes temporary battle effects; on enemies, it immediately kills them.
     */
    public abstract void battleEnd();


    public void increaseHitMultiplier(){
        hitMultiplier++;
    }

    public void decreaseHitMultiplier(){
        hitMultiplier = Math.max(hitMultiplier-1, 0);
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
        return statuses.clone();
    }

    public void setAttackElements(EnumSet<Element> attackElements){
        this.attackElements = attackElements;
    }

    public EnumSet<Element> getAttackElements(){
        return attackElements.clone();
    }

    public void setElementalResistances(EnumSet<Element> elementalResistances){
        this.elementalResistances = EnumSet.copyOf(elementalResistances);
    }

    public EnumSet<Element> getElementalResistances(){
        return elementalResistances.clone();
    }

    public void setElementalWeaknesses(EnumSet<Element> elementalWeaknesses){
        this.elementalWeaknesses = EnumSet.copyOf(elementalWeaknesses);
    }

    public EnumSet<Element> getElementalWeaknesses(){
        return elementalWeaknesses.clone();
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
        this.hp = hp;
    }

    public void offsetHp(int offset){
        setHp(hp + offset);
    }

    public int getDamage(){
        return damage;
    }

    public void setDamage(int damage) throws IllegalArgumentException{
        if(damage < 0 || damage > 255)
            throw new IllegalArgumentException("DAMAGE must be in range 0..255; got"+ damage +".");
        this.damage = damage;
    }

    public int getAbsorb(){
        return absorb;
    }

    public void setAbsorb(int absorb) throws IllegalArgumentException{
        if(absorb < 0 || absorb > 255)
            throw new IllegalArgumentException("ABSORB must be in range 0..255; got"+ absorb +".");
        this.absorb = absorb;
    }

    public int getEvadeChance(){
        return evadeChance;
    }

    public void setEvadeChance(int evadeChance) throws IllegalArgumentException{
        if(evadeChance < 0 || evadeChance > 255)
            throw new IllegalArgumentException("EVADE% must be in range 0..255; got"+ evadeChance +".");
        this.evadeChance = evadeChance;
    }

    public int getHitChance(){
        return hitChance;
    }

    public void setHitChance(int hitChance) throws IllegalArgumentException{
        if(hitChance < 0 || hitChance > 255)
            throw new IllegalArgumentException("HIT% must be in range 0..255; got"+ hitChance +".");
        this.hitChance = hitChance;
    }

    public int getHitMultiplier(){
        return hitMultiplier;
    }

    public abstract int getHitsPerTurn();
    
    public int getMagicAbsorb(){
        return magicAbsorb;
    }

    public void setMagicAbsorb(int magicAbsorb) throws IllegalArgumentException{
        if(magicAbsorb < 0 || magicAbsorb > 255)
            throw new IllegalArgumentException("MagABSORB must be in range 0..255; got"+ magicAbsorb +".");
        this.magicAbsorb = magicAbsorb;
    }
}
