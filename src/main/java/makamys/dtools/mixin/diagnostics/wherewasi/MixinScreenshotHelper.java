package makamys.dtools.mixin.diagnostics.wherewasi;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;

import makamys.dtools.diagnostics.WhereWasI;
import net.minecraft.util.ScreenShotHelper;


@Mixin(ScreenShotHelper.class)
abstract class MixinScreenshotHelper {
    
    @Redirect(method = "(Ljava/io/File;Ljava/lang/String;IILnet/minecraft/client/shader/Framebuffer;)Lnet/minecraft/util/IChatComponent;", at = @At(value = "INVOKE", target = "Ljavax/imageio/ImageIO;write(Ljava/awt/image/RenderedImage;Ljava/lang/String;Ljava/io/File;)Z"))
    private static boolean onSaveScreenshot(RenderedImage img, String str, File f) throws IOException {
        WhereWasI.onSaveScreenshot(f);
        return ImageIO.write(img, str, f);
    }
    
}
