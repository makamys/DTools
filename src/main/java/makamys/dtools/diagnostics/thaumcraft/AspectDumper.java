package makamys.dtools.diagnostics.thaumcraft;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.google.common.base.Objects;

import codechicken.lib.inventory.InventoryUtils;
import codechicken.nei.ItemPanels;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.util.NBTJson;
import makamys.dtools.DTools;
import makamys.dtools.util.CSVWriter;
import makamys.dtools.util.Util;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

public class AspectDumper {
    
    public static void dumpNEIAspects() throws IOException {
        if(ItemPanels.itemPanel.getItems().isEmpty()) {
            throw new IllegalStateException("Open your inventory first");
        }
        try (CSVWriter out = new CSVWriter(new FileWriter(Util.childFile(DTools.OUT_DIR, "tc-nei-aspects.csv")))) {
            out.writeRow("Item Name", "Item ID", "Item meta", "NBT", "Display Name", "Aspects");
            for (ItemStack stack : ItemPanels.itemPanel.getItems()) {
                AspectList aspects = ThaumcraftApiHelper.getObjectAspects(stack);
                aspects = ThaumcraftCraftingManager.getBonusTags(stack, aspects);
                out.writeRow(
                    Item.itemRegistry.getNameForObject(stack.getItem()),
                    Integer.toString(Item.getIdFromItem(stack.getItem())),
                    Integer.toString(InventoryUtils.actualDamage(stack)),
                    stack.stackTagCompound == null ? "" : NBTJson.toJson(stack.stackTagCompound),
                    EnumChatFormatting.getTextWithoutFormattingCodes(GuiContainerManager.itemDisplayNameShort(stack)),
                    aspectsToString(aspects)
                );
            }
        }
    }
    
    private static String aspectsToString(AspectList aspects) {
        if(aspects == null) {
            return "";
        }
        return String.join(";", aspects.aspects.entrySet().stream().map(AspectDumper::aspectMapEntryToString).filter(s -> !s.isEmpty()).toArray(String[]::new));
    }
    
    private static String aspectMapEntryToString(Entry<Aspect, Integer> e) {
        if(e.getKey() == null) {
            return "";
        }
        return e.getValue() + "x" + e.getKey().getName();
    }
    
}
