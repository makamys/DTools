package makamys.dtools.diagnostics;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import makamys.dtools.DTools;
import makamys.dtools.listener.IFMLEventListener;
import makamys.dtools.util.Util;

public class DumpSpawnTables implements IFMLEventListener {
    
    public static DumpSpawnTables instance;

    @Override
    public void onInit(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new DumpSpawnsCommand());
    }
    
    public static IChatComponent dumpSpawns() {
        String dump = "";
        
        for(BiomeGenBase bgb : BiomeGenBase.getBiomeGenArray()) {
            if(bgb != null) {
                dump += "\"" + bgb.biomeName + " @ " + bgb.getClass().getName() + "\": {\n";
                for(EnumCreatureType type : EnumCreatureType.values()) {
                    dump += "  " + type + ": [\n";
                    for(Object o : bgb.getSpawnableList(type)) {
                        dump += "    " + o + "\n";
                    }
                    dump +="  ]\n";
                }
                dump += "}\n\n";
            }
        }
        
        File outFile = Util.childFile(DTools.OUT_DIR, "spawns-dump.hjson");
        try {
            FileUtils.write(outFile, dump);
        } catch (IOException e) {
            e.printStackTrace();
            return new ChatComponentText(EnumChatFormatting.RED + "Failed to write dump: " + e.getMessage());
        }
        
        ChatComponentText cct = new ChatComponentText(outFile.getName());
        cct.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, outFile.getAbsolutePath()));
        cct.getChatStyle().setUnderlined(Boolean.valueOf(true));
        return new ChatComponentTranslation("Wrote dump to %s", new Object[] {cct});
    }
    
    public class DumpSpawnsCommand extends CommandBase {

        @Override
        public String getCommandName() {
            return "dumpspawns";
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
            sender.addChatMessage(DumpSpawnTables.dumpSpawns());
        }
        
    }
}
