/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.module.setting.impl;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\b\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\b\u00a8\u0006\t"}, d2={"Lorg/cobalt/api/module/setting/impl/InfoType;", "", "<init>", "(Ljava/lang/String;I)V", "INFO", "WARNING", "SUCCESS", "ERROR", "SEPARATOR", "cobalt"})
public final class InfoType
extends Enum<InfoType> {
    public static final /* enum */ InfoType INFO = new InfoType();
    public static final /* enum */ InfoType WARNING = new InfoType();
    public static final /* enum */ InfoType SUCCESS = new InfoType();
    public static final /* enum */ InfoType ERROR = new InfoType();
    public static final /* enum */ InfoType SEPARATOR = new InfoType();
    private static final /* synthetic */ InfoType[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    public static InfoType[] values() {
        return (InfoType[])$VALUES.clone();
    }

    public static InfoType valueOf(String value) {
        return Enum.valueOf(InfoType.class, value);
    }

    @NotNull
    public static EnumEntries<InfoType> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = infoTypeArray = new InfoType[]{InfoType.INFO, InfoType.WARNING, InfoType.SUCCESS, InfoType.ERROR, InfoType.SEPARATOR};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}

