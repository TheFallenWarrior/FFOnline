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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the aggregated results of a battle command execution.
 * This class provides mechanisms to build and store multiple individual outcomes
 * associated with a single {@link BattleCommand}.
 * @author Anna Jaqueline (TheFallenWarrior)
 */
public class CommandResult {
    private final List<IndividualCommandResult> resultList = new ArrayList<>();
    private final BattleCommand command;
    
    /**
     * Constructs a CommandResult object.
     * @param command The battle command that was executed.
     * @param resultList A list of individual outcomes recorded during the execution.
     */
    public CommandResult(BattleCommand command, List<IndividualCommandResult> resultList){
        this.resultList.addAll(resultList);
        this.command = command;
    }
    
    /**
     * Creates a new builder instance for constructing a {@link CommandResult}.
     * @param command The battle command associated with these results.
     * @return A {@link Builder} initialized with the given command.
     */
    public static Builder builder(BattleCommand command){
        return new Builder(command);
    }
    
    /**
     * Gets an unmodifiable list of all individual command results.
     * @return An unmodifiable list containing all {@link IndividualCommandResult}s.
     */
    public List<IndividualCommandResult> getResultList(){
        return Collections.unmodifiableList(resultList);
    }
    
    /**
     * Returns the original battle command that generated these results.
     * @return The original {@link BattleCommand}
     */
    public BattleCommand getCommand(){
        return command;
    }
    
    /**
     * A builder class used to construct and populate a CommandResult object sequentially.
     */
    public static final class Builder {
        private final BattleCommand command;
        private final List<IndividualCommandResult> results = new ArrayList<>();
        
        private Builder(BattleCommand command){
            this.command = command;
        }
        
        /**
         * Records a single, successful, non-critical hit against {@code target}.
         * @param target The {@code Battler} who received the damage
         * @param damage The amount of damage dealt
         * @return The current {@code Builder} instance for chaining calls
         */
        public Builder hit(Battler target, int damage){
            return multiHit(target, damage, 1);
        }
        
        /**
         * Records a multi-hit attack against {@code target}. If numHits is 0, a miss is
         * recorded instead.
         * @param target The {@code Battler} who received the damage
         * @param totalDamage The amount of damage dealt
         * @param numHits The number of successful hits
         * @return The current {@code Builder} instance for chaining calls
         */
        public Builder multiHit(Battler target, int totalDamage, int numHits){
            Type type = (numHits > 0 ? Type.HIT : Type.MISS);
            results.add(new IndividualCommandResult(target, type, numHits, totalDamage));
            return this;
        }
        
        /**
         * Records a multi-hit attack against {@code target}, of which at least one hit was
         * critical. If numHits is 0, a miss is recorded instead.
         * @param target The {@code Battler} who received the damage
         * @param totalDamage The amount of damage dealt
         * @param numHits The number of successful hits
         * @return The current {@code Builder} instance for chaining calls
         */
        public Builder critical(Battler target, int totalDamage, int numHits){
            Type type = (numHits > 0 ? Type.CRITICAL : Type.MISS);
            results.add(new IndividualCommandResult(target, type, numHits, totalDamage));
            return this;
        }
        
        /**
         * Records the action as ineffective against {@code target}.
         * @param target The {@code Battler} involved in the outcome
         * @return The current {@code Builder} instance for chaining calls
         */
        public Builder ineffective(Battler target){
            results.add(new IndividualCommandResult(target, CommandResult.Type.INEFFECTIVE, 0, 0));
            return this;
        }
        
        /**
         * Records a successful outcome against {@code target} without hit count or damage information.
         * @param target The {@code Battler} involved in the outcome
         * @return The current {@code Builder} instance for chaining calls
         */
        public Builder succeed(Battler target){
            results.add(new IndividualCommandResult(target, true));
            return this;
        }
        
        /**
         * Records a failed outcome against {@code target}.
         * @param target The {@code Battler} involved in the outcome
         * @return The current {@code Builder} instance for chaining calls
         */
        public Builder fail(Battler target){
            results.add(new IndividualCommandResult(target, false));
            return this;
        }
        
        /**
         * Records a Boolean outcome (success or failure) against {@code target} without hit count or damage information.
         * @param target The {@code Battler} involved in the outcome
         * @param success {@code true} if action succeeded, {@code false} otherwise
         * @return The current {@code Builder} instance for chaining calls
         */
        public Builder bool(Battler target, boolean success){
            results.add(new IndividualCommandResult(target, success));
            return this;
        }
        
        /**
         * Builds and returns the immutable {@link CommandResult} object containing all recorded outcomes.
         * @return the final {@code CommandResult}
         */
        public CommandResult build(){
            return new CommandResult(command, results);
        }
    }

    /**
     * Represents a single outcome or effect of a command executed against one battler.
     */
    public static class IndividualCommandResult {
        private final Battler target;
        private final Type type;
        private final int numHits;
        private final int totalDamage;
        
        /**
         * Constructor used for damage-related outcomes.
         * @param target The battler who was affected by the command.
         * @param type The type of outcome (e.g., HIT, CRITICAL).
         * @param numHits The number of hits recorded in this specific result.
         * @param totalDamage The total damage applied or blocked.
         */
        public IndividualCommandResult(
            Battler target,
            Type type,
            int numHits,
            int totalDamage
        ){
            this.target = target;
            this.type = type;
            this.numHits = numHits;
            this.totalDamage = totalDamage;
        }
        
        /**
         * Convenience constructor for recording simple Boolean outcomes (success/failure).
         * @param target The battler associated with the result.
         * @param success {@code true} if action succeeded, {@code false} otherwise
         */
        public IndividualCommandResult(Battler target, boolean success){
            this.target = target;
            this.type = (success ? Type.HIT : Type.MISS);
            this.numHits = 0;
            this.totalDamage = 0;
        }

        /**
         * Gets the battler who was the target of this command result.
         * @return The targeted {@link Battler}.
         */
        public Battler getTarget(){
            return target;
        }

        /**
         * Gets the type of outcome.
         * @return The recorded {@link Type}.
         */
        public Type getType(){
            return type;
        }

        /**
         * Gets the number of hits associated with this result.
         * @return The count of hits.
         */
        public int getNumHits(){
            return numHits;
        }

        /**
         * Gets the total damage dealt or mitigated by this result.
         * @return The amount of damage.
         */
        public int getTotalDamage(){
            return totalDamage;
        }
    }
    
    /**
     * Defines the possible types of outcomes for a battle command.
     */
    public enum Type{ 
        HIT,
        MISS,
        INEFFECTIVE,
        CRITICAL
    }
}
