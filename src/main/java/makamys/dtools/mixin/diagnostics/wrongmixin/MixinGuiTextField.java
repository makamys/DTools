package makamys.dtools.mixin.diagnostics.wrongmixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import makamys.dtools.diagnostics.WrongMixinHelper;
import net.minecraft.client.gui.GuiTextField;

@Mixin(GuiTextField.class)
public class MixinGuiTextField {
    
    @Inject(method = "<init>*", at = @At("RETURN"))
    private void preRenderWorld(CallbackInfo ci) {
        WrongMixinHelper.init();
    }
}
