package makamys.dtools.tweak.devsetup.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

/* Adapted from makamys.mclib.updatecheck.gui.GuiUpdateGeneric, which was adapted from tconstruct.client.tabs.AbstractTab */
public class GuiButtonGeneric extends GuiButton {
    
    private Runnable clickListener;
    
    public GuiButtonGeneric(int id, int posX, int posY, int width, int height, String text) {
        super(id, posX, posY, width, height, text);
    }

    @Override
    public boolean mousePressed (Minecraft mc, int mouseX, int mouseY) {
        boolean inWindow = this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        
        if (inWindow) {
            this.onClicked();
        }

        return inWindow;
    }

    public void onClicked() {
        if(clickListener != null) {
            clickListener.run();
        }
    }
    
    @Override
    public void drawButton(Minecraft p_146112_1_, int mouseX, int mouseY) {
        super.drawButton(p_146112_1_, mouseX, mouseY);
        
        boolean inWindow = this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        
        if(inWindow) {
            List<String> tooltipStrings = getTooltipStrings();
            if(tooltipStrings != null) {
                drawTooltip(mouseX, mouseY, tooltipStrings);
            }
        }
    }
    
    protected void drawTooltip(int mouseX, int mouseY, List<String> tooltipStrings) {
        Util.drawSimpleTooltip(mouseX, mouseY, (int)this.zLevel, tooltipStrings, Util.Anchor.AUTO);
    }
    
    public List<String> getTooltipStrings() {
        return null;
    }
    
    public GuiButtonGeneric setClickListener(Runnable clickListener) {
        this.clickListener = clickListener;
        return this;
    }
}
