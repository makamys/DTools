package makamys.dtools.diagnostics;

import static makamys.dtools.DTools.LOGGER;

import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import makamys.dtools.listener.IFMLEventListener;
import makamys.dtools.util.OSUtil;

public class ServerRunTimePrinter implements IFMLEventListener {
    
    public static ServerRunTimePrinter instance;
    
    private static final String START_COMMAND = System.getProperty("dtools.serverRunTimePrinter.startCommand");
    private static final String STOP_COMMAND = System.getProperty("dtools.serverRunTimePrinter.stopCommand");
    
    private static long startTime;
    
    @Override
    public void onServerStarted(FMLServerStartedEvent event) {
        startTime = System.nanoTime();
        
        if(START_COMMAND != null) {
            OSUtil.runCommand(START_COMMAND);
        }
    }
    
    @Override
    public void onServerStopped(FMLServerStoppedEvent event) {
        long runTime = System.nanoTime() - startTime;
        LOGGER.info("Server ran for " + runTime / 1000000000f + "s");
        
        if(STOP_COMMAND != null) {
            OSUtil.runCommand(STOP_COMMAND);
        }
    }
}
