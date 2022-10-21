package makamys.coretweaks.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CoreTweaksCommand extends CommandBase {
    
    public static final EnumChatFormatting HELP_COLOR = EnumChatFormatting.BLUE;
    public static final EnumChatFormatting HELP_USAGE_COLOR = EnumChatFormatting.YELLOW;
    public static final EnumChatFormatting HELP_EMPHASIS_COLOR = EnumChatFormatting.DARK_AQUA;
    
    private static Map<String, ISubCommand> subCommands = new HashMap<>();
    
    public static void registerSubCommand(String key, ISubCommand command) {
        subCommands.put(key, command);   
    }
    
    @Override
    public String getCommandName() {
        return "coretweaks";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "";
    }
    
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if(args.length > 0) {
            ISubCommand subCommand = subCommands.get(args[0]);
            if(subCommand != null) {
                subCommand.processCommand(sender, args);
                return;
            }
        }
        throw new WrongUsageException("coretweaks <" + String.join("|", subCommands.keySet()) + ">", new Object[0]);
    }
    
    public static void addColoredChatMessage(ICommandSender sender, String text, EnumChatFormatting color) {
        addColoredChatMessage(sender, text, color, null);
    }
    
    public static void addColoredChatMessage(ICommandSender sender, String text, EnumChatFormatting color, Consumer<ChatComponentText> fixup) {
        ChatComponentText msg = new ChatComponentText(text);
        msg.getChatStyle().setColor(color);
        if(fixup != null) {
            fixup.accept(msg);
        }
        sender.addChatMessage(msg);
    }
    
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, subCommands.keySet().toArray(new String[0])) : null;
    }
    
}
