package ca.retrylife.mandelbrot.util;

import com.raylib.Jaylib.Vector2;
import com.raylib.Raylib.Rectangle;

/**
 * Utilities for working with vectors
 */
public class VectorUtil {

    /**
     * Convert a pixel position vector on a screen to a vector inside a viewport
     * 
     * @param vectorPX     The input vector
     * @param screenWidth  Width of the screen
     * @param screenHeight Height of the screen
     * @param viewport     The viewport
     * @return Resulting vector
     */
    public static Vector2 mapVectorFromScreenToViewport(Vector2 vectorPX, int screenWidth, int screenHeight,
            Rectangle viewport) {

        // Calculate the screenspace percentages
        float xPercentOfWindowWidth = vectorPX.x() / screenWidth;
        float yPercentOfWindowHeight = vectorPX.y() / screenHeight;

        // Create a new coordinate
        Vector2 convertedVector = new Vector2();
        convertedVector.x((viewport.width() * xPercentOfWindowWidth) + viewport.x());
        convertedVector.y((viewport.height() * yPercentOfWindowHeight) + viewport.y());

        return convertedVector;
    }

}