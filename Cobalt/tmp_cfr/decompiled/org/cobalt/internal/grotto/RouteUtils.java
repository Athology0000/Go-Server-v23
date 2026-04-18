/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.StringsKt
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.grotto;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.cobalt.internal.grotto.GrottoChat;
import org.cobalt.internal.grotto.GrottoRouteRenderer;
import org.cobalt.internal.grotto.RouteOffsets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\b\n\u0002\b\u000e\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u00011B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J7\u0010\f\u001a\u00020\u000b2\u0006\u0010\u0005\u001a\u00020\u00042\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\t\u001a\u00020\u00042\b\u0010\n\u001a\u0004\u0018\u00010\u0004H\u0007\u00a2\u0006\u0004\b\f\u0010\rJ?\u0010\u0010\u001a\u00020\u000b2\u0006\u0010\u0005\u001a\u00020\u00042\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\t\u001a\u00020\u00042\b\u0010\n\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u000f\u001a\u00020\u000eH\u0007\u00a2\u0006\u0004\b\u0010\u0010\u0011J5\u0010\u0012\u001a\u00020\u000b2\u0006\u0010\u0005\u001a\u00020\u00042\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\t\u001a\u00020\u00042\u0006\u0010\u000f\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b\u0012\u0010\u0013J'\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u00042\u0006\u0010\u0018\u001a\u00020\u0017H\u0002\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u000f\u0010\u001c\u001a\u00020\u0019H\u0002\u00a2\u0006\u0004\b\u001c\u0010\u001dJ\u0017\u0010\u001f\u001a\u00020\u000b2\u0006\u0010\u001e\u001a\u00020\u0019H\u0002\u00a2\u0006\u0004\b\u001f\u0010 J!\u0010\"\u001a\u00020\u000b2\u0006\u0010\u001e\u001a\u00020\u00192\b\u0010!\u001a\u0004\u0018\u00010\u0004H\u0002\u00a2\u0006\u0004\b\"\u0010#J=\u0010(\u001a\u00020\u00042\u0006\u0010\u0016\u001a\u00020\u00042\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010%\u001a\u00020$2\u0006\u0010&\u001a\u00020$2\u0006\u0010'\u001a\u00020$H\u0002\u00a2\u0006\u0004\b(\u0010)J5\u0010*\u001a\u00020\u00042\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010%\u001a\u00020$2\u0006\u0010&\u001a\u00020$2\u0006\u0010'\u001a\u00020$H\u0002\u00a2\u0006\u0004\b*\u0010+J5\u0010,\u001a\u00020\u00042\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010%\u001a\u00020$2\u0006\u0010&\u001a\u00020$2\u0006\u0010'\u001a\u00020$H\u0002\u00a2\u0006\u0004\b,\u0010+J5\u0010-\u001a\u00020\u00042\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010%\u001a\u00020$2\u0006\u0010&\u001a\u00020$2\u0006\u0010'\u001a\u00020$H\u0002\u00a2\u0006\u0004\b-\u0010+J\u0019\u0010/\u001a\u00020\u00042\b\u0010.\u001a\u0004\u0018\u00010\u0004H\u0002\u00a2\u0006\u0004\b/\u00100\u00a8\u00062"}, d2={"Lorg/cobalt/internal/grotto/RouteUtils;", "", "<init>", "()V", "", "routeName", "", "Lorg/cobalt/internal/grotto/RouteOffsets$Offset;", "offsets", "preset", "mode", "", "generateRouteForClient", "(Ljava/lang/String;[Lorg/cobalt/internal/grotto/RouteOffsets$Offset;Ljava/lang/String;Ljava/lang/String;)V", "Lnet/minecraft/class_2338;", "base", "generateRouteForClientAt", "(Ljava/lang/String;[Lorg/cobalt/internal/grotto/RouteOffsets$Offset;Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/class_2338;)V", "exportJson", "(Ljava/lang/String;[Lorg/cobalt/internal/grotto/RouteOffsets$Offset;Ljava/lang/String;Lnet/minecraft/class_2338;)V", "Lorg/cobalt/internal/grotto/RouteUtils$ClientId;", "clientId", "name", "", "dm", "Ljava/io/File;", "getOutFileForClient", "(Lorg/cobalt/internal/grotto/RouteUtils$ClientId;Ljava/lang/String;Z)Ljava/io/File;", "getGlobalMinecraftDir", "()Ljava/io/File;", "file", "ensureParent", "(Ljava/io/File;)V", "contents", "writeUtf8", "(Ljava/io/File;Ljava/lang/String;)V", "", "baseX", "baseY", "baseZ", "formatPolarRoute", "(Ljava/lang/String;[Lorg/cobalt/internal/grotto/RouteOffsets$Offset;III)Ljava/lang/String;", "formatNebulaRoute", "([Lorg/cobalt/internal/grotto/RouteOffsets$Offset;III)Ljava/lang/String;", "formatPolinexRoute", "formatMelodyRoute", "text", "escapeJson", "(Ljava/lang/String;)Ljava/lang/String;", "ClientId", "cobalt"})
@SourceDebugExtension(value={"SMAP\nRouteUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RouteUtils.kt\norg/cobalt/internal/grotto/RouteUtils\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,237:1\n1#2:238\n*E\n"})
public final class RouteUtils {
    @NotNull
    public static final RouteUtils INSTANCE = new RouteUtils();

    private RouteUtils() {
    }

    @JvmStatic
    public static final void generateRouteForClient(@NotNull String routeName, @NotNull RouteOffsets.Offset[] offsets, @NotNull String preset, @Nullable String mode) {
        Intrinsics.checkNotNullParameter((Object)routeName, (String)"routeName");
        Intrinsics.checkNotNullParameter((Object)offsets, (String)"offsets");
        Intrinsics.checkNotNullParameter((Object)preset, (String)"preset");
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 client = class_3102;
        class_746 class_7462 = client.field_1724;
        if (class_7462 == null) {
            return;
        }
        class_746 player = class_7462;
        class_2338 class_23382 = player.method_24515();
        Intrinsics.checkNotNullExpressionValue((Object)class_23382, (String)"blockPosition(...)");
        class_2338 base = class_23382;
        RouteUtils.generateRouteForClientAt(routeName, offsets, preset, mode, base);
    }

    /*
     * WARNING - void declaration
     */
    @JvmStatic
    public static final void generateRouteForClientAt(@NotNull String routeName, @NotNull RouteOffsets.Offset[] offsets, @NotNull String preset, @Nullable String mode, @NotNull class_2338 base) {
        Object object;
        String string;
        String string2;
        block7: {
            block6: {
                void it;
                Object o;
                Intrinsics.checkNotNullParameter((Object)routeName, (String)"routeName");
                Intrinsics.checkNotNullParameter((Object)offsets, (String)"offsets");
                Intrinsics.checkNotNullParameter((Object)preset, (String)"preset");
                Intrinsics.checkNotNullParameter((Object)base, (String)"base");
                class_310 class_3102 = class_310.method_1551();
                Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
                class_310 client = class_3102;
                class_638 class_6382 = client.field_1687;
                if (class_6382 == null) {
                    return;
                }
                class_638 level2 = class_6382;
                ArrayList<class_243> points = new ArrayList<class_243>(offsets.length);
                int n = offsets.length;
                for (int i = 0; i < n; ++i) {
                    class_2338 p;
                    o = offsets[i];
                    Intrinsics.checkNotNullExpressionValue((Object)base.method_10069(((RouteOffsets.Offset)o).getX(), ((RouteOffsets.Offset)o).getY(), ((RouteOffsets.Offset)o).getZ()), (String)"offset(...)");
                    points.add(new class_243((double)p.method_10263() + 0.5, (double)p.method_10264() + 0.5, (double)p.method_10260() + 0.5));
                }
                if (mode != null && StringsKt.equals((String)mode, (String)"json", (boolean)true)) {
                    INSTANCE.exportJson(routeName, offsets, preset, base);
                }
                GrottoRouteRenderer.setRoute((class_1937)level2, (List<? extends class_243>)points);
                string2 = routeName;
                string = preset;
                object = mode;
                if (object == null) break block6;
                o = object;
                String string3 = string;
                String string4 = string2;
                boolean bl = false;
                String string5 = " mode=" + (String)it;
                string2 = string4;
                string = string3;
                String string6 = string5;
                object = string6;
                if (string6 != null) break block7;
            }
            object = "";
        }
        GrottoChat.autoRoutes("Loaded route: " + string2 + " preset=" + string + (String)object + " base=" + base.method_10263() + "," + base.method_10264() + "," + base.method_10260());
    }

    private final void exportJson(String routeName, RouteOffsets.Offset[] offsets, String preset, class_2338 base) {
        ClientId clientId = ClientId.Companion.from(preset);
        if (clientId == null) {
            GrottoChat.autoRoutes("Unknown client \"" + preset + "\". Valid: polar, nebula, polinex, melody.");
            return;
        }
        String payload = switch (WhenMappings.$EnumSwitchMapping$0[clientId.ordinal()]) {
            case 1 -> this.formatPolarRoute(routeName, offsets, base.method_10263(), base.method_10264(), base.method_10260());
            case 2 -> this.formatNebulaRoute(offsets, base.method_10263(), base.method_10264(), base.method_10260());
            case 3 -> this.formatPolinexRoute(offsets, base.method_10263(), base.method_10264(), base.method_10260());
            case 4 -> this.formatMelodyRoute(offsets, base.method_10263(), base.method_10264(), base.method_10260());
            default -> throw new NoWhenBranchMatchedException();
        };
        File outFile = this.getOutFileForClient(clientId, routeName, false);
        try {
            this.ensureParent(outFile);
            this.writeUtf8(outFile, payload);
            GrottoChat.autoRoutes("Wrote route to " + outFile.getAbsolutePath());
        }
        catch (IOException ex) {
            GrottoChat.autoRoutes("Failed to write route: " + ex.getMessage());
        }
    }

    private final File getOutFileForClient(ClientId clientId, String name, boolean dm) {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 client = class_3102;
        File file = client.field_1697;
        Intrinsics.checkNotNullExpressionValue((Object)file, (String)"gameDirectory");
        File mcDir = file;
        File globalDir = this.getGlobalMinecraftDir();
        String string = name.toLowerCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
        String lower = string;
        return switch (WhenMappings.$EnumSwitchMapping$0[clientId.ordinal()]) {
            case 1 -> new File(dm ? mcDir : globalDir, "PolarClient/routes/gemstone/" + lower + ".json");
            case 2 -> new File(mcDir, "Nebula/GemstoneMiner/routes/" + lower + ".json");
            case 3 -> new File(dm ? mcDir : globalDir, "polinex/gemstone/" + lower + ".json");
            case 4 -> new File(mcDir, "Melody/ARWayPoints/" + name + ".txt");
            default -> throw new NoWhenBranchMatchedException();
        };
    }

    private final File getGlobalMinecraftDir() {
        File dir;
        String appData = System.getenv("APPDATA");
        CharSequence charSequence = appData;
        if (!(charSequence == null || StringsKt.isBlank((CharSequence)charSequence)) && ((dir = new File(appData, ".minecraft")).exists() || dir.mkdirs())) {
            return dir;
        }
        dir = new File(System.getProperty("user.home"), ".minecraft");
        if (dir.exists() || dir.mkdirs()) {
            return dir;
        }
        File file = class_310.method_1551().field_1697;
        Intrinsics.checkNotNullExpressionValue((Object)file, (String)"gameDirectory");
        return file;
    }

    private final void ensureParent(File file) throws IOException {
        File file2 = file.getParentFile();
        if (file2 == null) {
            return;
        }
        File parent = file2;
        if (!(parent.exists() || parent.mkdirs() || parent.exists())) {
            throw new IOException("Could not create directories: " + parent.getAbsolutePath());
        }
    }

    private final void writeUtf8(File file, String contents) throws IOException {
        Path path = file.toPath();
        String string = contents;
        if (string == null) {
            string = "";
        }
        Files.writeString(path, (CharSequence)string, StandardCharsets.UTF_8, new OpenOption[0]);
    }

    private final String formatPolarRoute(String name, RouteOffsets.Offset[] offsets, int baseX, int baseY, int baseZ) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"name\": \"").append(this.escapeJson(name)).append("\",\n");
        sb.append("  \"type\": \"gemstone\",\n");
        sb.append("  \"positions\": [\n");
        int n = offsets.length;
        for (int i = 0; i < n; ++i) {
            RouteOffsets.Offset o = offsets[i];
            int x = baseX + o.getX();
            int y = baseY + o.getY();
            int z = baseZ + o.getZ();
            sb.append("    { \"x\":").append(x).append(", \"y\":").append(y).append(", \"z\":").append(z).append(", \"action\": \"VEIN\" }");
            sb.append(i == offsets.length - 1 ? "\n" : ",\n");
        }
        sb.append("  ],\n");
        sb.append("  \"targets\": [],\n");
        sb.append("  \"location\": \"CRYSTAL_HOLLOWS\"\n");
        sb.append("}\n");
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
        return string;
    }

    private final String formatNebulaRoute(RouteOffsets.Offset[] offsets, int baseX, int baseY, int baseZ) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        int n = offsets.length;
        for (int i = 0; i < n; ++i) {
            RouteOffsets.Offset o = offsets[i];
            int x = baseX + o.getX();
            int y = baseY + o.getY();
            int z = baseZ + o.getZ();
            sb.append("  { \"pos\": { \"x\":").append(x).append(", \"y\":").append(y).append(", \"z\":").append(z).append(" } }");
            sb.append(i == offsets.length - 1 ? "\n" : ",\n");
        }
        sb.append("]\n");
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
        return string;
    }

    private final String formatPolinexRoute(RouteOffsets.Offset[] offsets, int baseX, int baseY, int baseZ) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        int n = offsets.length;
        for (int i = 0; i < n; ++i) {
            RouteOffsets.Offset o = offsets[i];
            int x = baseX + o.getX();
            int y = baseY + o.getY();
            int z = baseZ + o.getZ();
            sb.append("  { \"x\":").append(x).append(", \"y\":").append(y).append(", \"z\":").append(z).append(", \"moveType\": \"WARP\" }");
            sb.append(i == offsets.length - 1 ? "\n" : ",\n");
        }
        sb.append("]\n");
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
        return string;
    }

    private final String formatMelodyRoute(RouteOffsets.Offset[] offsets, int baseX, int baseY, int baseZ) {
        StringBuilder sb = new StringBuilder();
        for (RouteOffsets.Offset o : offsets) {
            int x = baseX + o.getX();
            int y = baseY + o.getY();
            int z = baseZ + o.getZ();
            sb.append(x).append(":").append(y).append(":").append(z).append("%");
        }
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
        return string;
    }

    private final String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return StringsKt.replace$default((String)StringsKt.replace$default((String)text, (String)"\\", (String)"\\\\", (boolean)false, (int)4, null), (String)"\"", (String)"\\\"", (boolean)false, (int)4, null);
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\u0010\u000e\n\u0002\b\f\b\u0082\u0081\u0002\u0018\u0000 \t2\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\tB\u0011\b\u0002\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0006\u001a\u0004\b\u0007\u0010\bj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\r\u00a8\u0006\u000e"}, d2={"Lorg/cobalt/internal/grotto/RouteUtils$ClientId;", "", "", "id", "<init>", "(Ljava/lang/String;ILjava/lang/String;)V", "Ljava/lang/String;", "getId", "()Ljava/lang/String;", "Companion", "POLAR", "NEBULA", "POLINEX", "MELODY", "cobalt"})
    private static final class ClientId
    extends Enum<ClientId> {
        @NotNull
        public static final Companion Companion;
        @NotNull
        private final String id;
        public static final /* enum */ ClientId POLAR;
        public static final /* enum */ ClientId NEBULA;
        public static final /* enum */ ClientId POLINEX;
        public static final /* enum */ ClientId MELODY;
        private static final /* synthetic */ ClientId[] $VALUES;
        private static final /* synthetic */ EnumEntries $ENTRIES;

        private ClientId(String id) {
            this.id = id;
        }

        @NotNull
        public final String getId() {
            return this.id;
        }

        public static ClientId[] values() {
            return (ClientId[])$VALUES.clone();
        }

        public static ClientId valueOf(String value) {
            return Enum.valueOf(ClientId.class, value);
        }

        @NotNull
        public static EnumEntries<ClientId> getEntries() {
            return $ENTRIES;
        }

        static {
            POLAR = new ClientId("polar");
            NEBULA = new ClientId("nebula");
            POLINEX = new ClientId("polinex");
            MELODY = new ClientId("melody");
            $VALUES = clientIdArray = new ClientId[]{ClientId.POLAR, ClientId.NEBULA, ClientId.POLINEX, ClientId.MELODY};
            $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
            Companion = new Companion(null);
        }

        @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0019\u0010\u0007\u001a\u0004\u0018\u00010\u00062\b\u0010\u0005\u001a\u0004\u0018\u00010\u0004\u00a2\u0006\u0004\b\u0007\u0010\b\u00a8\u0006\t"}, d2={"Lorg/cobalt/internal/grotto/RouteUtils$ClientId$Companion;", "", "<init>", "()V", "", "value", "Lorg/cobalt/internal/grotto/RouteUtils$ClientId;", "from", "(Ljava/lang/String;)Lorg/cobalt/internal/grotto/RouteUtils$ClientId;", "cobalt"})
        @SourceDebugExtension(value={"SMAP\nRouteUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RouteUtils.kt\norg/cobalt/internal/grotto/RouteUtils$ClientId$Companion\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,237:1\n1401#2,2:238\n*S KotlinDebug\n*F\n+ 1 RouteUtils.kt\norg/cobalt/internal/grotto/RouteUtils$ClientId$Companion\n*L\n94#1:238,2\n*E\n"})
        public static final class Companion {
            private Companion() {
            }

            @Nullable
            public final ClientId from(@Nullable String value) {
                ClientId clientId;
                block2: {
                    if (value == null) {
                        return null;
                    }
                    String string = ((Object)StringsKt.trim((CharSequence)value)).toString().toLowerCase(Locale.ROOT);
                    Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
                    String v = string;
                    ClientId[] $this$firstOrNull$iv = ClientId.values();
                    boolean $i$f$firstOrNull = false;
                    int n = $this$firstOrNull$iv.length;
                    for (int i = 0; i < n; ++i) {
                        ClientId element$iv;
                        ClientId it = element$iv = $this$firstOrNull$iv[i];
                        boolean bl = false;
                        if (!Intrinsics.areEqual((Object)it.getId(), (Object)v)) continue;
                        clientId = element$iv;
                        break block2;
                    }
                    clientId = null;
                }
                return clientId;
            }

            public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
                this();
            }
        }
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[ClientId.values().length];
            try {
                nArray[ClientId.POLAR.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[ClientId.NEBULA.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[ClientId.POLINEX.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[ClientId.MELODY.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

