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
import ffonline.model.Magic;
import ffonline.model.PlayerCharacter;
import ffonline.model.CharacterProgression;
import ffonline.model.Weapon;
import java.io.File;
import java.util.EnumSet;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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

    // Thread-safe cache: key = logical name, value = loaded JsonNode
    private static final ConcurrentMap<String, JsonNode> JSON_CACHE = new ConcurrentHashMap<>();

    /**
     * Lazy-loads and caches a JSON root node. 
     * computeIfAbsent guarantees thread-safe initialization: only one thread will parse the file,
     *  others will wait and receive the cached instance.
     */
    private static JsonNode getJsonRoot(String cacheKey, String jsonPath){
        return JSON_CACHE.computeIfAbsent(cacheKey, k ->{
            try{
                return MAPPER.readTree(new File(jsonPath));
            } catch (JacksonException e){
                LOGGER.log(Level.SEVERE, "Critical error loading JSON from{0}", jsonPath);
                throw new IllegalStateException("Failed to load JSON: " + jsonPath, e);
            }
        });
    }

    public static <E extends Enum<E>> EnumSet<E> parseEnumSet(JsonNode node, Class<E> type, String label){
        EnumSet<E> result = EnumSet.noneOf(type);

        if(!node.isArray()) return result;
        
        for(JsonNode element : node){
            Optional<String> optValue = element.asStringOpt();
            
            if(optValue.isEmpty()){
                LOGGER.log(Level.WARNING, "Non-string {0} found in JSON", label);
                continue;
            }
            
            try{
                result.add(Enum.valueOf(type, optValue.get()));
            } catch(IllegalArgumentException e){
                LOGGER.log(Level.WARNING, "Unknown {0} found in JSON: {1}", new Object[]{label, optValue.get()});
            }
        }
        return result;
    }

    public static JsonNode getGrowth(){
        // Parsing of stat-growth data is handled by CharacterProgression directly
        return getJsonRoot("Growth", CharacterProgression.JSON_PATH);
    }

    private static <T> Optional<T> get(
            int jsonId,
            String jsonPath,
            Function<JsonNode, T> constructor,
            String typeName
    ){
        JsonNode root = getJsonRoot(typeName, jsonPath);
        try{
            JsonNode node = root.get(jsonId);
            if (node == null || node.isNull()){
                LOGGER.log(Level.SEVERE, "{0} ID {1} not found in {2}", new Object[]{typeName, jsonId, jsonPath});
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
            jsonId,
            Armor.JSON_PATH,
            Armor::buildFromJson,
            "Armor"
        );
    }

    public static Optional<Item> getItem(int jsonId){
        return get(
            jsonId,
            Item.JSON_PATH,
            Item::buildFromJson,
            "Item"
        );
    }

    public static Optional<Weapon> getWeapon(int jsonId){
        return get(
            jsonId,
            Weapon.JSON_PATH,
            Weapon::buildFromJson,
            "Weapon"
        );
    }

    public static Optional<Magic> getMagic(int jsonId){
        return get(
                jsonId,
                Magic.JSON_PATH,
                Magic::buildFromJson,
                "Magic"
        );
    }

    public static Optional<PlayerCharacter> getPlayerCharacter(int jsonId){
        return get(
                jsonId,
                PlayerCharacter.JSON_PATH,
                PlayerCharacter::buildFromJson,
                "PlayerCharacter"
        );
    }

    @Deprecated
    public static void init(){
        // Triggers loading of all JSON roots without failing on missing IDs
        getJsonRoot("armor", Armor.JSON_PATH);
        getJsonRoot("item", Item.JSON_PATH);
        getJsonRoot("weapon", Weapon.JSON_PATH);
        getJsonRoot("player", PlayerCharacter.JSON_PATH);
        getJsonRoot("magic", Magic.JSON_PATH);
        getJsonRoot("growth", CharacterProgression.JSON_PATH);
    }
}