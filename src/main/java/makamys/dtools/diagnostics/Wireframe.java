package makamys.dtools.diagnostics;

import makamys.dtools.CheatHelper;
import makamys.dtools.Config;
import makamys.dtools.command.DToolsCommand;
import makamys.dtools.command.ISubCommand;
import makamys.dtools.listener.IFMLEventListener;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class Wireframe implements IFMLEventListener {

    public static IFMLEventListener instance;
    
    public Wireframe() {
        DToolsCommand.registerSubCommand("wireframe", new WireframeSubCommand());
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
