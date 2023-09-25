package makamys.dtools.tweak.doweathercycle;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import makamys.dtools.Config;
import makamys.dtools.listener.IFMLEventListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

// DoWeatherCycleHelper is kept as a separate class to stay closer to EFR's code
public class DoWeatherCycle implements IFMLEventListener {
    public static DoWeatherCycle instance;
    
    @Override
    public void onInit(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void loadWorldEvent(WorldEvent.Load event)
    {
        if (Config.doWeatherCycle && !event.world.isRemote && !event.world.getGameRules().hasRule("doWeatherCycle")) {
            event.world.getGameRules().addGameRule("doWeatherCycle", "true");
        }
    }
    
    @SubscribeEvent
    public void onPreWorldTick(TickEvent.WorldTickEvent e) {
        if (Config.doWeatherCycle && e.phase == TickEvent.Phase.START && e.side == Side.SERVER) {
            DoWeatherCycleHelper.INSTANCE.isWorldTickInProgress = true;
            DoWeatherCycleHelper.INSTANCE.isCommandInProgress = false;
        }
    }
    
    @SubscribeEvent
    public void onPostWorldTick(TickEvent.WorldTickEvent e) {
        if (Config.doWeatherCycle && e.phase == TickEvent.Phase.END && e.side == Side.SERVER) {
            DoWeatherCycleHelper.INSTANCE.isWorldTickInProgress = false;
        }
    }
}
