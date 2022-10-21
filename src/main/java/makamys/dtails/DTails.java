package makamys.dtails;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import makamys.dtails.diagnostics.FMLBarProfiler;
import net.minecraft.launchwrapper.Launch;

public class DTails {
    
    public static final String MODID = "dtails";
    public static final String VERSION = "@VERSION@";
    
    public static final Logger LOGGER = LogManager.getLogger("dtails");
    
    public static final File MY_DIR = new File(Launch.minecraftHome, "dtails");
    public static final File OUT_DIR = new File(MY_DIR, "out");
    public static final File CACHE_DIR = new File(MY_DIR, "cache");
    
    public static void init(){
        if(FMLBarProfiler.isActive()) {
            FMLBarProfiler.instance().init();
        }
    }
}
