package makamys.dtools;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class Compat {

    private static Boolean isSamplerPresent;
    
    public enum EtFuturumFeature {
        SPRINT_FLYING("ganymedes01.etfuturum.mixins.flyspeed.MixinEntityPlayer"),
        DO_WEATHER_CYCLE("ganymedes01.etfuturum.mixins.doweathercycle.MixinWorldInfo", "ganymedes01.etfuturum.gamerule.DoWeatherCycle"),
        RANDOM_TICK_SPEED("ganymedes01.etfuturum.mixins.randomtickspeed.MixinWorldServer", "ganymedes01.etfuturum.gamerule.RandomTickSpeed"),
        GAMEMODE_SWITCHER("ganymedes01.etfuturum.client.gui.GuiGamemodeSwitcher");

        private final String[] classes;
        
        EtFuturumFeature(String...classes) {
            this.classes = classes;
        }
        
        public boolean areAnyClassesPresent() {
            for(String s : classes) {
                if(isClassPresent(s)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    public static boolean isNHEIPresent() {
        ModContainer mc = Loader.instance().getIndexedModList().get("NotEnoughItems");
        return mc != null && mc.getVersion().contains("-GTNH");
    }

    public static boolean isThaumcraftPresent() {
        return isClassPresent("thaumcraft.codechicken.core.launch.DepLoader");
    }
    
    public static boolean isEtFuturumFeaturePresent(EtFuturumFeature feature) {
        return isClassPresent("ganymedes01.etfuturum.EtFuturum") && feature.areAnyClassesPresent();
    }
    
    public static boolean isArchaicFixPresent() {
        return isClassPresent("org.embeddedt.archaicfix.ArchaicFix");
    }
    
    public static boolean isSamplerPresent() {
        if(isSamplerPresent == null) {
            isSamplerPresent = Loader.isModLoaded("sampler");
        }
        return isSamplerPresent;
    }

    private static boolean isClassPresent(String className) {
        return Compat.class.getResource("/" + className.replace('.', '/') + ".class") != null;
    }

}
