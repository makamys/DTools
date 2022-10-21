package makamys.coretweaks;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import makamys.coretweaks.diagnostics.FMLBarProfiler;
import net.minecraft.launchwrapper.Launch;

public class CoreTweaks {
    
    public static final String MODID = "coretweaks";
    public static final String VERSION = "@VERSION@";
    
    public static final Logger LOGGER = LogManager.getLogger("coretweaks");
    
    public static final File MY_DIR = new File(Launch.minecraftHome, "coretweaks");
    public static final File OUT_DIR = new File(MY_DIR, "out");
    public static final File CACHE_DIR = new File(MY_DIR, "cache");
    
    public static void init(){
        if(FMLBarProfiler.isActive()) {
            FMLBarProfiler.instance().init();
        }
    }
}
