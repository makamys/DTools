package makamys.coretweaks;

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
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import makamys.coretweaks.command.CoreTweaksCommand;
import makamys.coretweaks.diagnostics.FrameProfiler;
import makamys.coretweaks.diagnostics.MethodProfiler;
import makamys.coretweaks.diagnostics.ServerRunTimePrinter;
import makamys.coretweaks.diagnostics.WAIAA;
import makamys.coretweaks.tweak.automation.AutoWorldLoad;
import makamys.coretweaks.tweak.crashhandler.Crasher;
import makamys.coretweaks.util.KeyboardUtil;
import makamys.mclib.core.MCLib;
import makamys.mclib.core.MCLibModules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.ClientCommandHandler;

@Mod(modid = CoreTweaks.MODID, version = CoreTweaks.VERSION)
public class CoreTweaksMod
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
            }}, "CoreTweaks shutdown thread"));
        
        if(Config.crasher) {
            registerListener(Crasher.instance = new Crasher());
        }
        if(Config.serverRunTimePrinter) {
            registerListener(ServerRunTimePrinter.instance = new ServerRunTimePrinter());
        }
        registerListener(FrameProfiler.instance = new FrameProfiler());
        if(JVMArgs.LAUNCH_WORLD != null) {
        	registerListener(AutoWorldLoad.instance = new AutoWorldLoad());
        }
    }
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MCLibModules.updateCheckAPI.submitModTask(CoreTweaks.MODID, "@UPDATE_URL@");
        
        listeners.forEach(l -> l.onPreInit(event));
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(this);
        
        if(Config.coreTweaksCommand) {
            ClientCommandHandler.instance.registerCommand(new CoreTweaksCommand());
            WAIAA.instance = new WAIAA();
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
