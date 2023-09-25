package makamys.dtools.tweak.doweathercycle;

import net.minecraft.world.GameRules;

public class DoWeatherCycleHelper {
	
	public static final DoWeatherCycleHelper INSTANCE = new DoWeatherCycleHelper();
	
	public boolean isWorldTickInProgress;
	public boolean isCommandInProgress;
	
	public boolean canCancelWeatherChange(GameRules gameRules) {
		return isWorldTickInProgress && !isCommandInProgress && !gameRules.getGameRuleBooleanValue("doWeatherCycle");
	}
	
}
