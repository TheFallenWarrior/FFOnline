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
 * Represents a group of battlers, with utility methods for management
 *  and manipulation of the members within the group during gameplay.
 * @author thefa
 * @param <T> The type of the battlers that make up the BattlerGroup
 */
public class BattlerGroup<T extends Battler> implements Iterable<T> {
    private final List<T> members = new ArrayList<>();
    protected Random rng;

    /**
     * Constructs an empty {@code BattlerGroup} with a new random number generator.
     */
    public BattlerGroup(){
        this.rng = new Random();
    }

    /**
     * Constructs an empty {@code BattlerGroup} initialized with a specific random seed.
     * @param rngSeed the long value used to seed the internal random number generator
     */
    public BattlerGroup(long rngSeed){
        this.rng = new Random(rngSeed);
    }

    /**
     * Constructs a {@code BattlerGroup} using an existing array of initial members.
     * @param members the array of battlers to include in the group
     */
    public BattlerGroup(T[] members){
        this.members.addAll(Arrays.asList(members));
        this.rng = new Random();
    }

    /**
     * Constructs a {@code BattlerGroup} using an existing {@link List} of initial members.
     * @param members the array list of battlers to include in the group
     */
    public BattlerGroup(List<T> members){
        this.members.addAll(members);
        this.rng = new Random();
    }

    /**
     * Adds a single battler to the group.
     * @param element The battler to add
     * @return {@code true} if the element was added, {@code false} otherwise
     */
    public boolean add(T element){
        return members.add(element);
    }

    /**
     * Adds all specified battlers from a collection to the group.
     * @param elements The collection of battlers to add
     * @return {@code true} if any elements were added, {@code false} otherwise
     */
    public boolean addAll(Collection<? extends T> elements){
        return members.addAll(elements);
    }

    /**
     * Returns the total number of members in the group (including defeated ones).
     * @return The size of the battler group
     */
    public int size(){
        return members.size();
    }

    /**
     * Checks if the group contains any members.
     * @return {@code true} if the group is empty, {@code false} otherwise
     */
    public boolean isEmpty(){
        return members.isEmpty();
    }

    /**
     * Gets the battler at a specific index in the group.
     * @param index The zero-based index of the battler
     * @return The battler located at the specified index
     */
    public T get(int index){
        return members.get(index);
    }

    /**
     * Returns an unmodifiable view of all members in the group.
     * @return An unmodifiable {@link List} containing all battlers
     */
    public List<T> asUnmodifiableList(){
        return Collections.unmodifiableList(members);
    }

    /**
     * Returns an iterator over all members in the group.
     * @return an iterator for the contained battlers
     */
    @Override
    public Iterator<T> iterator(){
        return members.iterator();
    }

    /**
     * Retrieves an {@link Optional} containing the battler at a specific index,
     * or empty if the index is out of bounds.
     * @param index The zero-based index of the desired battler
     * @return an {@code Optional<T>} containing the battler, or {@code Optional.empty()} if the index is invalid
     */
    public Optional<T> getOptional(int index){
        if(index < 0 || index >= size()) return Optional.empty();
        return Optional.of(members.get(index));
    }

    /**
     * Calculates and returns the count of members who are currently alive.
     * @return The number of living battlers in the group
     */
    public int getAliveSize(){
        int alive = 0;
        for(var member : members){
            if(member.isAlive()) alive++;
        }
        return alive;
    }

    /**
     * Returns an unmodifiable list containing only the members who are currently alive.
     * @return An unmodifiable {@link List} of all living battlers
     */
    public List<T> getAliveMembers(){
        List<T> alive = new ArrayList<>();
        for(var member : members){
            if(member.isAlive()){
                alive.add(member);
            }
        }
        return Collections.unmodifiableList(alive);
    }

    /**
     * Retrieves a random battler from the group.
     * @return an {@code Optional<T>} containing a randomly selected battler, or {@code Optional.empty()} if the group is empty
     */
    public Optional<T> getRandom(){
        if(members.isEmpty()) return Optional.empty();

        int index = rng.nextInt(0, size());
        return Optional.of(members.get(index));
    }

    /**
     * Retrieves a random battler from the set of currently living members.
     * @return an {@code Optional<T>} containing a randomly selected alive battler, or {@code Optional.empty()} if no alive battlers exist
     */
    public Optional<T> getRandomAlive(){
        List<T> alive = getAliveMembers();
        if(alive.isEmpty()) return Optional.empty();

        int index = rng.nextInt(0, alive.size());
        return Optional.of(alive.get(index));
    }
    
    /**
     * Swaps the positions of two battlers int the group.
     * @param indexA the position of the first battler
     * @param indexB the position of the second battler
     */
    public void swap(int indexA, int indexB){
        T tmp = members.get(indexA);
        members.set(indexA, members.get(indexB));
        members.set(indexB, tmp);
    }
}