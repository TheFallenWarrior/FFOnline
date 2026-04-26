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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 *
 * @author thefa
 * @param <T> The type of item held in the inventory
 */
public class Inventory<T extends Item> implements Iterable<T> {
    private final int maxSize;
    private final List<T> items;
    
    public Inventory(){
        this.maxSize = Integer.MAX_VALUE;
        this.items = new ArrayList<>();
    }
    
    public Inventory(int maxSize){
        this.items = new ArrayList<>(maxSize);
        this.maxSize = maxSize;
    }
    
    /**
     * Add item to inventory. Will reject the operation if the item is {@code null},
     * or if {@code maxSize} was reached.
     * @param item the {@code Item} to be added
     * @return {@code true} if {@code item} was added, {@code false} otherwise
     */
    public boolean add(T item){
        if(item == null || items.size() >= maxSize) return false;
        items.add(item);
        return true;
    }
    
    /**
     * Add all items from the specified collection to the end of the inventory using
     * {@link Inventory#add}
     * @param items collection containing the items to be added to the inventory
     * @return {@code true} if the inventory was modified, {@code false} otherwise
     */
    public boolean addAll(Collection<? extends T> items){
        if(items == null) return false;
        
        boolean result = false;
        for(T item: items){
            result |= add(item);
        }
        return result;
    }
    
    public T get(int index){
        return items.get(index);
    }
    
    public int size(){
        return items.size();
    }
    
    public boolean remove(T item){
        return items.remove(item);
    }
    
    public T remove(int index){
        return items.remove(index);
    }
    
    public Stream<T> stream(){
        return items.stream();
    }
    
    @Override
    public Iterator<T> iterator() {
        return items.iterator();
    }
    
    public int count(int itemId){
        int accumulator = 0;
        for(T item : this){
            if(item.getItemId() == itemId) accumulator++;
        }
        return accumulator;
    }
    
    /**
     * Counts how many of each unique item is held in the inventory
     * @return A {@code Map} with unique items as keys and their counts as values
     */
    public Map<T, Integer> countAll(){
        Map<T, Integer> map = new HashMap<>();
        
        for(T item : items){
            if(!map.containsKey(item)){
                map.put(item, 1);
            } else{
                map.replace(item, 1 + map.get(item));
            }
        }
        return map;
    }
    
    public List<T> asUnmodifiableList(){
        return Collections.unmodifiableList(items);
    }
}
