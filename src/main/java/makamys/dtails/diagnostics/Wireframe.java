package makamys.dtails.diagnostics;

import makamys.dtails.CheatHelper;
import makamys.dtails.Config;
import makamys.dtails.IModEventListener;
import makamys.dtails.command.DTailsCommand;
import makamys.dtails.command.ISubCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class Wireframe implements IModEventListener {

    public static IModEventListener instance;
    
    public Wireframe() {
        DTailsCommand.registerSubCommand("wireframe", new WireframeSubCommand());
    }
    
    private static class WireframeSubCommand implements ISubCommand {
        
        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if(CheatHelper.canCheat()) {
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
        return Config.wireframeStartEnabled && CheatHelper.canCheat();
    }
    
}
