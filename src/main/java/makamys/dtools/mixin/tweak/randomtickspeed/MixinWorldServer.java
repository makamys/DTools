package makamys.dtools.mixin.tweak.randomtickspeed;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import makamys.dtools.tweak.RandomTickSpeed;
import org.spongepowered.asm.mixin.injection.Constant;

import net.minecraft.world.WorldServer;

@Mixin(WorldServer.class)
public class MixinWorldServer {
    @ModifyConstant(method = "func_147456_g()V", constant = @Constant(intValue = 3, ordinal = 2))
    public int changeChunkUpdateCount(int original) {
        return RandomTickSpeed.instance.getRandomTickSpeed(((WorldServer)(Object)this).getGameRules());
    }
}
