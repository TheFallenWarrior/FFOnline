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

/**
 *
 * @author thefa
 */
public class Enemy extends Battler{
    private static final int MAGIC_MAX_INVENTORY = 8;
    private static final int SKILL_MAX_INVENTORY = 4;
    
    private int exp;
    private int gil;
    
    private int morale;
    private int baseHitsPerTurn
    
    private final Inventory<Magic> magicInventory = new Inventory<>(MAGIC_MAX_INVENTORY);
    private int magicChance;
    
    private final Inventory<Magic> skillInventory = new Inventory<>(SKILL_MAX_INVENTORY);
    private int skillChance;
    
    @Override
    public boolean canReceiveStatus(StatusAilment status){
        return (status != StatusAilment.POISONED && super.canReceiveStatus(status));
    }

    @Override
    public void battleEnd(){
        setHp(0);
    }
    
    @Override
    public int getCritChance() {
        return 0;
    }
    
    @Override
    public int getBaseHitsPerTurn(){ return 1; }
}
