/*
 * Decompiled with CFR 0.152.
 */
package org.cobalt.pathfinder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.zip.CRC32;

public class NativeLoader {
    public static String extract(String resourcePath) throws IOException {
        Path tempDir = Path.of(System.getProperty("java.io.tmpdir"), "cobalt");
        Files.createDirectories(tempDir, new FileAttribute[0]);
        String fname = Path.of(resourcePath, new String[0]).getFileName().toString();
        Path dest = tempDir.resolve(fname);
        Path lock = tempDir.resolve(fname + ".lock");
        try (FileChannel fc = FileChannel.open(lock, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             FileLock fl = fc.lock();
             InputStream in = NativeLoader.class.getResourceAsStream("/" + resourcePath);){
            if (in == null) {
                throw new IOException("Native resource not found: " + resourcePath);
            }
            byte[] bytes = in.readAllBytes();
            if (Files.exists(dest, new LinkOption[0]) && NativeLoader.crc32(Files.readAllBytes(dest)) == NativeLoader.crc32(bytes)) {
                String string = dest.toAbsolutePath().toString();
                return string;
            }
            Files.write(dest, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
        return dest.toAbsolutePath().toString();
    }

    private static long crc32(byte[] data) {
        CRC32 crc = new CRC32();
        crc.update(data);
        return crc.getValue();
    }
}

