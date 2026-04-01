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
import ffonline.model.StatusAilment;

/**
 *
 * @author thefa
 */
public class FightCommand extends Command {
    public FightCommand(Battler actor, Battler target){
        super(null, null, actor, null, target, CommandTarget.SINGLE_ENEMY);
    }
    
    @Override
    public CommandResult execute(){
        int totalDamage = 0, successfulHits = 0;
        boolean isCritical = false;

        for(int i=0;i<actor.getHitsPerTurn();i++){
            // Calculate hit/miss
            int baseHitChance = 168;
            if(actor.hasStatus(StatusAilment.BLIND))       baseHitChance -= 40;
            if(enemyTarget.hasStatus(StatusAilment.BLIND)) baseHitChance += 40;

            int finalHitChance;
            if(enemyTarget.hasStatus(StatusAilment.ASLEEP) || enemyTarget.hasStatus(StatusAilment.PARALYZED))
                finalHitChance = baseHitChance;
            else finalHitChance = Math.min(baseHitChance + actor.getHitChance(), 255) - enemyTarget.getEvadeChance();

            // A hit roll of 200 is an automatic miss; 0 is an automatic hit
            int hitRoll = rng.nextInt(0, 201);
            if((hitRoll > finalHitChance && hitRoll != 0) || hitRoll == 200) continue; // Attack missed

            // Calculate damage
            // INTENTIONAL: Due to a bug in the original game, elements and enemy types have no
            // effect in damage output. 
            int baseDamage = actor.getDamage() + rng.nextInt(0, 1+actor.getDamage());
            if(enemyTarget.hasStatus(StatusAilment.ASLEEP) || enemyTarget.hasStatus(StatusAilment.PARALYZED))
                baseDamage = (baseDamage*5) / 4;

            int attackDamage = Math.max(baseDamage - enemyTarget.getAbsorb(), 1);
            
            if(hitRoll <= actor.getCritChance()){
                attackDamage += baseDamage;
                isCritical = true;
            }

            totalDamage += attackDamage;
            successfulHits++;

            enemyTarget.offsetHp(-attackDamage);
        }
        
        return new CommandResult(enemyTarget, totalDamage, successfulHits, isCritical);
    }
}
