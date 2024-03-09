package makamys.dtools.diagnostics;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import makamys.dtools.listener.IFMLEventListener;
import makamys.dtools.util.JavaUtil;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;

public class ExtraRAMInfo implements IFMLEventListener {
    
    public static ExtraRAMInfo instance;
    
    private boolean renderDebugText = false;
    
    private long allocatedInLastSecond;
    private long allocatedInThisSecond;
    private long lastUsedRam;
    private long lastUsedRamTimestamp;
    
    public ExtraRAMInfo() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.type.equals(RenderGameOverlayEvent.ElementType.DEBUG)) {
            renderDebugText = true;
        } else if (renderDebugText && (event instanceof RenderGameOverlayEvent.Text) && event.type.equals(RenderGameOverlayEvent.ElementType.TEXT)) {
            renderDebugText = false;
            RenderGameOverlayEvent.Text text = (RenderGameOverlayEvent.Text) event;
            text.right.add(null);
            
            long used = JavaUtil.getHeapUsage();
            long now = System.nanoTime();
            
            long allocatedSinceLast = used - lastUsedRam;
            if(allocatedSinceLast > 0) {
                allocatedInThisSecond += allocatedSinceLast;
            }
            if(now / 1_000_000_000 != lastUsedRamTimestamp / 1_000_000_000) {
                allocatedInLastSecond = allocatedInThisSecond;
                allocatedInThisSecond = 0;
            }
            
            lastUsedRam = used;
            lastUsedRamTimestamp = now;
            
            text.right.add("Allocation rate: " + allocatedInLastSecond / 1024L / 1024L + "MB/s");
        }
    }
    
}
