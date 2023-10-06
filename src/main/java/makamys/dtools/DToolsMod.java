package makamys.dtools;

import static makamys.dtools.DTools.registerListener;

import java.util.function.Consumer;

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
import makamys.dtools.diagnostics.DumpSpawnTables;
import makamys.dtools.diagnostics.ExtraRAMInfo;
import makamys.dtools.diagnostics.FMLProxyPacketCrasher;
import makamys.dtools.diagnostics.FrameProfiler;
import makamys.dtools.diagnostics.GLDebugLogger;
import makamys.dtools.diagnostics.MethodProfiler;
import makamys.dtools.diagnostics.PositionDeltaPrinter;
import makamys.dtools.diagnostics.ServerRunTimePrinter;
import makamys.dtools.diagnostics.SetBlockProfiler;
import makamys.dtools.diagnostics.StartupTimePrinter;
import makamys.dtools.diagnostics.WAIAA;
import makamys.dtools.diagnostics.Wireframe;
import makamys.dtools.diagnostics.thaumcraft.ThaumcraftTools;
import makamys.dtools.listener.IFMLEventListener;
import makamys.dtools.tweak.GamemodeSwitcher;
import makamys.dtools.tweak.HudlessF3;
import makamys.dtools.tweak.SkinReloader;
import makamys.dtools.tweak.automation.AutoChunkPregenGui;
import makamys.dtools.tweak.automation.AutoWorldLoad;
import makamys.dtools.tweak.automation.CommandRunner;
import makamys.dtools.tweak.crashhandler.Crasher;
import makamys.dtools.tweak.devsetup.DevWorldSetup;
import makamys.dtools.tweak.doweathercycle.DoWeatherCycle;
import makamys.dtools.util.KeyboardUtil;
import makamys.mclib.core.MCLib;
import makamys.mclib.core.MCLibModules;
import net.minecraftforge.client.ClientCommandHandler;

@Mod(modid = DTools.MODID, version = DTools.VERSION)
public class DToolsMod
{
    
    @EventHandler
    public void onConstruction(FMLConstructionEvent event) {
        MCLib.init();
        
        Config.reload();
        
        if(Config.crasher) {
            registerListener(Crasher.instance = new Crasher());
        }
        if(Config.serverRunTimePrinter) {
            registerListener(ServerRunTimePrinter.instance = new ServerRunTimePrinter());
        }
        if(Config.dumpSpawnTables) {
            registerListener(DumpSpawnTables.instance = new DumpSpawnTables());
        }
        if(Config.doWeatherCycle) {
            registerListener(DoWeatherCycle.instance = new DoWeatherCycle());
        }
        if(event.getSide() == Side.CLIENT) {
            registerListener(FrameProfiler.instance = new FrameProfiler());
            if(JVMArgs.LAUNCH_WORLD != null) {
            	registerListener(AutoWorldLoad.instance = new AutoWorldLoad());
            }
            if(JVMArgs.RUN_COMMAND_MAIN_MENU != null) {
                registerListener(CommandRunner.instance = new CommandRunner());
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
            if(FMLProxyPacketCrasher.isActive()) {
                registerListener(FMLProxyPacketCrasher.instance = new FMLProxyPacketCrasher());
            }
            if(Config.devWorldSetup) {
                registerListener(DevWorldSetup.instance = new DevWorldSetup());
            }
            if(Config.gamemodeSwitcher && !Compat.isEtFuturumRequiemPresent()) {
                registerListener(GamemodeSwitcher.instance = new GamemodeSwitcher());
            }
            if(Config.setBlockProfiler) {
                registerListener(SetBlockProfiler.instance = new SetBlockProfiler());
            }
            if(Config.forceReloadSkins) {
                registerListener(SkinReloader.instance = new SkinReloader());
            }
            if(Config.autoInitializeChunkPregenGui) {
                registerListener(AutoChunkPregenGui.instance = new AutoChunkPregenGui());
            }
            if(Config.logGlDebug) {
                registerListener(GLDebugLogger.instance = new GLDebugLogger());
            }
            if(Config.hudlessF3) {
                registerListener(HudlessF3.instance = new HudlessF3());
            }
            if(Config.printStartupTime && !Compat.isArchaicFixPresent()) {
                registerListener(StartupTimePrinter.instance = new StartupTimePrinter());
            }
        }
    }
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MCLibModules.updateCheckAPI.submitModTask(DTools.MODID, "@UPDATE_URL@");
        
        forEachFMLEventListener(l -> l.onPreInit(event));
    }
    
    private void forEachFMLEventListener(Consumer<IFMLEventListener> callback) {
        DTools.forEachListener(IFMLEventListener.class, l -> callback.accept(l));
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
        
        forEachFMLEventListener(l -> l.onInit(event));
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        forEachFMLEventListener(l -> l.onPostInit(event));
    }
    
    @EventHandler
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        Config.reload();
        forEachFMLEventListener(l -> l.onServerAboutToStart(event));
    }
    
    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        forEachFMLEventListener(l -> l.onServerStarting(event));
    }
    
    @EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        forEachFMLEventListener(l -> l.onServerStarted(event));
    }
    
    @EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        forEachFMLEventListener(l -> l.onServerStopping(event));
    }
    
    @EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        forEachFMLEventListener(l -> l.onServerStopped(event));
    }
    
    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        KeyboardUtil.tick();
    }
}
