package makamys.dtools.tweak.devsetup.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.lwjgl.input.Keyboard;

import makamys.dtools.tweak.devsetup.DevWorldSetup;
import makamys.dtools.tweak.devsetup.DevWorldSetup.Config.ConfigItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldType;

public class GuiButtonDevSetup extends GuiButtonGeneric {

    public DevWorldSetup.Config config = new DevWorldSetup.Config();
    private GuiButton worldType;
    private GuiCreateWorldManipulator guiManipulator;
    
    boolean firstClick = true;
    private String defaultGamemode;
    private int defaultWorldTypeIndex;
    private boolean defaultEnableCheats;
    private boolean defaultEnableStructures;
    private int superflatWorldTypeIndex;
    
    public GuiButtonDevSetup(int id, int posX, int posY, int width, int height, GuiCreateWorld createWorld, GuiButton worldTypeButton) {
        super(id, posX, posY, width, height, "Dev");
        this.worldType = worldTypeButton;
        this.guiManipulator = new GuiCreateWorldManipulator(createWorld);
    }
    
    public List<String> getTooltipStrings() {
        List<String> tooltip = new ArrayList<>();
        tooltip.add("Set up a test world.");
        tooltip.add("");
        String enabledColor = config.type == 1 ? ""+EnumChatFormatting.GREEN : ""+EnumChatFormatting.BLUE;
        String baseDisabledColor = "" + EnumChatFormatting.WHITE;
        String extraDisabledColor = "" + (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? EnumChatFormatting.WHITE : EnumChatFormatting.DARK_GRAY);
        tooltip.add(generateSettingDescription("Creative mode", config.setGamemodeCreative, enabledColor, baseDisabledColor));
        tooltip.add(generateSettingDescription("Enable cheats", config.enableCheats, enabledColor, baseDisabledColor));
        tooltip.add(generateSettingDescription("Disable daylight cycle", config.disableDoDaylightCycle, enabledColor, baseDisabledColor));
        tooltip.add(generateSettingDescription("Disable weather", config.disableDoWeatherCycle, enabledColor, baseDisabledColor));
        tooltip.add(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && config.type != 2 ? EnumChatFormatting.DARK_GRAY + "SHIFT:" : EnumChatFormatting.DARK_GRAY + "   --------------");
        tooltip.add(generateSettingDescription("Disable mob spawning", config.disableDoMobSpawning, enabledColor, extraDisabledColor));
        tooltip.add(generateSettingDescription("Disable structures", config.disableStructures, enabledColor, extraDisabledColor));
        tooltip.add(generateSettingDescription("Superflat", config.superflat, enabledColor, extraDisabledColor));
        return tooltip;
    }
    
    private String generateSettingDescription(String description, ConfigItem item, String enabledColor, String disabledColor) {
        return (item.isEffectivelyEnabled() ? enabledColor : disabledColor) + (item.isEffectivelyEnabled() ? "+" : "-") + " " + description;
    }

    @Override
    public void onClicked() {
        if(firstClick) {
            firstClick = false;
            defaultWorldTypeIndex = guiManipulator.getWorldTypeIndex();
            superflatWorldTypeIndex = IntStream.range(0, WorldType.worldTypes.length).filter(i -> WorldType.worldTypes[i] == WorldType.FLAT).findFirst().getAsInt();
            defaultGamemode = guiManipulator.getGamemodeName();
            defaultEnableCheats = guiManipulator.areCheatsEnabled();
            defaultEnableStructures = guiManipulator.areStructuresEnabled();
        }
        config.toggle(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT));
        applyConfig(config);
        this.displayString = config.type == 0 ? "Dev" : config.type == 1 ? ""+EnumChatFormatting.GREEN + EnumChatFormatting.BOLD + "Dev" : config.type == 2 ? ""+EnumChatFormatting.BLUE + EnumChatFormatting.BOLD + "Dev" : "???";
    }

    @Override
    protected void drawTooltip(int mouseX, int mouseY, List<String> tooltipStrings) {
        Util.drawSimpleTooltip(10, 10, (int)this.zLevel, tooltipStrings, Util.Anchor.TOP_LEFT);
    }
    
    @Override
    public void drawButton(Minecraft p_146112_1_, int mouseX, int mouseY) {
        updateConfig();
        if(worldType != null) {
            this.visible = !worldType.visible;
        }
        super.drawButton(p_146112_1_, mouseX, mouseY);
    }
    
    private void applyConfig(DevWorldSetup.Config config) {
        if(!guiManipulator.setGamemode(config.setGamemodeCreative.isEnabled() ? "creative" : defaultGamemode)) {
            config.setGamemodeCreative.setSynced(false);
        }
        if(!guiManipulator.setEnableCheats(config.enableCheats.isEnabled() ? true : defaultEnableCheats)) {
            config.enableCheats.setSynced(false);
        }
        if(!guiManipulator.setEnableStructures(config.disableStructures.isEnabled() ? false : defaultEnableStructures)) {
            config.disableStructures.setSynced(false);
        }
        if(!guiManipulator.setWorldTypeIndex(config.superflat.isEnabled() ? superflatWorldTypeIndex : defaultWorldTypeIndex)) {
            config.superflat.setSynced(false);
        }
    }

    private void updateConfig() {
        config.update(config.setGamemodeCreative, guiManipulator.isCreativeMode());
        config.update(config.enableCheats, guiManipulator.areCheatsEnabled());
        config.update(config.disableStructures, !guiManipulator.areStructuresEnabled());
        config.update(config.superflat, guiManipulator.isSuperflat());
    }

}
