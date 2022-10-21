package makamys.dtails.diagnostics;

import makamys.dtails.IModEventListener;
import makamys.dtails.command.DTailsCommand;
import makamys.dtails.command.ISubCommand;
import net.minecraft.command.ICommandSender;

public class Wireframe implements IModEventListener {

    public static IModEventListener instance;
    
    private static boolean enabled;
    
    public Wireframe() {
        DTailsCommand.registerSubCommand("wireframe", new WireframeSubCommand());
    }
    
    private static class WireframeSubCommand implements ISubCommand {
        
        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            enabled = !enabled;
        }
        
    }

    public static boolean isEnabled() {
        return enabled;
    }
    
}
