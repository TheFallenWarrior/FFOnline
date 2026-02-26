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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 *
 * @author user
 * @param <T> The type of the battlers that make up the BattlerGroup
 */
public class BattlerGroup<T extends Battler> {
    private final ArrayList<T> members = new ArrayList<>();
    
    public BattlerGroup(){}
    
    public BattlerGroup(T[] members){
        this.members.addAll(Arrays.asList(members));
    }
    
    public BattlerGroup(ArrayList<T> members){
        this.members.addAll(members);
    }
    
    public void addMember(T member){
        members.add(member);
    }
    
    public Optional<T> getMember(int index){
        if(index < 0 || index >= members.size()) return Optional.empty();
        return Optional.of(members.get(index));
    }
    
    public ArrayList<T> getMembers(){
        return (ArrayList<T>)members.clone();
    }
    
    public int getSize(){
        return members.size();
    }
    
    public int getAliveSize(){
        int alive = 0;
        for(var member : members){
            if(member.hasStatus(StatusAilment.DEAD)) alive++;
        }
        return alive;
    }
    
    public ArrayList<T> getAliveMembers(){
        ArrayList<T> alive = new ArrayList<>();
        for(var member : members){
            if(member.hasStatus(StatusAilment.DEAD))
                alive.add(member);
        }
        return alive;
    }
}