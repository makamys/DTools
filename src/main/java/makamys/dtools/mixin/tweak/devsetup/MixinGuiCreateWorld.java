package makamys.dtools.mixin.tweak.devsetup;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import makamys.dtools.tweak.devsetup.DevWorldSetup;
import net.minecraft.client.gui.GuiCreateWorld;

@Mixin(GuiCreateWorld.class)
public class MixinGuiCreateWorld {
    
    @Shadow
    private String field_146336_i;
    
    @Inject(method = "actionPerformed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;launchIntegratedServer(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/world/WorldSettings;)V"))
    private void preLaunchIntegratedServer(CallbackInfo ci) {
        DevWorldSetup.instance.preLaunchIntegratedServer((GuiCreateWorld)(Object)this, field_146336_i);
    }
    
}