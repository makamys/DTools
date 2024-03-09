package makamys.dtools.tweak;

import org.apache.commons.lang3.StringUtils;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import makamys.dtools.Config;
import makamys.dtools.listener.IFMLEventListener;
import net.minecraft.world.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

public class RandomTickSpeed implements IFMLEventListener {
    public static RandomTickSpeed instance;
    
    private static final String GAMERULE_NAME = "randomTickSpeed";
    private static final String DEFAULT_VALUE = "3";
    
    @Override
    public void onInit(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void loadWorldEvent(WorldEvent.Load event) {
        if (Config.randomTickSpeed && !event.world.isRemote && !event.world.getGameRules().hasRule(GAMERULE_NAME)) {
            event.world.getGameRules().addGameRule(GAMERULE_NAME, DEFAULT_VALUE);
        }
    }

    public int getRandomTickSpeed(GameRules gameRulesInstance) {
        return Integer.parseInt(StringUtils.defaultIfEmpty(gameRulesInstance.getGameRuleStringValue(GAMERULE_NAME), DEFAULT_VALUE));
    }
}
