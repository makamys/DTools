package makamys.dtools;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.relauncher.Side;
import makamys.dtools.automation.InputFreezer;
import makamys.dtools.command.DToolsCommand;
import makamys.dtools.diagnostics.ExtraRAMInfo;
import makamys.dtools.diagnostics.FrameProfiler;
import makamys.dtools.diagnostics.MethodProfiler;
import makamys.dtools.diagnostics.PositionDeltaPrinter;
import makamys.dtools.diagnostics.ServerRunTimePrinter;
import makamys.dtools.diagnostics.ThaumcraftTools;
import makamys.dtools.diagnostics.WAIAA;
import makamys.dtools.diagnostics.Wireframe;
import makamys.dtools.tweak.automation.AutoWorldLoad;
import makamys.dtools.tweak.crashhandler.Crasher;
import makamys.dtools.util.KeyboardUtil;
import makamys.mclib.core.MCLib;
import makamys.mclib.core.MCLibModules;
import net.minecraftforge.client.ClientCommandHandler;

@Mod(modid = DTools.MODID, version = DTools.VERSION)
public class DToolsMod
{
    private static List<IModEventListener> listeners = new ArrayList<>();
    
    @EventHandler
    public void onConstruction(FMLConstructionEvent event) {
        MCLib.init();
        
        Config.reload();
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                listeners.forEach(l -> l.onShutdown());
            }}, "DTools shutdown thread"));
        
        if(Config.crasher) {
            registerListener(Crasher.instance = new Crasher());
        }
        if(Config.serverRunTimePrinter) {
            registerListener(ServerRunTimePrinter.instance = new ServerRunTimePrinter());
        }
        if(event.getSide() == Side.CLIENT) {
            registerListener(FrameProfiler.instance = new FrameProfiler());
            if(JVMArgs.LAUNCH_WORLD != null) {
            	registerListener(AutoWorldLoad.instance = new AutoWorldLoad());
            }
            if(Config.wireframe) {
                registerListener(Wireframe.instance = new Wireframe());
            }
            if(Config.extraRamInfo) {
                registerListener(ExtraRAMInfo.instance = new ExtraRAMInfo());
            }
            if(Config.positionDeltaPrint) {
                registerListener(PositionDeltaPrinter.instance = new PositionDeltaPrinter());
            }
            if(Config.freezeInputKey) {
                registerListener(InputFreezer.instance = new InputFreezer());
            }
        }
    }
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MCLibModules.updateCheckAPI.submitModTask(DTools.MODID, "@UPDATE_URL@");
        
        listeners.forEach(l -> l.onPreInit(event));
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(this);
        
        if(Config.dtoolsCommand) {
            ClientCommandHandler.instance.registerCommand(new DToolsCommand());
            WAIAA.instance = new WAIAA();
            ThaumcraftTools.instance = new ThaumcraftTools();
        }
        if(MethodProfiler.isActive()) {
            FMLCommonHandler.instance().bus().register(MethodProfiler.instance);
        }
        
        listeners.forEach(l -> l.onInit(event));
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        listeners.forEach(l -> l.onPostInit(event));
    }
    
    @EventHandler
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        Config.reload();
        listeners.forEach(l -> l.onServerAboutToStart(event));
    }
    
    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        listeners.forEach(l -> l.onServerStarting(event));
    }
    
    @EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        listeners.forEach(l -> l.onServerStarted(event));
    }
    
    @EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        listeners.forEach(l -> l.onServerStopping(event));
    }
    
    @EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        listeners.forEach(l -> l.onServerStopped(event));
    }
    
    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        KeyboardUtil.tick();
    }
    
    public void registerListener(IModEventListener listener) {
        listeners.add(listener);
    }
}
