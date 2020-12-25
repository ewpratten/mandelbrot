
package ca.retrylife.mandelbrot;

import com.raylib.Jaylib;
import com.raylib.Raylib;
import com.raylib.Jaylib.Vector2;
import com.raylib.Raylib.Rectangle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.retrylife.mandelbrot.jni.MandelbrotRenderer;
import ca.retrylife.mandelbrot.util.RectangleUtil;
import ca.retrylife.mandelbrot.util.VectorUtil;

/**
 * Application entrypoint
 */
public class App {
    private final static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        // Init window
        logger.info("Setting up window");
        Jaylib.InitWindow(800, 600, "Mandelbrot Set");
        Jaylib.SetTargetFPS(60);

        // Zoom UI
        boolean isUserDrawingZoomBox = false;
        Vector2 zoomBoxStart = null;
        Vector2 zoomBoxEnd = null;

        // Viewport
        Rectangle viewport = new Rectangle();
        viewport.x(0);
        viewport.y(0);
        viewport.width(Jaylib.GetScreenWidth());
        viewport.height(Jaylib.GetScreenHeight());

        logger.info("Beginning render loop");
        while (!Jaylib.WindowShouldClose()) {

            /* Handle rendering */

            // Begin 2d context
            Jaylib.BeginDrawing();
            Jaylib.ClearBackground(Jaylib.RAYWHITE);

            // Handle "Rebuilding Render Cache" message
            if (!MandelbrotRenderer.isCacheBuilt()) {
                Jaylib.DrawText("Rebuilding Render Cache", 100, 100, 20, Jaylib.MAROON);

                // Reset the screen
                Jaylib.EndDrawing();
                Jaylib.BeginDrawing();
            }

            // Render the mandelbrot set
            MandelbrotRenderer.renderMandelbrotSet(Jaylib.GetScreenWidth(), Jaylib.GetScreenHeight(), viewport);

            // Render zoom box
            if (isUserDrawingZoomBox && zoomBoxStart != null && zoomBoxEnd != null) {

                Jaylib.DrawRectangleLinesEx(RectangleUtil.buildRectFromUnknownCorners(zoomBoxStart, zoomBoxEnd), 3,
                        Jaylib.PURPLE);
            }

            // Render HUD
            Jaylib.DrawText(String.format("(%.2f, %.2f):(%.2f, %.2f)", viewport.x(), viewport.y(), viewport.width(),
                    viewport.height()), 0, Jaylib.GetScreenHeight() - 10, 5, Jaylib.GRAY);
            Jaylib.DrawFPS(20, 20);
            Jaylib.DrawText("Select an area to zoom", 20, 40, 20, Jaylib.BLACK);
            Jaylib.DrawText("Press backspace to reset", 20, 60, 20, Jaylib.BLACK);

            Jaylib.EndDrawing();

            /* Handle user inputs */

            // Check for VP reset
            if (Jaylib.IsKeyPressed(Jaylib.KEY_BACKSPACE)) {

                // Reset the VP
                viewport.x(0);
                viewport.y(0);
                viewport.width(Jaylib.GetScreenWidth());
                viewport.height(Jaylib.GetScreenHeight());

                // Reset the zoom UI
                isUserDrawingZoomBox = false;
                zoomBoxStart = null;
                zoomBoxEnd = null;

                // Reset the render cache
                MandelbrotRenderer.resetRenderCache();

            }

            // Handle zooming
            if (Jaylib.IsMouseButtonDown(Jaylib.MOUSE_LEFT_BUTTON)) {

                // Get the mouse coord
                Raylib.Vector2 mouseCoord = Jaylib.GetMousePosition();

                // If not previously zooming, set the zoom box start location
                if (!isUserDrawingZoomBox) {

                    // Deep copy the pointer contents
                    zoomBoxStart = new Vector2();
                    zoomBoxStart.x(mouseCoord.x());
                    zoomBoxStart.y(mouseCoord.y());
                }

                // Set the end of the box
                zoomBoxEnd = new Vector2();
                zoomBoxEnd.x(mouseCoord.x());
                zoomBoxEnd.y(mouseCoord.y());

                // Enable "zooming mode"
                isUserDrawingZoomBox = true;

            } else {

                // If this is occurring right after the mouse is released, do zoom logic
                if (isUserDrawingZoomBox) {

                    // Get the screen size
                    int screenWidth = Jaylib.GetScreenWidth();
                    int screenHeight = Jaylib.GetScreenHeight();

                    // Get mapped vectors from screenspace to graph space
                    Vector2 mappedStart = VectorUtil.mapVectorFromScreenToViewport(zoomBoxStart, screenWidth,
                            screenHeight, viewport);
                    Vector2 mappedEnd = VectorUtil.mapVectorFromScreenToViewport(zoomBoxEnd, screenWidth, screenHeight,
                            viewport);

                    // Build a new viewport
                    viewport = RectangleUtil.buildRectFromUnknownCorners(mappedStart, mappedEnd);

                    // Reset the render cache
                    MandelbrotRenderer.resetRenderCache();

                }

                // Disable "zooming mode"
                isUserDrawingZoomBox = false;

            }

        }

        // Clean up
        logger.info("Freeing resources");
        Raylib.CloseWindow();

    }

}
