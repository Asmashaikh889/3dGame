package com.notbestlord.core.inventory;

public interface IInventory {
    void openInventory();
    void addItem(Item item);
    Item getItem(int index);
    void removeItem(int index);
    void removeItem(Item item);
    void removeItem(String itemType, int amount);
    void clear();
    boolean contains(Item item);
    boolean containsAtLeast(String itemType, int amount);
    int firstEmpty();
    int first(String type);
    boolean availableItemAmount(String type, int amount);
    int first(Item item);
    int getSize();
    boolean isEmpty();
    Item[] getContents();

}
