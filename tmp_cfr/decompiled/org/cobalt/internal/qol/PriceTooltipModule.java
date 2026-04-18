/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
 *  net.minecraft.class_124
 *  net.minecraft.class_1792$class_9635
 *  net.minecraft.class_1799
 *  net.minecraft.class_1836
 *  net.minecraft.class_2561
 *  net.minecraft.class_5250
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.qol;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.class_124;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1836;
import net.minecraft.class_2561;
import net.minecraft.class_5250;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.SettingKt;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.cobalt.api.module.setting.impl.InfoSetting;
import org.cobalt.api.module.setting.impl.InfoType;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.api.util.SkyblockItemUtilsKt;
import org.cobalt.internal.qol.SkyblockPriceService;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000`\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J%\u0010\n\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\u00042\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u0002\u00a2\u0006\u0004\b\n\u0010\u000bJ'\u0010\u0011\u001a\u00020\u00072\u0006\u0010\r\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u0007H\u0002\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u001f\u0010\u0017\u001a\u00020\u00072\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\b\u0017\u0010\u0018J\u0017\u0010\u0019\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\u0013H\u0002\u00a2\u0006\u0004\b\u0019\u0010\u001aR\u0014\u0010\u001c\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001c\u0010\u001dR\u0014\u0010\u001f\u001a\u00020\u001e8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001f\u0010 R\u0014\u0010!\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b!\u0010\u001dR\u0014\u0010\"\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\"\u0010\u001dR\u0014\u0010#\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b#\u0010\u001dR\u0014\u0010$\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b$\u0010\u001dR\u0014\u0010&\u001a\u00020%8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b&\u0010'R\u0014\u0010)\u001a\u00020(8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b)\u0010*\u00a8\u0006+"}, d2={"Lorg/cobalt/internal/qol/PriceTooltipModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lnet/minecraft/class_1799;", "stack", "", "Lnet/minecraft/class_2561;", "lines", "", "appendTooltipLines", "(Lnet/minecraft/class_1799;Ljava/util/List;)V", "", "label", "Lnet/minecraft/class_124;", "labelColor", "value", "labeledLine", "(Ljava/lang/String;Lnet/minecraft/class_124;Lnet/minecraft/class_2561;)Lnet/minecraft/class_2561;", "", "pricePerItem", "", "count", "formatCoinsMessage", "(DI)Lnet/minecraft/class_2561;", "formatCoins", "(D)Ljava/lang/String;", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "enabled", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "tooltipInfo", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "showLowestBin", "showBazaar", "showNpcSell", "showIds", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "refreshSeconds", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "Ljava/text/DecimalFormat;", "COIN_FORMAT", "Ljava/text/DecimalFormat;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nPriceTooltipModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PriceTooltipModule.kt\norg/cobalt/internal/qol/PriceTooltipModule\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,167:1\n1#2:168\n*E\n"})
public final class PriceTooltipModule
extends Module {
    @NotNull
    public static final PriceTooltipModule INSTANCE = new PriceTooltipModule();
    @NotNull
    private static final CheckboxSetting enabled = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Enabled", "Show SkyBlock price lines on item tooltips.", true), "Tooltip");
    @NotNull
    private static final InfoSetting tooltipInfo = (InfoSetting)SettingKt.inGroup((Setting)new InfoSetting("Sources", "Lowest BIN uses Moulberry. Bazaar and NPC sell values use Hypixel APIs.", InfoType.INFO), "Tooltip");
    @NotNull
    private static final CheckboxSetting showLowestBin = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Lowest BIN", "Show Auction House lowest BIN prices on item tooltips.", true), "Tooltip");
    @NotNull
    private static final CheckboxSetting showBazaar = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Bazaar", "Show Bazaar buy and sell prices on item tooltips.", true), "Tooltip");
    @NotNull
    private static final CheckboxSetting showNpcSell = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("NPC Sell", "Show NPC sell prices on item tooltips.", true), "Tooltip");
    @NotNull
    private static final CheckboxSetting showIds = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("SkyBlock IDs", "Show the internal SkyBlock ID and API ID used for price lookups.", true), "Tooltip");
    @NotNull
    private static final SliderSetting refreshSeconds = (SliderSetting)SettingKt.inGroup((Setting)new SliderSetting("Refresh Seconds", "Seconds between market refreshes.", 120.0, 30.0, 900.0, 10.0), "Data");
    @NotNull
    private static final DecimalFormat COIN_FORMAT;

    private PriceTooltipModule() {
        super("Price Tooltip");
    }

    private final void appendTooltipLines(class_1799 stack, List<class_2561> lines) {
        SkyblockPriceService.BazaarQuote bazaarQuote;
        Object object;
        CharSequence charSequence;
        String internalId = SkyblockItemUtilsKt.getSkyblockId(stack);
        if (((CharSequence)internalId).length() == 0) {
            return;
        }
        CharSequence charSequence2 = SkyblockItemUtilsKt.getSkyblockApiId(stack);
        if (charSequence2.length() == 0) {
            boolean bl = false;
            charSequence = internalId;
        } else {
            charSequence = charSequence2;
        }
        String apiId = (String)charSequence;
        SkyblockPriceService.INSTANCE.refreshIfNeeded((long)((Number)refreshSeconds.getValue()).doubleValue());
        int stackCount = RangesKt.coerceAtLeast((int)stack.method_7947(), (int)1);
        if (((Boolean)showNpcSell.getValue()).booleanValue()) {
            Double d = SkyblockPriceService.INSTANCE.getNpcSellPrice(internalId);
            if (d != null) {
                double npcPrice = ((Number)d).doubleValue();
                boolean bl = false;
                lines.add(INSTANCE.labeledLine("NPC Sell Price:", class_124.field_1054, INSTANCE.formatCoinsMessage(npcPrice, stackCount)));
            }
        }
        if ((object = (bazaarQuote = (Boolean)showBazaar.getValue() != false ? SkyblockPriceService.INSTANCE.getBazaarQuote(apiId) : null)) != null && (object = ((SkyblockPriceService.BazaarQuote)object).getBuyPrice()) != null) {
            double buyPrice = ((Number)object).doubleValue();
            boolean bl = false;
            lines.add(INSTANCE.labeledLine("Bazaar Buy Price:", class_124.field_1065, INSTANCE.formatCoinsMessage(buyPrice, stackCount)));
        }
        SkyblockPriceService.BazaarQuote bazaarQuote2 = bazaarQuote;
        if (bazaarQuote2 != null) {
            double sellPrice = bazaarQuote2.getSellPrice();
            boolean bl = false;
            lines.add(INSTANCE.labeledLine("Bazaar Sell Price:", class_124.field_1065, INSTANCE.formatCoinsMessage(sellPrice, stackCount)));
        }
        if (((Boolean)showLowestBin.getValue()).booleanValue() && !SkyblockPriceService.INSTANCE.hasBazaarQuote(apiId)) {
            Double d = SkyblockPriceService.INSTANCE.getLowestBin(apiId);
            if (d != null) {
                double lowestBin = ((Number)d).doubleValue();
                boolean bl = false;
                lines.add(INSTANCE.labeledLine("Lowest BIN Price:", class_124.field_1065, INSTANCE.formatCoinsMessage(lowestBin, stackCount)));
            }
        }
        if (((Boolean)showIds.getValue()).booleanValue()) {
            class_5250 class_52502 = class_2561.method_43470((String)internalId).method_27692(class_124.field_1062);
            Intrinsics.checkNotNullExpressionValue((Object)class_52502, (String)"withStyle(...)");
            lines.add(this.labeledLine("SkyBlock ID:", class_124.field_1080, (class_2561)class_52502));
            if (!StringsKt.equals((String)apiId, (String)internalId, (boolean)false)) {
                class_5250 class_52503 = class_2561.method_43470((String)apiId).method_27692(class_124.field_1062);
                Intrinsics.checkNotNullExpressionValue((Object)class_52503, (String)"withStyle(...)");
                lines.add(this.labeledLine("API ID:", class_124.field_1080, (class_2561)class_52503));
            }
        }
    }

    private final class_2561 labeledLine(String label, class_124 labelColor, class_2561 value) {
        class_5250 class_52502 = class_2561.method_43470((String)StringsKt.padEnd$default((String)label, (int)19, (char)'\u0000', (int)2, null)).method_27692(labelColor).method_10852(value);
        Intrinsics.checkNotNullExpressionValue((Object)class_52502, (String)"append(...)");
        return (class_2561)class_52502;
    }

    private final class_2561 formatCoinsMessage(double pricePerItem, int count) {
        int safeCount = RangesKt.coerceAtLeast((int)count, (int)1);
        String each = this.formatCoins(pricePerItem);
        if (safeCount == 1) {
            class_5250 class_52502 = class_2561.method_43470((String)(each + " Coins")).method_27692(class_124.field_1062);
            Intrinsics.checkNotNullExpressionValue((Object)class_52502, (String)"withStyle(...)");
            return (class_2561)class_52502;
        }
        String total = this.formatCoins(pricePerItem * (double)safeCount);
        class_5250 class_52503 = class_2561.method_43470((String)(total + " Coins ")).method_27692(class_124.field_1062).method_10852((class_2561)class_2561.method_43470((String)("(" + each + " each)")).method_27692(class_124.field_1080));
        Intrinsics.checkNotNullExpressionValue((Object)class_52503, (String)"append(...)");
        return (class_2561)class_52503;
    }

    private final String formatCoins(double value) {
        String string = COIN_FORMAT.format(value);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"format(...)");
        return string;
    }

    private static final void _init_$lambda$0(class_1799 stack, class_1792.class_9635 class_96352, class_1836 class_18362, List lines) {
        Intrinsics.checkNotNullParameter((Object)stack, (String)"stack");
        Intrinsics.checkNotNullParameter((Object)class_96352, (String)"<unused var>");
        Intrinsics.checkNotNullParameter((Object)class_18362, (String)"<unused var>");
        Intrinsics.checkNotNullParameter((Object)lines, (String)"lines");
        if (!((Boolean)enabled.getValue()).booleanValue()) {
            return;
        }
        INSTANCE.appendTooltipLines(stack, lines);
    }

    static {
        Setting[] settingArray = new Setting[]{enabled, tooltipInfo, showLowestBin, showBazaar, showNpcSell, showIds, refreshSeconds};
        INSTANCE.addSetting(settingArray);
        ItemTooltipCallback.EVENT.register(PriceTooltipModule::_init_$lambda$0);
        SkyblockPriceService.INSTANCE.refreshIfNeeded((long)((Number)refreshSeconds.getValue()).doubleValue());
        COIN_FORMAT = new DecimalFormat("#,##0.0", new DecimalFormatSymbols(Locale.US));
    }
}

