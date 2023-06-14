package makamys.dtools.tweak.automation;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;

import static makamys.dtools.DTools.MODID;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import makamys.dtools.listener.IFMLEventListener;
import makamys.dtools.util.ConfigHelper;
import makamys.dtools.util.Util;

public class AutoChunkPregenGui implements IFMLEventListener {
    
    public static AutoChunkPregenGui instance;
    
    private Settings settings;
    private static final ConfigHelper helper = new ConfigHelper(MODID);
    
    @Override
    public void onInit(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
        
        loadConfig();
    }
    
    private void loadConfig() {
        String relPath = "config/" + MODID + "/auto_chunk_pregen_gui.ini";
        File configFile = new File(Launch.minecraftHome, relPath);
        configFile.getParentFile().mkdirs();
        
        boolean overwrite = false;
        
        try {
            overwrite = configFile.isFile() && Files.lines(configFile.toPath()).findFirst().get().startsWith("#:replaceableFile");
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        helper.createDefaultConfigFileIfMissing(configFile, overwrite);
        
        Properties props = new Properties();
        try(FileReader fr = new FileReader(new File(Launch.minecraftHome, "config/" + MODID + "/auto_chunk_pregen_gui.ini"))) {
            props.load(fr);
            settings = new Settings(props);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public static class Settings {
        Integer speed;
        Integer radius;
        Boolean fullGen;
        String seed;
        
        public Settings(Properties props) {
            speed = parseIntegerOrNull(props.getProperty("speed"));
            radius = parseIntegerOrNull(props.getProperty("radius"));
            fullGen = parseBooleanOrNull(props.getProperty("fullGen"));
            seed = parseStringOrNull(props.getProperty("seed"));
        }

        private static String parseStringOrNull(String str) {
            return str == null ? null : str;
        }

        private static Boolean parseBooleanOrNull(String str) {
            return str == null ? null : Boolean.parseBoolean(str);
        }

        private static Integer parseIntegerOrNull(String str) {
            return str == null ? null : Integer.parseInt(str);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event){
        
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        try {
            Class guiClass = event.gui.getClass();
            if(guiClass.getName().equals("pregenerator.impl.client.preview.GuiSeedPreview")) {
                loadConfig();
                
                Class chunkProcessor = Class.forName("pregenerator.impl.processor.generator.ChunkProcessor");
                Class worldSeed = Class.forName("pregenerator.impl.client.preview.world.WorldSeed");
                
                if(settings.speed != null) {
                    chunkProcessor.getMethod("setMaxTime", int.class).invoke(chunkProcessor.getField("INSTANCE").get(null), settings.speed);
                }
                if(settings.radius != null) {
                    ReflectionHelper.setPrivateValue(guiClass, event.gui, settings.radius, "globalRadius");
                }
                if(settings.fullGen != null) {
                    ReflectionHelper.setPrivateValue(guiClass, event.gui, !settings.fullGen, "terrainOnly");
                }
                if(settings.seed != null) {
                    Object seed = ReflectionHelper.getPrivateValue(guiClass, event.gui, "seed");
                    worldSeed.getMethod("setSeed", String.class).invoke(seed, settings.seed);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
