package makamys.dtails;

import static makamys.dtails.DTails.LOGGER;
import static makamys.dtails.DTails.MODID;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.EnumUtils;

import makamys.dtails.util.ConfigDumper;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;

public class Config {
    
    @ConfigBoolean(cat="Diagnostics", def=false, com="Automatically start frame profiler as soon as the game starts.")
    public static boolean frameProfilerStartEnabled;
    @ConfigBoolean(cat="Diagnostics", def=false, com="Insert hooks that lets the frame profiler profile various parts of frame rendering. If this is disabled, the frame profiler will only be able to show very limited information.")
    public static boolean frameProfilerHooks;
    @ConfigBoolean(cat="Diagnostics", def=false, com="Print render tick times to log periodically.")
    public static boolean frameProfilerPrint;
    
    @ConfigBoolean(cat="Tweaks", def=true, com="Pause some ticks after auto-loaded world is loaded.\\nDelaying the pausing can be useful because some initialization like chunk updates won't happen while the game is paused.")
    public static boolean autoLoadPauseOnWorldEntry;
    @ConfigBoolean(cat="Tweaks", def=true, com="Ding once auto-loaded world is loaded.")
    public static boolean autoLoadDingOnWorldEntry;
    @ConfigInt(cat="Tweaks", def=20, min=0, max=Integer.MAX_VALUE, com="How many ticks to wait before pausing an auto-loaded world.")
    public static int autoLoadPauseWaitLength;
    
    @ConfigBoolean(cat="Diagnostics", def=true, com="Enables the /dtails command, used to access various diagnostics. Invoke it in-game for additional information.")
    public static boolean dtailsCommand;
    @ConfigString(cat="Diagnostics", def="", com="Comma-separated list of methods to profile. The results will be written to ./dtails/out/profiler-<timestamp>.csv. Currently only the call count is measured. Method names have the syntax of `<canonical class name>.<method name>`, like `some.package.SomeClass.method`.")
    public static String profilerMethods;
    
    @ConfigBoolean(cat="Diagnostics", def=false, com="Creates a report of how long each step of startup loading took in ./dtails/out/fml_bar_profiler.csv.")
    public static boolean forgeBarProfiler;
    @ConfigBoolean(cat="Diagnostics", def=false, com="Enables debug feature that crashes the game when pressing certain key combinations.")
    public static boolean crasher;
    @ConfigBoolean(cat="Diagnostics", def=false, com="Prints server run time.")
    public static boolean serverRunTimePrinter;
    @ConfigBoolean(cat="Diagnostics", def=false, com="Render world in wireframe mode. Tip: If this is enabled when the game is started, you will be able to toggle it without restarting the game, only the world needs to be reloaded.")
    public static boolean wireframe;
    
    private static Configuration config;
    private static File configFile = new File(Launch.minecraftHome, "config/" + MODID + ".cfg");

    
    public static void reload() {
        config = new Configuration(configFile);
        
        config.load();
        loadFields(config);
        
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
    
    private static boolean loadFields(Configuration config) {
        boolean needReload = false;
        
        for(Field field : Config.class.getFields()) {
            if(!Modifier.isStatic(field.getModifiers())) continue;
            
            NeedsReload needsReload = null;
            ConfigBoolean configBoolean = null;
            ConfigInt configInt = null;
            ConfigEnum configEnum = null;
            ConfigStringList configStringList = null;
            ConfigString configString = null;
            
            for(Annotation an : field.getAnnotations()) {
                if(an instanceof NeedsReload) {
                    needsReload = (NeedsReload) an;
                } else if(an instanceof ConfigInt) {
                    configInt = (ConfigInt) an;
                } else if(an instanceof ConfigBoolean) {
                    configBoolean = (ConfigBoolean) an;
                } else if(an instanceof ConfigEnum) {
                    configEnum = (ConfigEnum) an;
                } else if(an instanceof ConfigStringList) {
                    configStringList = (ConfigStringList) an;
                }  else if(an instanceof ConfigString) {
                    configString = (ConfigString) an;
                }
            }
            
            if(configBoolean == null && configInt == null && configEnum == null && configStringList == null && configString == null) continue;
            
            Object currentValue = null;
            Object newValue = null;
            try {
                currentValue = field.get(null);
            } catch (Exception e) {
                LOGGER.error("Failed to get value of field " + field.getName());
                e.printStackTrace();
                continue;
            }
            
            if(configBoolean != null) {
                newValue = config.getBoolean(field.getName(), configBoolean.cat(), configBoolean.def(), configBoolean.com());
            } else if(configInt != null) {
                newValue = config.getInt(field.getName(), configInt.cat(), configInt.def(), configInt.min(), configInt.max(), configInt.com()); 
            } else if(configEnum != null) {
                boolean lowerCase = true;
                
                Class<? extends Enum> configClass = configEnum.clazz();
                Map<String, ? extends Enum> enumMap = EnumUtils.getEnumMap(configClass);
                String[] valuesStrUpper = (String[])enumMap.keySet().stream().toArray(String[]::new);
                String[] valuesStr = Arrays.stream(valuesStrUpper).map(s -> lowerCase ? s.toLowerCase() : s).toArray(String[]::new);
                
                // allow upgrading boolean to string list
                ConfigCategory cat = config.getCategory(configEnum.cat());
                Property oldProp = cat.get(field.getName());
                String oldVal = null;
                if(oldProp != null && oldProp.getType() != Type.STRING) {
                    oldVal = oldProp.getString();
                    cat.remove(field.getName());
                }
                
                String newValueStr = config.getString(field.getName(), configEnum.cat(),
                        lowerCase ? configEnum.def().toLowerCase() : configEnum.def().toUpperCase(), configEnum.com(), valuesStr);
                if(oldVal != null) {
                    newValueStr = oldVal;
                }
                if(!enumMap.containsKey(newValueStr.toUpperCase())) {
                    newValueStr = configEnum.def().toUpperCase();
                    if(lowerCase) {
                        newValueStr = newValueStr.toLowerCase();
                    }
                }
                newValue = enumMap.get(newValueStr.toUpperCase());
                
                Property newProp = cat.get(field.getName());
                if(!newProp.getString().equals(newValueStr)) {
                    newProp.set(newValueStr);
                }
            } else if(configStringList != null) {
                newValue = config.getStringList(field.getName(), configStringList.cat(), configStringList.def(), configStringList.com());
            } else if(configString != null) {
                newValue = config.getString(field.getName(), configString.cat(), configString.def(), configString.com());
            }
            
            if(needsReload != null && !newValue.equals(currentValue)) {
                needReload = true;
            }
            
            try {
                field.set(null, newValue);
            } catch (Exception e) {
                LOGGER.error("Failed to set value of field " + field.getName());
                e.printStackTrace();
            }
        }
        
        return needReload;
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface NeedsReload {

    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface ConfigBoolean {

        String cat();
        boolean def();
        String com() default "";

    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface ConfigInt {

        String cat();
        int min();
        int max();
        int def();
        String com() default "";

    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface ConfigString {

        String cat();
        String def();
        String com() default "";

    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface ConfigStringList {

        String cat();
        String[] def();
        String com() default "";

    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface ConfigEnum {

        String cat();
        String def();
        String com() default "";
        Class<? extends Enum> clazz();

    }
    
}
