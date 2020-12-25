#include <math.h>

#include <iostream>

#include "MathUtil.hh"
#include "ca_retrylife_mandelbrot_jni_MandelbrotRenderer.h"

#define RED_CHAN(x) (uint8_t)(x >> 16)
#define GREEN_CHAN(x) (uint8_t)(x >> 8)
#define BLUE_CHAN(x) (uint8_t)(x)

// Render settings
#define MAX_DEPTH 512
#define RENDER_SCALE 1

// Function domain
#define FUN_X_MIN -2
#define FUN_X_MAX 1
#define FUN_Y_MIN -1
#define FUN_Y_MAX 1

/**
 * @brief Compute the Mandelbrot Set natively
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
JNIEXPORT jint JNICALL
Java_ca_retrylife_mandelbrot_jni_MandelbrotRenderer_computeMandelbrotPixel(
    JNIEnv *env, jclass clazz, jint x, jint y, jint widthPX, jint heightPX,
    jdouble topLeftXCoord, jdouble topLeftYCoord, jdouble bottomRightXCoord,
    jdouble bottomRightYCoord) {
    // Init storage values
    double zx = 0;
    double zy = 0;
    double tmp = 0;

    // Map raw pixel to viewport, then to screenspace percentage
    double xCoordScreenSpacePercentage =
        map(x, 0, widthPX, topLeftXCoord, bottomRightXCoord) / widthPX;
    double yCoordScreenSpacePercentage =
        map(y, 0, heightPX, topLeftYCoord, bottomRightYCoord) / heightPX;

    // Map screenspace percentage to graph coord
    double cX = map(xCoordScreenSpacePercentage, 0, 1, FUN_X_MIN, FUN_X_MAX);
    double cY = map(yCoordScreenSpacePercentage, 0, 1, FUN_Y_MIN, FUN_Y_MAX);

    // Recursive depth counter
    int n = 0;

    // Calculate Mandelbrot
    while (n++ < MAX_DEPTH && (zx * zx + zy * zy) < 4) {
        tmp = zx * zx - zy * zy + cX;

        zy = 2 * zx * zy + cY;
        zx = tmp;
    }

    //  Calculate the RRGGBB value
    return (MAX_DEPTH - n) | ((MAX_DEPTH - n) << 8);
}
