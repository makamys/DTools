package makamys.dtools.diagnostics;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

import makamys.dtools.DTools;
import makamys.dtools.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class WhereWasI {
    
    public static void onSaveScreenshot(File file) {
        final String tab = "    ";
        File outFile = getOutFile();
        
        try {
            Files.createDirectories(outFile.getParentFile().toPath());
            FileUtils.write(
                    outFile,
                    "\"" + file.getName() + "\": {\n" + String.join(
                            "\n",
                            getContext().stream()
                            .map(p -> tab + "\"" + p.getLeft() + "\": " + p.getRight())
                            .collect(Collectors.toList())) +
                    "\n}\n\n",
                    "UTF-8",
                    true);
        } catch (IOException e) {
            System.err.println("Failed to save screenshot info to file " + outFile);
            e.printStackTrace();
        }
    }
    
    private static List<Pair<String, Object>> getContext(){
        List<Pair<String, Object>> context = new ArrayList<>();
        Minecraft mc = Minecraft.getMinecraft();
        Entity player = Minecraft.getMinecraft().renderViewEntity;
        
        double yaw = player.rotationYaw % 360.0;
        if(yaw > 180.0) {
            yaw -= 360.0;
        }
        
        context.add(Pair.of("X", player.posX));
        context.add(Pair.of("Y", player.posY));
        context.add(Pair.of("Z", player.posZ));
        context.add(Pair.of("World name", getWorldName()));
        context.add(Pair.of("Seed", mc.getIntegratedServer().worldServerForDimension(0).getSeed()));
        context.add(Pair.of("Yaw", yaw));
        context.add(Pair.of("Pitch", player.rotationPitch));
        context.add(Pair.of("FOV", mc.gameSettings.fovSetting));
        
        return context;
    }
    
    private static File getOutFile(){
        File outBaseDir = Util.childFile(DTools.OUT_DIR, "wherewasi");
        return new File(outBaseDir, getWorldName() + ".hjson");
    }
    
    private static String getWorldName() {
        // TODO multiplayer support
        return Minecraft.getMinecraft().getIntegratedServer().getFolderName();
    }
}

