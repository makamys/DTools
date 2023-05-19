package makamys.dtools.tweak.devsetup.gui;

import java.util.function.Supplier;

import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.world.WorldType;

public class GuiCreateWorldManipulator {
    
    private final GuiCreateWorld gui;
    
    public GuiCreateWorldManipulator(GuiCreateWorld gui) {
        this.gui = gui;
    }
    
    public boolean isSuperflat() {
        return WorldType.worldTypes[getWorldTypeIndex()] == WorldType.FLAT;
    }

    public boolean areStructuresEnabled() {
        return gui.field_146341_s;
    }

    public boolean areCheatsEnabled() {
        return gui.field_146340_t;
    }

    public boolean isCreativeMode() {
        return getGamemodeName().equals("creative");
    }
    
    public String getGamemodeName() {
        return gui.field_146342_r;
    }

    public int getWorldTypeIndex() {
        return gui.field_146331_K;
    }
    
    public boolean setWorldTypeIndex(int index) {
        return tryToSetValue(() -> gui.actionPerformed(gui.field_146320_D), this::getWorldTypeIndex, index);
    }

    public boolean setEnableStructures(boolean value) {
        return tryToSetValue(() -> gui.actionPerformed(gui.field_146325_B), this::areStructuresEnabled, value);
    }

    public boolean setEnableCheats(boolean value) {
        return tryToSetValue(() -> gui.actionPerformed(gui.field_146321_E), this::areCheatsEnabled, value);
    }

    public boolean setGamemode(String value) {
        return tryToSetValue(() -> gui.actionPerformed(gui.field_146343_z), this::getGamemodeName, value);
    }
    
    private boolean tryToSetValue(Runnable stepFunction, Supplier<Object> getValue, Object target) {
        Object value = getValue.get();
        Object firstValue = value;
        int steps = 0;
        final int MAX_STEPS = 100;
        while(!value.equals(target) && !(steps > 0 && value.equals(firstValue)) && steps < MAX_STEPS) {
            stepFunction.run();
            value = getValue.get();
            steps++;
        }
        if(!value.equals(target)) {
            // restore original value
            while(!value.equals(firstValue) && steps < MAX_STEPS) {
                stepFunction.run();
                value = getValue.get();
                steps++;
            }
        } else {
            return true;
        }
        return false;
    }
    
}
