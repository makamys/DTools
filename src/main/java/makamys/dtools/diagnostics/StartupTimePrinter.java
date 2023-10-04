package makamys.dtools.diagnostics;

import java.lang.management.ManagementFactory;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import makamys.dtools.DTools;
import makamys.dtools.listener.IFMLEventListener;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;

public class StartupTimePrinter implements IFMLEventListener {
    public static StartupTimePrinter instance;
    
    private boolean hasSeenMainMenu = false;
    
    @Override
    public void onInit(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onGui(GuiOpenEvent event) {
        if(event.gui instanceof GuiMainMenu && !hasSeenMainMenu) {
            hasSeenMainMenu = true;
            DTools.LOGGER.info("Startup took " + ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0 + " seconds.");
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }
}
