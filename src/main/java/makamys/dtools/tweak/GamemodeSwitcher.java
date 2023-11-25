package makamys.dtools.tweak;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import makamys.dtools.listener.IFMLEventListener;
import net.minecraft.client.Minecraft;

public class GamemodeSwitcher implements IFMLEventListener {
    public static GamemodeSwitcher instance;
    
    private boolean wasF4Pressed;
    private Minecraft mc;
    
    @Override
    public void onInit(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(this);
        mc = Minecraft.getMinecraft();
    }
    
    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        if(mc.currentScreen == null) {
            boolean isF4Pressed = Keyboard.isKeyDown(Keyboard.KEY_F4);
            if(Keyboard.isKeyDown(Keyboard.KEY_F3) && isF4Pressed && !wasF4Pressed) {
                boolean creative = mc.thePlayer.capabilities.isCreativeMode;
                String newGamemode = creative ? "survival" : "creative";
                mc.thePlayer.sendChatMessage("/gamemode " + newGamemode);
                Minecraft.getMinecraft().gameSettings.showDebugInfo = false;
            }
            wasF4Pressed = isF4Pressed;
        }
    }
}
