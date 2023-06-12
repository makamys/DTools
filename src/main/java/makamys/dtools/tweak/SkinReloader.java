package makamys.dtools.tweak;

import java.util.Iterator;
import java.util.Map.Entry;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import makamys.dtools.listener.IFMLEventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.MinecraftForge;

public class SkinReloader implements IFMLEventListener {
    public static SkinReloader instance;

    @Override
    public void onInit(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onGui(InitGuiEvent.Post event) {
        if(event.gui instanceof GuiMainMenu) {
            clearSkins();
        }
    }

    private void clearSkins() {
        Iterator<Entry<ResourceLocation, ITextureObject>> it = Minecraft.getMinecraft().getTextureManager().mapTextureObjects.entrySet().iterator();

        while(it.hasNext()) {
            Entry<ResourceLocation, ITextureObject> e = it.next();
            ResourceLocation resLoc = (ResourceLocation)e.getKey();
            if (resLoc.getResourceDomain().equals("minecraft") && resLoc.getResourcePath().startsWith("skins/")) {
                it.remove();
            }
        }
    }
}
