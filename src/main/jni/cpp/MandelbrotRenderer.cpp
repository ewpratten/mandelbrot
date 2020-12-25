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

JNIEXPORT jint JNICALL
Java_ca_retrylife_mandelbrot_jni_MandelbrotRenderer_computeMandelbrotPixel(
    JNIEnv *env, jclass clazz, jint x, jint y, jint widthPX, jint heightPX,
    jdouble topLeftXCoord, jdouble topLeftYCoord, jdouble bottomRightXCoord,
    jdouble bottomRightYCoord) {
    // Init values
    double zx = 0;
    double zy = 0;
    double tmp = 0;

    // Set the coord pixel
    // float cX = (((x - (widthPX / 2)) / widthPX) *
    //             (bottomRightXCoord - topLeftXCoord)) +
    //            topLeftXCoord;
    // float cY = (((y - (heightPX / 2)) / heightPX) *
    //             (bottomRightYCoord - topLeftYCoord)) +
    //            topLeftYCoord;

    // cX = (x - (widthPX / 2)) / 50;
    // cY = (y - (heightPX / 2)) / 50;

    // cX = (((x / widthPX) * fabs(bottomRightXCoord - topLeftXCoord)) -
    //       (fabs(bottomRightXCoord - topLeftXCoord) / 2)) /
    //      RENDER_SCALE;
    // cY = (((y / heightPX) * fabs(bottomRightYCoord - topLeftYCoord)) -
    //       (fabs(bottomRightYCoord - topLeftYCoord) / 2)) /
    //      RENDER_SCALE;

    // Map raw pixel to viewport, then to screenspace percentage
    double xCoordScreenSpacePercentage =
        map(x, 0, widthPX, topLeftXCoord, bottomRightXCoord) / widthPX;
    double yCoordScreenSpacePercentage =
        map(y, 0, heightPX, topLeftYCoord, bottomRightYCoord) / heightPX;

    // Map screenspace percentage to graph coord
    double cX = map(xCoordScreenSpacePercentage, 0, 1, FUN_X_MIN, FUN_X_MAX);
    double cY = map(yCoordScreenSpacePercentage, 0, 1, FUN_Y_MIN, FUN_Y_MAX);

    // std::cout << x << ",i " << widthPX <<", " << xCoordScreenSpacePercentage<< std::endl;
    // std::cout << xCoordScreenSpacePercentage << ", " << yCoordScreenSpacePercentage << std::endl;
    // std::cout << cX << ", " << cY << std::endl;

    //  Pseudo-recurse the function
    int iter = MAX_DEPTH;
    while (zx * zx + zy * zy < 4 && iter > 0) {
        tmp = zx * zx - zy * zy + cX;
        zy = 2.0 * zx * zy + cY;
        zx = tmp;
        iter--;
    }

    // int n = 0;

    // while (n++ < MAX_DEPTH && (zx * zx + zy * zy) < 4) {
    //     tmp = zx * zx - zy * zy + cX;

    //     zy = 2 * zx * zy + cY;
    //     zx = tmp;
    // }

    // Plot the pixel
    return iter | (iter << 8);
    // return (n) % 255;
    // return iter / MAX_DEPTH;
    // return (n >= MAX_DEPTH) ? 0 : n / MAX_DEPTH;
    // return 128;
}
