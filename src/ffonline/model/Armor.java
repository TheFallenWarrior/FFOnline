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
import java.util.logging.Logger;
import java.util.logging.Level;
import tools.jackson.databind.*;

/**
 *
 * @author thefa
 */
public class Armor extends Item {
    private static final Logger LOGGER = Logger.getLogger(Armor.class.getName());
    public static final String JSON_PATH = "json/armor.json";
    
    private final int weight;
    private final int absorb;
    private final EnumSet<Element> elementalResistances;
    private final int spellId;

    public Armor(JsonNode node){
        super(node);
        this.weight = node.path("weight").asInt(0); 
        this.absorb = node.path("absorb").asInt(0);
        this.spellId = node.path("spellId").asInt(0);
        
        this.elementalResistances = EnumSet.noneOf(Element.class);
        JsonNode resistances = node.path("elementalResistances");
        if (resistances.isArray()) {
            for (JsonNode i : resistances) {
                try {
                    elementalResistances.add(Element.valueOf(i.asString("Nothing")));
                } catch (IllegalArgumentException e) {
                    LOGGER.log(Level.WARNING, "Unknown element found in JSON: {0}", i.asString());
                }
            }
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
