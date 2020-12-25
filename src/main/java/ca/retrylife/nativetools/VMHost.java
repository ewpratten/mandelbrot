package ca.retrylife.nativetools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.Nullable;

/**
 * Information about the VM Host
 */
public class VMHost {

    // Base path of all native libraries
    public static final String NATIVE_LIBRARY_BASE_PATH = "NATIVE";

    // System details
    private static OperatingSystem hostOS = null;
    private static HostArchitecture hostArch = null;

    // Details about native libraries
    private static String nativePlatformLibraryBasePath = null;
    private static String nativePlatformLibraryFilePrefix = null;
    private static String nativePlatformLibraryFileExtension = null;

    /**
     * All possible operating systems
     */
    public static enum OperatingSystem {
        WINDOWS, LINUX, DARWIN, RASPBIAN, ATHENA, UNKNOWN;
    }

    /**
     * ISA variants
     */
    public static enum ArchitectureVariant {
        INTEL, ARM;
    }

    /**
     * Architecture of a host
     */
    public static enum HostArchitecture {
        aarch64(ArchitectureVariant.ARM, 64), x86(ArchitectureVariant.INTEL, 32), x64(ArchitectureVariant.INTEL, 64),
        UNKNOWN(null, 0);

        private final ArchitectureVariant variant;
        private final int wordWidth;

        private HostArchitecture(ArchitectureVariant variant, int wordWidth) {
            this.variant = variant;
            this.wordWidth = wordWidth;
        }

        public ArchitectureVariant getVariant() {
            return this.variant;
        }

        public int getWordWidth() {
            return this.wordWidth;
        }
    }

    /**
     * Get the host's operating system
     * 
     * @return OperatingSystem
     */
    public static OperatingSystem getOS() {

        // Handle caching
        if (hostOS == null) {
            buildCache: {

                // Raspbian
                try (BufferedReader reader = Files.newBufferedReader(Paths.get("/etc/os-release"))) {
                    String value = reader.readLine();
                    if (value.contains("Raspbian")) {
                        hostOS = OperatingSystem.RASPBIAN;
                        break buildCache;
                    }
                } catch (IOException ex) {
                }

                // Athena
                File athenaEntrypoint = new File("/usr/local/frc/bin/frcRunRobot.sh");
                if (athenaEntrypoint.exists()) {
                    hostOS = OperatingSystem.ATHENA;
                }

                // Major OSes
                String osName = System.getProperty("os.name");
                if (osName.startsWith("Linux")) {
                    hostOS = OperatingSystem.LINUX;
                    break buildCache;
                } else if (osName.startsWith("Windows")) {
                    hostOS = OperatingSystem.WINDOWS;
                    break buildCache;
                } else if (osName.startsWith("Mac")) {
                    hostOS = OperatingSystem.DARWIN;
                    break buildCache;
                }

                // Unknown
                hostOS = OperatingSystem.UNKNOWN;
                break buildCache;
            }
        }

        return hostOS;

    }

    /**
     * Get the host's architecture
     * 
     * @return HostArchitecture
     */
    public static @Nullable HostArchitecture getArch() {

        if (hostArch == null) {
            // Get the arch property
            String osArch = System.getProperty("os.arch");

            // Handle output
            if (osArch.equals("aarch64")) {
                hostArch = HostArchitecture.aarch64;
            } else if (osArch.equals("x86") || osArch.equals("i386")) {
                hostArch = HostArchitecture.x86;
            } else if (osArch.equals("x86_64") || osArch.equals("amd64")) {
                hostArch = HostArchitecture.x64;
            } else {
                hostArch = HostArchitecture.UNKNOWN;
            }
        }

        return hostArch;
    }

    /**
     * Get the base path containing libraries for the host system
     * 
     * @return Base path
     */
    public static String getNativePlatformLibraryBasePath() {

        // Handle constructing the path
        if (nativePlatformLibraryBasePath == null) {

            switch (getOS()) {
                case ATHENA:
                    nativePlatformLibraryBasePath = "/linux/athena/";
                    break;
                case DARWIN:
                    if (getArch().equals(HostArchitecture.x64)) {
                        nativePlatformLibraryBasePath = "/osx/x86-64/";
                    } else if (getArch().equals(HostArchitecture.x86)) {
                        nativePlatformLibraryBasePath = "/osx/x86/";
                    } else {
                        throw new IllegalStateException(
                                String.format("%s is not a supported architecture on Darwin", getArch().toString()));
                    }
                    break;
                case LINUX:
                    if (getArch().equals(HostArchitecture.x64)) {
                        nativePlatformLibraryBasePath = "/linux/x86-64/";
                    } else if (getArch().equals(HostArchitecture.x86)) {
                        nativePlatformLibraryBasePath = "/linux/x86/";
                    } else if (getArch().equals(HostArchitecture.aarch64)) {
                        nativePlatformLibraryBasePath = "/linux/aarch64bionic/";
                    } else {
                        throw new IllegalStateException(
                                String.format("%s is not a supported architecture on Darwin", getArch().toString()));
                    }
                    break;
                case RASPBIAN:
                    nativePlatformLibraryBasePath = "/linux/raspbian/";
                    break;
                case WINDOWS:
                    if (getArch().equals(HostArchitecture.x64)) {
                        nativePlatformLibraryBasePath = "/windows/x86-64/";
                    } else if (getArch().equals(HostArchitecture.x86)) {
                        nativePlatformLibraryBasePath = "/windows/x86/";
                    } else {
                        throw new IllegalStateException(
                                String.format("%s is not a supported architecture on Windows", getArch().toString()));
                    }
                    break;
                default:
                    throw new IllegalStateException(
                            String.format("%s is not a supported operating system", getOS().toString()));

            }

        }

        return nativePlatformLibraryBasePath;
    }

    /**
     * Get the shared object file extension for the host
     * 
     * @return SO extension
     */
    public static String getNativePlatformLibraryFileExtension() {

        // Handle cache
        if (nativePlatformLibraryFileExtension == null) {

            if (getOS().equals(OperatingSystem.WINDOWS)) {
                nativePlatformLibraryFileExtension = ".dll";
            } else if (getOS().equals(OperatingSystem.DARWIN)) {
                nativePlatformLibraryFileExtension = ".dylib";
            } else {
                nativePlatformLibraryFileExtension = ".so";
            }

        }

        return nativePlatformLibraryFileExtension;
    }

    /**
     * Get the library name prefix for the host
     * 
     * @return Lib prefix
     */
    public static String getNativePlatformLibraryFilePrefix() {

        // Handle cache
        if (nativePlatformLibraryFilePrefix == null) {

            if (getOS().equals(OperatingSystem.WINDOWS)) {
                nativePlatformLibraryFilePrefix = "";
            } else {
                nativePlatformLibraryFilePrefix = "lib";
            }

        }

        return nativePlatformLibraryFilePrefix;
    }

    /**
     * Gets the file path for a library
     * 
     * @param libName Library name
     * @return File path
     */
    public static String getLibraryResourcePath(String libName) {
        return String.format("%s%s%s%s%s", NATIVE_LIBRARY_BASE_PATH, getNativePlatformLibraryBasePath(),
                getNativePlatformLibraryFilePrefix(), libName, getNativePlatformLibraryFileExtension());
    }

}