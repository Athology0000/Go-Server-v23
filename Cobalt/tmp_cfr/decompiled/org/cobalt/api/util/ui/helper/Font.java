/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.io.ByteStreamsKt
 *  kotlin.io.CloseableKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.util.ui.helper;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import kotlin.Metadata;
import kotlin.io.ByteStreamsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u0012\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0005\u0010\u0006J\r\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\u0011\u0010\u000b\u001a\u00020\nH\u0096\u0080\u0004\u00a2\u0006\u0004\b\u000b\u0010\fJ\u001b\u0010\u000f\u001a\u00020\u000e2\b\u0010\r\u001a\u0004\u0018\u00010\u0001H\u0096\u0082\u0004\u00a2\u0006\u0004\b\u000f\u0010\u0010R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0011\u001a\u0004\b\u0012\u0010\u0013R\u0014\u0010\u0004\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0004\u0010\u0011R\u0018\u0010\u0015\u001a\u0004\u0018\u00010\u00148\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0015\u0010\u0016\u00a8\u0006\u0017"}, d2={"Lorg/cobalt/api/util/ui/helper/Font;", "", "", "name", "resourcePath", "<init>", "(Ljava/lang/String;Ljava/lang/String;)V", "Ljava/nio/ByteBuffer;", "buffer", "()Ljava/nio/ByteBuffer;", "", "hashCode", "()I", "other", "", "equals", "(Ljava/lang/Object;)Z", "Ljava/lang/String;", "getName", "()Ljava/lang/String;", "", "cachedBytes", "[B", "cobalt"})
@SourceDebugExtension(value={"SMAP\nFont.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Font.kt\norg/cobalt/api/util/ui/helper/Font\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,72:1\n1#2:73\n*E\n"})
public final class Font {
    @NotNull
    private final String name;
    @NotNull
    private final String resourcePath;
    @Nullable
    private byte[] cachedBytes;

    public Font(@NotNull String name, @NotNull String resourcePath) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)resourcePath, (String)"resourcePath");
        this.name = name;
        this.resourcePath = resourcePath;
    }

    @NotNull
    public final String getName() {
        return this.name;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NotNull
    public final ByteBuffer buffer() {
        Object object = this.cachedBytes;
        if (this.cachedBytes == null) {
            Object it;
            Font $this$buffer_u24lambda_u240 = this;
            boolean bl = false;
            InputStream inputStream = $this$buffer_u24lambda_u240.getClass().getResourceAsStream($this$buffer_u24lambda_u240.resourcePath);
            if (inputStream == null) {
                throw new FileNotFoundException($this$buffer_u24lambda_u240.resourcePath);
            }
            InputStream stream = inputStream;
            Object object2 = stream;
            Throwable throwable = null;
            try {
                it = (InputStream)object2;
                boolean bl2 = false;
                it = ByteStreamsKt.readBytes((InputStream)it);
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
            finally {
                CloseableKt.closeFinally((Closeable)object2, (Throwable)throwable);
            }
            Object it2 = object2 = it;
            boolean bl3 = false;
            $this$buffer_u24lambda_u240.cachedBytes = (byte[])it2;
            object = object2;
        }
        byte[] bytes = object;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bytes.length).order(ByteOrder.nativeOrder()).put(bytes).flip();
        Intrinsics.checkNotNull((Object)byteBuffer, (String)"null cannot be cast to non-null type java.nio.ByteBuffer");
        return byteBuffer;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public boolean equals(@Nullable Object other) {
        return other instanceof Font && Intrinsics.areEqual((Object)this.name, (Object)((Font)other).name);
    }
}

