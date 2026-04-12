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
 * @author thefa
 */
public class CommandResult {
    private final List<IndividualCommandResult> resultList = new ArrayList<>();
    
    public CommandResult(List<IndividualCommandResult> resultList){
        this.resultList.addAll(resultList);
    }
    
    public CommandResult(Battler target, int totalDamage, int successfulHits, boolean isCritical){
        IndividualCommandResult result = new IndividualCommandResult(
                target,
                (isCritical ? CommandResultType.CRITICAL : CommandResultType.HIT),
                successfulHits,
                totalDamage
        );
        
        this.resultList.add(result);
    }
    
    public CommandResult(Battler target, boolean isHit){
        IndividualCommandResult result = new IndividualCommandResult(target, isHit);
        
        this.resultList.add(result);
    }
    
    public List<IndividualCommandResult> getResultList(){
        return Collections.unmodifiableList(resultList);
    }

    public static class IndividualCommandResult {
        public final Battler target;
        public final CommandResultType type;
        public final int numHits;
        public final int totalDamage;
        
        public IndividualCommandResult(
            Battler target,
            CommandResultType type,
            int numHits,
            int totalDamage
        ){
            this.target = target;
            this.type = (numHits == 0 ? CommandResultType.MISS : type);
            this.numHits = numHits;
            this.totalDamage = totalDamage;
        }
        
        public IndividualCommandResult(
            Battler target,
            CommandResultType type,
            int totalDamage
        ){
            this.target = target;
            this.type = type;
            this.numHits = 0;
            this.totalDamage = totalDamage;
        }
        
        public IndividualCommandResult(Battler target, boolean isHit){
            this.target = target;
            this.type = (isHit ? CommandResultType.HIT : CommandResultType.MISS);
            this.numHits = 0;
            this.totalDamage = 0;
        }
    }
}
