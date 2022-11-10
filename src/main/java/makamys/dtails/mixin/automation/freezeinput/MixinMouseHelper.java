package makamys.dtails.mixin.automation.freezeinput;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import makamys.dtails.automation.InputFreezer;
import net.minecraft.util.MouseHelper;

@Mixin(MouseHelper.class)
abstract class MixinMouseHelper {
    
    @Inject(method = "mouseXYChange", at = @At("RETURN"))
    private void postMouseXYChange(CallbackInfo ci) {
        InputFreezer.instance.postMouseXYChange((MouseHelper)(Object)this);
    }
    
}
