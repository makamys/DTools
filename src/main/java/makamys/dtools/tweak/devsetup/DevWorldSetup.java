package makamys.dtools.tweak.devsetup;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.WeakHashMap;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import makamys.dtools.listener.IFMLEventListener;
import makamys.dtools.tweak.devsetup.gui.GuiButtonDevSetup;
import makamys.mclib.updatecheck.ConfigUCL;
import makamys.mclib.updatecheck.gui.GuiButtonUpdates;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.MinecraftForge;

public class DevWorldSetup implements IFMLEventListener {

    public static DevWorldSetup instance;
    
    @SideOnly(Side.CLIENT)
    Map<GuiScreen, GuiButtonDevSetup> buttonMap = new WeakHashMap<>();
    
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
            event.buttonList.add(buttonMap.computeIfAbsent(event.gui, x -> new GuiButtonDevSetup(1337, event.gui.width / 2 + 75 + 8, 115, 40, 20, (GuiCreateWorld)event.gui, worldTypeButton)));
        }
        /*if(event.gui instanceof GuiMainMenu) {
            ConfigUCL.reload();
            if(ConfigUCL.showUpdatesButton) {
                String url = null;
                try {
                    url = updatesFile.toURI().toURL().toString();
                } catch (MalformedURLException e) {
                    url = "";
                    e.printStackTrace();
                }
                int buttonX = ConfigUCL.updatesButtonX + (ConfigUCL.updatesButtonAbsolutePos ? 0 : event.gui.width / 2);
                int buttonY = ConfigUCL.updatesButtonY + (ConfigUCL.updatesButtonAbsolutePos ? 0 : event.gui.height / 4);
                updatesButton = new GuiButtonUpdates(UPDATES_BUTTON_ID, buttonX, buttonY, 20, 20, updateCount, url);
                event.buttonList.add(updatesButton);
            }
        } else {
            updatesButton = null;
        }*/
    }
    
    public static class Config {
        // Base preset
        public MutableTrilean setGamemodeCreative = new MutableTrilean(Trilean.UNSET);
        public MutableTrilean enableCheats = new MutableTrilean(Trilean.UNSET);
        public MutableTrilean disableDoDaylightCycle = new MutableTrilean(Trilean.UNSET);
        public MutableTrilean disableDoWeatherCycle = new MutableTrilean(Trilean.UNSET);
        // Extra preset
        public MutableTrilean disableDoMobSpawning = new MutableTrilean(Trilean.UNSET);
        public MutableTrilean disableStructures = new MutableTrilean(Trilean.UNSET);
        public MutableTrilean superflat = new MutableTrilean(Trilean.UNSET);
        
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
            setValue(setGamemodeCreative, type >= 1);
            setValue(enableCheats, type >= 1);
            setValue(disableDoDaylightCycle, type >= 1);
            setValue(disableDoWeatherCycle, type >= 1);
            
            setValue(disableDoMobSpawning, type >= 2);
            setValue(disableStructures, type >= 2);
            setValue(superflat, type >= 2);
        }
        
        private void setValue(MutableTrilean tri, boolean value) {
            if(value) {
                tri.set(Trilean.TRUE);
            } else {
                if(tri.get() == Trilean.TRUE) {
                    // Don't erase dirty configs
                    tri.set(Trilean.FALSE);
                }
            }
        }

        public void update(MutableTrilean tri, boolean value) {
            if(value) {
                if(!tri.isTrue()) {
                    tri.set(Trilean.TRUE);
                }
            } else if(tri.get() != Trilean.FALSE){
                tri.set(Trilean.UNSET);
            }
        }
    }
    
    public enum Trilean {
        UNSET, FALSE, TRUE;
        
        public boolean isTrue() {
            return this == TRUE;
        }
    }
    
    public static class MutableTrilean {
        private Trilean value;
        
        public MutableTrilean(Trilean value) {
            this.value = value;
        }
        
        public Trilean get() {
            return value;
        }
        
        public void set(boolean val) {
            value = val ? Trilean.TRUE : Trilean.FALSE;
        }
        
        public void set(Trilean val) {
            value = val;
        }
        
        public boolean isTrue() {
            return value.isTrue();
        }
        
        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

}
