package makamys.dtools.tweak.devsetup.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import makamys.dtools.tweak.devsetup.Config;
import makamys.dtools.tweak.devsetup.Config.ConfigItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.util.EnumChatFormatting;

public class GuiButtonDevSetup extends GuiButtonGeneric {

    public Config config = new Config();
    private GuiButton worldType;
    private GuiCreateWorldManipulator guiManipulator;
    
    boolean firstClick = true;
    
    public GuiButtonDevSetup(int id, int posX, int posY, int width, int height, GuiCreateWorld createWorld) {
        super(id, posX, posY, width, height, "Dev");
        this.guiManipulator = new GuiCreateWorldManipulator(createWorld);
    }
    
    public void setWorldTypeButton(GuiButton worldTypeButton) {
        this.worldType = worldTypeButton;
    }
    
    public List<String> getTooltipStrings() {
        List<String> tooltip = new ArrayList<>();
        tooltip.add("Set up a test world.");
        tooltip.add("");
        String enabledColor = config.type == 1 ? ""+EnumChatFormatting.GREEN : ""+EnumChatFormatting.BLUE;
        String baseDisabledColor = config.type == 0 ? "" + EnumChatFormatting.WHITE : ""+EnumChatFormatting.DARK_GRAY;
        String extraDisabledColor = "" + (config.type <= 1 && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? EnumChatFormatting.WHITE : EnumChatFormatting.DARK_GRAY);
        for(int level = 1; level <= 2; level++) {
            List<ConfigItem> items = config.getItemsForLevel(level, true);
            String disabledColor = level == 1 ? baseDisabledColor : extraDisabledColor;
            if(!items.isEmpty()) {
                if(level == 2) {
                    tooltip.add(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && config.type != 2 ? EnumChatFormatting.DARK_GRAY + "SHIFT:" : EnumChatFormatting.DARK_GRAY + "   --------------");
                }
                for(ConfigItem item : items) {
                    tooltip.add(generateSettingDescription(item, enabledColor, disabledColor));
                }
            }
        }
        return tooltip;
    }
    
    private String generateSettingDescription(ConfigItem item, String enabledColor, String disabledColor) {
        return (item.isEffectivelyEnabled() ? enabledColor : disabledColor) + (item.isEffectivelyEnabled() ? "+" : "-") + " " + item.getDescription();
    }

    @Override
    public void onClicked() {
        if(firstClick) {
            firstClick = false;
            guiManipulator.saveDefaults();
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
    
    private void applyConfig(Config config) {
        for(ConfigItem item : config.items) {
            if(!item.manipulateGui(guiManipulator)) {
                item.setSynced(false);
            }
        }
    }

    private void updateConfig() {
        for(ConfigItem item : config.items) {
            item.update(guiManipulator);
        }
    }

}
