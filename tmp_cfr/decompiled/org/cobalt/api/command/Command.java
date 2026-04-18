/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.command;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0011\n\u0002\b\n\b&\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\b\u001a\u0004\b\t\u0010\nR\u001d\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00020\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010\u000b\u001a\u0004\b\f\u0010\r\u00a8\u0006\u000e"}, d2={"Lorg/cobalt/api/command/Command;", "", "", "name", "", "aliases", "<init>", "(Ljava/lang/String;[Ljava/lang/String;)V", "Ljava/lang/String;", "getName", "()Ljava/lang/String;", "[Ljava/lang/String;", "getAliases", "()[Ljava/lang/String;", "cobalt"})
public abstract class Command {
    @NotNull
    private final String name;
    @NotNull
    private final String[] aliases;

    public Command(@NotNull String name, @NotNull String[] aliases) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)aliases, (String)"aliases");
        this.name = name;
        this.aliases = aliases;
    }

    public /* synthetic */ Command(String string, String[] stringArray, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 2) != 0) {
            stringArray = new String[]{};
        }
        this(string, stringArray);
    }

    @NotNull
    public final String getName() {
        return this.name;
    }

    @NotNull
    public final String[] getAliases() {
        return this.aliases;
    }
}

