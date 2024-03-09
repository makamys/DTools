package makamys.dtools.util;

public class JavaUtil {
    
    public static long getHeapUsage() {
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        long used = total - free;
        return used;
    }
    
}
