package makamys.dtails.diagnostics;

import makamys.dtails.Config;
import makamys.dtails.IModEventListener;
import makamys.dtails.command.DTailsCommand;
import makamys.dtails.command.ISubCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class Wireframe implements IModEventListener {

    public static IModEventListener instance;
    
    private static final boolean IS_DEV_ENVIRONMENT = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    
    public Wireframe() {
        DTailsCommand.registerSubCommand("wireframe", new WireframeSubCommand());
    }
    
    private static class WireframeSubCommand implements ISubCommand {
        
        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if(playerHasPermission()) {
                Config.wireframeStartEnabled = !Config.wireframeStartEnabled;
                Config.save();
            } else {
                ChatComponentTranslation chatcomponenttranslation2 = new ChatComponentTranslation("commands.generic.permission", new Object[0]);
                chatcomponenttranslation2.getChatStyle().setColor(EnumChatFormatting.RED);
                sender.addChatMessage(chatcomponenttranslation2);
            }
        }
        
    }

    public static boolean isEnabled() {
        return Config.wireframeStartEnabled && playerHasPermission();
    }
    
    private static boolean playerHasPermission() {
        if(IS_DEV_ENVIRONMENT) {
            return true;
        } else {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            return (player != null && player.capabilities.isCreativeMode);
        }
    }
    
}
