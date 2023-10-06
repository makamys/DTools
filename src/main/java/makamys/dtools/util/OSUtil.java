package makamys.dtools.util;

import java.io.IOException;
import java.lang.management.ManagementFactory;

public class OSUtil {
    private static int cachedPid;

    public static void runCommand(String command) {
        try {
            Runtime.getRuntime().exec(command.replaceAll("\\{PID\\}", ""+getPid()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static int getPid() {
        if(cachedPid == -1) {
            try {
                cachedPid = Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return cachedPid;
    }
}
