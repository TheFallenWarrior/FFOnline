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

import ffonline.JsonLoader;
import java.io.File;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.JsonNodeException;

/**
 *
 * @author thefa
 */
public class Item{
    public static final String JSON_PATH = "json/item.json";
    
    private String name;
    private int shopId; // Number that indentifies the item in shops
    private int price;

    public Item(){}
    
    public Item(JsonNode node){
        if(node == null)throw new NullPointerException(
            "Attribute JsonNode node is null."
        );
        
        try{
            name = JsonLoader.require(node, "name").asString();
            shopId = JsonLoader.require(node, "shopId").asInt();
            price = JsonLoader.require(node, "price").asInt();
        } catch(JsonNodeException e){
            throw new RuntimeException(
                "Failed to get armor attributes from JSON." +
                " ("+e.getMessage()+")"
            );
        }
    }
    
    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public int getShopId() {
        return shopId;
    }

    protected void setShopId(int shopId) throws IllegalArgumentException{
	if(shopId < 0 || shopId > 255)
            throw new IllegalArgumentException("Shop ID must be in range 0..255; got"+ shopId +".");
        this.shopId = shopId;
    }

    public int getPrice() {
        return price;
    }

    protected void setPrice(int price) throws IllegalArgumentException{
        if(price < 0 || price > 65535)
            throw new IllegalArgumentException("Item price must be in range 0..65535; got"+ price +".");
        this.price = price;
    }
}
