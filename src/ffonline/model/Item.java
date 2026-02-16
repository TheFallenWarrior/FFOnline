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

import tools.jackson.databind.JsonNode;

/**
 *
 * @author thefa
 */
public class Item{
    public static final String JSON_PATH = "json/item.json";
    
    private final String name;
    private final int shopId; // Number that indentifies the item in shops
    private final int price;

    public Item(String name, int shopId, int price){
        this.name = name;
        this.shopId = shopId;
        this.price = price;
    }
    
    public Item(JsonNode node){
        this.name = node.path("name").asString("Non-coercible value");
        this.shopId = node.path("shopId").asInt(0);
        this.price = node.path("price").asInt(0);
    }
    
    public static Item buildFromJson(JsonNode node){
        String name = node.path("name").asString("Non-coercible value");
        int shopId = node.path("shopId").asInt(0);
        int price = node.path("price").asInt(0);
        
        return new Item(name, shopId, price);
    }
    
    public String getName() {
        return name;
    }

    public int getShopId() {
        return shopId;
    }

    public int getPrice() {
        return price;
    }
}
