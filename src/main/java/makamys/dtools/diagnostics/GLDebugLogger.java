package makamys.dtools.diagnostics;

import static makamys.dtools.DTools.MODID;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.KHRDebugCallback;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import makamys.dtools.listener.IFMLEventListener;
import makamys.dtools.util.BufferUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;

/** Adapted from {@link KHRDebugCallback} */
public class GLDebugLogger implements IFMLEventListener {
    
    public static GLDebugLogger instance;
    
    /** Severity levels. */
    private static final int
        GL_DEBUG_SEVERITY_HIGH         = 0x9146,
        GL_DEBUG_SEVERITY_MEDIUM       = 0x9147,
        GL_DEBUG_SEVERITY_LOW          = 0x9148,
        GL_DEBUG_SEVERITY_NOTIFICATION = 0x826B;

    /** Sources. */
    private static final int
        GL_DEBUG_SOURCE_API             = 0x8246,
        GL_DEBUG_SOURCE_WINDOW_SYSTEM   = 0x8247,
        GL_DEBUG_SOURCE_SHADER_COMPILER = 0x8248,
        GL_DEBUG_SOURCE_THIRD_PARTY     = 0x8249,
        GL_DEBUG_SOURCE_APPLICATION     = 0x824A,
        GL_DEBUG_SOURCE_OTHER           = 0x824B;

    /** Types. */
    private static final int
        GL_DEBUG_TYPE_ERROR               = 0x824C,
        GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR = 0x824D,
        GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR  = 0x824E,
        GL_DEBUG_TYPE_PORTABILITY         = 0x824F,
        GL_DEBUG_TYPE_PERFORMANCE         = 0x8250,
        GL_DEBUG_TYPE_OTHER               = 0x8251,
        GL_DEBUG_TYPE_MARKER              = 0x8268;
    
    public static final Logger LOGGER = LogManager.getLogger(MODID + "-gldebug");
    
    private static BlockingQueue<QueuedMessage> queuedMessages = new LinkedBlockingQueue<>();
    
    @Data
    @RequiredArgsConstructor
    private static class QueuedMessage {
        private final int source, type, id, severity;
        private final String message;
    }
    
    private static boolean hasRegisteredCallback;
    private boolean renderDebugText = false;
    
    private static Map<String, Integer> msgCount = new HashMap<>();
    private static int totalMsgCount = 0;
    private static final int MAX_REPEATS = 100;
    
    @Override
    public void onInit(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }
    
    // If I do this in the main menu or at init, it doesn't work
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onConnectToServer(ClientConnectedToServerEvent event) {
        init();
    }
    
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        for(QueuedMessage msg = queuedMessages.poll(); msg != null; msg = queuedMessages.poll()) {
            printMessage(msg.source, msg.type, msg.id, msg.severity, msg.message, true);
        }
    }
    
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.type.equals(RenderGameOverlayEvent.ElementType.DEBUG)) {
            renderDebugText = true;
        } else if (renderDebugText && (event instanceof RenderGameOverlayEvent.Text) && event.type.equals(RenderGameOverlayEvent.ElementType.TEXT)) {
            renderDebugText = false;
            RenderGameOverlayEvent.Text text = (RenderGameOverlayEvent.Text) event;
            text.right.add(null);
            text.right.add(EnumChatFormatting.AQUA + "" + totalMsgCount + " GL debug message" + (totalMsgCount == 1 ? "" : "s"));
        }
    }
    
    public static void init() {
        if(!hasRegisteredCallback) {
            replayMessageLog();
            GL43.glDebugMessageCallback(new KHRDebugCallback(GLDebugLogger::printMessage));
            hasRegisteredCallback = true;
        }
    }
    
    private static void replayMessageLog() {
        // Print messages logged prior to the installation of the callback handler
        int msgs = -1;
        while(msgs != 0) {
            IntBuffer sources = BufferUtils.createIntBuffer(1);
            IntBuffer ids = BufferUtils.createIntBuffer(1);
            IntBuffer types = BufferUtils.createIntBuffer(1);
            IntBuffer severities = BufferUtils.createIntBuffer(1);
            IntBuffer lengths = BufferUtils.createIntBuffer(1);
            ByteBuffer log = BufferUtils.createByteBuffer(512);
            
            msgs = GL43.glGetDebugMessageLog(1, sources, ids, types, severities, lengths, log);
            
            if(msgs > 0) {
                String msg = BufferUtil.byteBufferToString(log);
                printMessage(sources.get(0), types.get(0), ids.get(0), severities.get(0), msg, true);
            }
        }
    }
    
    static void printMessage(final int source, final int type, final int id, final int severity, String message) {
        printMessage(source, type, id, severity, message, false);
    }
    
    static void printMessage(final int source, final int type, final int id, final int severity, String message, final boolean replay) {
        if(!Minecraft.getMinecraft().func_152345_ab()) {
            // If we're not on the main thread, logging doesn't work (idk why). Queue it up and replay it on the main thread instead.
            queuedMessages.add(new QueuedMessage(source, type, id, severity, message));
            return;
        }
        
        int count = msgCount.computeIfAbsent(message, (s) -> 0);
        
        if(!replay) {
            msgCount.put(message, count + 1);
        }
        
        if(count <= MAX_REPEATS) {
            totalMsgCount++;
            if(count == MAX_REPEATS) {
                message = "Too many identical messages, ignoring";
            }
            originalHandleMessage(source, type, id, severity, message, replay);
        }
    }
    
    private static void originalHandleMessage(final int source, final int type, final int id, final int severity, final String message, final boolean replay) {
        LOGGER.debug("[LWJGL] KHR_debug message" + (replay ? " (replay)" : ""));
        
        if(!replay) {
            LOGGER.debug("\tStack trace: " + createStackTrace());
        }
        
        LOGGER.debug("\tID: " + id);

        String description;
        switch ( source ) {
            case GL_DEBUG_SOURCE_API:
                description = "API";
                break;
            case GL_DEBUG_SOURCE_WINDOW_SYSTEM:
                description = "WINDOW SYSTEM";
                break;
            case GL_DEBUG_SOURCE_SHADER_COMPILER:
                description = "SHADER COMPILER";
                break;
            case GL_DEBUG_SOURCE_THIRD_PARTY:
                description = "THIRD PARTY";
                break;
            case GL_DEBUG_SOURCE_APPLICATION:
                description = "APPLICATION";
                break;
            case GL_DEBUG_SOURCE_OTHER:
                description = "OTHER";
                break;
            default:
                description = printUnknownToken(source);
        }
        LOGGER.debug("\tSource: " + description);

        switch ( type ) {
            case GL_DEBUG_TYPE_ERROR:
                description = "ERROR";
                break;
            case GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR:
                description = "DEPRECATED BEHAVIOR";
                break;
            case GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR:
                description = "UNDEFINED BEHAVIOR";
                break;
            case GL_DEBUG_TYPE_PORTABILITY:
                description = "PORTABILITY";
                break;
            case GL_DEBUG_TYPE_PERFORMANCE:
                description = "PERFORMANCE";
                break;
            case GL_DEBUG_TYPE_OTHER:
                description = "OTHER";
                break;
            case GL_DEBUG_TYPE_MARKER:
                description = "MARKER";
                break;
            default:
                description = printUnknownToken(type);
        }
        LOGGER.debug("\tType: " + description);

        switch ( severity ) {
            case GL_DEBUG_SEVERITY_HIGH:
                description = "HIGH";
                break;
            case GL_DEBUG_SEVERITY_MEDIUM:
                description = "MEDIUM";
                break;
            case GL_DEBUG_SEVERITY_LOW:
                description = "LOW";
                break;
            case GL_DEBUG_SEVERITY_NOTIFICATION:
                description = "NOTIFICATION";
                break;
            default:
                description = printUnknownToken(severity);
        }
        LOGGER.debug("\tSeverity: " + description);

        LOGGER.debug("\tMessage: " + message);
    }

    private static String createStackTrace() {
        String trace = "";
        StackTraceElement[] stes = new Throwable().getStackTrace();
        for(int i = 0; i < stes.length; i++) {
            StackTraceElement ste = stes[i];
            if(!ste.getClassName().equals(GLDebugLogger.class.getName())) {
                trace += ste.getClassName() + "." + ste.getMethodName() + ":" + ste.getLineNumber() + (i < stes.length - 1 ? " < " : "");
            }
        }
        return trace;
    }

    private static String printUnknownToken(final int token) {
        return "Unknown (0x" + Integer.toHexString(token).toUpperCase() + ")";
    }
}
