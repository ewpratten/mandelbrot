package ca.retrylife.mandelbrot.jni;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.retrylife.nativetools.RuntimeLibraryLoader;

import com.raylib.Jaylib;
import com.raylib.Jaylib.Vector2;
import com.raylib.Raylib.Rectangle;
import com.raylib.Raylib.RenderTexture2D;

/**
 * Utilities for rendering a Mandelbrot Set
 */
public class MandelbrotRenderer {
    private final static Logger logger = LoggerFactory.getLogger(MandelbrotRenderer.class);

    // JNI loading
    // static {

    // // Load C++ library
    // logger.info("java.library.path: " + System.getProperty("java.library.path"));

    // try {
    // System.loadLibrary("bridge");
    // } catch (Error | Exception e) {
    // logger.error("Failed to load JNI bridge library: ", e);
    // }

    // logger.info("Native library initialization sequence complete.");

    // }

    // JNI
    private static RuntimeLibraryLoader<MandelbrotRenderer> jniLoader = new RuntimeLibraryLoader<>("bridge",
            MandelbrotRenderer.class);

    static {
        jniLoader.load();
    }

    private MandelbrotRenderer() {

    }

    // Texture cache
    private static RenderTexture2D renderCache = null;

    /**
     * Build the image cache
     * 
     * @param widthPX  Image texture width in pixels
     * @param heightPX Image texture height in pixels
     * @param viewport The active viewport
     */
    private static void buildImageCache(int widthPX, int heightPX, Rectangle viewport) {

        // Set up a texture
        renderCache = Jaylib.LoadRenderTexture(widthPX, heightPX);
        Jaylib.BeginTextureMode(renderCache);
        Jaylib.ClearBackground(Jaylib.BLACK);
        Jaylib.EndTextureMode();

        // Alloc resources
        // Suppressing a false-positive "resource leak" warning on this pointer
        @SuppressWarnings("resource")
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

    /**
     * Check if the image cache is built
     * 
     * @return Is built?
     */
    public static boolean isCacheBuilt() {
        return renderCache != null;
    }

    /**
     * Reset and clear the image cache
     */
    public static void resetRenderCache() {

        // Unload and free
        if (renderCache != null) {
            Jaylib.UnloadRenderTexture(renderCache);
            renderCache.close();
        }
        renderCache = null;
    }

    /**
     * Render the Mandelbrot Set from cache, or build the cache if it does not exist
     * 
     * @param widthPX  Image texture width in pixels
     * @param heightPX Image texture height in pixels
     * @param viewport The active viewport
     */
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

    /**
     * Compute the Mandelbrot Set
     * 
     * @param x        Screenspace pixel X coord
     * @param y        Screenspace pixel Y coord
     * @param widthPX  Screen width
     * @param heightPX Screen height
     * @param viewport The active viewport
     * @return Pixel value (uint32_t)RRGGBB
     */
    private static int computeMandelbrotPixel(int x, int y, int widthPX, int heightPX, Rectangle viewport) {
        return computeMandelbrotPixel(x, y, widthPX, heightPX, viewport.x(), viewport.y(),
                viewport.x() + viewport.width(), viewport.y() + viewport.height());
    }

    /**
     * Compute the Mandelbrot Set natively
     * 
     * @param x                 Screenspace pixel X coord
     * @param y                 Screenspace pixel Y coord
     * @param widthPX           Screen width
     * @param heightPX          Screen height
     * @param topLeftXCoord     Screenspace pixel X coord of the top left of the
     *                          viewport
     * @param topLeftYCoord     Screenspace pixel Y coord of the top left of the
     *                          viewport
     * @param bottomRightXCoord Screenspace pixel X coord of the bottom right of the
     *                          viewport
     * @param bottomRightYCoord Screenspace pixel y coord of the bottom right of the
     *                          viewport
     * @return Pixel value (uint32_t)RRGGBB
     */
    private static native int computeMandelbrotPixel(int x, int y, int widthPX, int heightPX, double topLeftXCoord,
            double topLeftYCoord, double bottomRightXCoord, double bottomRightYCoord);

}