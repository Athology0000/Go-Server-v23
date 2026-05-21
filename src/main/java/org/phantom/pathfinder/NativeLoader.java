package org.phantom.pathfinder;

import java.io.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.CRC32;

public class NativeLoader {
    private static final Map<String, Path> downloadedNatives = new ConcurrentHashMap<>();

    public static void installDownloaded(String name, byte[] bytes) throws IOException {
        Path tempDir = Path.of(System.getProperty("java.io.tmpdir"), "phantom");
        Files.createDirectories(tempDir);

        String fname = Path.of(name).getFileName().toString();
        Path dest = tempDir.resolve(fname);
        Path lock = tempDir.resolve(fname + ".lock");

        try (FileChannel fc = FileChannel.open(lock,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             FileLock fl = fc.lock()) {
            if (!Files.exists(dest) || crc32(Files.readAllBytes(dest)) != crc32(bytes)) {
                Files.write(dest, bytes,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        }

        downloadedNatives.put(fname, dest.toAbsolutePath());
    }

    public static String extract(String resourcePath) throws IOException {
        Path tempDir = Path.of(System.getProperty("java.io.tmpdir"), "phantom");
        Files.createDirectories(tempDir);
        String fname = Path.of(resourcePath).getFileName().toString();
        Path downloaded = downloadedNatives.get(fname);
        if (downloaded != null && Files.exists(downloaded)) {
            return downloaded.toAbsolutePath().toString();
        }

        Path dest  = tempDir.resolve(fname);
        Path lock  = tempDir.resolve(fname + ".lock");

        // Exclusive lock guards against two game instances extracting simultaneously
        try (FileChannel fc = FileChannel.open(lock,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             FileLock fl = fc.lock()) {

            try (InputStream in = NativeLoader.class.getResourceAsStream("/" + resourcePath)) {
                if (in == null) throw new IOException("Native resource not found: " + resourcePath);
                byte[] bytes = in.readAllBytes();

                // CRC32 comparison - size alone is insufficient across recompilations
                if (Files.exists(dest) && crc32(Files.readAllBytes(dest)) == crc32(bytes)) {
                    return dest.toAbsolutePath().toString();
                }
                Files.write(dest, bytes,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        }
        return dest.toAbsolutePath().toString();
    }

    private static long crc32(byte[] data) {
        CRC32 crc = new CRC32();
        crc.update(data);
        return crc.getValue();
    }
}
