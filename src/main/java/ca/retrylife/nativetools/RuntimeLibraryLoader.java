package ca.retrylife.nativetools;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import ca.retrylife.nativetools.VMHost.OperatingSystem;

public class RuntimeLibraryLoader<BridgeClass> {

    private final String name;
    private final Class<BridgeClass> clazz;

    public RuntimeLibraryLoader(String name, Class<BridgeClass> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    private String buildLoadErrorString(UnsatisfiedLinkError error) {

        // Set up message builder
        StringBuilder msg = new StringBuilder(512);

        // Add base information to the message
        msg.append(this.name);
        msg.append(" could not be loaded from path or an embedded resource.\n" + "\tattempted to load for platform ");
        msg.append(VMHost.getNativePlatformLibraryBasePath());
        msg.append("\nLast Load Error: \n");
        msg.append(error.getMessage());
        msg.append('\n');

        // Handle extra info for Windows
        if (VMHost.getOS().equals(OperatingSystem.WINDOWS)) {
            msg.append("A common cause of this error is missing the C++ runtime.\n"
                    + "Download the latest at https://support.microsoft.com/en-us/help/2977003/the-latest-supported-visual-c-downloads\n");
        }

        return msg.toString();
    }

    public void load() {

        // Try loading from the library path
        try {
            System.loadLibrary(this.name);
            return;
        } catch (UnsatisfiedLinkError e) {

            // Get the library path
            String libPath = VMHost.getLibraryResourcePath(this.name);

            // Try loading the path manually
            try {
                System.loadLibrary(
                        Paths.get(this.clazz.getClassLoader().getResource(libPath).toURI()).toFile().getAbsolutePath());
                return;
            } catch (URISyntaxException e1) {
                
            }

        }
    }

}