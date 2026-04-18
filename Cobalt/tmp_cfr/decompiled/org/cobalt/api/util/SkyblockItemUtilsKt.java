/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.mojang.authlib.properties.Property
 *  kotlin.Metadata
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.Charsets
 *  kotlin.text.StringsKt
 *  net.minecraft.class_1799
 *  net.minecraft.class_2487
 *  net.minecraft.class_9279
 *  net.minecraft.class_9296
 *  net.minecraft.class_9334
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;
import java.util.Base64;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import net.minecraft.class_1799;
import net.minecraft.class_2487;
import net.minecraft.class_9279;
import net.minecraft.class_9296;
import net.minecraft.class_9334;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=2, xi=48, d1={"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u0000\n\u0002\b\u0004\u001a\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0001*\u00020\u0000\u00a2\u0006\u0004\b\u0002\u0010\u0003\u001a\u0011\u0010\u0005\u001a\u00020\u0004*\u00020\u0000\u00a2\u0006\u0004\b\u0005\u0010\u0006\u001a\u0013\u0010\u0007\u001a\u0004\u0018\u00010\u0004*\u00020\u0000\u00a2\u0006\u0004\b\u0007\u0010\u0006\u001a\u0011\u0010\b\u001a\u00020\u0004*\u00020\u0000\u00a2\u0006\u0004\b\b\u0010\u0006\u001a\u001b\u0010\n\u001a\u0004\u0018\u00010\u0004*\u00020\u00012\u0006\u0010\t\u001a\u00020\u0004\u00a2\u0006\u0004\b\n\u0010\u000b\u001a\u001b\u0010\r\u001a\u0004\u0018\u00010\f*\u00020\u00012\u0006\u0010\t\u001a\u00020\u0004\u00a2\u0006\u0004\b\r\u0010\u000e\u001a\u001b\u0010\u000f\u001a\u0004\u0018\u00010\u0001*\u00020\u00012\u0006\u0010\t\u001a\u00020\u0004\u00a2\u0006\u0004\b\u000f\u0010\u0010\u001a!\u0010\u0014\u001a\u0004\u0018\u00018\u0000\"\u0004\b\u0000\u0010\u00112\b\u0010\u0013\u001a\u0004\u0018\u00010\u0012H\u0002\u00a2\u0006\u0004\b\u0014\u0010\u0015\u00a8\u0006\u0016"}, d2={"Lnet/minecraft/class_1799;", "Lnet/minecraft/class_2487;", "getSkyblockExtraAttributes", "(Lnet/minecraft/class_1799;)Lnet/minecraft/class_2487;", "", "getSkyblockId", "(Lnet/minecraft/class_1799;)Ljava/lang/String;", "getHeadTextureId", "getSkyblockApiId", "key", "tagString", "(Lnet/minecraft/class_2487;Ljava/lang/String;)Ljava/lang/String;", "", "tagInt", "(Lnet/minecraft/class_2487;Ljava/lang/String;)Ljava/lang/Integer;", "tagCompound", "(Lnet/minecraft/class_2487;Ljava/lang/String;)Lnet/minecraft/class_2487;", "T", "", "value", "unwrapOptional", "(Ljava/lang/Object;)Ljava/lang/Object;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nSkyblockItemUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SkyblockItemUtils.kt\norg/cobalt/api/util/SkyblockItemUtilsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,146:1\n1#2:147\n*E\n"})
public final class SkyblockItemUtilsKt {
    @Nullable
    public static final class_2487 getSkyblockExtraAttributes(@NotNull class_1799 $this$getSkyblockExtraAttributes) {
        class_2487 class_24872;
        Intrinsics.checkNotNullParameter((Object)$this$getSkyblockExtraAttributes, (String)"<this>");
        try {
            class_9279 class_92792 = (class_9279)$this$getSkyblockExtraAttributes.method_58694(class_9334.field_49628);
            if (class_92792 == null) {
                return null;
            }
            class_9279 customData = class_92792;
            class_2487 class_24873 = (class_2487)SkyblockItemUtilsKt.unwrapOptional(customData.method_57461());
            if (class_24873 == null) {
                return null;
            }
            class_2487 root = class_24873;
            class_24872 = root.method_10545("ExtraAttributes") ? (class_2487)SkyblockItemUtilsKt.unwrapOptional(root.method_10562("ExtraAttributes")) : (root.method_10545("extra_attributes") ? (class_2487)SkyblockItemUtilsKt.unwrapOptional(root.method_10562("extra_attributes")) : (root.method_10545("id") ? root : null));
        }
        catch (Exception exception) {
            class_24872 = null;
        }
        return class_24872;
    }

    @NotNull
    public static final String getSkyblockId(@NotNull class_1799 $this$getSkyblockId) {
        Intrinsics.checkNotNullParameter((Object)$this$getSkyblockId, (String)"<this>");
        class_2487 class_24872 = SkyblockItemUtilsKt.getSkyblockExtraAttributes($this$getSkyblockId);
        String string = class_24872 != null ? SkyblockItemUtilsKt.tagString(class_24872, "id") : null;
        if (string == null) {
            string = "";
        }
        return string;
    }

    @Nullable
    public static final String getHeadTextureId(@NotNull class_1799 $this$getHeadTextureId) {
        CharSequence charSequence;
        Object $this$getHeadTextureId_u24lambda_u240;
        Intrinsics.checkNotNullParameter((Object)$this$getHeadTextureId, (String)"<this>");
        class_9296 class_92962 = (class_9296)$this$getHeadTextureId.method_58694(class_9334.field_49617);
        if (class_92962 == null) {
            return null;
        }
        class_9296 profile = class_92962;
        Collection collection = profile.method_73313().properties().get((Object)"textures");
        Intrinsics.checkNotNullExpressionValue((Object)collection, (String)"get(...)");
        Collection textures = collection;
        Object object = (Property)CollectionsKt.firstOrNull((Iterable)textures);
        if (object == null || (object = object.value()) == null) {
            return null;
        }
        Object value = object;
        class_1799 class_17992 = $this$getHeadTextureId;
        try {
            $this$getHeadTextureId_u24lambda_u240 = class_17992;
            boolean bl = false;
            byte[] byArray = Base64.getDecoder().decode((String)value);
            Intrinsics.checkNotNullExpressionValue((Object)byArray, (String)"decode(...)");
            byte[] byArray2 = byArray;
            $this$getHeadTextureId_u24lambda_u240 = Result.constructor-impl((Object)new String(byArray2, Charsets.UTF_8));
        }
        catch (Throwable bl) {
            $this$getHeadTextureId_u24lambda_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
        }
        class_17992 = $this$getHeadTextureId_u24lambda_u240;
        String string = (String)(Result.isFailure-impl((Object)class_17992) ? null : class_17992);
        if (string == null) {
            return null;
        }
        String decoded = string;
        String urlKey = "\"url\"";
        int keyIndex = StringsKt.indexOf$default((CharSequence)decoded, (String)urlKey, (int)0, (boolean)false, (int)6, null);
        if (keyIndex < 0) {
            return null;
        }
        int colonIndex = StringsKt.indexOf$default((CharSequence)decoded, (char)':', (int)keyIndex, (boolean)false, (int)4, null);
        int startQuote = StringsKt.indexOf$default((CharSequence)decoded, (char)'\"', (int)(colonIndex + 1), (boolean)false, (int)4, null) + 1;
        if (startQuote <= 0) {
            return null;
        }
        int endQuote = StringsKt.indexOf$default((CharSequence)decoded, (char)'\"', (int)startQuote, (boolean)false, (int)4, null);
        if (endQuote <= startQuote) {
            return null;
        }
        String string2 = decoded.substring(startQuote, endQuote);
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"substring(...)");
        CharSequence charSequence2 = StringsKt.substringAfterLast$default((String)string2, (char)'/', null, (int)2, null);
        if (StringsKt.isBlank((CharSequence)charSequence2)) {
            boolean bl = false;
            charSequence = null;
        } else {
            charSequence = charSequence2;
        }
        return (String)charSequence;
    }

    @NotNull
    public static final String getSkyblockApiId(@NotNull class_1799 $this$getSkyblockApiId) {
        Object object;
        String id;
        Intrinsics.checkNotNullParameter((Object)$this$getSkyblockApiId, (String)"<this>");
        class_2487 class_24872 = SkyblockItemUtilsKt.getSkyblockExtraAttributes($this$getSkyblockApiId);
        if (class_24872 == null) {
            return "";
        }
        class_2487 attributes = class_24872;
        String string = SkyblockItemUtilsKt.tagString(attributes, "id");
        if (string == null) {
            string = "";
        }
        if (((CharSequence)(id = string)).length() == 0) {
            return "";
        }
        if (attributes.method_10545("is_shiny")) {
            return "SHINY_" + id;
        }
        switch (id) {
            case "ENCHANTED_BOOK": {
                int level2;
                String string2;
                class_2487 enchants = SkyblockItemUtilsKt.tagCompound(attributes, "enchantments");
                Object object2 = enchants;
                if ((object2 != null && (object2 = object2.method_10541()) != null ? (String)CollectionsKt.firstOrNull((Iterable)((Iterable)object2)) : (string2 = null)) == null) {
                    string2 = "";
                }
                String enchant = string2;
                Object object3 = enchants;
                int n = level2 = object3 != null && (object3 = SkyblockItemUtilsKt.tagInt((class_2487)object3, enchant)) != null ? (Integer)object3 : 0;
                if (((CharSequence)enchant).length() > 0 && level2 > 0) {
                    String string3 = enchant;
                    Locale locale = Locale.ROOT;
                    Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
                    String string4 = string3.toUpperCase(locale);
                    Intrinsics.checkNotNullExpressionValue((Object)string4, (String)"toUpperCase(...)");
                    object = "ENCHANTMENT_" + string4 + "_" + level2;
                    break;
                }
                object = id;
                break;
            }
            case "PET": {
                String tier;
                String string5;
                String string6;
                Object object4;
                String type;
                String string7;
                String string8;
                Object $this$getSkyblockApiId_u24lambda_u240;
                String string9 = SkyblockItemUtilsKt.tagString(attributes, "petInfo");
                if (string9 == null) {
                    string9 = "";
                }
                String rawPetInfo = string9;
                class_1799 level2 = $this$getSkyblockApiId;
                try {
                    $this$getSkyblockApiId_u24lambda_u240 = level2;
                    boolean bl = false;
                    $this$getSkyblockApiId_u24lambda_u240 = Result.constructor-impl((Object)JsonParser.parseString((String)rawPetInfo).getAsJsonObject());
                }
                catch (Throwable bl) {
                    $this$getSkyblockApiId_u24lambda_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
                }
                level2 = $this$getSkyblockApiId_u24lambda_u240;
                JsonObject petInfo = (JsonObject)(Result.isFailure-impl((Object)level2) ? null : level2);
                Object object5 = petInfo;
                if (object5 != null && (object5 = object5.get("type")) != null && (object5 = object5.getAsString()) != null) {
                    Object object6 = object5;
                    Locale locale = Locale.ROOT;
                    Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
                    String string10 = ((String)object6).toUpperCase(locale);
                    string8 = string10;
                    Intrinsics.checkNotNullExpressionValue((Object)string10, (String)"toUpperCase(...)");
                } else {
                    string8 = string7 = null;
                }
                if (string8 == null) {
                    string7 = type = "";
                }
                if ((object4 = petInfo) != null && (object4 = object4.get("tier")) != null && (object4 = object4.getAsString()) != null) {
                    Object object7 = object4;
                    Locale locale = Locale.ROOT;
                    Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
                    String string11 = ((String)object7).toUpperCase(locale);
                    string6 = string11;
                    Intrinsics.checkNotNullExpressionValue((Object)string11, (String)"toUpperCase(...)");
                } else {
                    string6 = string5 = null;
                }
                if (string6 == null) {
                    string5 = tier = "";
                }
                if (((CharSequence)type).length() > 0 && ((CharSequence)tier).length() > 0) {
                    object = "LVL_1_" + tier + "_" + type;
                    break;
                }
                object = id;
                break;
            }
            case "POTION": {
                String string12 = SkyblockItemUtilsKt.tagString(attributes, "potion");
                if (string12 == null) {
                    string12 = "";
                }
                String potion = string12;
                Integer n = SkyblockItemUtilsKt.tagInt(attributes, "potion_level");
                int level3 = n != null ? n : 0;
                if (((CharSequence)potion).length() > 0 && level3 > 0) {
                    CharSequence type = new StringBuilder();
                    StringBuilder $this$getSkyblockApiId_u24lambda_u241 = type;
                    boolean bl = false;
                    $this$getSkyblockApiId_u24lambda_u241.append(potion);
                    $this$getSkyblockApiId_u24lambda_u241.append("_POTION_");
                    $this$getSkyblockApiId_u24lambda_u241.append(level3);
                    if (attributes.method_10545("enhanced")) {
                        $this$getSkyblockApiId_u24lambda_u241.append("_ENHANCED");
                    }
                    if (attributes.method_10545("extended")) {
                        $this$getSkyblockApiId_u24lambda_u241.append("_EXTENDED");
                    }
                    if (attributes.method_10545("splash")) {
                        $this$getSkyblockApiId_u24lambda_u241.append("_SPLASH");
                    }
                    type = ((StringBuilder)type).toString();
                    Locale locale = Locale.ROOT;
                    Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
                    String string13 = ((String)type).toUpperCase(locale);
                    object = string13;
                    Intrinsics.checkNotNullExpressionValue((Object)string13, (String)"toUpperCase(...)");
                    break;
                }
                object = id;
                break;
            }
            case "RUNE": {
                int level4;
                String string14;
                class_2487 runes = SkyblockItemUtilsKt.tagCompound(attributes, "runes");
                Object object8 = runes;
                if ((object8 != null && (object8 = object8.method_10541()) != null ? (String)CollectionsKt.firstOrNull((Iterable)((Iterable)object8)) : (string14 = null)) == null) {
                    string14 = "";
                }
                String rune = string14;
                Object object9 = runes;
                int n = level4 = object9 != null && (object9 = SkyblockItemUtilsKt.tagInt((class_2487)object9, rune)) != null ? (Integer)object9 : 0;
                if (((CharSequence)rune).length() > 0 && level4 > 0) {
                    String string15 = rune;
                    Locale locale = Locale.ROOT;
                    Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
                    String string16 = string15.toUpperCase(locale);
                    Intrinsics.checkNotNullExpressionValue((Object)string16, (String)"toUpperCase(...)");
                    object = string16 + "_RUNE_" + level4;
                    break;
                }
                object = id;
                break;
            }
            case "NEW_YEAR_CAKE": {
                int cakeYear;
                Integer n = SkyblockItemUtilsKt.tagInt(attributes, "new_years_cake");
                int n2 = cakeYear = n != null ? n : 0;
                if (cakeYear > 0) {
                    object = id + "_" + cakeYear;
                    break;
                }
                object = id;
                break;
            }
            case "PARTY_HAT_CRAB": 
            case "PARTY_HAT_CRAB_ANIMATED": 
            case "BALLOON_HAT_2024": 
            case "BALLOON_HAT_2025": {
                String color;
                String string17;
                String string18;
                String string19 = SkyblockItemUtilsKt.tagString(attributes, "party_hat_color");
                if (string19 != null) {
                    String string20 = string19;
                    Locale locale = Locale.ROOT;
                    Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
                    String string21 = string20.toUpperCase(locale);
                    string18 = string21;
                    Intrinsics.checkNotNullExpressionValue((Object)string21, (String)"toUpperCase(...)");
                } else {
                    string18 = string17 = null;
                }
                if (string18 == null) {
                    string17 = "";
                }
                if (((CharSequence)(color = string17)).length() > 0) {
                    object = id + "_" + color;
                    break;
                }
                object = id;
                break;
            }
            case "PARTY_HAT_SLOTH": {
                String emoji;
                String string22;
                String string23;
                String string24 = SkyblockItemUtilsKt.tagString(attributes, "party_hat_emoji");
                if (string24 != null) {
                    String string25 = string24;
                    Locale locale = Locale.ROOT;
                    Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
                    String string26 = string25.toUpperCase(locale);
                    string23 = string26;
                    Intrinsics.checkNotNullExpressionValue((Object)string26, (String)"toUpperCase(...)");
                } else {
                    string23 = string22 = null;
                }
                if (string23 == null) {
                    string22 = "";
                }
                if (((CharSequence)(emoji = string22)).length() > 0) {
                    object = id + "_" + emoji;
                    break;
                }
                object = id;
                break;
            }
            case "MIDAS_SWORD": {
                Integer n = SkyblockItemUtilsKt.tagInt(attributes, "winning_bid");
                if ((n != null ? n : 0) >= 50000000) {
                    object = id + "_50M";
                    break;
                }
                object = id;
                break;
            }
            case "MIDAS_STAFF": {
                Integer n = SkyblockItemUtilsKt.tagInt(attributes, "winning_bid");
                if ((n != null ? n : 0) >= 100000000) {
                    object = id + "_100M";
                    break;
                }
                object = id;
                break;
            }
            default: {
                object = id;
            }
        }
        return object;
    }

    @Nullable
    public static final String tagString(@NotNull class_2487 $this$tagString, @NotNull String key) {
        Object object;
        Intrinsics.checkNotNullParameter((Object)$this$tagString, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        Object object2 = $this$tagString;
        try {
            class_2487 $this$tagString_u24lambda_u240 = object2;
            boolean bl = false;
            object = Result.constructor-impl((Object)((String)SkyblockItemUtilsKt.unwrapOptional($this$tagString_u24lambda_u240.method_10558(key))));
        }
        catch (Throwable throwable) {
            object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
        object2 = object;
        return (String)(Result.isFailure-impl((Object)object2) ? null : object2);
    }

    @Nullable
    public static final Integer tagInt(@NotNull class_2487 $this$tagInt, @NotNull String key) {
        Object object;
        Intrinsics.checkNotNullParameter((Object)$this$tagInt, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        Object object2 = $this$tagInt;
        try {
            class_2487 $this$tagInt_u24lambda_u240 = object2;
            boolean bl = false;
            object = Result.constructor-impl((Object)((Integer)SkyblockItemUtilsKt.unwrapOptional($this$tagInt_u24lambda_u240.method_10550(key))));
        }
        catch (Throwable throwable) {
            object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
        object2 = object;
        return (Integer)(Result.isFailure-impl((Object)object2) ? null : object2);
    }

    @Nullable
    public static final class_2487 tagCompound(@NotNull class_2487 $this$tagCompound, @NotNull String key) {
        Object object;
        Intrinsics.checkNotNullParameter((Object)$this$tagCompound, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        Object object2 = $this$tagCompound;
        try {
            class_2487 $this$tagCompound_u24lambda_u240 = object2;
            boolean bl = false;
            object = Result.constructor-impl($this$tagCompound_u24lambda_u240.method_10545(key) ? (class_2487)SkyblockItemUtilsKt.unwrapOptional($this$tagCompound_u24lambda_u240.method_10562(key)) : null);
        }
        catch (Throwable throwable) {
            object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
        object2 = object;
        return Result.isFailure-impl((Object)object2) ? null : object2;
    }

    private static final <T> T unwrapOptional(Object value) {
        if (value == null) {
            return null;
        }
        Object object = value;
        return (T)(object instanceof Optional ? ((Optional)value).orElse(null) : (object instanceof OptionalInt ? (((OptionalInt)value).isPresent() ? (Object)((OptionalInt)value).orElse(0) : null) : (object instanceof OptionalLong ? (((OptionalLong)value).isPresent() ? (Object)((OptionalLong)value).orElse(0L) : null) : (object instanceof OptionalDouble ? (((OptionalDouble)value).isPresent() ? (Object)((OptionalDouble)value).orElse(0.0) : null) : value))));
    }
}

