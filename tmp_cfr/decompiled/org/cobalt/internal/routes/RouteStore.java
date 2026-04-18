/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  kotlin.Metadata
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.Unit
 *  kotlin.collections.ArraysKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.io.FilesKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.jvm.internal.StringCompanionObject
 *  kotlin.text.StringsKt
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.routes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.text.StringsKt;
import net.minecraft.class_310;
import org.cobalt.internal.routes.RoutePoint;
import org.cobalt.internal.routes.RoutePointType;
import org.cobalt.internal.routes.RouteType;
import org.cobalt.internal.routes.SavedRoute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000V\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0016\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010%\n\u0002\b\u0004\b\u00c0\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0013\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u001b\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\t\u001a\u00020\b\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0015\u0010\u000e\u001a\u00020\r2\u0006\u0010\f\u001a\u00020\u0005\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0015\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0011\u001a\u00020\u0010\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0017\u0010\u0015\u001a\u00020\r2\b\u0010\f\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0004\b\u0015\u0010\u000fJ\u0015\u0010\u0016\u001a\u00020\r2\u0006\u0010\t\u001a\u00020\b\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0017\u0010\u0018\u001a\u0004\u0018\u00010\u00102\u0006\u0010\t\u001a\u00020\b\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u0017\u0010\u001a\u001a\u0004\u0018\u00010\u00052\u0006\u0010\t\u001a\u00020\b\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u0017\u0010\u001d\u001a\u0004\u0018\u00010\u00102\u0006\u0010\u001c\u001a\u00020\u0010\u00a2\u0006\u0004\b\u001d\u0010\u001eJ\u001f\u0010\u001f\u001a\u00020\r2\u0006\u0010\u001c\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0010\u00a2\u0006\u0004\b\u001f\u0010 J\u0015\u0010!\u001a\u00020\r2\u0006\u0010\u001c\u001a\u00020\u0010\u00a2\u0006\u0004\b!\u0010\"J\r\u0010#\u001a\u00020\r\u00a2\u0006\u0004\b#\u0010\u0003J\u000f\u0010$\u001a\u00020\rH\u0002\u00a2\u0006\u0004\b$\u0010\u0003J\u0015\u0010%\u001a\u00020\u00122\u0006\u0010\u0011\u001a\u00020\u0010\u00a2\u0006\u0004\b%\u0010\u0014J\r\u0010&\u001a\u00020\r\u00a2\u0006\u0004\b&\u0010\u0003J\u000f\u0010'\u001a\u00020\rH\u0002\u00a2\u0006\u0004\b'\u0010\u0003J\u0015\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u0002\u00a2\u0006\u0004\b(\u0010\u0007J\u0017\u0010*\u001a\u00020)2\u0006\u0010\u0011\u001a\u00020\u0010H\u0002\u00a2\u0006\u0004\b*\u0010+J!\u0010-\u001a\u0004\u0018\u00010\u00052\u0006\u0010,\u001a\u00020)2\u0006\u0010\u0011\u001a\u00020\u0010H\u0002\u00a2\u0006\u0004\b-\u0010.R\u0014\u00100\u001a\u00020/8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b0\u00101R\u001c\u00104\u001a\n 3*\u0004\u0018\u000102028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b4\u00105R\u0014\u00106\u001a\u00020)8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b6\u00107R\u0014\u00108\u001a\u00020)8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b8\u00107R\u0014\u00109\u001a\u00020)8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b9\u00107R \u0010;\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00100:8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b;\u0010<R \u0010=\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00100:8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b=\u0010<\u00a8\u0006>"}, d2={"Lorg/cobalt/internal/routes/RouteStore;", "", "<init>", "()V", "", "Lorg/cobalt/internal/routes/SavedRoute;", "loadAll", "()Ljava/util/List;", "Lorg/cobalt/internal/routes/RouteType;", "type", "listByType", "(Lorg/cobalt/internal/routes/RouteType;)Ljava/util/List;", "route", "", "save", "(Lorg/cobalt/internal/routes/SavedRoute;)V", "", "name", "", "delete", "(Ljava/lang/String;)Z", "setLoaded", "clearLoaded", "(Lorg/cobalt/internal/routes/RouteType;)V", "getLoadedName", "(Lorg/cobalt/internal/routes/RouteType;)Ljava/lang/String;", "getLoaded", "(Lorg/cobalt/internal/routes/RouteType;)Lorg/cobalt/internal/routes/SavedRoute;", "slotKey", "getSlotRoute", "(Ljava/lang/String;)Ljava/lang/String;", "setSlotRoute", "(Ljava/lang/String;Ljava/lang/String;)V", "clearSlotRoute", "(Ljava/lang/String;)V", "loadAssignments", "saveAssignments", "isValidName", "migrate", "ensureDirExists", "loadAllFromDir", "Ljava/io/File;", "fileForName", "(Ljava/lang/String;)Ljava/io/File;", "file", "migrateFile", "(Ljava/io/File;Ljava/lang/String;)Lorg/cobalt/internal/routes/SavedRoute;", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lcom/google/gson/Gson;", "kotlin.jvm.PlatformType", "gson", "Lcom/google/gson/Gson;", "routesDir", "Ljava/io/File;", "legacyDir", "assignmentsFile", "", "loadedRoutes", "Ljava/util/Map;", "slotAssignments", "cobalt"})
@SourceDebugExtension(value={"SMAP\nRouteStore.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RouteStore.kt\norg/cobalt/internal/routes/RouteStore\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 5 _Strings.kt\nkotlin/text/StringsKt___StringsKt\n+ 6 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,202:1\n777#2:203\n873#2,2:204\n296#2,2:207\n1915#2,2:209\n1642#2,10:231\n1915#2:241\n1916#2:243\n1652#2:244\n1#3:206\n1#3:228\n1#3:242\n221#4,2:211\n1874#5,2:213\n14060#6,2:215\n12033#6,10:217\n14060#6:227\n14061#6:229\n12043#6:230\n*S KotlinDebug\n*F\n+ 1 RouteStore.kt\norg/cobalt/internal/routes/RouteStore\n*L\n32#1:203\n32#1:204,2\n59#1:207,2\n89#1:209,2\n174#1:231,10\n174#1:241\n174#1:243\n174#1:244\n150#1:228\n174#1:242\n99#1:211,2\n112#1:213,2\n127#1:215,2\n150#1:217,10\n150#1:227\n150#1:229\n150#1:230\n*E\n"})
public final class RouteStore {
    @NotNull
    public static final RouteStore INSTANCE = new RouteStore();
    @NotNull
    private static final class_310 mc;
    private static final Gson gson;
    @NotNull
    private static final File routesDir;
    @NotNull
    private static final File legacyDir;
    @NotNull
    private static final File assignmentsFile;
    @NotNull
    private static final Map<RouteType, String> loadedRoutes;
    @NotNull
    private static final Map<String, String> slotAssignments;

    private RouteStore() {
    }

    @NotNull
    public final List<SavedRoute> loadAll() {
        this.ensureDirExists();
        return this.loadAllFromDir();
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public final List<SavedRoute> listByType(@NotNull RouteType type) {
        void $this$filterTo$iv$iv;
        Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
        Iterable $this$filter$iv = this.loadAll();
        boolean $i$f$filter = false;
        Iterable iterable = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            SavedRoute it = (SavedRoute)element$iv$iv;
            boolean bl = false;
            if (!(it.getType() == type)) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        return (List)destination$iv$iv;
    }

    public final void save(@NotNull SavedRoute route) {
        Intrinsics.checkNotNullParameter((Object)route, (String)"route");
        this.ensureDirExists();
        File file = this.fileForName(route.getName());
        String string = gson.toJson((JsonElement)route.toJson());
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toJson(...)");
        FilesKt.writeText$default((File)file, (String)string, null, (int)2, null);
    }

    public final boolean delete(@NotNull String name) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        File f = this.fileForName(name);
        boolean bl = false;
        return f.exists() && f.delete();
    }

    public final void setLoaded(@Nullable SavedRoute route) {
        if (route == null) {
            return;
        }
        loadedRoutes.put(route.getType(), route.getName());
    }

    public final void clearLoaded(@NotNull RouteType type) {
        Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
        loadedRoutes.remove((Object)type);
    }

    @Nullable
    public final String getLoadedName(@NotNull RouteType type) {
        Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
        return loadedRoutes.get((Object)type);
    }

    @Nullable
    public final SavedRoute getLoaded(@NotNull RouteType type) {
        Object v1;
        block2: {
            Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
            String string = loadedRoutes.get((Object)type);
            if (string == null) {
                return null;
            }
            String name = string;
            Iterable $this$firstOrNull$iv = this.loadAll();
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                SavedRoute it = (SavedRoute)element$iv;
                boolean bl = false;
                if (!Intrinsics.areEqual((Object)it.getName(), (Object)name)) continue;
                v1 = element$iv;
                break block2;
            }
            v1 = null;
        }
        return v1;
    }

    @Nullable
    public final String getSlotRoute(@NotNull String slotKey) {
        Intrinsics.checkNotNullParameter((Object)slotKey, (String)"slotKey");
        return slotAssignments.get(slotKey);
    }

    public final void setSlotRoute(@NotNull String slotKey, @Nullable String name) {
        Intrinsics.checkNotNullParameter((Object)slotKey, (String)"slotKey");
        if (name == null) {
            slotAssignments.remove(slotKey);
        } else {
            slotAssignments.put(slotKey, name);
        }
        this.saveAssignments();
    }

    public final void clearSlotRoute(@NotNull String slotKey) {
        Intrinsics.checkNotNullParameter((Object)slotKey, (String)"slotKey");
        this.setSlotRoute(slotKey, null);
    }

    public final void loadAssignments() {
        slotAssignments.clear();
        if (!assignmentsFile.exists()) {
            return;
        }
        RouteStore routeStore = this;
        try {
            Unit unit;
            RouteStore $this$loadAssignments_u24lambda_u240 = routeStore;
            boolean bl = false;
            JsonObject root = JsonParser.parseString((String)FilesKt.readText$default((File)assignmentsFile, null, (int)1, null)).getAsJsonObject();
            Object object = root.getAsJsonObject("slots");
            if (object != null && (object = object.entrySet()) != null) {
                Iterable $this$forEach$iv = (Iterable)object;
                boolean $i$f$forEach = false;
                for (Object element$iv : $this$forEach$iv) {
                    Map.Entry entry = (Map.Entry)element$iv;
                    boolean bl2 = false;
                    Intrinsics.checkNotNull((Object)entry);
                    String k = (String)entry.getKey();
                    JsonElement v = (JsonElement)entry.getValue();
                    if (!v.isJsonPrimitive()) continue;
                    slotAssignments.put(k, v.getAsString());
                }
                unit = Unit.INSTANCE;
            } else {
                unit = null;
            }
            Object object2 = Result.constructor-impl(unit);
        }
        catch (Throwable throwable) {
            Object object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
    }

    private final void saveAssignments() {
        RouteStore routeStore = this;
        try {
            RouteStore $this$saveAssignments_u24lambda_u240 = routeStore;
            boolean bl = false;
            File file = assignmentsFile.getParentFile();
            if (file != null) {
                file.mkdirs();
            }
            JsonObject slotsObj = new JsonObject();
            Map<String, String> $this$forEach$iv = slotAssignments;
            boolean $i$f$forEach = false;
            Iterator<Map.Entry<String, String>> iterator = $this$forEach$iv.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> element$iv;
                Map.Entry<String, String> entry = element$iv = iterator.next();
                boolean bl2 = false;
                String k = entry.getKey();
                String v = entry.getValue();
                slotsObj.addProperty(k, v);
            }
            JsonObject root = new JsonObject();
            root.add("slots", (JsonElement)slotsObj);
            String string = gson.toJson((JsonElement)root);
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toJson(...)");
            FilesKt.writeText$default((File)assignmentsFile, (String)string, null, (int)2, null);
            Object object = Result.constructor-impl((Object)Unit.INSTANCE);
        }
        catch (Throwable throwable) {
            Object object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
    }

    public final boolean isValidName(@NotNull String name) {
        boolean bl;
        block4: {
            Intrinsics.checkNotNullParameter((Object)name, (String)"name");
            if (StringsKt.isBlank((CharSequence)name)) {
                return false;
            }
            if (Intrinsics.areEqual((Object)name, (Object)".") || Intrinsics.areEqual((Object)name, (Object)"..")) {
                return false;
            }
            if (StringsKt.endsWith$default((String)name, (String)".", (boolean)false, (int)2, null) || StringsKt.endsWith$default((String)name, (String)" ", (boolean)false, (int)2, null)) {
                return false;
            }
            char[] cArray = new char[]{'\\', '/', ':', '*', '?', '\"', '<', '>', '|'};
            char[] invalid = cArray;
            CharSequence $this$none$iv = name;
            boolean $i$f$none = false;
            for (int i = 0; i < $this$none$iv.length(); ++i) {
                char element$iv;
                char it = element$iv = $this$none$iv.charAt(i);
                boolean bl2 = false;
                if (!ArraysKt.contains((char[])invalid, (char)it)) continue;
                bl = false;
                break block4;
            }
            bl = true;
        }
        return bl;
    }

    public final void migrate() {
        if (!legacyDir.exists()) {
            return;
        }
        File[] fileArray = legacyDir.listFiles(RouteStore::migrate$lambda$0);
        if (fileArray == null) {
            return;
        }
        File[] legacyFiles = fileArray;
        if (legacyFiles.length == 0) {
            return;
        }
        this.ensureDirExists();
        File[] $this$forEach$iv = legacyFiles;
        boolean $i$f$forEach = false;
        int n = $this$forEach$iv.length;
        for (int i = 0; i < n; ++i) {
            SavedRoute converted;
            File dest;
            File element$iv;
            File f = element$iv = $this$forEach$iv[i];
            boolean bl = false;
            Intrinsics.checkNotNull((Object)f);
            String name = ((Object)StringsKt.trim((CharSequence)FilesKt.getNameWithoutExtension((File)f))).toString();
            if (!INSTANCE.isValidName(name) || (dest = INSTANCE.fileForName(name)).exists() || INSTANCE.migrateFile(f, name) == null) continue;
            String string = gson.toJson((JsonElement)converted.toJson());
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toJson(...)");
            FilesKt.writeText$default((File)dest, (String)string, null, (int)2, null);
        }
    }

    private final void ensureDirExists() {
        if (!routesDir.exists()) {
            routesDir.mkdirs();
            this.migrate();
        }
    }

    /*
     * WARNING - void declaration
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private final List<SavedRoute> loadAllFromDir() {
        List list;
        if (!routesDir.exists()) {
            return CollectionsKt.emptyList();
        }
        File[] fileArray = routesDir.listFiles(RouteStore::loadAllFromDir$lambda$0);
        if (fileArray != null) {
            void $this$mapNotNullTo$iv$iv;
            File[] $this$mapNotNull$iv = fileArray;
            boolean $i$f$mapNotNull = false;
            File[] fileArray2 = $this$mapNotNull$iv;
            Collection destination$iv$iv = new ArrayList();
            boolean $i$f$mapNotNullTo = false;
            void $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
            boolean $i$f$forEach = false;
            int n = ((void)$this$forEach$iv$iv$iv).length;
            for (int i = 0; i < n; ++i) {
                SavedRoute savedRoute;
                block7: {
                    Object object;
                    void element$iv$iv$iv;
                    void element$iv$iv = element$iv$iv$iv = $this$forEach$iv$iv$iv[i];
                    boolean bl = false;
                    void f = element$iv$iv;
                    boolean bl2 = false;
                    Object object2 = INSTANCE;
                    try {
                        RouteStore $this$loadAllFromDir_u24lambda_u241_u240 = object2;
                        boolean bl3 = false;
                        Intrinsics.checkNotNull((Object)f);
                        String text = ((Object)StringsKt.trim((CharSequence)FilesKt.readText$default((File)f, null, (int)1, null))).toString();
                        if (((CharSequence)text).length() == 0) {
                            savedRoute = null;
                            break block7;
                        }
                        JsonObject jsonObject = JsonParser.parseString((String)text).getAsJsonObject();
                        Intrinsics.checkNotNullExpressionValue((Object)jsonObject, (String)"getAsJsonObject(...)");
                        object = Result.constructor-impl((Object)SavedRoute.Companion.fromJson(jsonObject));
                    }
                    catch (Throwable throwable) {
                        object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
                    }
                    object2 = object;
                    savedRoute = (SavedRoute)(Result.isFailure-impl((Object)object2) ? null : object2);
                }
                if (savedRoute == null) continue;
                SavedRoute it$iv$iv = savedRoute;
                boolean bl = false;
                destination$iv$iv.add(it$iv$iv);
            }
            Comparator comparator = StringsKt.getCASE_INSENSITIVE_ORDER((StringCompanionObject)StringCompanionObject.INSTANCE);
            List list2 = CollectionsKt.sortedWith((Iterable)((List)destination$iv$iv), (Comparator)new Comparator(comparator){
                final /* synthetic */ Comparator $comparator;
                {
                    this.$comparator = $comparator;
                }

                public final int compare(T a, T b) {
                    SavedRoute savedRoute = (SavedRoute)a;
                    Comparator comparator = this.$comparator;
                    boolean bl = false;
                    SavedRoute it = (SavedRoute)b;
                    String string = it.getName();
                    bl = false;
                    String string2 = it.getName();
                    return comparator.compare(string, string2);
                }
            });
            if (list2 != null) {
                list = list2;
                return list;
            }
        }
        list = CollectionsKt.emptyList();
        return list;
    }

    private final File fileForName(String name) {
        return new File(routesDir, name + ".json");
    }

    /*
     * WARNING - void declaration
     */
    private final SavedRoute migrateFile(File file, String name) {
        void $this$mapNotNullTo$iv$iv;
        Object $this$migrateFile_u24lambda_u242;
        Object it;
        Object $this$migrateFile_u24lambda_u240;
        Object object = this;
        try {
            $this$migrateFile_u24lambda_u240 = object;
            boolean bl = false;
            $this$migrateFile_u24lambda_u240 = Result.constructor-impl((Object)((Object)StringsKt.trim((CharSequence)FilesKt.readText$default((File)file, null, (int)1, null))).toString());
        }
        catch (Throwable bl) {
            $this$migrateFile_u24lambda_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
        }
        object = $this$migrateFile_u24lambda_u240;
        Throwable throwable = Result.exceptionOrNull-impl((Object)object);
        if (throwable != null) {
            it = throwable;
            boolean bl = false;
            return null;
        }
        String text = (String)object;
        if (((CharSequence)text).length() == 0) {
            return null;
        }
        it = this;
        try {
            $this$migrateFile_u24lambda_u242 = (RouteStore)it;
            boolean bl = false;
            $this$migrateFile_u24lambda_u242 = Result.constructor-impl((Object)JsonParser.parseString((String)text));
        }
        catch (Throwable bl) {
            $this$migrateFile_u24lambda_u242 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
        }
        it = $this$migrateFile_u24lambda_u242;
        Throwable throwable2 = Result.exceptionOrNull-impl((Object)it);
        if (throwable2 != null) {
            Throwable it2 = throwable2;
            boolean bl = false;
            return null;
        }
        JsonElement parsed = (JsonElement)it;
        Object object2 = parsed.isJsonArray() ? parsed.getAsJsonArray() : (parsed.isJsonObject() ? parsed.getAsJsonObject().getAsJsonArray("points") : null);
        if (object2 == null) {
            return null;
        }
        JsonArray pointsArr = object2;
        Iterable $this$mapNotNull$iv = (Iterable)pointsArr;
        boolean $i$f$mapNotNull = false;
        Iterable iterable = $this$mapNotNull$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$mapNotNullTo = false;
        void $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
        boolean $i$f$forEach = false;
        Iterator iterator = $this$forEach$iv$iv$iv.iterator();
        while (iterator.hasNext()) {
            RoutePoint it$iv$iv;
            Object object3;
            Object element$iv$iv$iv;
            Object element$iv$iv = element$iv$iv$iv = iterator.next();
            boolean bl = false;
            JsonElement el = (JsonElement)element$iv$iv;
            boolean bl2 = false;
            Object object4 = INSTANCE;
            try {
                RoutePoint routePoint;
                RouteStore $this$migrateFile_u24lambda_u244_u240 = object4;
                boolean bl3 = false;
                JsonObject o = el.getAsJsonObject();
                JsonElement jsonElement = o.get("x");
                if (jsonElement == null) {
                    routePoint = null;
                } else {
                    int x = jsonElement.getAsInt();
                    JsonElement jsonElement2 = o.get("y");
                    if (jsonElement2 == null) {
                        routePoint = null;
                    } else {
                        int y = jsonElement2.getAsInt();
                        JsonElement jsonElement3 = o.get("z");
                        if (jsonElement3 == null) {
                            routePoint = null;
                        } else {
                            Object object5;
                            RoutePointType newType;
                            String string;
                            int z = jsonElement3.getAsInt();
                            Object object6 = o.get("type");
                            if (object6 != null && (object6 = object6.getAsString()) != null) {
                                String string2 = ((String)object6).toLowerCase(Locale.ROOT);
                                v9 = string2;
                                Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
                            } else {
                                v9 = string = null;
                            }
                            RoutePointType routePointType = newType = Intrinsics.areEqual((Object)string, (Object)"warp") ? RoutePointType.WARP : (Intrinsics.areEqual((Object)string, (Object)"mine") ? RoutePointType.MINE : RoutePointType.WALK);
                            int n = x;
                            int n2 = y;
                            int n3 = z;
                            JsonElement jsonElement4 = o.get("mx");
                            Integer n4 = jsonElement4 != null ? Integer.valueOf(jsonElement4.getAsInt()) : null;
                            JsonElement jsonElement5 = o.get("my");
                            Integer n5 = jsonElement5 != null ? Integer.valueOf(jsonElement5.getAsInt()) : null;
                            JsonElement jsonElement6 = o.get("mz");
                            Integer n6 = jsonElement6 != null ? Integer.valueOf(jsonElement6.getAsInt()) : null;
                            Object object7 = o.get("bid");
                            if (object7 != null && (object7 = object7.getAsString()) != null && (object7 = ((Object)StringsKt.trim((CharSequence)((CharSequence)object7))).toString()) != null) {
                                void it3;
                                Object object8;
                                Object object9 = object8 = object7;
                                Integer n7 = n6;
                                Integer n8 = n5;
                                Integer n9 = n4;
                                int n10 = n3;
                                int n11 = n2;
                                int n12 = n;
                                RoutePointType routePointType2 = routePointType;
                                boolean bl4 = false;
                                boolean bl5 = ((CharSequence)it3).length() > 0;
                                routePointType = routePointType2;
                                n = n12;
                                n2 = n11;
                                n3 = n10;
                                n4 = n9;
                                n5 = n8;
                                n6 = n7;
                                object5 = bl5 ? object8 : null;
                            } else {
                                object5 = null;
                            }
                            Object object10 = object5;
                            Integer n13 = n6;
                            Integer n14 = n5;
                            Integer n15 = n4;
                            int n16 = n3;
                            int n17 = n2;
                            int n18 = n;
                            RoutePointType routePointType3 = routePointType;
                            routePoint = new RoutePoint(routePointType3, n18, n17, n16, n15, n14, n13, (String)object10);
                        }
                    }
                }
                object3 = Result.constructor-impl(routePoint);
            }
            catch (Throwable throwable3) {
                object3 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable3));
            }
            object4 = object3;
            if ((RoutePoint)(Result.isFailure-impl((Object)object4) ? null : object4) == null) continue;
            boolean bl6 = false;
            destination$iv$iv.add(it$iv$iv);
        }
        List points = (List)destination$iv$iv;
        return new SavedRoute(name, RouteType.ORE_MINER, null, points, null, 20, null);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static final boolean migrate$lambda$0(File f) {
        if (!f.isFile()) return false;
        Intrinsics.checkNotNull((Object)f);
        if (!StringsKt.equals((String)FilesKt.getExtension((File)f), (String)"json", (boolean)true)) return false;
        return true;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static final boolean loadAllFromDir$lambda$0(File f) {
        if (!f.isFile()) return false;
        Intrinsics.checkNotNull((Object)f);
        if (!StringsKt.equals((String)FilesKt.getExtension((File)f), (String)"json", (boolean)true)) return false;
        return true;
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        gson = new GsonBuilder().setPrettyPrinting().create();
        routesDir = new File(RouteStore.mc.field_1697, "config/cobalt/routes2");
        legacyDir = new File(RouteStore.mc.field_1697, "config/cobalt/routes");
        assignmentsFile = new File(RouteStore.mc.field_1697, "config/cobalt/route-assignments.json");
        loadedRoutes = new LinkedHashMap();
        slotAssignments = new LinkedHashMap();
    }
}

