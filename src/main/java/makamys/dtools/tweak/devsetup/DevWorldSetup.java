package makamys.dtools.tweak.devsetup;

import java.util.Map;
import java.util.WeakHashMap;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import makamys.dtools.listener.IFMLEventListener;
import makamys.dtools.tweak.devsetup.gui.GuiButtonDevSetup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.MinecraftForge;

public class DevWorldSetup implements IFMLEventListener {

    public static DevWorldSetup instance;
    
    @SideOnly(Side.CLIENT)
    Map<GuiScreen, GuiButtonDevSetup> buttonMap = new WeakHashMap<>();
    
    @SideOnly(Side.CLIENT)
    public static final int buttonId = -916057709;
    
    @Override
    public void onInit(FMLInitializationEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
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
    
    public static class Config {
        // Base preset
        public ConfigItem setGamemodeCreative = new ConfigItem();
        public ConfigItem enableCheats = new ConfigItem();
        public ConfigItem disableDoDaylightCycle = new ConfigItem();
        public ConfigItem disableDoWeatherCycle = new ConfigItem();
        // Extra preset
        public ConfigItem disableDoMobSpawning = new ConfigItem();
        public ConfigItem disableStructures = new ConfigItem();
        public ConfigItem superflat = new ConfigItem();
        
        public int type;
        
        public void toggle(boolean shiftDown) {
            if(!shiftDown) {
                type = type == 0 ? 1 : 0;
            } else {
                type = type == 2 ? 0 : 2;
            }
            setType(type);
        }
        
        private void setType(int type) {
            setGamemodeCreative.setValue(type >= 1);
            enableCheats.setValue(type >= 1);
            disableDoDaylightCycle.setValue(type >= 1);
            disableDoWeatherCycle.setValue(type >= 1);
            
            disableDoMobSpawning.setValue(type >= 2);
            disableStructures.setValue(type >= 2);
            superflat.setValue(type >= 2);
        }
        
        public static class ConfigItem {
            private boolean value = false;
            private boolean guiValue = false;
            private boolean isSynced = true;
            
            private void setValue(boolean newVal) {
                value = newVal;
            }

            public void update(boolean newVal) {
                guiValue = newVal;
                if(isSynced) {
                    value = guiValue;
                }
            }

            public boolean isEffectivelyEnabled() {
                return value || guiValue;
            }
            
            public boolean isEnabled() {
                return value;
            }

            public void setSynced(boolean synced) {
                this.isSynced = synced;
            }
            
            public boolean isSynced() {
                return isSynced;
            }
        }
    }

}
