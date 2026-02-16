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
package ffonline;

import ffonline.model.Armor;
import ffonline.model.Item;
import ffonline.model.Weapon;
import java.io.File;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * @author thefa
 */
public class JsonLoader {
    public static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = Logger.getLogger(JsonLoader.class.getName());
    
    private static JsonNode armorJsonRoot = null;
    private static JsonNode itemJsonRoot = null;
    private static JsonNode weaponJsonRoot = null;
    
    public static void init() throws JacksonException{
        armorJsonRoot = MAPPER.readTree(new File(Armor.JSON_PATH));
        itemJsonRoot = MAPPER.readTree(new File(Item.JSON_PATH));
        weaponJsonRoot = MAPPER.readTree(new File(Weapon.JSON_PATH));
    }
    
    private static <T> Optional<T> get(
        JsonNode root,
        int jsonId,
        String jsonPath,
        Function<JsonNode, T> constructor,
        String typeName
    ){
        if(root == null)
            throw new IllegalStateException("JsonLoader not initialized");
        
        try{
            JsonNode node = root.get(jsonId);
            
            if(node == null || node.isNull()){
                LOGGER.log(
                    Level.SEVERE,
                    "{0} ID {1} not found in {2}",
                    new Object[]{typeName, jsonId, jsonPath}
                );
                return Optional.empty();
            }
            
            return Optional.of(constructor.apply(node));
        } catch(JacksonException e){
            LOGGER.log(Level.SEVERE, "Critical error loading "+typeName+" data", e);
            return Optional.empty();
        }
    }
    
    public static Optional<Armor> getArmor(int jsonId){
        return get(
            armorJsonRoot,
            jsonId,
            Armor.JSON_PATH,
            Armor::buildFromJson,
            "Armor"
        );
    }
    
    public static Optional<Item> getItem(int jsonId){
        return get(
            itemJsonRoot,
            jsonId,
            Item.JSON_PATH,
            Item::buildFromJson,
            "Item"
        );
    }
    
    public static Optional<Weapon> getWeapon(int jsonId){
        return get(
            weaponJsonRoot,
            jsonId,
            Weapon.JSON_PATH,
            Weapon::new,
            "Weapon"
        );
    }
}
