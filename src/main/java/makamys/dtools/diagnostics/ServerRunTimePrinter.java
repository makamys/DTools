package makamys.dtools.diagnostics;

import static makamys.dtools.DTools.LOGGER;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import makamys.dtools.listener.IFMLEventListener;

public class ServerRunTimePrinter implements IFMLEventListener {
    
    public static ServerRunTimePrinter instance;
    
    private static final String START_COMMAND = System.getProperty("dtools.serverRunTimePrinter.startCommand");
    private static final String STOP_COMMAND = System.getProperty("dtools.serverRunTimePrinter.stopCommand");
    
    private static int cachedPid = -1;
    
    private static long startTime;
    
    @Override
    public void onServerStarted(FMLServerStartedEvent event) {
        startTime = System.nanoTime();
        
        if(START_COMMAND != null) {
            try {
                Runtime.getRuntime().exec(START_COMMAND.replaceAll("\\{PID\\}", ""+getPid()));
    	    } catch (IOException e) {
    	        e.printStackTrace();
    	    }
        }
    }
    
    @Override
    public void onServerStopped(FMLServerStoppedEvent event) {
        long runTime = System.nanoTime() - startTime;
        LOGGER.info("Server ran for " + runTime / 1000000000f + "s");
        
        if(STOP_COMMAND != null) {
            try {
                Runtime.getRuntime().exec(STOP_COMMAND.replaceAll("\\{PID\\}", ""+getPid()));
    	    } catch (IOException e) {
    	        e.printStackTrace();
    	    }
        }
    }

    private int getPid() {
    	if(cachedPid == -1) {
    	    try {
    	        cachedPid = Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    	    } catch(Exception e) {
    	        e.printStackTrace();
    	    }
    	}
    	return cachedPid;
    }
}
