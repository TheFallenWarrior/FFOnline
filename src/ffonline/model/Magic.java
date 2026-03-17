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

import java.util.EnumSet;

/**
 *
 * @author thefa
 */
public class Magic extends Item {
    final private int spellId;
    final private int effectivity;
    final private CommandTarget targeting;
    final private MagicEffect effect;
    final private EnumSet<Element> elements;
    final private EnumSet<Element> effectElements;
    final private EnumSet<StatusAilment> effectStatuses;
    final private EnumSet<CharacterJob> equippable;

    public Magic(
        String name,
        int shopId,
        int price,
        int spellId,
        int effectivity,
        CommandTarget targeting,
        MagicEffect effect,
        EnumSet<Element> elements,
        EnumSet<Element> effectElements,
        EnumSet<StatusAilment> effectStatuses,
        EnumSet<CharacterJob> equippable
    ){
        super(name, shopId, price);
        this.spellId = spellId&0xff;
        this.effectivity = effectivity&0xff;
        this.targeting = targeting;
        this.effect = effect;
        this.elements = EnumSet.copyOf(elements);
        this.effectElements = EnumSet.copyOf(elements);
        this.effectStatuses = EnumSet.copyOf(effectStatuses);
        this.equippable = EnumSet.copyOf(equippable);
    }

    public int getSpellId(){
        return spellId;
    }

    public int getEffectivity(){
        return effectivity;
    }

    public EnumSet<Element> getElements(){
        return elements;
    }

    public CommandTarget getTargeting(){
        return targeting;
    }

    public MagicEffect getEffect(){
        return effect;
    }

    public EnumSet<StatusAilment> getEffectStatuses(){
        return effectStatuses;
    }

    public EnumSet<Element> getEffectElements(){
        return effectElements;
    }

    public EnumSet<CharacterJob> getEquippable(){
        return equippable;
    }
}
