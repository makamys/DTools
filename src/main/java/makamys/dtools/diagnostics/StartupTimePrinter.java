package makamys.dtools.diagnostics;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import makamys.dtools.DTools;
import makamys.dtools.listener.IFMLEventListener;
import makamys.dtools.util.Util;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;

public class StartupTimePrinter implements IFMLEventListener {
    public static StartupTimePrinter instance;
    
    private static final boolean writeToFile = Boolean.parseBoolean(System.getProperty("dtools.startupTimePrinter.writeToFile", "false"));
    
    private boolean hasSeenMainMenu = false;
    
    @Override
    public void onInit(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onGui(GuiOpenEvent event) {
        if(event.gui instanceof GuiMainMenu && !hasSeenMainMenu) {
            hasSeenMainMenu = true;
            double theTime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0;
            DTools.LOGGER.info("Startup took " + theTime + " seconds.");
            
            if(writeToFile) {
                try (FileWriter fw = new FileWriter(Util.childFile(DTools.OUT_DIR, "startup-times.txt"), true)){
                    fw.write("" + theTime + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }
}
