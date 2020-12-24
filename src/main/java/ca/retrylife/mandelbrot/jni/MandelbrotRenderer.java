package ca.retrylife.mandelbrot.jni;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.raylib.Jaylib;
import com.raylib.Raylib;
import com.raylib.Jaylib.Vector2;
import com.raylib.Raylib.Rectangle;

public class MandelbrotRenderer {
    private final static Logger logger = LoggerFactory.getLogger(MandelbrotRenderer.class);

    static {

        // Load C++ library
        logger.info("java.library.path: " + System.getProperty("java.library.path"));

        try {
            System.loadLibrary("bridge");
        } catch (Error | Exception e) {
            logger.error("Failed to load JNI bridge library: ", e);
        }

        logger.info("Native library initialization sequence complete.");

    }

    private MandelbrotRenderer() {

    }
    
    public static void renderMandelbrotSet(int widthPX, int heightPX, Rectangle viewport) {
        renderMandelbrotSet(widthPX, heightPX, viewport.x(), viewport.y(), viewport.x() + viewport.width(), viewport.y() + viewport.height());
    }
    
    private static native void renderMandelbrotSet(int widthPX, int heightPX, double topLeftXCoord, double topLeftYCoord, double bottomRightXCoord, double bottomRightYCoord);

    private static void plotPX(int x, int y, int r, int g, int b) {

        // Create a color struct
        Raylib.Color color = new Raylib.Color();
        color.r((byte) r).g((byte) g).b((byte) b);

        // Plot the pixel
        Jaylib.DrawPixel(x, y, color);

        // Free the color
        color.close();
    }

}