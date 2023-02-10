package makamys.dtools.asm;

import makamys.dtools.diagnostics.ClassLoadLog;
import net.minecraft.launchwrapper.IClassTransformer;

public class LoggingTransformer implements IClassTransformer {
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        ClassLoadLog.instance.onClassLoaded(transformedName);
        return basicClass;
    }

}
