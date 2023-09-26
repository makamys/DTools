package makamys.dtools.util;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;

public class GuiHelper {
    public static void drawRectangle(int x, int y, int w, int h, int color) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(color);
        tessellator.addVertex(x, y, 0);
        tessellator.addVertex(x, y+h, 0);
        tessellator.addVertex(x+w, y+h, 0);
        tessellator.addVertex(x+w, y, 0);
        
        tessellator.draw();
        
        GL11.glPopAttrib();
    }
}
