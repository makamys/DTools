package makamys.dtools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import makamys.dtools.diagnostics.ClassLoadLog;
import makamys.dtools.diagnostics.FMLBarProfiler;
import makamys.dtools.listener.ILifeCycleListener;
import net.minecraft.launchwrapper.Launch;

public class DTools {
    
    public static final List<Object> listeners = new ArrayList<>();
    
    public static final String MODID = "dtools";
    public static final String VERSION = "@VERSION@";
    
    public static final Logger LOGGER = LogManager.getLogger("dtools");
    
    public static final File MY_DIR = new File(Launch.minecraftHome, "dtools");
    public static final File OUT_DIR = new File(MY_DIR, "out");
    public static final File CACHE_DIR = new File(MY_DIR, "cache");
    
    public static void init(){
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                DTools.forEachListener(ILifeCycleListener.class, l -> l.onShutdown());
            }}, "DTools shutdown thread"));
        
        if(FMLBarProfiler.isActive()) {
            FMLBarProfiler.instance().init();
        }
        if(ClassLoadLog.isActive()) {
            listeners.add(ClassLoadLog.instance = new ClassLoadLog());
        }
    }
    
    public static void registerListener(Object o) {
        listeners.add(o);
    }
    
    public static void forEachLifeCycleEventListener(Consumer<ILifeCycleListener> callback) {
        DTools.forEachListener(ILifeCycleListener.class, l -> callback.accept(l));
    }
    
    public static <T> void forEachListener(Class<T> tClass, Consumer<T> callback) {
        for(Object o : DTools.listeners) {
            if(tClass.isAssignableFrom(o.getClass())) {
                callback.accept((T) o);
            }
        }
    }
}
