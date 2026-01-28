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

import java.io.File;
import java.util.EnumSet;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.*;
import tools.jackson.databind.exc.JsonNodeException;

/**
 *
 * @author thefa
 */
public class Armor extends Item {
    private static final String JSON_PATH = "json/armor.json";
    
    private final int weight;
    private final int absorb;
    private final EnumSet<Element> elementalResistances;
    private final int spellId;

    public Armor(JsonNode node){
        if(node == null)throw new NullPointerException(
            "Attribute JsonNode node is null."
        );

        try{
            super.setName(require(node, "name").asString());
            super.setShopId(require(node, "shopId").asInt());
            super.setPrice(require(node, "price").asInt());
            weight = require(node, "weight").asInt();
            absorb = require(node, "absorb").asInt();
            spellId = require(node, "spellId").asInt();

            elementalResistances = EnumSet.noneOf(Element.class);
            for(JsonNode i : require(node, "elementalResistances")){
                elementalResistances.add(Element.valueOf(i.asString()));
            }
        } catch(JsonNodeException e){
            throw new RuntimeException(
                "Failed to get armor attributes from JSON." +
                " ("+e.getMessage()+")"
            );
        }
    }

    public static Armor createFromId(int jsonId){
        try{
            JsonNode jsonRoot = MAPPER.readTree(new File(JSON_PATH));

            if(jsonId < 0 || jsonId >= jsonRoot.size()){
                throw new IllegalArgumentException("Index out of bounds: "+jsonId);
            }
            return new Armor(jsonRoot.get(jsonId));
        } catch(JacksonException e){
            throw new RuntimeException(
                "Failed to read from "+JSON_PATH+"." +
                " ("+e.getMessage()+")"
            );
        }
    }
    
    public int getWeight() {
        return weight;
    }

    public int getAbsorb() {
        return absorb;
    }

    public EnumSet<Element> getElementalResistances() {
        return elementalResistances.clone();
    }

    public int getSpellId() {
        return spellId;
    }
}
