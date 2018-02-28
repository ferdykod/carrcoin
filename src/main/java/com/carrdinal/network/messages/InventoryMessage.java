package com.carrdinal.network.messages;

import com.carrdinal.core.Inventory;

public class InventoryMessage {

    public static String get(Inventory inventory) {
        return "inv \"" + inventory.toJSON() + "\"";
    }

}
