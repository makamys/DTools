package makamys.dtails;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.launchwrapper.Launch;

public class CheatHelper {
    
    private static final boolean IS_DEV_ENVIRONMENT = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    
    public static boolean canCheat() {
        if(IS_DEV_ENVIRONMENT) {
            return true;
        } else {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            return (player != null && player.capabilities.isCreativeMode);
        }
    }
    
}
