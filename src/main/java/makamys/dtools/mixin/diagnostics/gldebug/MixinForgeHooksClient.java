package makamys.dtools.mixin.diagnostics.gldebug;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraftforge.client.ForgeHooksClient;

@Mixin(value = ForgeHooksClient.class, remap = false)
abstract class MixinForgeHooksClient {
    /**
     * @reason Create a debug context so debug messages can be captured.
     */
    @Redirect(method = "createDisplay", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;create(Lorg/lwjgl/opengl/PixelFormat;)V"))
    private static void redirectCreateDisplay(PixelFormat pixel_format) throws LWJGLException {
        Display.create(pixel_format, new ContextAttribs().withDebug(true));
    }
}
