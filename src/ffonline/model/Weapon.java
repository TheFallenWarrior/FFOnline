/*
 * The MIT License
 *
 * Copyright 2026 user.
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

import java.util.EnumSet;

/**
 *
 * @author thefa
 */
public class Weapon extends Item {
    private int hitChance;
    private int critChance; // Weapon index number, 1-based
    private int damage;
    private int spellId;
    private EnumSet<Element> attackElements;
    private EnumSet<EnemyType> enemyTypes; // The enemy types the weapon is strong against

    public Weapon(){
        attackElements = EnumSet.noneOf(Element.class);
        enemyTypes = EnumSet.noneOf(EnemyType.class);
    }
    
    public int getHitChance() {
        return hitChance;
    }
    
    public int getCritChance() {
        return critChance;
    }

    public int getDamage() {
        return damage;
    }

    public int getSpellId() {
        return spellId;
    }

    public EnumSet<Element> getAttackElements() {
        return attackElements.clone();
    }

    public EnumSet<EnemyType> getEnemyTypes() {
        return enemyTypes.clone();
    }
}
