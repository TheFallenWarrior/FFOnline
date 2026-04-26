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
        if(getAliveSize() == 0) return Optional.empty();
        if(size() != 4) return getRandomAlive();
        
        while(true){
            int choice = rng.nextInt(8);
            List<Integer> weights = List.of(4, 2, 1, 1);
            PlayerCharacter chosen = get(0);
            
            int accumulator = 0;
            
            for(int i=0;i<4;i++){
                accumulator += weights.get(i);
                if(choice < accumulator){
                    chosen = get(i);
                    break;
                }
            }
            
            if(chosen.isAlive()) return Optional.of(chosen);
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
