/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLConnection;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=2, xi=48, d1={"\u0000\u001a\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a)\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0001\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\b\u00a8\u0006\t"}, d2={"", "url", "", "timeout", "", "useCaches", "Ljava/io/InputStream;", "setupConnection", "(Ljava/lang/String;IZ)Ljava/io/InputStream;", "cobalt"})
public final class WebUtilsKt {
    @NotNull
    public static final InputStream setupConnection(@NotNull String url, int timeout, boolean useCaches) {
        Intrinsics.checkNotNullParameter((Object)url, (String)"url");
        URLConnection uRLConnection = new URI(url).toURL().openConnection();
        Intrinsics.checkNotNull((Object)uRLConnection, (String)"null cannot be cast to non-null type java.net.HttpURLConnection");
        HttpURLConnection connection = (HttpURLConnection)uRLConnection;
        connection.setRequestMethod("GET");
        connection.setUseCaches(useCaches);
        connection.addRequestProperty("User-Agent", "Cobalt");
        connection.setReadTimeout(timeout);
        connection.setConnectTimeout(timeout);
        connection.setDoOutput(true);
        InputStream inputStream = connection.getInputStream();
        Intrinsics.checkNotNullExpressionValue((Object)inputStream, (String)"getInputStream(...)");
        return inputStream;
    }

    public static /* synthetic */ InputStream setupConnection$default(String string, int n, boolean bl, int n2, Object object) {
        if ((n2 & 2) != 0) {
            n = 5000;
        }
        if ((n2 & 4) != 0) {
            bl = true;
        }
        return WebUtilsKt.setupConnection(string, n, bl);
    }
}

