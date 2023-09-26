package makamys.dtools.tweak;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import makamys.dtools.Compat;
import makamys.dtools.Config;
import makamys.dtools.command.DToolsCommand;
import makamys.dtools.command.ISubCommand;
import makamys.dtools.listener.IFMLEventListener;
import makamys.dtools.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;

public class HudlessF3 implements IFMLEventListener {
    public static HudlessF3 instance;
    
    private Minecraft mc;
    
    public HudlessF3() {
        DToolsCommand.registerSubCommand("hudlessF3", new ToggleHudlessF3SubCommand());
    }
    
    @Override
    public void onInit(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        mc = Minecraft.getMinecraft();
    }
    
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if(!Config.hudlessF3StartEnabled) return;
        switch(event.type) {
        case DEBUG:
            if(Compat.isSamplerPresent()) {
                int bottom = event.resolution.getScaledHeight();
                int top = bottom - 90;
                int left = 0;
                int right = event.resolution.getScaledWidth();
                GuiHelper.drawRectangle(left, top, right - left, bottom - top, 0);
            }
            break;
        case HOTBAR:
            if(this.mc.gameSettings.showDebugInfo) {
                event.setCanceled(true);
            }
            break;
        default:
            break;
        }
    }
    
    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if(!Config.hudlessF3StartEnabled) return;
        if(this.mc.gameSettings.showDebugInfo) {
            event.setCanceled(true);
        }
    }
    
    private static class ToggleHudlessF3SubCommand implements ISubCommand {   
        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            Config.hudlessF3StartEnabled = !Config.hudlessF3StartEnabled;
            Config.save();
        }
    }
    
}
