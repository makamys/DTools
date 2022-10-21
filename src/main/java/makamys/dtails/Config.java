package makamys.dtails;

import static makamys.dtails.DTails.LOGGER;
import static makamys.dtails.DTails.MODID;
import static makamys.dtails.util.AnnotationBasedConfig.*;

import java.io.File;
import makamys.dtails.util.ConfigDumper;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Configuration;

public class Config {
    
    @ConfigBoolean(cat="_General", def=true, com="Enables the /dtails command, used to access various diagnostics. Invoke it in-game for additional information.")
    public static boolean dtailsCommand;
    
    @ConfigBoolean(cat="Profiling", def=false, com="Automatically start frame profiler as soon as the game starts.")
    public static boolean frameProfilerStartEnabled;
    @ConfigBoolean(cat="Profiling", def=false, com="Insert hooks that lets the frame profiler profile various parts of frame rendering. If this is disabled, the frame profiler will only be able to show very limited information.")
    public static boolean frameProfilerHooks;
    @ConfigBoolean(cat="Profiling", def=false, com="Print render tick times to log periodically.")
    public static boolean frameProfilerPrint;
    @ConfigString(cat="Profiling", def="", com="Comma-separated list of methods to profile. The results will be written to ./dtails/out/profiler-<timestamp>.csv. Currently only the call count is measured. Method names have the syntax of `<canonical class name>.<method name>`, like `some.package.SomeClass.method`.")
    public static String methodProfilerMethods;
    @ConfigBoolean(cat="Profiling", def=false, com="Creates a report of how long each step of startup loading took in ./dtails/out/fml_bar_profiler.csv.")
    public static boolean forgeBarProfiler;
    @ConfigBoolean(cat="Profiling", def=false, com="Prints server run time.")
    public static boolean serverRunTimePrinter;
    
    @ConfigBoolean(cat="Automation", def=true, com="Pause some ticks after auto-loaded world is loaded.\\nDelaying the pausing can be useful because some initialization like chunk updates won't happen while the game is paused.")
    public static boolean autoLoadPauseOnWorldEntry;
    @ConfigBoolean(cat="Automation", def=true, com="Ding once auto-loaded world is loaded.")
    public static boolean autoLoadDingOnWorldEntry;
    @ConfigInt(cat="Automation", def=20, min=0, max=Integer.MAX_VALUE, com="How many ticks to wait before pausing an auto-loaded world.")
    public static int autoLoadPauseWaitLength;
    
    @ConfigBoolean(cat="Testing", def=false, com="Enables debug feature that crashes the game when pressing certain key combinations.")
    public static boolean crasher;
    @ConfigBoolean(cat="Testing", def=false, com="Render world in wireframe mode. Tip: If this is enabled when the game is started, you will be able to toggle it without restarting the game, only the world needs to be reloaded.")
    public static boolean wireframe;
    
    private static Configuration config;
    private static File configFile = new File(Launch.minecraftHome, "config/" + MODID + ".cfg");

    
    public static void reload() {
        config = new Configuration(configFile);
        
        config.load();
        loadFields(config, Config.class, LOGGER);
        
        config.addCustomCategoryComment("Tweaks", "In addition to these settings, there are some tweaks that are activated via JVM flags:\n" +
                "* -Ddtails.launchWorld=WORLD : Automatically loads the world with the folder name WORLD once the main menu is reached. WORLD can be left blank, in this case the most recently played world will be loaded. Hold down shift when the main menu appears to cancel the automatic loading.\n" +
                "* -Ddtails.launchMinimized : Launch Minecraft minimized. Only implemented on Windows at the moment.\n" +
                "* -Ddtails.launchOnDesktop=NUMBER : Launch Minecraft on the virtual desktop with ordinal NUMBER. Only implemented on Linux at the moment. xprop has to be installed for it to work. Only tested on Openbox.");
        
        if(ConfigDumper.ENABLED) {
            ConfigDumper.dumpConfig(config);
        }
        
        if(config.hasChanged()) {
            config.save();
        }
    }
    
}
