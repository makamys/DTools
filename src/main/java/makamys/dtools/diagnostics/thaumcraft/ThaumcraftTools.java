package makamys.dtools.diagnostics.thaumcraft;

import static makamys.dtools.command.DToolsCommand.HELP_COLOR;
import static makamys.dtools.command.DToolsCommand.HELP_USAGE_COLOR;
import static makamys.dtools.command.DToolsCommand.ERROR_COLOR;
import static makamys.dtools.command.DToolsCommand.addChatMessage;
import static makamys.dtools.command.DToolsCommand.addColoredChatMessage;

import cpw.mods.fml.common.Loader;
import makamys.dtools.Compat;
import makamys.dtools.command.DToolsCommand;
import makamys.dtools.command.ISubCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

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
            String usage = "dtools tc <dumpneiaspects|help>";
            if(args.length >= 2) {
                switch(args[1]) {
                    case "dumpneiaspects": {
                        if(Compat.isNHEIPresent()) {
                            try {
                                AspectDumper.dumpNEIAspects();
                                addChatMessage(sender, "Dumped aspects to ./dtools/out/tc-nei-aspects.csv");
                            } catch(Exception e) {
                                e.printStackTrace();
                                addColoredChatMessage(sender, "Failed to dump aspects: " + e + ". See log for details.", ERROR_COLOR);
                            }
                        } else {
                            addColoredChatMessage(sender, "This feature requires the GTNH fork of NotEnoughItems." + usage, ERROR_COLOR);
                        }
                        return;
                    }
                    case "help": {
                        addColoredChatMessage(sender, "Usage: " + usage, HELP_USAGE_COLOR);
                        addColoredChatMessage(sender, "/dumpneiaspects: Dump Thaumcraft aspects of all items in NEI item panel.", HELP_COLOR);
                        return;
                    }
                }
            }
            throw new WrongUsageException(usage, new Object[0]);
        }
    }
    
}
