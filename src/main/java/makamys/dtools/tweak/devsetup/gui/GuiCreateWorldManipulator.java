package makamys.dtools.tweak.devsetup.gui;

import makamys.dtools.tweak.devsetup.DevWorldSetup.MutableTrilean;
import makamys.dtools.tweak.devsetup.DevWorldSetup.Trilean;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.world.WorldType;

public class GuiCreateWorldManipulator {
    
    private final GuiCreateWorld gui;
    
    public String defaultGamemode;
    public int defaultWorldType;
    
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
    
    public void setSuperflat(MutableTrilean value) {
        if(value.get() != Trilean.UNSET) {
            boolean newVal = value.isTrue();
            while(!(newVal ? isSuperflat() : getWorldTypeIndex() == defaultWorldType)) {
                gui.actionPerformed(gui.field_146320_D);
            }
        }
    }

    public void setDisableStructures(MutableTrilean value) {
        if(value.get() != Trilean.UNSET) {
            boolean newVal = value.isTrue();
            while(areStructuresEnabled() != !newVal) {
                gui.actionPerformed(gui.field_146325_B);
            }
        }
    }

    public void setEnableCheats(MutableTrilean value) {
        if(value.get() != Trilean.UNSET) {
            boolean newVal = value.isTrue();
            while(areCheatsEnabled() != newVal) {
                gui.actionPerformed(gui.field_146321_E);
            }
        }
    }

    public void setEnableCreativeMode(MutableTrilean value) {
        if(value.get() != Trilean.UNSET) {
            boolean newVal = value.isTrue();
            while(!(newVal ? isCreativeMode() : getGamemodeName().equals(defaultGamemode))) {
                gui.actionPerformed(gui.field_146343_z);
            }
        }
    }
    
}
