package makamys.dtools;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class Compat {

    public static boolean isNHEIPresent() {
        ModContainer mc = Loader.instance().getIndexedModList().get("NotEnoughItems");
        return mc != null && mc.getVersion().contains("-GTNH");
    }

}
