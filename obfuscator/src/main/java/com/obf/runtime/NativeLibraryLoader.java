package com.obf.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Injected into target JARs. Extracts bundled native libraries to a temp file
 * and loads them with System.load.
 */
public final class NativeLibraryLoader {
    private NativeLibraryLoader() {
    }

    public static void loadBundled(String resourceName) {
        String normalized = resourceName.startsWith("/") ? resourceName : "/" + resourceName;
        String suffix = resourceName.endsWith(".dll") ? ".dll" : null;

        try (InputStream in = NativeLibraryLoader.class.getResourceAsStream(normalized)) {
            if (in == null) {
                throw new UnsatisfiedLinkError("Bundled native library not found: " + normalized);
            }

            Path temp = Files.createTempFile("obf-native-", suffix);
            temp.toFile().deleteOnExit();
            Files.copy(in, temp, StandardCopyOption.REPLACE_EXISTING);
            System.load(temp.toAbsolutePath().toString());
        } catch (IOException e) {
            UnsatisfiedLinkError error = new UnsatisfiedLinkError("Failed to extract native library: " + normalized);
            error.initCause(e);
            throw error;
        }
    }
}
