/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.class_1304
 *  net.minecraft.class_1799
 *  net.minecraft.class_638
 *  net.minecraft.class_742
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.wardrobe;

import com.mojang.authlib.GameProfile;
import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.class_1304;
import net.minecraft.class_1799;
import net.minecraft.class_638;
import net.minecraft.class_742;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u001d\u0010\n\u001a\u00020\t2\u000e\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006\u00a2\u0006\u0004\b\n\u0010\u000b\u00a8\u0006\f"}, d2={"Lorg/cobalt/internal/wardrobe/WardrobeFakePlayer;", "Lnet/minecraft/class_742;", "Lnet/minecraft/class_638;", "level", "<init>", "(Lnet/minecraft/class_638;)V", "", "Lnet/minecraft/class_1799;", "armor", "", "applyArmor", "(Ljava/util/List;)V", "cobalt"})
@SourceDebugExtension(value={"SMAP\nWardrobeFakePlayer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WardrobeFakePlayer.kt\norg/cobalt/internal/wardrobe/WardrobeFakePlayer\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,49:1\n1#2:50\n*E\n"})
public final class WardrobeFakePlayer
extends class_742 {
    public WardrobeFakePlayer(@NotNull class_638 level2) {
        Intrinsics.checkNotNullParameter((Object)level2, (String)"level");
        super(level2, new GameProfile(UUID.randomUUID(), "WardrobePreview"));
    }

    public final void applyArmor(@NotNull List<class_1799> armor) {
        Object object;
        class_1304 class_13042;
        WardrobeFakePlayer wardrobeFakePlayer;
        block15: {
            block14: {
                Object object2;
                class_1304 class_13043;
                WardrobeFakePlayer wardrobeFakePlayer2;
                boolean bl;
                class_1799 it;
                WardrobeFakePlayer wardrobeFakePlayer3;
                class_1304 class_13044;
                class_1799 class_17992;
                block13: {
                    block12: {
                        Object object3;
                        class_1304 class_13045;
                        WardrobeFakePlayer wardrobeFakePlayer4;
                        block11: {
                            block10: {
                                Object object4;
                                class_1304 class_13046;
                                WardrobeFakePlayer wardrobeFakePlayer5;
                                block9: {
                                    block8: {
                                        Intrinsics.checkNotNullParameter(armor, (String)"armor");
                                        wardrobeFakePlayer5 = this;
                                        class_13046 = class_1304.field_6169;
                                        object4 = (class_1799)CollectionsKt.getOrNull(armor, (int)0);
                                        if (object4 == null) break block8;
                                        class_1799 class_17993 = class_17992 = object4;
                                        class_13044 = class_13046;
                                        wardrobeFakePlayer3 = wardrobeFakePlayer5;
                                        boolean bl2 = false;
                                        bl = !it.method_7960();
                                        wardrobeFakePlayer5 = wardrobeFakePlayer3;
                                        class_13046 = class_13044;
                                        object4 = bl ? class_17992 : null;
                                        if (object4 != null) break block9;
                                    }
                                    class_1799 class_17994 = class_1799.field_8037;
                                    object4 = class_17994;
                                    Intrinsics.checkNotNullExpressionValue((Object)class_17994, (String)"EMPTY");
                                }
                                wardrobeFakePlayer5.method_5673(class_13046, (class_1799)object4);
                                wardrobeFakePlayer4 = this;
                                class_13045 = class_1304.field_6174;
                                object3 = (class_1799)CollectionsKt.getOrNull(armor, (int)1);
                                if (object3 == null) break block10;
                                it = class_17992 = object3;
                                class_13044 = class_13045;
                                wardrobeFakePlayer3 = wardrobeFakePlayer4;
                                boolean bl3 = false;
                                bl = !it.method_7960();
                                wardrobeFakePlayer4 = wardrobeFakePlayer3;
                                class_13045 = class_13044;
                                object3 = bl ? class_17992 : null;
                                if (object3 != null) break block11;
                            }
                            class_1799 class_17995 = class_1799.field_8037;
                            object3 = class_17995;
                            Intrinsics.checkNotNullExpressionValue((Object)class_17995, (String)"EMPTY");
                        }
                        wardrobeFakePlayer4.method_5673(class_13045, (class_1799)object3);
                        wardrobeFakePlayer2 = this;
                        class_13043 = class_1304.field_6172;
                        object2 = (class_1799)CollectionsKt.getOrNull(armor, (int)2);
                        if (object2 == null) break block12;
                        it = class_17992 = object2;
                        class_13044 = class_13043;
                        wardrobeFakePlayer3 = wardrobeFakePlayer2;
                        boolean bl4 = false;
                        bl = !it.method_7960();
                        wardrobeFakePlayer2 = wardrobeFakePlayer3;
                        class_13043 = class_13044;
                        object2 = bl ? class_17992 : null;
                        if (object2 != null) break block13;
                    }
                    class_1799 class_17996 = class_1799.field_8037;
                    object2 = class_17996;
                    Intrinsics.checkNotNullExpressionValue((Object)class_17996, (String)"EMPTY");
                }
                wardrobeFakePlayer2.method_5673(class_13043, (class_1799)object2);
                wardrobeFakePlayer = this;
                class_13042 = class_1304.field_6166;
                object = (class_1799)CollectionsKt.getOrNull(armor, (int)3);
                if (object == null) break block14;
                it = class_17992 = object;
                class_13044 = class_13042;
                wardrobeFakePlayer3 = wardrobeFakePlayer;
                boolean bl5 = false;
                bl = !it.method_7960();
                wardrobeFakePlayer = wardrobeFakePlayer3;
                class_13042 = class_13044;
                object = bl ? class_17992 : null;
                if (object != null) break block15;
            }
            class_1799 class_17997 = class_1799.field_8037;
            object = class_17997;
            Intrinsics.checkNotNullExpressionValue((Object)class_17997, (String)"EMPTY");
        }
        wardrobeFakePlayer.method_5673(class_13042, (class_1799)object);
    }
}

