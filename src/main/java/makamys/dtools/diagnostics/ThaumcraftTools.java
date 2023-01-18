package makamys.dtools.diagnostics;

import static makamys.dtools.command.DToolsCommand.HELP_COLOR;
import static makamys.dtools.command.DToolsCommand.HELP_USAGE_COLOR;
import static makamys.dtools.command.DToolsCommand.addColoredChatMessage;

import cpw.mods.fml.common.Loader;
import makamys.dtools.command.DToolsCommand;
import makamys.dtools.command.ISubCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;

public class ThaumcraftTools {
    
public static ThaumcraftTools instance;
    
    public ThaumcraftTools() {
        if(Loader.isModLoaded("Thaumcraft")) {
            DToolsCommand.registerSubCommand("tc", new TCSubCommand());
        }
    }
    
    private static class TCSubCommand implements ISubCommand {
        
        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            String usage = "dtools tc <dumpaspects|help>";
            if(args.length >= 2) {
                switch(args[1]) {
                    case "dumpaspects": {
                        dump();
                        sender.addChatMessage(new ChatComponentText("Dumped aspects (lie)"));
                        return;
                    }
                    case "help": {
                        addColoredChatMessage(sender, "Usage: " + usage, HELP_USAGE_COLOR);
                        addColoredChatMessage(sender, "/dumpaspects: Dump all Thaumcraft aspects.", HELP_COLOR);
                        return;
                    }
                }
            }
            throw new WrongUsageException(usage, new Object[0]);
        }
    }
    
    private static void dump() {
        
    }
    
}
