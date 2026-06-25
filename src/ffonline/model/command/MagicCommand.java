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

package ffonline.model.command;

import ffonline.model.Battler;
import ffonline.model.BattlerGroup;
import ffonline.model.Element;
import ffonline.model.Enemy;
import ffonline.model.EnemyType;
import ffonline.model.Magic;
import ffonline.model.PlayerCharacter;
import ffonline.model.StatusAilment;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 * @author Anna Jaqueline (TheFallenWarrior)
 */
public class MagicCommand extends BattleCommand {
    private final Magic spell;
    private final CommandResult.Builder builder = CommandResult.builder(this);
    private final Map<Magic.Effect, Consumer<Battler>> effectHandlers = new EnumMap(Magic.Effect.class);
    
    public MagicCommand(
        BattlerGroup<? extends Battler> allies,
        BattlerGroup<? extends Battler> enemies,
        Battler actor,
        Battler allyTarget,
        Battler enemyTarget,
        Magic spell
    ){
        super(allies, enemies, actor, allyTarget, enemyTarget, spell.getTargeting());
        this.spell = spell;
        registerEffectHandlers();
    }
    
    private void registerEffectHandlers(){
        effectHandlers.put(Magic.Effect.NOTHING, ((t) -> {
            builder.ineffective(t);
        }));
        effectHandlers.put(Magic.Effect.DAMAGE, this::applyDamage);
        effectHandlers.put(Magic.Effect.HARM, this::applyHarm);
        effectHandlers.put(Magic.Effect.HIT_MULTIPLIER_DOWN, this::applyHitMultiplierDown);
        effectHandlers.put(Magic.Effect.MORALE_DOWN, this::applyMoraleDown);
        effectHandlers.put(Magic.Effect.UNUSED, this::applyHpRecovery); 
        effectHandlers.put(Magic.Effect.HP_RECOVERY, this::applyHpRecovery);
        effectHandlers.put(Magic.Effect.STATUS_RECOVERY, this::applyStatusRecovery);
        effectHandlers.put(Magic.Effect.DEFENSE_UP, this::applyDefenseUp);
        effectHandlers.put(Magic.Effect.RESIST_ELEMENT, this::applyResistElement);
        effectHandlers.put(Magic.Effect.ATTACK_UP, this::applyAttackUp);
        effectHandlers.put(Magic.Effect.HIT_MULTIPLIER_UP, this::applyHitMultiplierUp);
        effectHandlers.put(Magic.Effect.ATTACK_ACCURACY_UP, this::applyAttackAccuracyUp);
        effectHandlers.put(Magic.Effect.EVASION_DOWN, ((t) -> {
            // INTENTIONAL: Evasion down always misses
            builder.fail(t);
        }));
        effectHandlers.put(Magic.Effect.FULL_RECOVERY, this::applyFullRecovery);
        effectHandlers.put(Magic.Effect.EVASION_UP, this::applyEvasionUp);
        effectHandlers.put(Magic.Effect.UNRESIST_ELEMENT, this::applyUnresistElement);
        effectHandlers.put(Magic.Effect.HP300_STATUS, this::applyHp300Status);
    }
    
    /**
     * @return {@code true} if a and b have any elements in common, false otherwise
     */
    private <E extends Enum<E>> boolean enumSetContainsAny(EnumSet<E> a, EnumSet<E> b){
        for(var i : b){
            if(a.contains(i)) return true;
        }
        return false;
    }
    
    private int calculateDamage(){
        return calculateDamage(spell.getEffectivity());
    }
    
    private int calculateDamage(int effectivity){
        return effectivity + rng.nextInt(0, 1+effectivity);
    }
    
    private boolean calculateHit(Battler target){
        int baseHitChance = 148;
        
        // INTENTIONAL: It is possible for a battler to be weak and resistant to the same element
        if(enumSetContainsAny(target.getElementalResistances(), spell.getElements()))
            baseHitChance = 0;
        if(enumSetContainsAny(target.getElementalWeaknesses(), spell.getElements()))
            baseHitChance += 40;
        
        int finalHitChance = baseHitChance + spell.getAccuracy() - target.getMagicDefense();
        
        // A hit roll of 200 is an automatic miss; 0 is an automatic hit
        int hitRoll = rng.nextInt(0, 201);
        return !((hitRoll > finalHitChance && hitRoll != 0) || hitRoll == 200);
    }
    
    private void apply(Battler target){
        Consumer<Battler> handler = effectHandlers.get(spell.getEffect());
        handler.accept(target);    
    }
    
    @Override
    public CommandResult execute(){
        switch(targeting){
            case ALL_ENEMIES -> {
                for(Battler enemy : enemies){
                    apply(enemy);
                }
            }
            
            case SINGLE_ENEMY -> {
                apply(enemyTarget);
            }
            
            case ACTOR -> {
                apply(actor);
            }
            
            case ALL_ALLIES -> {
                for(Battler ally : allies){
                    // INTENTIONAL: Enemies' all-allies spells always miss the caster
                    if(actor instanceof Enemy && actor == ally)
                        builder.fail(ally);
                    else apply(ally);
                }
            }
            
            case SINGLE_ALLY -> {
                apply(allyTarget);
            }
        }
        
        return builder.build();
    }
    
    private void applyDamage(Battler target){
        int effectivity = spell.getEffectivity();
                
        if(enumSetContainsAny(target.getElementalResistances(), spell.getElements()))
            effectivity -= effectivity/2;
        if(enumSetContainsAny(target.getElementalWeaknesses(), spell.getElements()))
            effectivity += effectivity/2;

        int damage = calculateDamage(effectivity);
        if(calculateHit(target)) damage *= 2;

        target.offsetHp(-damage);
        builder.hit(target, damage);
    }
    
    private void applyHarm(Battler target){
        if(!(target instanceof Enemy enemy) || !enemy.getEnemyTypes().contains(EnemyType.UNDEAD)){
            builder.ineffective(target);
            return;
        }

        int damage = calculateDamage();
        if(calculateHit(target)) damage *= 2;

        target.offsetHp(-damage);
        builder.hit(target, damage);
    }
    
    private void applyHitMultiplierDown(Battler target){
        if(calculateHit(target)){
            target.increaseHitMultiplier();
            builder.succeed(target);
        } else
            builder.fail(target);
    }
    
    private void applyMoraleDown(Battler target){
        if(target instanceof Enemy enemy){
            enemy.offsetMorale(-spell.getEffectivity());
        } else
            builder.ineffective(target);
    }
    
    private void applyHpRecovery(Battler target){
        int recovery = calculateDamage();
        target.offsetHp(recovery);
        builder.hit(target, recovery);
    }
    
    private void applyStatusRecovery(Battler target){
        EnumSet<StatusAilment> toRemove = spell.getEffectStatuses();
        toRemove.removeAll(EnumSet.of(StatusAilment.DEAD, StatusAilment.PETRIFIED));
        target.removeAllStatuses(toRemove);

        if(target.isAlive())
            builder.succeed(target);
        else
            builder.ineffective(target);
    }
    
    private void applyDefenseUp(Battler target){
        int absorb = target.getAbsorb() + spell.getEffectivity();
        target.setAbsorb(absorb);
        builder.succeed(target);
    }
    
    private void applyResistElement(Battler target){
        EnumSet<Element> resistances = target.getElementalResistances();
        resistances.addAll(spell.getEffectElements());
        target.setElementalResistances(resistances);

        builder.succeed(target);
    }
    
    private void applyAttackUp(Battler target){
        // INTENTIONAL: "Attack up" spells do work on player characters
        if(target instanceof PlayerCharacter pc){
            builder.ineffective(pc);
            return;
        }
        @SuppressWarnings("null")
        int damage = target.getDamage() + spell.getEffectivity();
        target.setDamage(damage);
        builder.succeed(target);
    }
    
    private void applyHitMultiplierUp(Battler target){
        target.increaseHitMultiplier();
        builder.succeed(target);
    }
    
    private void applyAttackAccuracyUp(Battler target){
        // INTENTIONAL: "Attack/accuracy up" spells do not work on player characters
        if(target instanceof PlayerCharacter pc){
            builder.ineffective(pc);
            return;
        }
        @SuppressWarnings("null")
        int damage = target.getDamage() + spell.getEffectivity();
        target.setDamage(damage);
        // INTENTIONAL: The spell's accuracy stat is added to the target's accuracy
        int hitChance = target.getHitChance() + spell.getAccuracy();
        target.setHitChance(hitChance);
        builder.succeed(target);
    }
    
    private void applyFullRecovery(Battler target){
        target.setHp(target.getMaxHp());
                
        // Remove all statuses except death and stone
        EnumSet<StatusAilment> toRemove = EnumSet.allOf(StatusAilment.class);
        toRemove.removeAll(EnumSet.of(StatusAilment.DEAD, StatusAilment.PETRIFIED));
        target.removeAllStatuses(toRemove);

        builder.succeed(target);
    }
    
    private void applyEvasionUp(Battler target){
        int evadeChance = target.getEvadeChance() + spell.getEffectivity();
        target.setEvadeChance(evadeChance);
        builder.succeed(target);
    }
    
    private void applyUnresistElement(Battler target){
        // INTENTIONAL: "Remove resistance" spell effect does not work on enemies
        if(target instanceof PlayerCharacter character && calculateHit(character)){
            character.setElementalResistances(EnumSet.noneOf(Element.class));
            builder.succeed(target);
        } else
            builder.fail(target);
    }
    
    private void applyHp300Status(Battler target){
        if(
            target.getHp() < 300 &&
            !enumSetContainsAny(target.getElementalResistances(), spell.getEffectElements())
        ){
            target.addAllStatuses(spell.getEffectStatuses());
            builder.succeed(target);
        } else
            builder.fail(target);
    }
}
