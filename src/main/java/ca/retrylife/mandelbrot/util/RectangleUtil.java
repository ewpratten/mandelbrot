package ca.retrylife.mandelbrot.util;

import com.raylib.Jaylib.Vector2;
import com.raylib.Raylib.Rectangle;

/**
 * Utilities for working with rectangles
 */
public class RectangleUtil {

    /**
     * Build a rectangle from any two opposing corners
     * 
     * @param cornerA Corner A
     * @param cornerB Corner B
     * @return Rectangle
     */
    public static Rectangle buildRectFromUnknownCorners(Vector2 cornerA, Vector2 cornerB) {

        // Determine the width and height
        float width = Math.abs(cornerA.x() - cornerB.x());
        float height = Math.abs(cornerA.y() - cornerB.y());

        // Top left corner pointer
        Vector2 topLeft = null;

        // Determine the position of cornerA
        // cornerB MUST be the opposing corner
        if (cornerA.x() < cornerB.x()) { // Left
            if (cornerA.y() < cornerB.y()) {// Top

                // This is top left
                topLeft = cornerA;

            } else { // Bottom

                // Build the top left corner
                topLeft = new Vector2();
                topLeft.x(cornerA.x());
                topLeft.y(cornerB.y());

            }
        } else { // Right
            if (cornerB.y() > cornerA.y()) {// Top

                // Build the top left corner
                topLeft = new Vector2();
                topLeft.x(cornerB.x());
                topLeft.y(cornerA.y());

            } else { // Bottom

                // cornerB is the top left
                topLeft = cornerB;

            }
        }

        // Build rect
        Rectangle rect = new Rectangle();
        rect.x(topLeft.x());
        rect.y(topLeft.y());
        rect.width(width);
        rect.height(height);

        return rect;
    }

}