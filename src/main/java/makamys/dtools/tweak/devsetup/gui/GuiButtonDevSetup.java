package makamys.dtools.tweak.devsetup.gui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.ReflectionHelper;
import makamys.dtools.tweak.devsetup.DevWorldSetup;
import makamys.dtools.tweak.devsetup.DevWorldSetup.MutableTrilean;
import makamys.dtools.tweak.devsetup.DevWorldSetup.Trilean;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldType;

public class GuiButtonDevSetup extends GuiButtonGeneric {

    public DevWorldSetup.Config config = new DevWorldSetup.Config();
    private GuiButton worldType;
    private GuiCreateWorld createWorld;
    
    boolean firstClick = true;
    private String defaultGamemode;
    private int defaultWorldType;
    
    public GuiButtonDevSetup(int id, int posX, int posY, int width, int height, GuiCreateWorld createWorld, GuiButton worldTypeButton) {
        super(id, posX, posY, width, height, "Dev");
        this.worldType = worldTypeButton;
        this.createWorld = createWorld;
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
    
    private String generateSettingDescription(String description, MutableTrilean enabled, String enabledColor, String disabledColor) {
        return (enabled.isTrue() ? enabledColor : disabledColor) + (enabled.isTrue() ? "+" : "-") + " " + description;
    }

    @Override
    public void onClicked() {
        if(firstClick) {
            firstClick = false;
            defaultWorldType = getWorldTypeIndex();
            defaultGamemode = getGamemodeName();
        }
        config.toggle(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT));
        applyConfig(config);
        this.displayString = config.type == 0 ? "Dev" : config.type == 1 ? ""+EnumChatFormatting.GREEN + EnumChatFormatting.BOLD + "Dev" : config.type == 2 ? ""+EnumChatFormatting.BLUE + EnumChatFormatting.BOLD + "Dev" : "???";
    }

    private String getGamemodeName() {
        return ReflectionHelper.getPrivateValue(GuiCreateWorld.class, createWorld, "field_146342_r");
    }

    private int getWorldTypeIndex() {
        return ReflectionHelper.getPrivateValue(GuiCreateWorld.class, createWorld, "field_146331_K");
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
        setEnableCreativeMode(config.setGamemodeCreative);
        setEnableCheats(config.enableCheats);
        setDisableStructures(config.disableStructures);
        setSuperflat(config.superflat);
    }

    private void setSuperflat(MutableTrilean value) {
        if(value.get() != Trilean.UNSET) {
            boolean newVal = value.isTrue();
            while(!(newVal ? isSuperflat() : getWorldTypeIndex() == defaultWorldType)) {
                try {
                    Method m = ReflectionHelper.findMethod(GuiCreateWorld.class, createWorld, new String[] {"actionPerformed"}, GuiButton.class);
                    m.setAccessible(true);
                    GuiButton button = ReflectionHelper.getPrivateValue(GuiCreateWorld.class, createWorld, "field_146320_D");
                    m.invoke(createWorld, button);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setDisableStructures(MutableTrilean value) {
        if(value.get() != Trilean.UNSET) {
            boolean newVal = value.isTrue();
            while(areStructuresEnabled() != !newVal) {
                try {
                    Method m = ReflectionHelper.findMethod(GuiCreateWorld.class, createWorld, new String[] {"actionPerformed"}, GuiButton.class);
                    m.setAccessible(true);
                    GuiButton button = ReflectionHelper.getPrivateValue(GuiCreateWorld.class, createWorld, "field_146325_B");
                    m.invoke(createWorld, button);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setEnableCheats(MutableTrilean value) {
        if(value.get() != Trilean.UNSET) {
            boolean newVal = value.isTrue();
            while(areCheatsEnabled() != newVal) {
                try {
                    Method m = ReflectionHelper.findMethod(GuiCreateWorld.class, createWorld, new String[] {"actionPerformed"}, GuiButton.class);
                    m.setAccessible(true);
                    GuiButton button = ReflectionHelper.getPrivateValue(GuiCreateWorld.class, createWorld, "field_146321_E");
                    m.invoke(createWorld, button);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setEnableCreativeMode(MutableTrilean value) {
        if(value.get() != Trilean.UNSET) {
            boolean newVal = value.isTrue();
            while(!(newVal ? isCreativeMode() : getGamemodeName().equals(defaultGamemode))) {
                try {
                    Method m = ReflectionHelper.findMethod(GuiCreateWorld.class, createWorld, new String[] {"actionPerformed"}, GuiButton.class);
                    m.setAccessible(true);
                    GuiButton button = ReflectionHelper.getPrivateValue(GuiCreateWorld.class, createWorld, "field_146343_z");
                    m.invoke(createWorld, button);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateConfig() {
        config.update(config.setGamemodeCreative, isCreativeMode());
        config.update(config.enableCheats, areCheatsEnabled());
        config.update(config.disableStructures, !areStructuresEnabled());
        config.update(config.superflat, isSuperflat());
    }

    private boolean isSuperflat() {
        return WorldType.worldTypes[getWorldTypeIndex()] == WorldType.FLAT;
    }

    private boolean areStructuresEnabled() {
        return ReflectionHelper.getPrivateValue(GuiCreateWorld.class, createWorld, "field_146341_s");
    }

    private boolean areCheatsEnabled() {
        return ReflectionHelper.getPrivateValue(GuiCreateWorld.class, createWorld, "field_146340_t");
    }

    private boolean isCreativeMode() {
        return getGamemodeName().equals("creative");
    }

}
