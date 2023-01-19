package makamys.dtools.mixin.tweak.thaumcraft.unlockallaspects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import makamys.dtools.CheatHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.research.PlayerKnowledge;

@Mixin(value = PlayerKnowledge.class, remap = false)
abstract class MixinPlayerKnowledge {
    
    @Inject(method = "getAspectsDiscovered", at = @At("HEAD"), cancellable = true)
    private void haveAllAspectsDiscovered(String player, CallbackInfoReturnable<AspectList> cir) {
        if(CheatHelper.isCreativeByName(player)) {
            AspectList al = new AspectList();
            for(Aspect a : Aspect.aspects.values()) {
                al.add(a, 1);
            }
            cir.setReturnValue(al);
        }
    }
    
}
