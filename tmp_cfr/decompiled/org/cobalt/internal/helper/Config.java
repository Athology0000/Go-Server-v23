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
 *  kotlin.Pair
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.io.CloseableKt
 *  kotlin.io.TextStreamsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.Charsets
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.io.CloseableKt;
import kotlin.io.TextStreamsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.Charsets;
import net.minecraft.class_310;
import org.cobalt.api.addon.Addon;
import org.cobalt.api.addon.AddonMetadata;
import org.cobalt.api.hud.HudAnchor;
import org.cobalt.api.hud.HudElement;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.ModuleManager;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.ui.theme.Theme;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.ui.theme.impl.CustomTheme;
import org.cobalt.internal.loader.AddonLoader;
import org.cobalt.internal.ui.theme.ThemeSerializer;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c0\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J!\u0010\b\u001a\u0014\u0012\u0004\u0012\u00020\u0005\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u00060\u0004H\u0002\u00a2\u0006\u0004\b\b\u0010\tJ\r\u0010\u000b\u001a\u00020\n\u00a2\u0006\u0004\b\u000b\u0010\u0003J\r\u0010\f\u001a\u00020\n\u00a2\u0006\u0004\b\f\u0010\u0003J\u000f\u0010\r\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\r\u0010\u0003J\u000f\u0010\u000e\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\u000e\u0010\u0003R\u0014\u0010\u000f\u001a\u00020\u00058\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u000f\u0010\u0010R\u0014\u0010\u0012\u001a\u00020\u00118\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0012\u0010\u0013R\u001c\u0010\u0016\u001a\n \u0015*\u0004\u0018\u00010\u00140\u00148\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0016\u0010\u0017R\u0014\u0010\u0019\u001a\u00020\u00188\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0019\u0010\u001aR\u0014\u0010\u001b\u001a\u00020\u00188\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001b\u0010\u001a\u00a8\u0006\u001c"}, d2={"Lorg/cobalt/internal/helper/Config;", "", "<init>", "()V", "", "", "", "Lorg/cobalt/api/module/Module;", "buildGroupedModules", "()Ljava/util/Map;", "", "loadModulesConfig", "saveModulesConfig", "loadThemesConfig", "saveThemesConfig", "BUILTIN_ADDON_ID", "Ljava/lang/String;", "Lnet/minecraft/class_310;", "mc", "Lnet/minecraft/class_310;", "Lcom/google/gson/Gson;", "kotlin.jvm.PlatformType", "gson", "Lcom/google/gson/Gson;", "Ljava/io/File;", "modulesFile", "Ljava/io/File;", "themesFile", "cobalt"})
@SourceDebugExtension(value={"SMAP\nConfig.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Config.kt\norg/cobalt/internal/helper/Config\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,194:1\n1915#2,2:195\n777#2:197\n873#2,2:198\n1915#2:201\n1220#2,2:202\n1249#2,4:204\n1915#2:208\n1220#2,2:209\n1249#2,4:211\n1915#2,2:215\n1220#2,2:217\n1249#2,4:219\n1915#2:223\n1915#2,2:224\n1916#2:226\n1916#2:227\n1916#2:228\n1915#2:230\n1915#2,2:231\n1915#2:233\n1915#2,2:234\n1916#2:236\n1916#2:237\n1915#2,2:239\n296#2,2:241\n1915#2,2:243\n1#3:200\n221#4:229\n222#4:238\n*S KotlinDebug\n*F\n+ 1 Config.kt\norg/cobalt/internal/helper/Config\n*L\n30#1:195,2\n38#1:197\n38#1:198,2\n61#1:201\n66#1:202,2\n66#1:204,4\n68#1:208\n73#1:209,2\n73#1:211,4\n74#1:215,2\n78#1:217,2\n78#1:219,4\n79#1:223\n94#1:224,2\n79#1:226\n68#1:227\n61#1:228\n113#1:230\n118#1:231,2\n124#1:233\n134#1:234,2\n124#1:236\n113#1:237\n166#1:239,2\n171#1:241,2\n180#1:243,2\n108#1:229\n108#1:238\n*E\n"})
public final class Config {
    @NotNull
    public static final Config INSTANCE = new Config();
    @NotNull
    private static final String BUILTIN_ADDON_ID = "cobalt";
    @NotNull
    private static final class_310 mc;
    private static final Gson gson;
    @NotNull
    private static final File modulesFile;
    @NotNull
    private static final File themesFile;

    private Config() {
    }

    /*
     * WARNING - void declaration
     */
    private final Map<String, List<Module>> buildGroupedModules() {
        void $this$filterTo$iv$iv;
        Object element$iv2;
        Set addonModules = new LinkedHashSet();
        Map grouped = new LinkedHashMap();
        Iterable $this$forEach$iv = AddonLoader.INSTANCE.getAddons();
        boolean $i$f$forEach = false;
        for (Object element$iv2 : $this$forEach$iv) {
            Pair pair = (Pair)element$iv2;
            boolean bl = false;
            AddonMetadata metadata = (AddonMetadata)pair.component1();
            Addon addon = (Addon)pair.component2();
            List<Module> modules = addon.getModules();
            addonModules.addAll((Collection)modules);
            if (!(!((Collection)modules).isEmpty())) continue;
            grouped.put(metadata.getId(), CollectionsKt.toMutableList((Collection)modules));
        }
        Iterable $this$filter$iv = ModuleManager.getModules();
        boolean $i$f$filter = false;
        element$iv2 = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            Module it = (Module)element$iv$iv;
            boolean bl = false;
            if (!(!addonModules.contains(it))) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        List builtinModules = (List)destination$iv$iv;
        if (!((Collection)builtinModules).isEmpty()) {
            grouped.put(BUILTIN_ADDON_ID, CollectionsKt.toMutableList((Collection)builtinModules));
        }
        return grouped;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    public final void loadModulesConfig() {
        block32: {
            void $this$forEach$iv;
            Object $this$loadModulesConfig_u24lambda_u241;
            Object it;
            this.loadThemesConfig();
            if (!modulesFile.exists()) {
                File file = modulesFile.getParentFile();
                if (file != null) {
                    file.mkdirs();
                }
                modulesFile.createNewFile();
                return;
            }
            Object object = modulesFile;
            Object object2 = Charsets.UTF_8;
            int n = 8192;
            Object object3 = object;
            object = (object3 = (Reader)new InputStreamReader((InputStream)new FileInputStream((File)object3), (Charset)object2)) instanceof BufferedReader ? (BufferedReader)object3 : new BufferedReader((Reader)object3, n);
            object2 = null;
            try {
                it = (BufferedReader)object;
                boolean bl = false;
                it = TextStreamsKt.readText((Reader)((Reader)it));
            }
            catch (Throwable bl) {
                object2 = bl;
                throw bl;
            }
            finally {
                CloseableKt.closeFinally((Closeable)object, (Throwable)object2);
            }
            Object text = it;
            if (((CharSequence)text).length() == 0) {
                return;
            }
            Map<String, List<Module>> grouped = this.buildGroupedModules();
            it = this;
            try {
                $this$loadModulesConfig_u24lambda_u241 = (Config)it;
                boolean bl = false;
                $this$loadModulesConfig_u24lambda_u241 = Result.constructor-impl((Object)JsonParser.parseString((String)text).getAsJsonArray());
            }
            catch (Throwable throwable) {
                $this$loadModulesConfig_u24lambda_u241 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
            }
            it = $this$loadModulesConfig_u24lambda_u241;
            object2 = (JsonArray)(Result.isFailure-impl((Object)it) ? null : it);
            if (object2 == null) break block32;
            it = (Iterable)object2;
            boolean $i$f$forEach = false;
            for (Object element$iv : $this$forEach$iv) {
                void $this$associateByTo$iv$iv;
                List<Module> modules;
                JsonElement element = (JsonElement)element$iv;
                boolean bl = false;
                JsonObject addonObj = element.getAsJsonObject();
                String addonId = addonObj.get("addon").getAsString();
                if (grouped.get(addonId) == null) continue;
                Iterable $this$associateBy$iv = modules;
                boolean $i$f$associateBy = false;
                int capacity$iv = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateBy$iv, (int)10)), (int)16);
                Iterable iterable = $this$associateBy$iv;
                Map destination$iv$iv = new LinkedHashMap(capacity$iv);
                boolean $i$f$associateByTo = false;
                for (Object element$iv$iv : $this$associateByTo$iv$iv) {
                    void it2;
                    Module module = (Module)element$iv$iv;
                    Map map = destination$iv$iv;
                    boolean bl2 = false;
                    map.put(it2.getName(), element$iv$iv);
                }
                Map modulesMap = destination$iv$iv;
                JsonArray jsonArray = addonObj.getAsJsonArray("modules");
                if (jsonArray == null) continue;
                Iterable $this$forEach$iv2 = (Iterable)jsonArray;
                boolean $i$f$forEach2 = false;
                for (Object element$iv2 : $this$forEach$iv2) {
                    void $this$associateByTo$iv$iv2;
                    Object $this$loadModulesConfig_u24lambda_u242_u241_u241_u240;
                    Map map;
                    void $this$associateByTo$iv$iv3;
                    Module module;
                    JsonElement moduleElement = (JsonElement)element$iv2;
                    boolean bl3 = false;
                    JsonObject moduleObj = moduleElement.getAsJsonObject();
                    String moduleName = moduleObj.get("name").getAsString();
                    if ((Module)modulesMap.get(moduleName) == null) continue;
                    Iterable $this$associateBy$iv2 = module.getSettings();
                    boolean $i$f$associateBy2 = false;
                    int capacity$iv2 = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateBy$iv2, (int)10)), (int)16);
                    Iterable iterable2 = $this$associateBy$iv2;
                    Object destination$iv$iv2 = new LinkedHashMap(capacity$iv2);
                    boolean $i$f$associateByTo2 = false;
                    for (Object element$iv$iv : $this$associateByTo$iv$iv3) {
                        void it3;
                        Setting setting = (Setting)element$iv$iv;
                        map = destination$iv$iv2;
                        boolean bl4 = false;
                        map.put(it3.getName(), element$iv$iv);
                    }
                    Map settingsMap = destination$iv$iv2;
                    Object object4 = moduleObj.getAsJsonObject("settings");
                    if (object4 != null && (object4 = object4.entrySet()) != null) {
                        Iterable $this$forEach$iv3 = (Iterable)object4;
                        boolean $i$f$forEach3 = false;
                        for (Object element$iv3 : $this$forEach$iv3) {
                            Map.Entry entry = (Map.Entry)element$iv3;
                            boolean bl5 = false;
                            Intrinsics.checkNotNull((Object)entry);
                            String key = (String)entry.getKey();
                            JsonElement value = (JsonElement)entry.getValue();
                            Config config = INSTANCE;
                            try {
                                Unit unit;
                                $this$loadModulesConfig_u24lambda_u242_u241_u241_u240 = config;
                                boolean bl6 = false;
                                Setting setting = (Setting)settingsMap.get(key);
                                if (setting != null) {
                                    Intrinsics.checkNotNull((Object)value);
                                    setting.read(value);
                                    unit = Unit.INSTANCE;
                                } else {
                                    unit = null;
                                }
                                $this$loadModulesConfig_u24lambda_u242_u241_u241_u240 = Result.constructor-impl(unit);
                            }
                            catch (Throwable throwable) {
                                $this$loadModulesConfig_u24lambda_u242_u241_u241_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
                            }
                        }
                    }
                    Iterable $this$associateBy$iv3 = module.getHudElements();
                    boolean $i$f$associateBy3 = false;
                    int capacity$iv3 = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateBy$iv3, (int)10)), (int)16);
                    destination$iv$iv2 = $this$associateBy$iv3;
                    Map destination$iv$iv3 = new LinkedHashMap(capacity$iv3);
                    boolean $i$f$associateByTo3 = false;
                    for (Object element$iv$iv : $this$associateByTo$iv$iv2) {
                        void it4;
                        HudElement value = (HudElement)element$iv$iv;
                        map = destination$iv$iv3;
                        boolean bl7 = false;
                        map.put(it4.getId(), element$iv$iv);
                    }
                    Map hudElementsMap = destination$iv$iv3;
                    JsonArray jsonArray2 = moduleObj.getAsJsonArray("hudElements");
                    if (jsonArray2 == null) continue;
                    Iterable $this$forEach$iv4 = (Iterable)jsonArray2;
                    boolean $i$f$forEach4 = false;
                    for (Object element$iv4 : $this$forEach$iv4) {
                        Object object5;
                        HudElement hudElement;
                        HudElement hudElement2;
                        JsonObject hudObj;
                        block34: {
                            block33: {
                                Object $this$loadModulesConfig_u24lambda_u242_u241_u243_u240_u240;
                                Object hudId;
                                JsonElement hudEl = (JsonElement)element$iv4;
                                boolean bl8 = false;
                                hudObj = hudEl.getAsJsonObject();
                                Object object6 = hudObj.get("id");
                                if (object6 == null) continue;
                                if ((object6 = object6.getAsString()) == null || (HudElement)hudElementsMap.get(hudId = object6) == null) continue;
                                JsonElement jsonElement = hudObj.get("enabled");
                                hudElement2.setEnabled(jsonElement != null ? jsonElement.getAsBoolean() : true);
                                hudElement = hudElement2;
                                object5 = hudObj.get("anchor");
                                if (object5 == null || (object5 = object5.getAsString()) == null) break block33;
                                $this$loadModulesConfig_u24lambda_u242_u241_u241_u240 = object5;
                                HudElement hudElement3 = hudElement;
                                boolean bl9 = false;
                                Object object7 = INSTANCE;
                                try {
                                    void it5;
                                    $this$loadModulesConfig_u24lambda_u242_u241_u243_u240_u240 = object7;
                                    boolean bl10 = false;
                                    $this$loadModulesConfig_u24lambda_u242_u241_u243_u240_u240 = Result.constructor-impl((Object)((Object)HudAnchor.valueOf((String)it5)));
                                }
                                catch (Throwable bl10) {
                                    $this$loadModulesConfig_u24lambda_u242_u241_u243_u240_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl10));
                                }
                                object7 = $this$loadModulesConfig_u24lambda_u242_u241_u243_u240_u240;
                                HudAnchor hudAnchor = (HudAnchor)((Object)(Result.isFailure-impl((Object)object7) ? null : object7));
                                hudElement = hudElement3;
                                object5 = hudAnchor;
                                if (hudAnchor != null) break block34;
                            }
                            object5 = HudAnchor.TOP_LEFT;
                        }
                        hudElement.setAnchor((HudAnchor)((Object)object5));
                        JsonElement jsonElement = hudObj.get("offsetX");
                        hudElement2.setOffsetX(jsonElement != null ? jsonElement.getAsFloat() : 10.0f);
                        JsonElement jsonElement2 = hudObj.get("offsetY");
                        hudElement2.setOffsetY(jsonElement2 != null ? jsonElement2.getAsFloat() : 10.0f);
                        JsonElement jsonElement3 = hudObj.get("scale");
                        hudElement2.setScale(jsonElement3 != null ? RangesKt.coerceIn((float)jsonElement3.getAsFloat(), (float)0.5f, (float)3.0f) : 1.0f);
                        JsonObject hudSettingsObj = hudObj.getAsJsonObject("settings");
                        if (hudSettingsObj == null) continue;
                        Iterable $this$forEach$iv5 = hudElement2.getSettings();
                        boolean $i$f$forEach5 = false;
                        for (Object element$iv5 : $this$forEach$iv5) {
                            Object object8;
                            Setting setting = (Setting)element$iv5;
                            boolean bl11 = false;
                            if (hudSettingsObj.get(setting.getName()) == null) continue;
                            boolean bl12 = false;
                            Config config = INSTANCE;
                            try {
                                JsonElement jsonEl;
                                Config $this$loadModulesConfig_u24lambda_u242_u241_u243_u241_u240_u240 = config;
                                boolean bl13 = false;
                                setting.read(jsonEl);
                                object8 = Result.constructor-impl((Object)Unit.INSTANCE);
                            }
                            catch (Throwable throwable) {
                                object8 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
                            }
                        }
                    }
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void saveModulesConfig() {
        JsonArray jsonArray = new JsonArray();
        Map<String, List<Module>> $this$forEach$iv = this.buildGroupedModules();
        boolean $i$f$forEach = false;
        Iterator<Map.Entry<String, List<Module>>> iterator = $this$forEach$iv.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<Module>> entry;
            Map.Entry<String, List<Module>> entry2 = entry = iterator.next();
            boolean bl = false;
            String addonId = entry2.getKey();
            List<Module> modules = entry2.getValue();
            JsonObject addonObject = new JsonObject();
            addonObject.addProperty("addon", addonId);
            JsonArray modulesArray = new JsonArray();
            Iterable $this$forEach$iv2 = modules;
            boolean $i$f$forEach2 = false;
            for (Object element$iv3 : $this$forEach$iv2) {
                Module module = (Module)element$iv3;
                boolean bl2 = false;
                JsonObject moduleObject = new JsonObject();
                moduleObject.addProperty("name", module.getName());
                JsonObject settingsObject = new JsonObject();
                Iterable $this$forEach$iv3 = module.getSettings();
                boolean $i$f$forEach3 = false;
                for (Object element$iv4 : $this$forEach$iv3) {
                    Setting setting = (Setting)element$iv4;
                    boolean bl3 = false;
                    settingsObject.add(setting.getName(), setting.write());
                }
                moduleObject.add("settings", (JsonElement)settingsObject);
                JsonArray hudElementsArray = new JsonArray();
                Iterable $this$forEach$iv4 = module.getHudElements();
                boolean $i$f$forEach4 = false;
                for (Object element$iv5 : $this$forEach$iv4) {
                    HudElement hudElement = (HudElement)element$iv5;
                    boolean bl4 = false;
                    JsonObject hudObj = new JsonObject();
                    hudObj.addProperty("id", hudElement.getId());
                    hudObj.addProperty("enabled", Boolean.valueOf(hudElement.getEnabled()));
                    hudObj.addProperty("anchor", hudElement.getAnchor().name());
                    hudObj.addProperty("offsetX", (Number)Float.valueOf(hudElement.getOffsetX()));
                    hudObj.addProperty("offsetY", (Number)Float.valueOf(hudElement.getOffsetY()));
                    hudObj.addProperty("scale", (Number)Float.valueOf(hudElement.getScale()));
                    JsonObject hudSettingsObj = new JsonObject();
                    Iterable $this$forEach$iv5 = hudElement.getSettings();
                    boolean $i$f$forEach5 = false;
                    for (Object element$iv6 : $this$forEach$iv5) {
                        Setting setting = (Setting)element$iv6;
                        boolean bl5 = false;
                        hudSettingsObj.add(setting.getName(), setting.write());
                    }
                    hudObj.add("settings", (JsonElement)hudSettingsObj);
                    hudElementsArray.add((JsonElement)hudObj);
                }
                moduleObject.add("hudElements", (JsonElement)hudElementsArray);
                modulesArray.add((JsonElement)moduleObject);
            }
            addonObject.add("modules", (JsonElement)modulesArray);
            jsonArray.add((JsonElement)addonObject);
        }
        Object object = modulesFile;
        Object object2 = Charsets.UTF_8;
        int n = 8192;
        Object object3 = object;
        Writer throwable = new OutputStreamWriter((OutputStream)new FileOutputStream((File)object3), (Charset)object2);
        object = throwable instanceof BufferedWriter ? (BufferedWriter)throwable : new BufferedWriter(throwable, n);
        object2 = null;
        try {
            BufferedWriter it = (BufferedWriter)object;
            boolean bl = false;
            it.write(gson.toJson((JsonElement)jsonArray));
            Unit unit = Unit.INSTANCE;
        }
        catch (Throwable throwable2) {
            object2 = throwable2;
            throw throwable2;
        }
        finally {
            CloseableKt.closeFinally((Closeable)object, (Throwable)object2);
        }
        this.saveThemesConfig();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void loadThemesConfig() {
        block17: {
            Object object;
            Object $this$loadThemesConfig_u24lambda_u241;
            Object it;
            if (!themesFile.exists()) {
                File file = themesFile.getParentFile();
                if (file != null) {
                    file.mkdirs();
                }
                themesFile.createNewFile();
                return;
            }
            Object object2 = themesFile;
            Object object3 = Charsets.UTF_8;
            int n = 8192;
            Object object4 = object2;
            object2 = (object4 = (Reader)new InputStreamReader((InputStream)new FileInputStream((File)object4), (Charset)object3)) instanceof BufferedReader ? (BufferedReader)object4 : new BufferedReader((Reader)object4, n);
            object3 = null;
            try {
                it = (BufferedReader)object2;
                boolean bl = false;
                it = TextStreamsKt.readText((Reader)((Reader)it));
            }
            catch (Throwable bl) {
                object3 = bl;
                throw bl;
            }
            finally {
                CloseableKt.closeFinally((Closeable)object2, (Throwable)object3);
            }
            Object text = it;
            if (((CharSequence)text).length() == 0) {
                return;
            }
            object3 = this;
            try {
                $this$loadThemesConfig_u24lambda_u241 = (Config)object3;
                boolean bl = false;
                $this$loadThemesConfig_u24lambda_u241 = Result.constructor-impl((Object)JsonParser.parseString((String)text).getAsJsonObject());
            }
            catch (Throwable bl) {
                $this$loadThemesConfig_u24lambda_u241 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
            }
            object3 = $this$loadThemesConfig_u24lambda_u241;
            object2 = (JsonObject)(Result.isFailure-impl((Object)object3) ? null : object3);
            if (object2 == null) break block17;
            Object root = object2;
            boolean bl = false;
            JsonArray jsonArray = root.getAsJsonArray("themes");
            if (jsonArray != null) {
                Iterable $this$forEach$iv = (Iterable)jsonArray;
                boolean $i$f$forEach = false;
                for (Object element$iv : $this$forEach$iv) {
                    JsonElement element = (JsonElement)element$iv;
                    boolean bl2 = false;
                    JsonObject jsonObject = element.getAsJsonObject();
                    Intrinsics.checkNotNullExpressionValue((Object)jsonObject, (String)"getAsJsonObject(...)");
                    ThemeManager.INSTANCE.registerTheme(ThemeSerializer.INSTANCE.fromJson(jsonObject));
                }
            }
            if ((object = root.get("currentTheme")) != null && (object = object.getAsString()) != null) {
                Object v4;
                block16: {
                    Object themeName = object;
                    boolean bl3 = false;
                    Iterable $this$firstOrNull$iv = ThemeManager.INSTANCE.getThemes();
                    boolean $i$f$firstOrNull = false;
                    for (Object element$iv : $this$firstOrNull$iv) {
                        Theme it2 = (Theme)element$iv;
                        boolean bl4 = false;
                        if (!Intrinsics.areEqual((Object)it2.getName(), (Object)themeName)) continue;
                        v4 = element$iv;
                        break block16;
                    }
                    v4 = null;
                }
                Theme theme = v4;
                if (theme != null) {
                    Theme it3 = theme;
                    boolean bl5 = false;
                    ThemeManager.INSTANCE.setTheme(it3);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void saveThemesConfig() {
        Object theme;
        JsonArray themeArray = new JsonArray();
        Iterable $this$forEach$iv = ThemeManager.INSTANCE.getThemes();
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            theme = (Theme)element$iv;
            boolean bl = false;
            if (!(theme instanceof CustomTheme)) continue;
            themeArray.add((JsonElement)ThemeSerializer.INSTANCE.toJson((CustomTheme)theme));
        }
        JsonObject root = new JsonObject();
        root.add("themes", (JsonElement)themeArray);
        root.addProperty("currentTheme", ThemeManager.INSTANCE.getCurrentTheme().getName());
        File file = themesFile.getParentFile();
        if (file != null) {
            file.mkdirs();
        }
        Object object = themesFile;
        Object object2 = Charsets.UTF_8;
        int element$iv = 8192;
        theme = object;
        theme = new OutputStreamWriter((OutputStream)new FileOutputStream((File)theme), (Charset)object2);
        object = theme instanceof BufferedWriter ? (BufferedWriter)theme : new BufferedWriter((Writer)theme, element$iv);
        object2 = null;
        try {
            BufferedWriter it = (BufferedWriter)object;
            boolean bl = false;
            it.write(gson.toJson((JsonElement)root));
            Unit unit = Unit.INSTANCE;
        }
        catch (Throwable throwable) {
            object2 = throwable;
            throw throwable;
        }
        finally {
            CloseableKt.closeFinally((Closeable)object, (Throwable)object2);
        }
    }

    static {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        mc = class_3102;
        gson = new GsonBuilder().setPrettyPrinting().create();
        modulesFile = new File(Config.mc.field_1697, "config/cobalt/addons.json");
        themesFile = new File(Config.mc.field_1697, "config/cobalt/themes.json");
    }
}

