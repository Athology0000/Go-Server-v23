/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.tree.ClassNode
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
 *  org.spongepowered.asm.mixin.extensibility.IMixinInfo
 */
package org.cobalt.init;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class MixinAutoDiscover
implements IMixinConfigPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(MixinAutoDiscover.class);
    private final List<String> mixins = new ArrayList<String>();
    private static final String CLASS = ".class";

    public void onLoad(String mixinPackage) {
        try {
            URL location = MixinAutoDiscover.class.getProtectionDomain().getCodeSource().getLocation();
            Path path = Paths.get(location.toURI());
            if (Files.isDirectory(path, new LinkOption[0])) {
                this.scanDirectory(path, mixinPackage);
            } else {
                this.scanJar(path, mixinPackage);
            }
            for (int i = 0; i < this.mixins.size(); ++i) {
                String cls = this.mixins.get(i);
                if (!cls.startsWith(mixinPackage + ".")) continue;
                cls = cls.substring(mixinPackage.length() + 1);
                this.mixins.set(i, cls);
            }
        }
        catch (IOException e) {
            throw new MixinDiscoveryException("Failed to read mixin files", e);
        }
        catch (URISyntaxException e) {
            throw new MixinDiscoveryException("Invalid URI for mixin location", e);
        }
    }

    private void scanDirectory(Path root, String mixinPackage) throws IOException {
        String mixinPath = mixinPackage.replace(".", "/");
        Path mixinDir = root.resolve(mixinPath);
        if (!Files.exists(mixinDir, new LinkOption[0])) {
            LOGGER.warn("Mixin directory does not exist: {}", (Object)mixinDir);
            return;
        }
        try (Stream<Path> paths = Files.walk(mixinDir, new FileVisitOption[0]);){
            paths.filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).filter(p -> p.toString().endsWith(CLASS)).filter(p -> !p.toString().endsWith("package-info.class")).forEach(p -> {
                String relativePath = root.relativize((Path)p).toString();
                String className = relativePath.replace("/", ".").replace("\\", ".").replace(CLASS, "");
                if (!className.isEmpty()) {
                    this.mixins.add(className);
                }
            });
        }
    }

    private void scanJar(Path jarPath, String mixinPackage) throws IOException {
        String mixinPath = mixinPackage.replace(".", "/");
        try (ZipInputStream zip = new ZipInputStream(Files.newInputStream(jarPath, new OpenOption[0]));){
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                String className;
                String entryName = entry.getName();
                if (entryName.startsWith(mixinPath) && entryName.endsWith(CLASS) && !entryName.endsWith("package-info.class") && !(className = entryName.replace("/", ".").replace(CLASS, "")).isEmpty()) {
                    this.mixins.add(className);
                }
                zip.closeEntry();
            }
        }
    }

    public List<String> getMixins() {
        return this.mixins;
    }

    public String getRefMapperConfig() {
        return null;
    }

    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    private static class MixinDiscoveryException
    extends RuntimeException {
        public MixinDiscoveryException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

