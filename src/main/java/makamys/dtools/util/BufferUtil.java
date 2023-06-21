package makamys.dtools.util;

import java.nio.ByteBuffer;

import org.apache.commons.lang3.ArrayUtils;

public class BufferUtil {
    public static byte[] byteBufferToArray(ByteBuffer buffer) {
        byte[] dst = new byte[buffer.limit()];
        int pos = buffer.position();
        buffer.position(0);
        buffer.get(dst);
        buffer.position(pos);
        return dst;
    }
    
    public static String byteBufferToString(ByteBuffer buffer) {
        byte[] bytes = byteBufferToArray(buffer);
        return new String(bytes, 0, ArrayUtils.indexOf(bytes, (byte)0));
    }
}
