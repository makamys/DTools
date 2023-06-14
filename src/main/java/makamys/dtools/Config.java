package makamys.dtools;

import static makamys.dtools.DTools.LOGGER;
import static makamys.dtools.DTools.MODID;
import static makamys.dtools.util.AnnotationBasedConfigHelper.*;

import java.io.File;

import makamys.dtools.util.AnnotationBasedConfigHelper;
import makamys.dtools.util.ConfigDumper;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Configuration;

public class Config {
    
    @ConfigBoolean(cat="_General", def=true, com="Enables the /dtools command, used to access various features. Invoke it in-game for additional information.")
    public static boolean dtoolsCommand;
    
    @ConfigBoolean(cat="Profiling", def=false, com="Automatically start frame profiler as soon as the game starts.")
    public static boolean frameProfilerStartEnabled;
    @ConfigBoolean(cat="Profiling", def=false, com="Insert hooks that lets the frame profiler profile various parts of frame rendering. If this is disabled, the frame profiler will only be able to show very limited information.")
    public static boolean frameProfilerHooks;
    @ConfigBoolean(cat="Profiling", def=false, com="Print render tick times to log periodically.")
    public static boolean frameProfilerPrint;
    @ConfigString(cat="Profiling", def="", com="Comma-separated list of methods to profile. The results will be written to ./dtools/out/profiler-<timestamp>.csv. Currently only the call count is measured. Method names have the syntax of `<canonical class name>.<method name>`, like `some.package.SomeClass.method`.")
    public static String methodProfilerMethods;
    @ConfigBoolean(cat="Profiling", def=false, com="Creates a report of how long each step of startup loading took in ./dtools/out/fml_bar_profiler.csv.")
    public static boolean forgeBarProfiler;
    @ConfigBoolean(cat="Profiling", def=false, com="Prints server run time.")
    public static boolean serverRunTimePrinter;
    @ConfigBoolean(cat="Profiling", def=false, com="Show extra RAM info in F3 overlay.")
    public static boolean extraRamInfo;
    @ConfigBoolean(cat="Profiling", def=false, com="Logs how many times World#setBlock was called, with breakdowns by caller and block type. The results are written to ./dtools/out/setblockprofiler.txt. Slows down world gen by a lot!")
    public static boolean setBlockProfiler;
    
    @ConfigBoolean(cat="Automation", def=true, com="Pause some ticks after auto-loaded world is loaded.\nDelaying the pausing can be useful because some initialization like chunk updates won't happen while the game is paused.")
    public static boolean autoLoadPauseOnWorldEntry;
    @ConfigBoolean(cat="Automation", def=true, com="Ding once auto-loaded world is loaded.")
    public static boolean autoLoadDingOnWorldEntry;
    @ConfigInt(cat="Automation", def=20, min=0, max=Integer.MAX_VALUE, com="How many ticks to wait before pausing an auto-loaded world.")
    public static int autoLoadPauseWaitLength;
    @ConfigBoolean(cat="Automation", def=false, com="Press F to freeze input.\n(Cheat feature)")
    public static boolean freezeInputKey;
    @ConfigBoolean(cat="Automation.chunkPregenerator", def=false, com="Auto-initialize Chunk Pregenerator GUI fields. This can be customized in `auto_chunk_pregen.ini`.")
    public static boolean autoInitializeChunkPregenGui;
    
    @ConfigBoolean(cat="Debug", def=false, com="Enables debug feature that crashes the game when pressing certain key combinations.")
    public static boolean crasher;
    @ConfigBoolean(cat="Debug", def=false, com="Render world in wireframe mode. Toggle using /dtools wireframe.\n(Cheat feature)")
    public static boolean wireframe;
    @ConfigBoolean(cat="Debug", def=false, com="Enable wireframe at startup")
    public static boolean wireframeStartEnabled;
    @ConfigBoolean(cat="Debug", def=false, com="Print change in XYZ coordinates every tick")
    public static boolean positionDeltaPrint;
    @ConfigBoolean(cat="Debug", def=false, com="Log class loading. Useful for determining the cause of 'mixin was loaded too early' issues. (You should always try adding Mixingasm first though.)")
    public static boolean logClassLoading;
    @ConfigBoolean(cat="Debug", def=true, com="Adds a command to dump mob spawn tables")
    public static boolean dumpSpawnTables;
    @ConfigBoolean(cat="Debug", def=false, com="Writes the camera coordinates and world seed of each screenshot into `.minecraft/dtools/wherewasi/<world_name>.hjson`")
    public static boolean logScreenshotPosition;
    
    @ConfigBoolean(cat="Tweaks", def=true, com="Increase fly speed while sprinting. From Et Futurum Requiem.\nCompatibility note: Will be disabled if Et Futurum Requiem is present.")
    public static boolean sprintFlying;
    @ConfigBoolean(cat="Tweaks", def=true, com="Backports the doWeatherCycle game rule. From Et Futurum Requiem.\nCompatibility note: Will be disabled if Et Futurum Requiem is present.")
    public static boolean doWeatherCycle;
    @ConfigBoolean(cat="Tweaks", def=true, com="Adds a button to the world creation GUI for convenient setup of test worlds. This can be customized in `devsetup.ini`.")
    public static boolean devWorldSetup;
    @ConfigBoolean(cat="Tweaks", def=true, com="Switch gamemode between survival and creative when pressing F3+F4.\nCompatibility note: Will be disabled if Et Futurum Requiem is present.")
    public static boolean gamemodeSwitcher;
    @ConfigBoolean(cat="Tweaks", def=false, com="Adds keyboard combinations to quickly delete worlds: Pressing Alt+D in the world selection GUI will delete only the world region data, Shift+D will delete the world completely forever (a long time!)")
    public static boolean worldDeleter;
    @ConfigBoolean(cat="Tweaks", def=false, com="Force skins to get reloaded when relogging.")
    public static boolean forceReloadSkins;
    @ConfigBoolean(cat="Tweaks.thaumcraft", def=true, com="Show aspects for all items without having to scan them if player is in creative mode.")
    public static boolean unlockAllAspects;
    
    private static Configuration config;
    private static File oldConfigFile = new File(Launch.minecraftHome, "config/" + MODID + ".cfg");
    private static File configFile = new File(Launch.minecraftHome, "config/" + MODID + "/" + MODID + ".cfg");
    
    private static AnnotationBasedConfigHelper configHelper = new AnnotationBasedConfigHelper(Config.class, LOGGER);

    
    public static void reload() {
        if(oldConfigFile.isFile() && !configFile.isFile()) {
            LOGGER.info("Migrating config from " + oldConfigFile + " to " + configFile);
            oldConfigFile.renameTo(configFile);
        }
        
        config = new Configuration(configFile);
        
        config.load();
        configHelper.loadFields(config);
        
        config.addCustomCategoryComment("_General", "Features marked as (Cheat feature) are only available in creative mode, or in a dev environment.");
        
        config.addCustomCategoryComment("Automation", "In addition to these settings, there are some tweaks that are activated via JVM flags:\n" +
                "* -Ddtools.launchWorld=WORLD : Automatically loads the world with the folder name WORLD once the main menu is reached. WORLD can be left blank, in this case the most recently played world will be loaded. Hold down shift when the main menu appears to cancel the automatic loading.\n" +
                "* -Ddtools.launchMinimized : Launch Minecraft minimized. Only implemented on Windows at the moment.\n" +
                "* -Ddtools.launchOnDesktop=NUMBER : Launch Minecraft on the virtual desktop with ordinal NUMBER. Only implemented on Linux at the moment. xprop has to be installed for it to work. Only tested on Openbox.");
        
        ConfigDumper.dumpConfigIfEnabled(config, MODID);
        
        if(config.hasChanged()) {
            config.save();
        }
    }
    
    public static void save() {
        if(config == null) {
            LOGGER.error("Failed to save config because it hasn't been loaded yet");
        }
        configHelper.saveFields(config);
    }
    
}
