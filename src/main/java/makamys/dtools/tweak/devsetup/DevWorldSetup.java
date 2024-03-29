package makamys.dtools.tweak.devsetup;

import java.util.Map;
import java.util.WeakHashMap;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import makamys.dtools.listener.IFMLEventListener;
import makamys.dtools.tweak.devsetup.Config.ConfigItem;
import makamys.dtools.tweak.devsetup.gui.GuiButtonDevSetup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.MinecraftForge;

public class DevWorldSetup implements IFMLEventListener {

    public static DevWorldSetup instance;
    
    @SideOnly(Side.CLIENT)
    Map<GuiScreen, GuiButtonDevSetup> buttonMap = new WeakHashMap<>();
    
    @SideOnly(Side.CLIENT)
    Map<String, Config> configMap = new WeakHashMap<>();
    
    @SideOnly(Side.CLIENT)
    public static final int buttonId = -916057709;
    
    @Override
    public void onInit(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }
    
    @Override
    public void onServerStarting(FMLServerStartingEvent event) {
        if(event.getSide() == Side.CLIENT) {
            ISaveFormat saveLoader = Minecraft.getMinecraft().getSaveLoader();
            WorldInfo wi = saveLoader.getWorldInfo(event.getServer().getFolderName());
            if(!wi.isInitialized()) {
                Config config = configMap.get(event.getServer().getFolderName());
                if(config != null) {
                    applyConfig(config, event.getServer());
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    private void applyConfig(Config config, MinecraftServer server) {
        for(ConfigItem item : config.items) {
            if(item.isEnabled()) {
                item.applyChange(server);
            }
        }
    }
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onConnectToServer(ClientConnectedToServerEvent event) {
        buttonMap.clear();
        configMap.clear();
    }
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onGui(InitGuiEvent.Post event) {
        if(event.gui instanceof GuiCreateWorld) {
            GuiButton worldTypeButton = (GuiButton)event.buttonList.stream().filter(b -> b instanceof GuiButton && ((GuiButton)b).id == 5).findFirst().orElseGet(() -> null);
            GuiButtonDevSetup button = buttonMap.computeIfAbsent(event.gui, x -> new GuiButtonDevSetup(buttonId, event.gui.width / 2 + 75 + 8, 115, 40, 20, (GuiCreateWorld)event.gui));
            button.setWorldTypeButton(worldTypeButton);
            event.buttonList.add(button);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void preLaunchIntegratedServer(GuiCreateWorld gui, String folderName) {
        GuiButtonDevSetup button = buttonMap.get(gui);
        if(button != null) {
            configMap.put(folderName, button.config);
        }
    }

}
