package com.notbestlord.core.inventory;

import com.notbestlord.core.inventory.equipment.EquipmentItem;
import com.notbestlord.core.inventory.equipment.EquipmentWeapon;

import java.util.UUID;

public class Item {
    private String displayName;
    private String type;
    private int amount;
    private int maxStackSize;
    private String id;

    public Item(){}

    public Item(String displayName, String type, int amount, int maxStackSize) {
        this.displayName = displayName;
        this.type = type;
        this.amount = amount;
        this.maxStackSize = maxStackSize;
        this.id = UUID.randomUUID().toString();
    }
    public Item(Item item){
        this.displayName = item.displayName;
        this.type = item.type;
        this.amount = item.amount;
        this.maxStackSize = item.maxStackSize;
        this.id = UUID.randomUUID().toString();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public String getId() {
        return id;
    }

    public boolean doesMatch(Item item){
        return item != null && this.type.equals(item.getType());
    }

    public static Item New(Item item){
        if(item instanceof ConsumableItem) return new ConsumableItem((ConsumableItem) item);
        if(item instanceof EquipmentWeapon) return new EquipmentWeapon((EquipmentWeapon) item);
        if(item instanceof EquipmentItem) return new EquipmentItem((EquipmentItem) item);
        return new Item(item);
    }

    @Override
    public String toString(){
        return  "[Name:" + this.displayName + ", Type:" + this.type + ", Amount:" + this.amount + ", MaxStackSize:" + this.maxStackSize + "]";
    }

    public ItemRegistry asItemRegistry() {
        return new ItemRegistry(type, amount);
    }
}
