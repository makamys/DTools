package makamys.dtools.mixin.diagnostics.setblockprofiler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import makamys.dtools.diagnostics.SetBlockProfiler;
import net.minecraft.block.Block;
import net.minecraft.world.World;

@Mixin(World.class)
public class MixinWorld {
    
    @Inject(method = "setBlock", at = @At("HEAD"))
    private void onSetBlock(int x, int y, int z, Block block, int meta, int flags, CallbackInfoReturnable<Boolean> cir) {
        SetBlockProfiler.instance.onSetBlock(x, y, z, block, meta, flags);
    }
}
    