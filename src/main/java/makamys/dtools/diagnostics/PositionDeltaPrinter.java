package makamys.dtools.diagnostics;

import java.util.Locale;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import makamys.dtools.DTools;
import makamys.dtools.listener.IFMLEventListener;

public class PositionDeltaPrinter implements IFMLEventListener {

    public static PositionDeltaPrinter instance;
    
    private double lastX;
    private double lastY;
    private double lastZ;
    
    
    public PositionDeltaPrinter() {
        FMLCommonHandler.instance().bus().register(this);
    }
    
    @SubscribeEvent
    public void onTick(PlayerTickEvent event) {
        if(event.side == Side.SERVER && event.phase == Phase.START) {
            PlayerTickEvent pte = (PlayerTickEvent)event;
            double x = pte.player.posX;
            double y = pte.player.posY;
            double z = pte.player.posZ;
            DTools.LOGGER.info(String.format(Locale.ROOT, "delta XYZ:%12.4f%12.4f%12.4f", x-lastX, y-lastY, z-lastZ));
            lastX = x;
            lastY = y;
            lastZ = z;
        }
    }

}
