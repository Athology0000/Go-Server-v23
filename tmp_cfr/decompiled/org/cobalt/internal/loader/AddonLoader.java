/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.io.CloseableKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.Charsets
 *  kotlin.text.StringsKt
 *  net.fabricmc.loader.api.FabricLoader
 *  net.fabricmc.loader.api.entrypoint.EntrypointContainer
 *  net.fabricmc.loader.api.metadata.ModMetadata
 *  net.fabricmc.loader.impl.launch.FabricLauncherBase
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixins
 */
package org.cobalt.internal.loader;

import com.google.gson.Gson;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import org.cobalt.api.addon.Addon;
import org.cobalt.api.addon.AddonMetadata;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.api.util.ui.helper.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixins;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000V\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010!\n\u0002\b\u0002\n\u0002\u0010%\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u0017\u0010\t\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b\t\u0010\nJ\u001f\u0010\u000e\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\r0\f0\u000b\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0017\u0010\u0013\u001a\u0004\u0018\u00010\u00122\u0006\u0010\u0011\u001a\u00020\u0010\u00a2\u0006\u0004\b\u0013\u0010\u0014R\u0014\u0010\u0015\u001a\u00020\u00068\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0015\u0010\u0016R&\u0010\u0018\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\r0\f0\u00178\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0018\u0010\u0019R \u0010\u001b\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\b0\u001a8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001b\u0010\u001cR\u0014\u0010\u001e\u001a\u00020\u001d8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001e\u0010\u001f\u00a8\u0006 "}, d2={"Lorg/cobalt/internal/loader/AddonLoader;", "", "<init>", "()V", "", "findAddons", "Ljava/nio/file/Path;", "jarPath", "Lorg/cobalt/api/addon/AddonMetadata;", "loadAddon", "(Ljava/nio/file/Path;)Lorg/cobalt/api/addon/AddonMetadata;", "", "Lkotlin/Pair;", "Lorg/cobalt/api/addon/Addon;", "getAddons", "()Ljava/util/List;", "", "addonId", "Lorg/cobalt/api/util/ui/helper/Image;", "getAddonIcon", "(Ljava/lang/String;)Lorg/cobalt/api/util/ui/helper/Image;", "addonsDir", "Ljava/nio/file/Path;", "", "addons", "Ljava/util/List;", "", "addonsById", "Ljava/util/Map;", "Lcom/google/gson/Gson;", "gson", "Lcom/google/gson/Gson;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nAddonLoader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AddonLoader.kt\norg/cobalt/internal/loader/AddonLoader\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,121:1\n1#2:122\n*E\n"})
public final class AddonLoader {
    @NotNull
    public static final AddonLoader INSTANCE = new AddonLoader();
    @NotNull
    private static final Path addonsDir;
    @NotNull
    private static final List<Pair<AddonMetadata, Addon>> addons;
    @NotNull
    private static final Map<String, AddonMetadata> addonsById;
    @NotNull
    private static final Gson gson;

    private AddonLoader() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void findAddons() {
        if (FabricLauncherBase.getLauncher().isDevelopment()) {
            for (EntrypointContainer entry : FabricLoader.getInstance().getEntrypointContainers("cobalt", Addon.class)) {
                Object object;
                ModMetadata modMeta = entry.getProvider().getMetadata();
                String string = modMeta.getId();
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getId(...)");
                String string2 = modMeta.getName();
                Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"getName(...)");
                Object object2 = modMeta.getVersion();
                if (object2 == null || (object2 = object2.toString()) == null) {
                    object2 = "unknown";
                }
                AddonMetadata metadata = new AddonMetadata(string, string2, (String)object2, CollectionsKt.listOf((Object)entry.getEntrypoint().getClass().getName()), CollectionsKt.emptyList(), null, 32, null);
                try {
                    object = entry.getEntrypoint();
                    Intrinsics.checkNotNull((Object)object);
                    object = (Addon)object;
                }
                catch (Throwable e) {
                    throw new RuntimeException("Failed to initialize addon \"" + modMeta.getName() + "\"", e);
                }
                Object addonInstance = object;
                ((Collection)addons).add(TuplesKt.to((Object)metadata, (Object)addonInstance));
                addonsById.put(metadata.getId(), metadata);
            }
        }
        if (!Files.isDirectory(addonsDir, new LinkOption[0])) {
            Files.createDirectories(addonsDir, new FileAttribute[0]);
            return;
        }
        try {
            Closeable closeable = Files.newDirectoryStream(addonsDir, "*.jar");
            Throwable throwable = null;
            try {
                DirectoryStream stream = (DirectoryStream)closeable;
                boolean bl = false;
                Iterator iterator = stream.iterator();
                Intrinsics.checkNotNullExpressionValue(iterator, (String)"iterator(...)");
                Iterator iterator2 = iterator;
                while (iterator2.hasNext()) {
                    Path jarPath = (Path)iterator2.next();
                    try {
                        FabricLauncherBase.getLauncher().addToClassPath(jarPath, new String[0]);
                        Intrinsics.checkNotNull((Object)jarPath);
                        INSTANCE.loadAddon(jarPath);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Unit unit = Unit.INSTANCE;
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
            finally {
                CloseableKt.closeFinally((Closeable)closeable, (Throwable)throwable);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final AddonMetadata loadAddon(Path jarPath) {
        Closeable closeable = new ZipFile(jarPath.toFile());
        Throwable throwable = null;
        try {
            Object input;
            ZipFile zip = (ZipFile)closeable;
            boolean bl = false;
            ZipEntry zipEntry = zip.getEntry("cobalt.addon.json");
            if (zipEntry == null) {
                throw new IllegalStateException("Missing cobalt.addon.json in " + jarPath);
            }
            ZipEntry jsonEntry = zipEntry;
            Closeable closeable2 = zip.getInputStream(jsonEntry);
            Throwable throwable2 = null;
            try {
                input = (InputStream)closeable2;
                boolean bl2 = false;
                Intrinsics.checkNotNull((Object)input);
                InputStream inputStream = input;
                Charset charset = Charsets.UTF_8;
                input = (AddonMetadata)gson.fromJson((Reader)new InputStreamReader(inputStream, charset), AddonMetadata.class);
            }
            catch (Throwable bl2) {
                throwable2 = bl2;
                throw bl2;
            }
            finally {
                CloseableKt.closeFinally((Closeable)closeable2, (Throwable)throwable2);
            }
            Object metadata = input;
            if (!(!((Collection)((AddonMetadata)metadata).getEntrypoints()).isEmpty())) {
                boolean $i$a$-require-AddonLoader$loadAddon$1$22 = false;
                String $i$a$-require-AddonLoader$loadAddon$1$22 = "Addon " + ((AddonMetadata)metadata).getId() + " has no entrypoints defined";
                throw new IllegalArgumentException($i$a$-require-AddonLoader$loadAddon$1$22.toString());
            }
            Class<Mixins> $i$a$-require-AddonLoader$loadAddon$1$22 = Mixins.class;
            synchronized ($i$a$-require-AddonLoader$loadAddon$1$22) {
                boolean $i$a$-synchronized-AddonLoader$loadAddon$1$32 = false;
                for (String mixin : ((AddonMetadata)metadata).getMixins()) {
                    Mixins.addConfiguration((String)mixin);
                }
                Unit $i$a$-synchronized-AddonLoader$loadAddon$1$32 = Unit.INSTANCE;
            }
            for (String entrypoint : ((AddonMetadata)metadata).getEntrypoints()) {
                Object object;
                String classPath = StringsKt.replace$default((String)entrypoint, (char)'.', (char)'/', (boolean)false, (int)4, null) + ".class";
                if (zip.getEntry(classPath) == null) {
                    boolean bl3 = false;
                    String string = "Entrypoint class '" + entrypoint + "' does not exist inside " + jarPath.getFileName();
                    throw new IllegalStateException(string.toString());
                }
                Class<?> it = Class.forName(entrypoint);
                boolean bl4 = false;
                try {
                    object = it.getField("INSTANCE").get(null);
                }
                catch (NoSuchFieldException noSuchFieldException) {
                    Constructor<?> constructor = it.getDeclaredConstructor(new Class[0]);
                    constructor.setAccessible(true);
                    object = constructor.newInstance(new Object[0]);
                }
                Object instance = object;
                if (!(instance instanceof Addon)) {
                    boolean bl5 = false;
                    String string = "Entrypoint '" + entrypoint + "' must implement Addon";
                    throw new IllegalStateException(string.toString());
                }
                ((Collection)addons).add(TuplesKt.to((Object)metadata, (Object)instance));
            }
            addonsById.put(((AddonMetadata)metadata).getId(), (AddonMetadata)metadata);
            Intrinsics.checkNotNull((Object)metadata);
            Object object = metadata;
            return object;
        }
        catch (Throwable throwable3) {
            throwable = throwable3;
            throw throwable3;
        }
        finally {
            CloseableKt.closeFinally((Closeable)closeable, (Throwable)throwable);
        }
    }

    @NotNull
    public final List<Pair<AddonMetadata, Addon>> getAddons() {
        return CollectionsKt.toList((Iterable)addons);
    }

    @Nullable
    public final Image getAddonIcon(@NotNull String addonId) {
        Intrinsics.checkNotNullParameter((Object)addonId, (String)"addonId");
        Object object = addonsById.get(addonId);
        if (object == null || (object = ((AddonMetadata)object).getIcon()) == null) {
            return null;
        }
        return NVGRenderer.createImage((String)object);
    }

    static {
        Path path = Paths.get("config/cobalt/addons/", new String[0]);
        Intrinsics.checkNotNullExpressionValue((Object)path, (String)"get(...)");
        addonsDir = path;
        addons = new ArrayList();
        addonsById = new LinkedHashMap();
        gson = new Gson();
    }
}

