package makamys.dtools.diagnostics;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import makamys.dtools.listener.IFMLEventListener;

public class FMLProxyPacketCrasher implements IFMLEventListener {

    public static FMLProxyPacketCrasher instance;
    
    private static final boolean IS_ENABLED = Boolean.parseBoolean(System.getProperty("dtools.enableFMLProxyPacketCrasher", "false"));
    
    @Override
    public void onPreInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(this);
    }
    
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        throw new RuntimeException("Test exception to invoke a crash in FMLProxyPacket#processPacket");
    }

    public static boolean isActive() {
        return IS_ENABLED;
    }

}
