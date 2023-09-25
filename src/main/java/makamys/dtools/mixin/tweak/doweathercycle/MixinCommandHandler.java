package makamys.dtools.mixin.tweak.doweathercycle;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import makamys.dtools.tweak.doweathercycle.DoWeatherCycleHelper;
import net.minecraft.command.CommandHandler;

@Mixin(CommandHandler.class)
public class MixinCommandHandler {
	
	@Inject(method = "executeCommand", at = @At("HEAD"))
	private void preExecuteCommand(CallbackInfoReturnable<Integer> ci) {
		DoWeatherCycleHelper.INSTANCE.isCommandInProgress = true;
	}
	
	@Inject(method = "executeCommand", at = @At("RETURN"))
	private void postExecuteCommand(CallbackInfoReturnable<Integer> ci) {
		DoWeatherCycleHelper.INSTANCE.isCommandInProgress = false;
	}
	
}
