package makamys.dtools.tweak.devsetup;

import static makamys.dtools.DTools.MODID;
import static makamys.dtools.DTools.LOGGER;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import makamys.dtools.tweak.devsetup.Config.ConfigItem;
import makamys.dtools.util.ConfigHelper;
import makamys.dtools.util.Util;
import net.minecraft.launchwrapper.Launch;

// Portions adapted from makamys.mixingasm.Mixingasm
public class ConfigReader {
    
    private static final ConfigHelper helper = new ConfigHelper(MODID);
    
    public static List<ConfigItem> getItems() {
        List<ConfigItem> items = new ArrayList<>();
        
        int currentMinLevel = -1;
        for(String line : readConfig()) {
            line = line.trim();
            if(line.startsWith(":")) {
                //ignore
            } else if(line.startsWith("[") && line.endsWith("]")) {
                String category = line.substring(1, line.length() - 1).toLowerCase();
                switch(category) {
                case "base":
                    currentMinLevel = 1;
                    break;
                case "shift":
                    currentMinLevel = 2;
                    break;
                }
                if(currentMinLevel == -1) {
                    LOGGER.warn("Invalid category: " + line);
                }
            } else if(currentMinLevel != -1){
                boolean child = false;
                if(line.startsWith("+")) {
                    child = true;
                    line = line.substring(1);
                }
                ConfigItem item = ConfigItem.fromWords(line.split(" "), currentMinLevel, child);
                if(item != null) {
                    items.add(item);
                } else {
                    LOGGER.warn("Failed to parse line: " + line);
                }
            }
        }
        return items;
    }
    
    private static List<String> readConfig(){
        String relPath = "config/" + MODID + "/devsetup.ini";
        File instanceFile = new File(Launch.minecraftHome, relPath);
        File sharedFile = Util.getSharedDataDir() == null ? null : new File(Util.getSharedDataDir(), relPath);
        
        File configFile = sharedFile != null && sharedFile.isFile() ? sharedFile : instanceFile;
        
        configFile.getParentFile().mkdirs();
        
        List<String> lines = configFile.exists() ? readConfigLines(configFile) : null;
        boolean overwrite = lines != null && lines.contains(":replaceableFile");
        
        if(lines == null || overwrite) {
            helper.createDefaultConfigFileIfMissing(configFile, overwrite);
            lines = readConfigLines(configFile);
        }
        
        return lines;
    }
    
    private static List<String> readConfigLines(File file){
        try (FileReader fr = new FileReader(file)){
            return IOUtils.readLines(fr).stream()
                    .map(l -> l.contains("#") ? l.substring(0, l.indexOf('#')) : l)
                    .map(l -> l.trim())
                    .filter(l -> !l.isEmpty())
                    .collect(Collectors.toList());
        } catch(Exception e) {
            System.out.println("Failed to read " + file);
            e.printStackTrace();
        }
        return Arrays.asList();
    }

}
