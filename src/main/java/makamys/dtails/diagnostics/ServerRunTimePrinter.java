package makamys.dtails.diagnostics;

import static makamys.dtails.DTails.LOGGER;

import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import makamys.dtails.IModEventListener;

public class ServerRunTimePrinter implements IModEventListener {
    
    public static IModEventListener instance;
    
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
