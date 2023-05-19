package makamys.dtools.tweak.devsetup;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Config {
    // Base preset
    public Config.ConfigItem setGamemodeCreative = new ConfigItem();
    public Config.ConfigItem enableCheats = new ConfigItem();
    public Config.ConfigItem disableDoDaylightCycle = new ConfigItem();
    public Config.ConfigItem disableDoWeatherCycle = new ConfigItem();
    // Extra preset
    public Config.ConfigItem disableDoMobSpawning = new ConfigItem();
    public Config.ConfigItem disableStructures = new ConfigItem();
    public Config.ConfigItem superflat = new ConfigItem();
    
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
        setGamemodeCreative.setValue(type >= 1);
        enableCheats.setValue(type >= 1);
        disableDoDaylightCycle.setValue(type >= 1);
        disableDoWeatherCycle.setValue(type >= 1);
        
        disableDoMobSpawning.setValue(type >= 2);
        disableStructures.setValue(type >= 2);
        superflat.setValue(type >= 2);
    }
    
    public static class ConfigItem {
        private boolean value = false;
        private boolean guiValue = false;
        private boolean isSynced = true;
        
        private void setValue(boolean newVal) {
            value = newVal;
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
    }
}