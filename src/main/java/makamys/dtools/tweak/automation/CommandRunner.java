package makamys.dtools.tweak.automation;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import makamys.dtools.JVMArgs;
import makamys.dtools.listener.IFMLEventListener;
import makamys.dtools.util.OSUtil;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;

public class CommandRunner implements IFMLEventListener {
    public static CommandRunner instance = new CommandRunner();
    
    private static final boolean quit = Boolean.parseBoolean(System.getProperty("dtools.runCommand.mainMenu.quit", "false"));
    
    private boolean seenMainMenu;
    
    @Override
    public void onInit(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if(event.gui != null && event.gui instanceof GuiMainMenu && !seenMainMenu) {
            seenMainMenu = true;
            OSUtil.runCommand(JVMArgs.RUN_COMMAND_MAIN_MENU);
            if(quit) {
                FMLCommonHandler.instance().exitJava(0, false);
            }
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }
}
