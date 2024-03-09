package makamys.dtools.tweak.devsetup;

import static makamys.dtools.DTools.LOGGER;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.RequiredArgsConstructor;
import makamys.dtools.tweak.devsetup.gui.GuiCreateWorldManipulator;
import net.minecraft.client.resources.I18n;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldType;

@SideOnly(Side.CLIENT)
public class Config {
    
    private static final Map<String, Function<String[], IConfigCommand>> commandRegistry = new HashMap<>();
    
    static {
        registerCommand("gamemode", GamemodeCommand::fromWords);
        registerCommand("enablecheats", EnableCheatsCommand::fromWords);
        registerCommand("gamerule", GameruleCommand::fromWords);
        registerCommand("time", TimeCommand::fromWords);
        registerCommand("spawnstructures", EnableStructuresCommand::fromWords);
        registerCommand("worldtype", WorldTypeCommand::fromWords);
    }
    
    public static void registerCommand(String name, Function<String[], IConfigCommand> supplier) {
        commandRegistry.put(name, supplier);
    }
    
    @Nullable
    public static IConfigCommand commandFromWords(String[] words) {
        try {
            Preconditions.checkArgument(words.length > 0);
            String name = words[0].toLowerCase();
            Function<String[], IConfigCommand> supplier = commandRegistry.get(name);
            if(supplier != null) {
                return supplier.apply(words);
            } else {
                throw new IllegalArgumentException("No command named " + name + " exists");
            }
        } catch(Exception e) {
            LOGGER.warn("Failed to create command from " + Arrays.toString(words) + ": " + e.getMessage());
        }
        return null;
    }
    
    public List<ConfigItem> items = ConfigReader.getItems();
    
    public int type;
    
    public void toggle(boolean shiftDown) {
        if(!shiftDown) {
            type = type == 0 ? 1 : 0;
        } else {
            type = type == 2 ? 0 : 2;
        }
        setType(type);
    }
    
    private void setType(int type) {
        for(ConfigItem item : items) {
            item.configTypeChanged(type);
        }
    }
    
    public List<ConfigItem> getItemsForLevel(int level, boolean topLevelOnly) {
        return items.stream().filter(item -> item.minType == level && (!topLevelOnly || !item.child)).collect(Collectors.toList());
    }
    
    public static class ConfigItem {
        private boolean value = false;
        private boolean guiValue = false;
        private boolean isSynced = true;
        
        private int minType;
        private boolean child;
        
        private IConfigCommand command;
        
        private void setValue(boolean newVal) {
            value = newVal;
        }

        public void configTypeChanged(int type) {
            setValue(type >= minType);
        }

        public void update(boolean newVal) {
            guiValue = newVal;
            if(isSynced) {
                value = guiValue;
            }
        }

        public boolean isEffectivelyEnabled() {
            return value || guiValue;
        }
        
        public boolean isEnabled() {
            return value;
        }

        public void setSynced(boolean synced) {
            // TODO override gui
            //this.isSynced = synced;
        }
        
        public boolean isSynced() {
            return isSynced;
        }

        public void applyChange(MinecraftServer server) {
            command.applyToServer(server);
        }

        public String getDescription() {
            return command.getDescription();
        }

        public boolean manipulateGui(GuiCreateWorldManipulator gui) {
            return command.manipulateGui(gui, isEnabled());
        }

        public void update(GuiCreateWorldManipulator gui) {
            Boolean guiValue = command.getGuiValue(gui);
            if(guiValue != null) {
                update(guiValue);
            }
        }

        public static ConfigItem fromWords(String[] words, int minType, boolean child) {
            ConfigItem item = new ConfigItem();
            item.command = commandFromWords(words);
            item.minType = minType;
            item.child = child;
            return item.command != null ? item : null;
        }
    }
    
    public interface IConfigCommand {
        default void applyToServer(MinecraftServer server) {};
        String getDescription();
        default boolean manipulateGui(GuiCreateWorldManipulator gui, boolean enabled) {return false;};
        @Nullable
        default Boolean getGuiValue(GuiCreateWorldManipulator gui) {return null;};
    }
    
    @RequiredArgsConstructor
    public static class GameruleCommand implements IConfigCommand {        
        public final String gamerule;
        public final String value;
        
        public static GameruleCommand fromWords(String[] words) {
            Preconditions.checkArgument(words.length == 3);
            return new GameruleCommand(words[1], words[2]);
        }

        @Override
        public void applyToServer(MinecraftServer server) {
            server.worldServerForDimension(0).getGameRules().setOrCreateGameRule(gamerule, value);
        }

        @Override
        public String getDescription() {
            if(value.equals("true") || value.equals("false")) {
                return (value.equals("true") ? "Enable" : "Disable") + " " + gamerule;
            } else {
                return "Set " + gamerule + " to " + value;
            }
        }        
    }
    
    @RequiredArgsConstructor
    public static class TimeCommand implements IConfigCommand {
        public final int time;
        
        public static TimeCommand fromWords(String[] words) {
            Preconditions.checkArgument(words.length == 3);
            Preconditions.checkArgument(words[1].toLowerCase().equals("set"));
            return new TimeCommand(Integer.parseInt(words[2]));
        }

        @Override
        public void applyToServer(MinecraftServer server) {
            for (int j = 0; j < server.worldServers.length; ++j) {
                MinecraftServer.getServer().worldServers[j].setWorldTime((long)time);
            }
        }

        @Override
        public String getDescription() {
            return "Set time to " + time;
        }        
    }
    
    @RequiredArgsConstructor
    public static class GamemodeCommand implements IConfigCommand {        
        public final String gamemode;

        public static GamemodeCommand fromWords(String[] words) {
            Preconditions.checkArgument(words.length == 2);
            return new GamemodeCommand(words[1]);
        }
        
        @Override
        public boolean manipulateGui(GuiCreateWorldManipulator gui, boolean enabled) {
            return gui.setGamemode(enabled ? gamemode : gui.getDefaultGamemode());
        }
        
        @Override
        public Boolean getGuiValue(GuiCreateWorldManipulator gui) {
            return gui.getGamemodeName().equals(gamemode);
        }
        
        @Override
        public String getDescription() {
            return StringUtils.capitalize(gamemode) + " mode";
        }
    }
    
    @RequiredArgsConstructor
    public static class EnableCheatsCommand implements IConfigCommand {        
        public final boolean enable;

        public static EnableCheatsCommand fromWords(String[] words) {
            Preconditions.checkArgument(words.length == 2);
            return new EnableCheatsCommand(Boolean.parseBoolean(words[1]));
        }
        
        @Override
        public boolean manipulateGui(GuiCreateWorldManipulator gui, boolean enabled) {
            return gui.setEnableCheats(enabled ? enable : gui.getDefaultEnableCheats());
        }
        
        @Override
        public Boolean getGuiValue(GuiCreateWorldManipulator gui) {
            return gui.areCheatsEnabled() == enable;
        }
        
        @Override
        public String getDescription() {
            return (enable ? "Enable" : "Disable") + " cheats";
        }
    }
    
    @RequiredArgsConstructor
    public static class EnableStructuresCommand implements IConfigCommand {        
        public final boolean enable;
        
        public static EnableStructuresCommand fromWords(String[] words) {
            Preconditions.checkArgument(words.length == 2);
            return new EnableStructuresCommand(Boolean.parseBoolean(words[1]));
        }
        
        @Override
        public boolean manipulateGui(GuiCreateWorldManipulator gui, boolean enabled) {
            return gui.setEnableStructures(enabled ? enable : gui.getDefaultEnableStructures());
        }
        
        @Override
        public Boolean getGuiValue(GuiCreateWorldManipulator gui) {
            return gui.areStructuresEnabled() == enable;
        }
        
        @Override
        public String getDescription() {
            return (enable ? "Enable" : "Disable") + " structures";
        }
    }
    
    public static class WorldTypeCommand implements IConfigCommand {        
        public final String worldType;
        public final int worldTypeIndex;
        
        public static WorldTypeCommand fromWords(String[] words) {
            Preconditions.checkArgument(words.length == 2);
            return new WorldTypeCommand(words[1]);
        }
        
        public WorldTypeCommand(String worldType) {
            this.worldType = worldType;
            this.worldTypeIndex = findWorldTypeIndex(worldType);
        }

        private final int findWorldTypeIndex(String worldType) {
            return IntStream.range(0, WorldType.worldTypes.length).filter(i -> WorldType.worldTypes[i].getWorldTypeName().equals(worldType)).findFirst().getAsInt();
        }

        @Override
        public boolean manipulateGui(GuiCreateWorldManipulator gui, boolean enabled) {
            return gui.setWorldTypeIndex(enabled ? worldTypeIndex : gui.getDefaultWorldTypeIndex());
        }
        
        @Override
        public Boolean getGuiValue(GuiCreateWorldManipulator gui) {
            return gui.getWorldTypeIndex() == worldTypeIndex;
        }
        
        @Override
        public String getDescription() {
            return I18n.format(WorldType.worldTypes[worldTypeIndex].getTranslateName());
        }
    }
}
