package makamys.dtools.diagnostics;

import static makamys.dtools.DTools.LOGGER;

import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import makamys.dtools.listener.IFMLEventListener;

public class ServerRunTimePrinter implements IFMLEventListener {
    
    public static ServerRunTimePrinter instance;
    
    private static long startTime;
    
    @Override
    public void onServerStarted(FMLServerStartedEvent event) {
        startTime = System.nanoTime();
    }
    
    @Override
    public void onServerStopped(FMLServerStoppedEvent event) {
        long runTime = System.nanoTime() - startTime;
        LOGGER.info("Server ran for " + runTime / 1000000000f + "s");
    }
}
