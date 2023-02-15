package makamys.dtools.mixin.diagnostics.wrongmixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.gui.achievement.GuiAchievements;

@Mixin(GuiAchievements.class)
public class MixinGuiAchievements {
    
    @Redirect(method = "dghhkdhghjkgd", at = @At(value = "INVOKE", target = "Lasdf;ghjkl()V"), require = 1)
    private void wrongMethod() {
        
    }
    
}
