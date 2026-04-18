/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.util.StringRepresentable$EnumCodec
 */
package net.minecraft.client.gui.components.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringRepresentable;

@Environment(value=EnvType.CLIENT)
public enum DebugScreenEntryStatus implements StringRepresentable
{
    ALWAYS_ON("alwaysOn"),
    IN_OVERLAY("inOverlay"),
    NEVER("never");

    public static final StringRepresentable.EnumCodec<DebugScreenEntryStatus> CODEC;
    private final String name;

    private DebugScreenEntryStatus(String string2) {
        this.name = string2;
    }

    public String getSerializedName() {
        return this.name;
    }

    static {
        CODEC = StringRepresentable.fromEnum(DebugScreenEntryStatus::values);
    }
}

