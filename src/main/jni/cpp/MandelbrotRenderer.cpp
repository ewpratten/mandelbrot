#include <iostream>

#include "ca_retrylife_mandelbrot_jni_MandelbrotRenderer.h"

#define RED_CHAN(x) (uint8_t)(x >> 16)
#define GREEN_CHAN(x) (uint8_t)(x >> 8)
#define BLUE_CHAN(x) (uint8_t)(x)

#define MAX_DEPTH 100

JNIEXPORT void JNICALL
Java_ca_retrylife_mandelbrot_jni_MandelbrotRenderer_renderMandelbrotSet(
    JNIEnv *env, jclass clazz, jint widthPX, jint heightPX,
    jdouble topLeftXCoord, jdouble topLeftYCoord, jdouble bottomRightXCoord,
    jdouble bottomRightYCoord) {
    // Data vars
    double zx, zy, cX, cY, tmp;

    // Get the pixel plotting function
    jclass thisClass = env->GetObjectClass(clazz);
    jmethodID plotPX = env->GetStaticMethodID(thisClass, "plotPX", "(IIIII)V");
    if (plotPX == NULL) {
        std::cerr << "Could not load JVM function: plotPX (IIIII)V"
                  << std::endl;
        return;
    }

    // Handle every pixel
    for (int x = 0; x < widthPX; x++) {
        for (int y = 0; y < heightPX; y++) {
            // Reset values
            zx = 0;
            zy = 0;

            // Set the coord pixel
            cX = (((x - (widthPX / 2)) / widthPX) *
                  (bottomRightXCoord - topLeftXCoord)) +
                 topLeftXCoord;
            cY = (((y - (heightPX / 2)) / heightPX) *
                  (bottomRightYCoord - topLeftYCoord)) +
                 topLeftYCoord;

            //  Pseudo-recurse the function
            int iter = MAX_DEPTH;
            while (zx * zx + zy * zy < 4 && iter > 0) {
                tmp = zx * zx - zy * zy + cX;
                zy = 2.0 * zx * zy + cY;
                zx = tmp;
                iter--;
            }

            // Plot the pixel
            uint32_t colorRGB = iter | (iter << 8);
            env->CallVoidMethod(clazz, plotPX, x, y, RED_CHAN(colorRGB), GREEN_CHAN(colorRGB), BLUE_CHAN(colorRGB));
        }
    }
}