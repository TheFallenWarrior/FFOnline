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

import ffonline.model.command.CommandTarget;
import ffonline.JsonLoader;
import java.util.EnumSet;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.jackson.databind.JsonNode;

/**
 *
 * @author Anna Jaqueline (TheFallenWarrior)
 */
public class Magic extends Item {
    private static final Logger LOGGER = Logger.getLogger(Magic.class.getName());
    public static final String JSON_PATH = "json/magic.json";
    
    public static final int MAGIC_LEVELS = 8;
    
    final private int level;
    final private int spellId;
    final private int effectivity;
    final private int accuracy;
    final private CommandTarget targeting;
    final private Effect effect;
    final private EnumSet<Element> elements;
    final private EnumSet<Element> effectElements;
    final private EnumSet<StatusAilment> effectStatuses;
    final private EnumSet<CharacterJob> equippable;

    public Magic(
        String name,
        int itemId,
        int price,
        int level,
        int spellId,
        int effectivity,
        int accuracy,
        CommandTarget targeting,
        Effect effect,
        EnumSet<Element> elements,
        EnumSet<Element> effectElements,
        EnumSet<StatusAilment> effectStatuses,
        EnumSet<CharacterJob> equippable
    ){
        super(name, itemId, price);
        this.level = level&0xff;
        this.spellId = spellId&0xff;
        this.effectivity = effectivity&0xff;
        this.accuracy = accuracy&0xff;
        this.targeting = targeting;
        this.effect = effect;
        this.elements = EnumSet.copyOf(elements);
        this.effectElements = EnumSet.copyOf(effectElements);
        this.effectStatuses = EnumSet.copyOf(effectStatuses);
        this.equippable = EnumSet.copyOf(equippable);
    }
    
    public static Magic buildFromJson(JsonNode node){
        String name = node.path("name").asString("Non-coercible value");
        int itemId = node.path("itemId").asInt(0);
        int price = node.path("price").asInt(0);
        int level = node.path("level").asInt(0);
        int spellId = node.path("spellId").asInt(0);
        int effectivity = node.path("effectivity").asInt(0);
        int accuracy = node.path("accuracy").asInt(0);

        
        Optional<String> optTarget = node.path("targeting").asStringOpt();
        CommandTarget resolvedTarget = CommandTarget.SINGLE_ENEMY;
        try{
            resolvedTarget = CommandTarget.valueOf(optTarget.orElse("Non-coercible value"));
        } catch(IllegalArgumentException e){
            LOGGER.log(Level.WARNING, "Unknown magic targeting found in JSON: {0}", optTarget.orElse("Non-coercible value"));
        }
        
        Optional<String> optEffect = node.path("effect").asStringOpt();
        Effect resolvedEffect = Effect.DAMAGE;
        try{
            resolvedEffect = Effect.valueOf(optEffect.orElse("Non-coercible value"));
        } catch(IllegalArgumentException e){
            LOGGER.log(Level.WARNING, "Unknown magic effect found in JSON: {0}", optEffect.orElse("Non-coercible value"));
        }
        
        EnumSet<Element> elements = JsonLoader.parseEnumSet(node.path("elements"), Element.class, "element");
        EnumSet<Element> effectElements = JsonLoader.parseEnumSet(node.path("effectElements"), Element.class, "element");
        EnumSet<StatusAilment> effectStatuses = JsonLoader.parseEnumSet(node.path("effectStatuses"), StatusAilment.class, "status");
        EnumSet<CharacterJob> equippable = JsonLoader.parseEnumSet(node.path("equippable"), CharacterJob.class, "job");
        
        return new Magic(
            name,
            itemId,
            price,
            level,
            spellId,
            effectivity,
            accuracy,
            resolvedTarget,
            resolvedEffect,
            elements,
            effectElements,
            effectStatuses,
            equippable
        );
    }
    
    public static Optional<Magic> getBySpellId(int spellId){
        Optional<Magic> optMagic = JsonLoader.getMagic(spellId-1);
        if(optMagic.isEmpty() || optMagic.get().spellId != spellId) return Optional.empty();
        
        return optMagic;
    }
    
    public boolean isEquippable(CharacterJob job){
        return equippable.contains(job);
    }

    public int getLevel(){
        return level;
    }

    public int getSpellId(){
        return spellId;
    }

    public int getEffectivity(){
        return effectivity;
    }
    
    public int getAccuracy(){
        return accuracy;
    }

    public EnumSet<Element> getElements(){
        return EnumSet.copyOf(elements);
    }

    public CommandTarget getTargeting(){
        return targeting;
    }

    public Effect getEffect(){
        return effect;
    }

    public EnumSet<StatusAilment> getEffectStatuses(){
        return EnumSet.copyOf(effectStatuses);
    }

    public EnumSet<Element> getEffectElements(){
        return EnumSet.copyOf(effectElements);
    }

    public EnumSet<CharacterJob> getEquippable(){
        return EnumSet.copyOf(equippable);
    }
    
    public static enum Effect {
        NOTHING,                // 0x00
        DAMAGE,                 // 0x01
        HARM,                   // 0x02
        STATUS,                 // 0x03
        HIT_MULTIPLIER_DOWN,    // 0x04
        MORALE_DOWN,            // 0x05
        UNUSED,                 // 0x06
        HP_RECOVERY,            // 0x07
        STATUS_RECOVERY,        // 0x08
        DEFENSE_UP,             // 0x09
        RESIST_ELEMENT,         // 0x0A
        ATTACK_UP,              // 0x0B
        HIT_MULTIPLIER_UP,      // 0x0C
        ATTACK_ACCURACY_UP,     // 0x0D
        EVASION_DOWN,           // 0x0E
        FULL_RECOVERY,          // 0x0F
        EVASION_UP,             // 0x10
        UNRESIST_ELEMENT,       // 0x11
        HP300_STATUS            // 0x12
    }
}
