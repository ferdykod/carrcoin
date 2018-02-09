package com.carrdinal;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class CarrcoinSerializer {
    private static final int COMMAND_LEN = 12;
    private boolean usingChecksuming;
    private static Map<Class<? extends Message>, String> names = new HashMap<Class<? extends Message>, String>();
    private static Map<String, Constructor<? extends Message>> messageConstructors = new HashMap<String, Constructor<? extends Message>>();

    static {
        names.put(VersionMessage.class, "version");
        names.put(InventoryMessage.class, "inv");
        names.put(BlockMessage.class, "");
        names.put(GetDataMessage.class, "");
        names.put(TransactionMessage.class, "");
        names.put(AddressMessage.class, "");
        names.put(PingMessage.class, "");
        names.put(VersionAckMessage.class, "");
        names.put(GetBlocksMessage.class, "");
    }

    public CarrcoinSerializer(boolean usingChecksumming){
        this.usingChecksuming = usingChecksumming;
        for(Class<? extends Message> clazz : names.keySet()){
            Constructor<? extends Message> constructor = makeConstructor(clazz);
            if(clazz != null){
                messageConstructors.put(names.get(clazz), constructor);
            }
        }
    }

    public void useChecksumming(boolean usingChecksuming){
        this.usingChecksuming = usingChecksuming;
    }

    public void serialize(Message message, OutputStream out) throws IOException{
        String name = names.get(message.getClass());
        if(name == null){
            throw new Error("Don't know how to serialize " + message.getClass());
        }

        byte[] header = new byte[4 + COMMAND_LEN + 4 + (usingChecksuming ? 4:0)];
        

    }

}
