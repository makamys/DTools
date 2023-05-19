package makamys.dtools.mixin.tweak.worlddeleter;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import makamys.dtools.tweak.WorldDeleter;
import net.minecraft.client.gui.GuiSelectWorld;

@Mixin(GuiSelectWorld.class)
abstract class MixinGuiSelectWorld {
    
    @Shadow
    private int field_146640_r;
    @Shadow
    private List field_146639_s;
    @Shadow
    protected String func_146621_a(int p_146621_1_) {return "";}
    
    @Inject(method = "drawScreen", at = @At("RETURN"), cancellable = true)
    public void onDrawScreen(CallbackInfo ci) {
        if(field_146640_r >= 0 && field_146640_r < field_146639_s.size()) {
            WorldDeleter.tick(func_146621_a(field_146640_r), field_146640_r, (GuiSelectWorld)(Object)this);
        }
    }
}
