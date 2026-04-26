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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 *
 * @author thefa
 * @param <T> The type of the battlers that make up the BattlerGroup
 */
public class BattlerGroup<T extends Battler> implements Iterable<T> {
    private final List<T> members = new ArrayList<>();
    protected Random rng;

    public BattlerGroup(){
        this.rng = new Random();
    }

    public BattlerGroup(long rngSeed){
        this.rng = new Random(rngSeed);
    }

    public BattlerGroup(T[] members){
        this.members.addAll(Arrays.asList(members));
        this.rng = new Random();
    }

    public BattlerGroup(List<T> members){
        this.members.addAll(members);
        this.rng = new Random();
    }

    public boolean add(T element){
        return members.add(element);
    }

    public boolean addAll(Collection<? extends T> elements){
        return members.addAll(elements);
    }

    public int size(){
        return members.size();
    }

    public boolean isEmpty(){
        return members.isEmpty();
    }

    public T get(int index){
        return members.get(index);
    }

    public List<T> asUnmodifiableList(){
        return Collections.unmodifiableList(members);
    }

    @Override
    public Iterator<T> iterator(){
        return members.iterator();
    }

    public Optional<T> getOptional(int index){
        if(index < 0 || index >= size()) return Optional.empty();
        return Optional.of(members.get(index));
    }

    public int getAliveSize(){
        int alive = 0;
        for(var member : members){
            if(member.isAlive()) alive++;
        }
        return alive;
    }

    public List<T> getAliveMembers(){
        List<T> alive = new ArrayList<>();
        for(var member : members){
            if(member.isAlive()){
                alive.add(member);
            }
        }
        return Collections.unmodifiableList(alive);
    }

    public Optional<T> getRandom(){
        if(members.isEmpty()) return Optional.empty();

        int index = rng.nextInt(0, size());
        return Optional.of(members.get(index));
    }

    public Optional<T> getRandomAlive(){
        List<T> alive = getAliveMembers();
        if(alive.isEmpty()) return Optional.empty();

        int index = rng.nextInt(0, alive.size());
        return Optional.of(alive.get(index));
    }
}