package makamys.dtools.mixin.tweak.thaumcraft.unlockallaspects;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import makamys.dtools.CheatHelper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.client.lib.ClientTickEventsFML;

@Mixin(value = ClientTickEventsFML.class, remap = false)
abstract class MixinClientTickEventsFML {
    
    private int dtls$itemHash;
    private WeakReference<EntityPlayer> dtls$player;
    
    @Inject(method = "renderAspectsInGui", at = @At("HEAD"))
    private void storePlayer(GuiContainer gui, EntityPlayer player, CallbackInfo ci) {
        dtls$player = new WeakReference<>(player);
    }
    
    @ModifyVariable(method = "renderAspectsInGui", at = @At("STORE"), name = "h")
    private int storeItemHash(int itemHash) {
        dtls$itemHash = itemHash;
        return itemHash;
    }
    
    @ModifyVariable(method = "renderAspectsInGui", at = @At("STORE"), name = "list")
    private List<String> haveEverythingScanned(List<String> list) {
        if(CheatHelper.isCreative(dtls$player.get())) {
            if(list == null) {
                list = new ArrayList<>();
            }
            list.add("@" + dtls$itemHash);
        }
        return list;
    }
    
}
