/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.module.setting.impl;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.util.helper.KeyBind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\n\b\u0086\b\u0018\u00002\u00020\u0001B\u001b\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\b\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\n\u0010\u000bJ$\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u0004H\u00c6\u0001\u00a2\u0006\u0004\b\f\u0010\rJ\u001b\u0010\u0010\u001a\u00020\u000f2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0011\u0010\u0013\u001a\u00020\u0012H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0011\u0010\u0015\u001a\u00020\u0004H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0015\u0010\u000bR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u0016\u001a\u0004\b\u0017\u0010\tR\"\u0010\u0005\u001a\u00020\u00048\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0005\u0010\u0018\u001a\u0004\b\u0019\u0010\u000b\"\u0004\b\u001a\u0010\u001b\u00a8\u0006\u001c"}, d2={"Lorg/cobalt/api/module/setting/impl/CommandHotkeyValue;", "", "Lorg/cobalt/api/util/helper/KeyBind;", "keyBind", "", "command", "<init>", "(Lorg/cobalt/api/util/helper/KeyBind;Ljava/lang/String;)V", "component1", "()Lorg/cobalt/api/util/helper/KeyBind;", "component2", "()Ljava/lang/String;", "copy", "(Lorg/cobalt/api/util/helper/KeyBind;Ljava/lang/String;)Lorg/cobalt/api/module/setting/impl/CommandHotkeyValue;", "other", "", "equals", "(Ljava/lang/Object;)Z", "", "hashCode", "()I", "toString", "Lorg/cobalt/api/util/helper/KeyBind;", "getKeyBind", "Ljava/lang/String;", "getCommand", "setCommand", "(Ljava/lang/String;)V", "cobalt"})
public final class CommandHotkeyValue {
    @NotNull
    private final KeyBind keyBind;
    @NotNull
    private String command;

    public CommandHotkeyValue(@NotNull KeyBind keyBind, @NotNull String command) {
        Intrinsics.checkNotNullParameter((Object)keyBind, (String)"keyBind");
        Intrinsics.checkNotNullParameter((Object)command, (String)"command");
        this.keyBind = keyBind;
        this.command = command;
    }

    public /* synthetic */ CommandHotkeyValue(KeyBind keyBind, String string, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 1) != 0) {
            keyBind = new KeyBind(-1);
        }
        if ((n & 2) != 0) {
            string = "";
        }
        this(keyBind, string);
    }

    @NotNull
    public final KeyBind getKeyBind() {
        return this.keyBind;
    }

    @NotNull
    public final String getCommand() {
        return this.command;
    }

    public final void setCommand(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        this.command = string;
    }

    @NotNull
    public final KeyBind component1() {
        return this.keyBind;
    }

    @NotNull
    public final String component2() {
        return this.command;
    }

    @NotNull
    public final CommandHotkeyValue copy(@NotNull KeyBind keyBind, @NotNull String command) {
        Intrinsics.checkNotNullParameter((Object)keyBind, (String)"keyBind");
        Intrinsics.checkNotNullParameter((Object)command, (String)"command");
        return new CommandHotkeyValue(keyBind, command);
    }

    public static /* synthetic */ CommandHotkeyValue copy$default(CommandHotkeyValue commandHotkeyValue, KeyBind keyBind, String string, int n, Object object) {
        if ((n & 1) != 0) {
            keyBind = commandHotkeyValue.keyBind;
        }
        if ((n & 2) != 0) {
            string = commandHotkeyValue.command;
        }
        return commandHotkeyValue.copy(keyBind, string);
    }

    @NotNull
    public String toString() {
        return "CommandHotkeyValue(keyBind=" + this.keyBind + ", command=" + this.command + ")";
    }

    public int hashCode() {
        int result = this.keyBind.hashCode();
        result = result * 31 + this.command.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CommandHotkeyValue)) {
            return false;
        }
        CommandHotkeyValue commandHotkeyValue = (CommandHotkeyValue)other;
        if (!Intrinsics.areEqual((Object)this.keyBind, (Object)commandHotkeyValue.keyBind)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.command, (Object)commandHotkeyValue.command);
    }

    public CommandHotkeyValue() {
        this(null, null, 3, null);
    }
}

