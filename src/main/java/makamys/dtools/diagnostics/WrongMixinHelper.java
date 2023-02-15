package makamys.dtools.diagnostics;

import static makamys.dtools.DTools.LOGGER;

/**
 * <p>This class lets you produce a useless crash log which doesn't contain the mixin error on demand.
 * <p>To use it:
 * <p>1. Enable the JVM arg
 * <p>2. Open chat. This will cause the class with the wrong mixin to load inside a try-catch block.
 * <p>3. Open the pause menu. This will cause the class to load without a try-catch block, causing a crash with a useless crash log.
 */
public class WrongMixinHelper {

    private static final boolean IS_ENABLED = Boolean.parseBoolean(System.getProperty("dtools.enableWrongMixin", "false"));
    
    private static boolean hasRun = false;
    
    public static void init() {
        if(hasRun) return;
        hasRun = true;
        
        loadWrongMixin();
    }
    
    private static void loadWrongMixin() {
        try {
            LOGGER.info("Loading wrong GuiAchievements mixin!");
            Class.forName("net.minecraft.client.gui.achievement.GuiAchievements");
            LOGGER.warn("That succeeded somehow, this is not supposed to happen");
        } catch(Throwable t) {
            LOGGER.info("Got throwable:");
            t.printStackTrace();
        }
    }

    public static boolean isEnabled() {
        return IS_ENABLED;
    }

}
