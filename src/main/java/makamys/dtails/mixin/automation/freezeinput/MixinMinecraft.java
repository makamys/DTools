package makamys.dtails.mixin.automation.freezeinput;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import makamys.dtails.automation.InputFreezer;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
abstract class MixinMinecraft {
    
    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;next()Z"))
    public boolean redirectKeyboardNext() {
         return InputFreezer.instance.redirectKeyboardNext();
    }
    
    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;next()Z"))
    public boolean redirectMouseNext() {
         return InputFreezer.instance.redirectMouseNext();
    }
    
}
