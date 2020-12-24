
package ca.retrylife.mandelbrot;

import com.raylib.Jaylib;
import com.raylib.Raylib;
import com.raylib.Jaylib.Camera;
import com.raylib.Jaylib.Vector3;


public class App {

    public static void main(String[] args) {

        // Init window
        Jaylib.InitWindow(800, 600, "Mandelbrot Set");
        Jaylib.SetTargetFPS(60);
        Camera camera = new Camera(new Vector3(18,16,18),new Vector3(), new Vector3(0,1,0), 45, 0);

        while (!Jaylib.WindowShouldClose()) {
            
            // Handle camera
            Jaylib.UpdateCamera(camera);

            // Begin 2d context
            Jaylib.BeginDrawing();
            Jaylib.ClearBackground(Jaylib.BLACK);

            // Render HUD
            Jaylib.DrawFPS(20, 20);
            Jaylib.DrawText("Select an area to zoom", 20, 40, 20, Jaylib.RAYWHITE);
            Jaylib.DrawText("Press backspace to reset", 20, 60, 20, Jaylib.RAYWHITE);


            Jaylib.EndDrawing();
        }
        Raylib.CloseWindow();

    }
}
