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

/**
 * Base class for battle commands
 * @author thefa
 */
public abstract class Command {
    // NOTE: These fields should be references to the respective objects
    protected final BattlerGroup allies;
    protected final BattlerGroup enemies;
    protected final Battler actor; // Entity performing the command
    protected final Battler allyTarget;
    protected final Battler enemyTarget;
    protected final CommandTarget targeting;
    
    public Command(
        BattlerGroup allies,
        BattlerGroup enemies,
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
    }
    
    public abstract void execute();
    
    public BattlerGroup getAllies(){
        return allies;
    }

    public BattlerGroup getEnemies(){
        return enemies;
    }

    public Battler getActor(){
        return actor;
    }

    public Battler getAllyTarget(){
        return allyTarget;
    }

    public Battler getEnemyTarget(){
        return enemyTarget;
    }
    
    public CommandTarget getTargeting(){
        return targeting;
    }
}
