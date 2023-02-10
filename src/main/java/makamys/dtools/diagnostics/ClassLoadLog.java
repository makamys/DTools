package makamys.dtools.diagnostics;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.MixinEnvironment;

import makamys.dtools.Config;
import makamys.dtools.DTools;
import makamys.dtools.util.Util;
import net.minecraft.launchwrapper.Launch;

public class ClassLoadLog {
    
    public static ClassLoadLog instance;
    
    FileWriter out;

    public ClassLoadLog() {
        // We register the transformer this way to register it as early as possible.
        Launch.classLoader.registerTransformer("makamys.dtools.asm.LoggingTransformer");
        
        try {
            out = new FileWriter(Util.childFile(DTools.OUT_DIR, "class-load.log"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static String getPrefix() {
        return "[" + StringUtils.center(MixinEnvironment.getCurrentEnvironment().getPhase().toString(), 7) + "] ";
    }
    
    public static boolean isActive() {
        return Config.logClassLoading;
    }

    public void onClassLoaded(String name) {
        log(getPrefix() + name + "\n");
    }
    
    private void log(String line) {
        if(out != null) {
            try {
                out.write(line);
                // flush after every write so we don't lose information when a crash happens
                out.flush();
            } catch(IOException e) {
                e.printStackTrace();
                line = null;
            }
        }
    }

}
