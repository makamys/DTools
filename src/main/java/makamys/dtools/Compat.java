package makamys.dtools;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class Compat {

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

    private static boolean isClassPresent(String className) {
        return Compat.class.getResource("/" + className.replace('.', '/') + ".class") != null;
    }

}
