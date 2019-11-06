package com.github.davidmoten.bplustree;

import java.io.File;
import java.util.UUID;

public class Testing {
    
    public static File newDirectory() {
        String directoryName = "target/"+ UUID.randomUUID().toString().substring(0,6);
        File file = new File(directoryName);
        file.mkdirs();
        return file;
    }
}
