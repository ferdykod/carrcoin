package com.carrdinal.network.messages;

import com.carrdinal.core.Inventory;

public class GetDataMessage {

    public static String get(Inventory inventory) {
        return "getdata \"" + inventory.toJSON() + "\"";
    }

}
