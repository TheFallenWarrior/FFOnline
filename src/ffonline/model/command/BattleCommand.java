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
import java.util.Random;

/**
 * Abstract base class for all battle commands, it encapsulates all necessary context
 *  and execution mechanism to perform the action.
 * @author Anna Jaqueline (TheFallenWarrior)
 */
public abstract class BattleCommand {
    // NOTE: These fields should be references to the respective objects
    protected final BattlerGroup<? extends Battler> allies;
    protected final BattlerGroup<? extends Battler> enemies;
    protected final Battler actor; // Entity performing the command
    protected final Battler allyTarget;
    protected final Battler enemyTarget;
    protected final CommandTarget targeting;
    protected final Random rng;
    
    /**
     * Constructs a {@code BattleCommand} object.
     * @param allies The {@code BattlerGroup} that contains {@code actor}
     * @param enemies The {@code BattlerGroup} {@code actor} is fighting against
     * @param actor The {@code Battler} performing the battle command
     * @param allyTarget An optional target within the allied group; can be null
     * @param enemyTarget An optional target within the enemy group; can be null
     * @param targeting The targeting mode of the command
     */
    public BattleCommand(
        BattlerGroup<? extends Battler> allies,
        BattlerGroup<? extends Battler> enemies,
        Battler actor,
        Battler allyTarget,
        Battler enemyTarget,
        CommandTarget targeting
    ){
        this.allies = allies;
        this.enemies = enemies;
        this.actor = actor;
        this.allyTarget = allyTarget;
        this.enemyTarget = enemyTarget;
        this.targeting = targeting;
        rng = new Random();
    }
    
    /**
     * Executes the battle command and applies the effects on the target(s).
     * @return A {@link CommandResult} with the outcomes of this command
     */
    public abstract CommandResult execute();
    
    /**
     * @return The {@code BattlerGroup} that contains the command's actor
     */
    public BattlerGroup<? extends Battler> getAllies(){
        return allies;
    }

    /**
     * @return The {@code BattlerGroup} command's actor is fighting against
     */
    public BattlerGroup<? extends Battler> getEnemies(){
        return enemies;
    }

    /**
     * @return The {@code Battler} performing the battle command
     */
    public Battler getActor(){
        return actor;
    }

    /**
     * @return The allied {@code Battler} targeted by this command
     */
    public Battler getAllyTarget(){
        return allyTarget;
    }

    /**
     * @return The enemy {@code Battler} targeted by this command
     */
    public Battler getEnemyTarget(){
        return enemyTarget;
    }
    
    /**
     * @return The targeting mode of the command
     */
    public CommandTarget getTargeting(){
        return targeting;
    }
}
