package makamys.dtools.tweak;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.util.ResourceLocation;

public class WorldDeleter {
    
    private static boolean wasDPressed = false;
    
    public static void tick(String worldName, int worldIndex, GuiSelectWorld gui) {
        boolean dPressed = Keyboard.isKeyDown(Keyboard.KEY_D);
        if(dPressed && !wasDPressed) {
            if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                gui.field_146643_x = true;
                gui.confirmClicked(true, worldIndex);
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("random.fizz"), 1.0F));
            } else if(Keyboard.isKeyDown(Keyboard.KEY_LMENU)){
                File regionDir = new File(Minecraft.getMinecraft().mcDataDir, "saves/" + worldName + "/region");
                try {
                    FileUtils.deleteDirectory(regionDir);
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("random.explode"), 0.7F));
                    System.out.println("Deleted " + regionDir);
                } catch (IOException e) {
                    System.out.println("Failed to delete " + regionDir + ": " + e);
                }
            }
        }
        wasDPressed = dPressed;
    }
    
}
