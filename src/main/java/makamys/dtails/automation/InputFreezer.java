package makamys.dtails.automation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import makamys.dtails.CheatHelper;
import makamys.dtails.IModEventListener;
import net.minecraft.util.MouseHelper;

public class InputFreezer implements IModEventListener {
    
    public static InputFreezer instance;
    
    private boolean frozen;
    
    private static final int FREEZE_KEY = Keyboard.KEY_F;
    
    public boolean redirectKeyboardNext() {
        if(active()) {
            while(Keyboard.next()) {
                int key = Keyboard.getEventKey();
                boolean down = Keyboard.getEventKeyState();
                
                if(key == FREEZE_KEY && down) {
                    frozen = !frozen;
                }
                
                if(frozen) {
                    // consume key
                } else {
                    return true;
                }
            }
            return false;
        } else {
            return Keyboard.next();
        }
    }

    public boolean redirectMouseNext() {
        if(active() && frozen) {
            while(Mouse.next()) {
                // consume all events
            }
            return false;
        } else {
            return Mouse.next();
        }
    }

    public void postMouseXYChange(MouseHelper mouseHelper) {
        if(active() && frozen) {
            mouseHelper.deltaX = mouseHelper.deltaY = 0;
        }
    }
    
    private boolean active() {
        return CheatHelper.canCheat();
    }
    
    public static boolean isFrozen() {
        return instance.frozen;
    }
    
    public static void setFrozen(boolean frozen) {
        instance.frozen = frozen;
    }
    
}
