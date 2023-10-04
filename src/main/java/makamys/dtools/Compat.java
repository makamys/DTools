package makamys.dtools;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class Compat {

    private static Boolean isSamplerPresent;
    
    public static boolean isNHEIPresent() {
        ModContainer mc = Loader.instance().getIndexedModList().get("NotEnoughItems");
        return mc != null && mc.getVersion().contains("-GTNH");
    }

    public static boolean isThaumcraftPresent() {
        return isClassPresent("thaumcraft.codechicken.core.launch.DepLoader");
    }
    
    public static boolean isEtFuturumRequiemPresent() {
        return isClassPresent("ganymedes01.etfuturum.EtFuturum");
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
