package makamys.dtools.util;

import java.io.File;

import javax.annotation.Nullable;

public class Util {

    public static File childFile(File parent, String childName) {
        parent.mkdirs();
        return new File(parent, childName);
    }
    
    @Nullable
    public static File getSharedDataDir() {
        String sharedDataDir = System.getProperty("minecraft.sharedDataDir");
        if(sharedDataDir == null) {
            sharedDataDir = System.getenv("MINECRAFT_SHARED_DATA_DIR");
        }
        return sharedDataDir == null ? null : new File(sharedDataDir);
    }
    
}
