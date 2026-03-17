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
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.jackson.databind.JsonNode;

/**
 *
 * @author thefa
 */
public class Magic extends Item {
    private static final Logger LOGGER = Logger.getLogger(Armor.class.getName());
    public static final String JSON_PATH = "json/armor.json";
    
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
    
    public static Magic buildFromJson(JsonNode node){
        String name = node.path("name").asString("Non-coercible value");
        int shopId = node.path("shopId").asInt(0);
        int price = node.path("price").asInt(0);
        int spellId = node.path("spellId").asInt(0);
        int effectivity = node.path("effectivity").asInt(0);

        
        Optional<String> optTarget = node.path("targeting").asStringOpt();
        CommandTarget resolvedTarget = CommandTarget.SINGLE_ENEMY;
        try{
            resolvedTarget = CommandTarget.valueOf(optTarget.orElse("Non-coercible value"));
        } catch(IllegalArgumentException e){
            LOGGER.log(Level.WARNING, "Unknown magic targeting found in JSON: {0}", optTarget.orElse("Non-coercible value"));
        }
        
        Optional<String> optEffect = node.path("effect").asStringOpt();
        MagicEffect resolvedEffect = MagicEffect.DAMAGE;
        try{
            resolvedTarget = CommandTarget.valueOf(optEffect.orElse("Non-coercible value"));
        } catch(IllegalArgumentException e){
            LOGGER.log(Level.WARNING, "Unknown magic effect found in JSON: {0}", optEffect.orElse("Non-coercible value"));
        }
        
        EnumSet<Element> elements = EnumSet.noneOf(Element.class);
        JsonNode elemNode = node.path("elements");
        if (elemNode.isArray()){
            for (JsonNode i : elemNode){
                Optional<String> optValue = i.asStringOpt();
                if(optValue.isEmpty()){
                    LOGGER.log(Level.WARNING, "Non-string element found in JSON");
                } else{
                    try{
                        elements.add(Element.valueOf(optValue.get()));
                    } catch (IllegalArgumentException e){
                        LOGGER.log(Level.WARNING, "Unknown element found in JSON: {0}", optValue.get());
                    }
                }
            }
        }
        
        EnumSet<Element> effectElements = EnumSet.noneOf(Element.class);
        JsonNode effectElemNode = node.path("effectElements");
        if (effectElemNode.isArray()){
            for (JsonNode i : effectElemNode){
                Optional<String> optValue = i.asStringOpt();
                if(optValue.isEmpty()){
                    LOGGER.log(Level.WARNING, "Non-string element found in JSON");
                } else{
                    try{
                        effectElements.add(Element.valueOf(optValue.get()));
                    } catch (IllegalArgumentException e){
                        LOGGER.log(Level.WARNING, "Unknown element found in JSON: {0}", optValue.get());
                    }
                }
            }
        }
        
        EnumSet<StatusAilment> effectStatuses = EnumSet.noneOf(StatusAilment.class);
        JsonNode effectStatNode = node.path("elements");
        if(effectStatNode.isArray()){
            for (JsonNode i : effectStatNode){
                Optional<String> optValue = i.asStringOpt();
                if(optValue.isEmpty()){
                    LOGGER.log(Level.WARNING, "Non-string element found in JSON");
                } else{
                    try{
                        effectStatuses.add(StatusAilment.valueOf(optValue.get()));
                    } catch(IllegalArgumentException e){
                        LOGGER.log(Level.WARNING, "Unknown element found in JSON: {0}", optValue.get());
                    }
                }
            }
        }
        
        // TODO: Add equippable information to magic JSON
        EnumSet<CharacterJob> equippable = EnumSet.noneOf(CharacterJob.class);
        
        return new Magic(
                name,
                shopId,
                price,
                spellId,
                effectivity,
                resolvedTarget,
                resolvedEffect,
                elements,
                effectElements,
                effectStatuses,
                equippable
        );
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
