package ca.retrylife.mandelbrot.jni;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.raylib.Jaylib;
import com.raylib.Jaylib.Vector2;
import com.raylib.Raylib.Rectangle;
import com.raylib.Raylib.RenderTexture2D;

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

    private static RenderTexture2D renderCache = null;

    private static void buildImageCache(int widthPX, int heightPX, Rectangle viewport) {

        // Set up a texture
        renderCache = Jaylib.LoadRenderTexture(widthPX, heightPX);
        Jaylib.BeginTextureMode(renderCache);
        Jaylib.ClearBackground(Jaylib.BLACK);
        Jaylib.EndTextureMode();

        // Alloc resources
        Jaylib.Color color = new Jaylib.Color();
        int pixel = 0;

        // Handle every pixel
        Jaylib.BeginTextureMode(renderCache);
        for (int x = 0; x < widthPX; x++) {
            for (int y = 0; y < heightPX; y++) {

                // Get the pixel value
                pixel = computeMandelbrotPixel(x, y, widthPX, heightPX, viewport);

                // Convert to RGB
                if (pixel == 0) {
                    color = Jaylib.BLACK;
                } else {
                    color.r((byte) (pixel >> 16));
                    color.g((byte) (pixel >> 8));
                    color.b((byte) (pixel));
                    color.a((byte) 255);
                }

                // Plot the pixel
                Jaylib.DrawPixel(x, y, color);
            }
        }
        Jaylib.EndTextureMode();
    }

    public static boolean isCacheBuilt() {
        return renderCache != null;
    }

    public static void resetRenderCache() {

        // Unload and free
        if (renderCache != null) {
            Jaylib.UnloadRenderTexture(renderCache);
            renderCache.close();
        }
        renderCache = null;
    }

    public static void renderMandelbrotSet(int widthPX, int heightPX, Rectangle viewport) {

        // Check if the cache needs to be rebuilt
        if (!isCacheBuilt()) {
            buildImageCache(widthPX, heightPX, viewport);
        }

        // Build a rect that will Y-flip the texture (convert from OpenGL coords to
        // Raylib coords)
        Rectangle flipRect = new Rectangle();
        flipRect.width(renderCache.texture().width()).height(renderCache.texture().height() * -1);

        // BLIT the render cache
        Jaylib.DrawTextureRec(renderCache.texture(), flipRect, new Vector2(), Jaylib.WHITE);

    }

    private static int computeMandelbrotPixel(int x, int y, int widthPX, int heightPX, Rectangle viewport) {
        return computeMandelbrotPixel(x, y, widthPX, heightPX, viewport.x(), viewport.y(),
                viewport.x() + viewport.width(), viewport.y() + viewport.height());
    }

    private static native int computeMandelbrotPixel(int x, int y, int widthPX, int heightPX, double topLeftXCoord,
            double topLeftYCoord, double bottomRightXCoord, double bottomRightYCoord);

}