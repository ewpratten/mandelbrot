package ca.retrylife.mandelbrot.jni;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.raylib.Jaylib;
import com.raylib.Raylib;
import com.raylib.Jaylib.Vector2;

public class MandelbrotRenderer {
    private final static Logger logger = LoggerFactory.getLogger(MandelbrotRenderer.class);

    static {

    }

    private MandelbrotRenderer() {

    }
    
    public static void renderMandelbrotSet(int widthPX, int heightPX, Vector2 topLeft, Vector2 bottomRight) {
        renderMandelbrotSet(widthPX, heightPX, topLeft.x(), topLeft.y(), bottomRight.x(), bottomRight.y());
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