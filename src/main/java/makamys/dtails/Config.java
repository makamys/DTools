package makamys.dtails;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang3.EnumUtils;

import makamys.dtails.util.ConfigDumper;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Configuration;

public class Config {
    
    public static boolean frameProfilerStartEnabled;
    public static boolean frameProfilerHooks;
    public static boolean frameProfilerPrint;
    
    public static boolean pauseOnWorldEntry;
	public static boolean dingOnWorldEntry;
	public static int pauseWaitLength;
    
    public static boolean dtailsCommand;
    public static String profilerMethods;
    
    public static boolean forgeBarProfiler;
    public static boolean crasher;
    public static boolean serverRunTimePrinter;
    public static boolean wireframe;
    
    public static void reload() {
        Configuration config = new Configuration(new File(Launch.minecraftHome, "config/dtails.cfg"));
        
        config.load();
        
        config.addCustomCategoryComment("Tweaks", "In addition to these settings, there are some tweaks that are activated via JVM flags:\n" +
        "* -Ddtails.launchWorld=WORLD : Automatically loads the world with the folder name WORLD once the main menu is reached. WORLD can be left blank, in this case the most recently played world will be loaded. Hold down shift when the main menu appears to cancel the automatic loading.\n" +
        "* -Ddtails.launchMinimized : Launch Minecraft minimized. Only implemented on Windows at the moment.\n" +
        "* -Ddtails.launchOnDesktop=NUMBER : Launch Minecraft on the virtual desktop with ordinal NUMBER. Only implemented on Linux at the moment. xprop has to be installed for it to work. Only tested on Openbox.");
        
        pauseOnWorldEntry = config.getBoolean("autoLoadPauseOnWorldEntry", "Tweaks", true, "Pause some ticks after auto-loaded world is loaded.\nDelaying the pausing can be useful because some initialization like chunk updates won't happen while the game is paused.");
        dingOnWorldEntry = config.getBoolean("autoLoadDingOnWorldEntry", "Tweaks", true, "Ding once auto-loaded world is loaded.");
        pauseWaitLength = config.getInt("autoLoadPauseWaitLength", "Tweaks", 20, 0, Integer.MAX_VALUE, "How many ticks to wait before pausing an auto-loaded world.");
        
        dtailsCommand = config.getBoolean("dtailsCommand", "Diagnostics", true, "Enables the /dtails command, used to access various diagnostics. Invoke it in-game for additional information.");
        profilerMethods = config.getString("profilerMethods", "Diagnostics", "", "Comma-separated list of methods to profile. The results will be written to ./dtails/out/profiler-<timestamp>.csv. Currently only the call count is measured. Method names have the syntax of `<canonical class name>.<method name>`, like `some.package.SomeClass.method`.");
        frameProfilerStartEnabled = config.getBoolean("frameProfilerStartEnabled", "Diagnostics", false, "Automatically start frame profiler as soon as the game starts.");
        frameProfilerHooks = config.getBoolean("frameProfilerHooks", "Diagnostics", false, "Insert hooks that lets the frame profiler profile various parts of frame rendering. If this is disabled, the frame profiler will only be able to show very limited information.");
        frameProfilerPrint = config.getBoolean("frameProfilerPrint", "Diagnostics", false, "Print render tick times to log periodically.");
        forgeBarProfiler = config.getBoolean("forgeBarProfiler", "Diagnostics", false, "Creates a report of how long each step of startup loading took in ./dtails/out/fml_bar_profiler.csv.");
        crasher = config.getBoolean("crasher", "Diagnostics", false, "Enables debug feature that crashes the game when pressing certain key combinations.");
        serverRunTimePrinter = config.getBoolean("serverRunTimePrinter", "Diagnostics", false, "Prints server run time.");
        wireframe = config.getBoolean("wireframe", "Diagnostics", false, "Render world in wireframe mode. Tip: If this is enabled when the game is started, you will be able to toggle it without restarting the game, only the world needs to be reloaded.");
        
        if(ConfigDumper.ENABLED) {
            ConfigDumper.dumpConfig(config);
        }
        
        if(config.hasChanged()) {
            config.save();
        }
    }
    
    // TODO move this to MCLib
    private static <E extends Enum> E getEnum(Configuration config, String propName, String propCat, E propDefault, String propComment) {
        return getEnum(config, propName, propCat, propDefault, propComment, false);
    }
    
    private static <E extends Enum> E getEnum(Configuration config, String propName, String propCat, E propDefault, String propComment, boolean lowerCase) {
        Map enumMap = EnumUtils.getEnumMap(propDefault.getClass());
        String[] valuesStr = (String[])enumMap.keySet().toArray(new String[]{});
        String defaultString = propDefault.toString();
        if(lowerCase) defaultString = defaultString.toLowerCase();
        return (E)enumMap.get(config.getString(propName, propCat, defaultString, propComment, valuesStr).toUpperCase());
    }
    
}
