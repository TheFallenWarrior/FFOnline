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
 *
 * @author Anna Jaqueline (TheFallenWarrior)
 */
public class CommandResult {
    private final List<IndividualCommandResult> resultList = new ArrayList<>();
    private final BattleCommand command;
    
    public CommandResult(BattleCommand command, List<IndividualCommandResult> resultList){
        this.resultList.addAll(resultList);
        this.command = command;
    }
    
    public static Builder builder(BattleCommand command){
        return new Builder(command);
    }
    
    public List<IndividualCommandResult> getResultList(){
        return Collections.unmodifiableList(resultList);
    }
    
    public BattleCommand getCommand(){
        return command;
    }
    
    public static final class Builder {
        private final BattleCommand command;
        private final List<IndividualCommandResult> results = new ArrayList<>();
        
        private Builder(BattleCommand command){
            this.command = command;
        }
        
        public Builder hit(Battler target, int damage){
            return multiHit(target, damage, 1);
        }
        
        public Builder multiHit(Battler target, int totalDamage, int numHits){
            Type type = (numHits > 0 ? Type.HIT : Type.MISS);
            results.add(new IndividualCommandResult(target, type, numHits, totalDamage));
            return this;
        }
        
        public Builder critical(Battler target, int totalDamage, int numHits){
            Type type = (numHits > 0 ? Type.CRITICAL : Type.MISS);
            results.add(new IndividualCommandResult(target, type, numHits, totalDamage));
            return this;
        }
        
        public Builder ineffective(Battler target){
            results.add(new IndividualCommandResult(target, CommandResult.Type.INEFFECTIVE, 0));
            return this;
        }
        
        public Builder succeed(Battler target){
            results.add(new IndividualCommandResult(target, true));
            return this;
        }
        
        public Builder fail(Battler target){
            results.add(new IndividualCommandResult(target, false));
            return this;
        }
        
        public Builder bool(Battler target, boolean success){
            results.add(new IndividualCommandResult(target, success));
            return this;
        }
        
        public CommandResult build(){
            return new CommandResult(command, results);
        }
    }

    public static class IndividualCommandResult {
        private final Battler target;
        private final Type type;
        private final int numHits;
        private final int totalDamage;
        
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
        
        public IndividualCommandResult(Battler target, Type type, int totalDamage){
            this.target = target;
            this.type = type;
            this.numHits = 1;
            this.totalDamage = totalDamage;
        }
        
        public IndividualCommandResult(Battler target, boolean isHit){
            this.target = target;
            this.type = (isHit ? Type.HIT : Type.MISS);
            this.numHits = 0;
            this.totalDamage = 0;
        }

        public Battler getTarget(){
            return target;
        }

        public Type getType(){
            return type;
        }

        public int getNumHits(){
            return numHits;
        }

        public int getTotalDamage(){
            return totalDamage;
        }
    }
    
    public enum Type{ 
        HIT,
        MISS,
        INEFFECTIVE,
        CRITICAL
    }
}
