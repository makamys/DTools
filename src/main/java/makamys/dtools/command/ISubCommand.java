package makamys.dtools.command;

import net.minecraft.command.ICommandSender;

public interface ISubCommand {
    
    void processCommand(ICommandSender sender, String[] args);
    
}
