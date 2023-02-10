package makamys.dtools.tweak.automation;

import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;

import static makamys.dtools.DTools.LOGGER;
import static makamys.dtools.tweak.automation.AutoWorldLoad.PauseStatus.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import makamys.dtools.Config;
import makamys.dtools.JVMArgs;
import makamys.dtools.listener.IFMLEventListener;
import makamys.dtools.util.MCUtil;

public class AutoWorldLoad implements IFMLEventListener {
	
	public static AutoWorldLoad instance;
	
	public enum PauseStatus { NONE, ENQUEUED, PAUSED, FINISHED }
    
    private int timesSeenMainMenu = 0;
    private int timesWentIngame = 0;
    
    private PauseStatus pauseStatus = NONE;
    private int pauseWait = 0;
    
    private boolean guiChanged = false;
    
    private Minecraft mc;
    
    private boolean cancelled;
    
    @Override
    public void onInit(FMLInitializationEvent event) {
    	mc = Minecraft.getMinecraft();
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }
    
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event){
        if(event.phase == TickEvent.Phase.START) {
        	if(pauseStatus == ENQUEUED && pauseWait++ >= Config.autoLoadPauseWaitLength) {
        		mc.displayInGameMenu();
        		pauseStatus = PAUSED;
        	} else if(pauseStatus == PAUSED && Display.isActive()) {
        		mc.displayGuiScreen(null);
        		pauseStatus = FINISHED;
        	}
        	
        	if(guiChanged) {
            	onGuiChanged(mc.currentScreen);
        	}
        }
    }
    
    @SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) {
    	guiChanged = true;
    }
    
    private void onGuiChanged(GuiScreen gui) {
    	if(JVMArgs.LAUNCH_WORLD != null && !cancelled) {
	    	if(gui instanceof GuiMainMenu && timesSeenMainMenu++ == 0) {
	    	    if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
	                cancelled = true;
	                LOGGER.info("Cancelled world auto-load because the Shift key was held down.");
	                mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("random.break"), 1.0F));
	            } else {
	                tryToLoadWorld();
	            }
	    	} else if(gui instanceof GuiMainMenu) {
	    		LOGGER.debug("times seen main menu: " + timesSeenMainMenu);
	    	}
	    	if(gui == null && timesWentIngame++ == 0 && !Display.isActive()) {
	    		if(Config.autoLoadDingOnWorldEntry) {
	    			mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("random.orb"), 1.0F));
	    		}
	    		if(Config.autoLoadPauseOnWorldEntry) {
	    			pauseStatus = ENQUEUED;
	    		}
	    	}
    	}
    }
    
    private void tryToLoadWorld() {
		if(JVMArgs.LAUNCH_WORLD != null) {
			MCUtil.tryToLoadWorld(JVMArgs.LAUNCH_WORLD);
		}
    }
}
