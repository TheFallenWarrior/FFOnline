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

import java.util.List;
import java.util.Optional;

/**
 *
 * @author thefa
 */
public class PlayerParty extends BattlerGroup<PlayerCharacter> {
    private final Inventory<Item> inventory = new Inventory<>();
    private int gil = 400; // Starting amount of gold
    
    public List<Item> getInventory(){
        return inventory.asUnmodifiableList();
    }
    
    public Optional<PlayerCharacter> getFromName(String name){
        for(PlayerCharacter member : this){
            if(member.getName().equals(name)) return Optional.of(member);
        }
        return Optional.empty();
    }
    
    public Optional<PlayerCharacter> getRandomWeighted(){
        if(countAlive() == 0) return Optional.empty();
        if(size() != 4) return getRandomAlive();
        
        while(true){
            int choice = rng.nextInt(8);
            int[] weights = {4, 2, 1 ,1};
            PlayerCharacter chosen = get(0);
            
            int accumulator = 0;
            
            for(int i=0;i<4;i++){
                accumulator += weights[i];
                if(choice < accumulator){
                    chosen = get(i);
                    break;
                }
            }
            
            if(chosen.isAlive()) return Optional.of(chosen);
        }
    }
    
    /**
     * Re-orders the party after battle, moving characters with status ailments
     * toward the back. Priority: Normal > Poisoned > Petrified > Dead.
     * Only operates on a full 4-character party.
     */
    public void sort(){
        if(size() != 4) return;

        int n = size();

        // Each tag = original slot index (0–3) + status weight:
        //  Dead=64, Petrified=32, Poisoned=16, Normal=0
        int[] tags = new int[n];
        for(int i = 0; i < n; i++){
            PlayerCharacter pc = get(i);
            int statusVal = 0;
            if(pc.hasStatus(StatusAilment.DEAD))           statusVal = 64;
            else if(pc.hasStatus(StatusAilment.PETRIFIED)) statusVal = 32;
            else if(pc.hasStatus(StatusAilment.POISONED))  statusVal = 16;
            tags[i] = i + statusVal;
        }

        // Bubble sort: n passes of n-1 comparisons (no early-exit optimization).
        for(int pass = 0; pass < n; pass++){
            for(int i = 0; i < n-1; i++){
                if(tags[i] > tags[i+1]){
                    // INTENTIONAL: Bug from the original game: the swap routine is
                    //  passed the *original* slot numbers encoded in the tag values
                    //  (low 2 bits) rather than the *current* comparison positions i
                    //  and i+1. As tags migrate through the array during sorting, the
                    //  original slot numbers they carry no longer correspond to their
                    //  current positions, so the wrong pair of characters ends up being
                    //  swapped. This affects 106 of the 256 possible status combinations.
                    int slotA = tags[i]   & 0x03;
                    int slotB = tags[i+1] & 0x03;

                    // reorder party members
                    swap(slotA, slotB);

                    // mirror swap in the tag array
                    int tmp  = tags[slotA];
                    tags[slotA] = tags[slotB];
                    tags[slotB] = tmp;
                }
            }
        }
    }

    public int getGil(){
        return gil;
    }

    public void setGil(int gil){
        this.gil = Math.clamp(gil, 0, 999999);
    }
    
    public void offsetGil(int offset){
        setGil(gil+offset);
    }
}
