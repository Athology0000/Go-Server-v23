/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.io.ByteStreamsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.system.MemoryUtil
 */
package org.cobalt.api.util.ui.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import kotlin.Metadata;
import kotlin.io.ByteStreamsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.cobalt.api.util.WebUtilsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0011\u0018\u0000  2\u00020\u0001:\u0001 B/\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0004\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0006\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\b\u00a2\u0006\u0004\b\n\u0010\u000bJ\r\u0010\t\u001a\u00020\b\u00a2\u0006\u0004\b\t\u0010\fJ\u001b\u0010\u000e\u001a\u00020\u00042\b\u0010\r\u001a\u0004\u0018\u00010\u0001H\u0096\u0082\u0004\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0011\u0010\u0011\u001a\u00020\u0010H\u0096\u0080\u0004\u00a2\u0006\u0004\b\u0011\u0010\u0012R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0013\u001a\u0004\b\u0014\u0010\u0015R\"\u0010\u0005\u001a\u00020\u00048\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0005\u0010\u0016\u001a\u0004\b\u0005\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\"\u0010\u0007\u001a\u00020\u00068\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0007\u0010\u001a\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001eR\u0018\u0010\t\u001a\u0004\u0018\u00010\b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\t\u0010\u001f\u00a8\u0006!"}, d2={"Lorg/cobalt/api/util/ui/helper/Image;", "", "", "identifier", "", "isSVG", "Ljava/io/InputStream;", "stream", "Ljava/nio/ByteBuffer;", "buffer", "<init>", "(Ljava/lang/String;ZLjava/io/InputStream;Ljava/nio/ByteBuffer;)V", "()Ljava/nio/ByteBuffer;", "other", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "Ljava/lang/String;", "getIdentifier", "()Ljava/lang/String;", "Z", "()Z", "setSVG", "(Z)V", "Ljava/io/InputStream;", "getStream", "()Ljava/io/InputStream;", "setStream", "(Ljava/io/InputStream;)V", "Ljava/nio/ByteBuffer;", "Companion", "cobalt"})
public final class Image {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final String identifier;
    private boolean isSVG;
    @NotNull
    private InputStream stream;
    @Nullable
    private ByteBuffer buffer;

    public Image(@NotNull String identifier, boolean isSVG, @NotNull InputStream stream, @Nullable ByteBuffer buffer) {
        Intrinsics.checkNotNullParameter((Object)identifier, (String)"identifier");
        Intrinsics.checkNotNullParameter((Object)stream, (String)"stream");
        this.identifier = identifier;
        this.isSVG = isSVG;
        this.stream = stream;
        this.buffer = buffer;
        this.isSVG = StringsKt.endsWith((String)this.identifier, (String)".svg", (boolean)true);
    }

    public /* synthetic */ Image(String string, boolean bl, InputStream inputStream, ByteBuffer byteBuffer, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 2) != 0) {
            bl = false;
        }
        if ((n & 4) != 0) {
            inputStream = Image.Companion.getStream(string);
        }
        if ((n & 8) != 0) {
            byteBuffer = null;
        }
        this(string, bl, inputStream, byteBuffer);
    }

    @NotNull
    public final String getIdentifier() {
        return this.identifier;
    }

    public final boolean isSVG() {
        return this.isSVG;
    }

    public final void setSVG(boolean bl) {
        this.isSVG = bl;
    }

    @NotNull
    public final InputStream getStream() {
        return this.stream;
    }

    public final void setStream(@NotNull InputStream inputStream) {
        Intrinsics.checkNotNullParameter((Object)inputStream, (String)"<set-?>");
        this.stream = inputStream;
    }

    @NotNull
    public final ByteBuffer buffer() {
        if (this.buffer == null) {
            byte[] bytes = ByteStreamsKt.readBytes((InputStream)this.stream);
            ByteBuffer byteBuffer = MemoryUtil.memAlloc((int)bytes.length).put(bytes).flip();
            Intrinsics.checkNotNull((Object)byteBuffer, (String)"null cannot be cast to non-null type java.nio.ByteBuffer");
            this.buffer = byteBuffer;
            this.stream.close();
        }
        ByteBuffer byteBuffer = this.buffer;
        if (byteBuffer == null) {
            throw new IllegalStateException("Image has no data");
        }
        return byteBuffer;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Image)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.identifier, (Object)((Image)other).identifier);
    }

    public int hashCode() {
        return this.identifier.hashCode();
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u0007\u0010\b\u00a8\u0006\t"}, d2={"Lorg/cobalt/api/util/ui/helper/Image$Companion;", "", "<init>", "()V", "", "path", "Ljava/io/InputStream;", "getStream", "(Ljava/lang/String;)Ljava/io/InputStream;", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        private final InputStream getStream(String path) {
            InputStream inputStream;
            String trimmedPath = ((Object)StringsKt.trim((CharSequence)path)).toString();
            if (StringsKt.startsWith$default((String)trimmedPath, (String)"http", (boolean)false, (int)2, null)) {
                inputStream = WebUtilsKt.setupConnection$default(trimmedPath, 0, false, 6, null);
            } else {
                File file = new File(trimmedPath);
                if (file.exists() && file.isFile()) {
                    InputStream inputStream2 = Files.newInputStream(file.toPath(), new OpenOption[0]);
                    inputStream = inputStream2;
                    Intrinsics.checkNotNullExpressionValue((Object)inputStream2, (String)"newInputStream(...)");
                } else {
                    inputStream = this.getClass().getResourceAsStream(trimmedPath);
                    if (inputStream == null) {
                        throw new FileNotFoundException(trimmedPath);
                    }
                }
            }
            return inputStream;
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

