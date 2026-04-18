/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.BanDetails
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.ComponentUtils
 *  net.minecraft.network.chat.Style
 *  net.minecraft.util.CommonLinks
 *  net.minecraft.util.Util
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.client.gui.screens;

import com.mojang.authlib.minecraft.BanDetails;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.multiplayer.chat.report.BanReason;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.util.CommonLinks;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;

@Environment(value=EnvType.CLIENT)
public class BanNoticeScreens {
    private static final Component TEMPORARY_BAN_TITLE = Component.translatable((String)"gui.banned.title.temporary").withStyle(ChatFormatting.BOLD);
    private static final Component PERMANENT_BAN_TITLE = Component.translatable((String)"gui.banned.title.permanent").withStyle(ChatFormatting.BOLD);
    public static final Component NAME_BAN_TITLE = Component.translatable((String)"gui.banned.name.title").withStyle(ChatFormatting.BOLD);
    private static final Component SKIN_BAN_TITLE = Component.translatable((String)"gui.banned.skin.title").withStyle(ChatFormatting.BOLD);
    private static final Component SKIN_BAN_DESCRIPTION = Component.translatable((String)"gui.banned.skin.description", (Object[])new Object[]{Component.translationArg((URI)CommonLinks.SUSPENSION_HELP)});

    public static ConfirmLinkScreen create(BooleanConsumer booleanConsumer, BanDetails banDetails) {
        return new ConfirmLinkScreen(booleanConsumer, BanNoticeScreens.getBannedTitle(banDetails), BanNoticeScreens.getBannedScreenText(banDetails), CommonLinks.SUSPENSION_HELP, CommonComponents.GUI_ACKNOWLEDGE, true);
    }

    public static ConfirmLinkScreen createSkinBan(Runnable runnable) {
        URI uRI = CommonLinks.SUSPENSION_HELP;
        return new ConfirmLinkScreen(bl -> {
            if (bl) {
                Util.getPlatform().openUri(uRI);
            }
            runnable.run();
        }, SKIN_BAN_TITLE, SKIN_BAN_DESCRIPTION, uRI, CommonComponents.GUI_ACKNOWLEDGE, true);
    }

    public static ConfirmLinkScreen createNameBan(String string, Runnable runnable) {
        URI uRI = CommonLinks.SUSPENSION_HELP;
        return new ConfirmLinkScreen(bl -> {
            if (bl) {
                Util.getPlatform().openUri(uRI);
            }
            runnable.run();
        }, NAME_BAN_TITLE, (Component)Component.translatable((String)"gui.banned.name.description", (Object[])new Object[]{Component.literal((String)string).withStyle(ChatFormatting.YELLOW), Component.translationArg((URI)CommonLinks.SUSPENSION_HELP)}), uRI, CommonComponents.GUI_ACKNOWLEDGE, true);
    }

    private static Component getBannedTitle(BanDetails banDetails) {
        return BanNoticeScreens.isTemporaryBan(banDetails) ? TEMPORARY_BAN_TITLE : PERMANENT_BAN_TITLE;
    }

    private static Component getBannedScreenText(BanDetails banDetails) {
        return Component.translatable((String)"gui.banned.description", (Object[])new Object[]{BanNoticeScreens.getBanReasonText(banDetails), BanNoticeScreens.getBanStatusText(banDetails), Component.translationArg((URI)CommonLinks.SUSPENSION_HELP)});
    }

    private static Component getBanReasonText(BanDetails banDetails) {
        String string = banDetails.reason();
        String string2 = banDetails.reasonMessage();
        if (StringUtils.isNumeric((CharSequence)string)) {
            int i = Integer.parseInt(string);
            BanReason banReason = BanReason.byId(i);
            Object component = banReason != null ? ComponentUtils.mergeStyles((Component)banReason.title(), (Style)Style.EMPTY.withBold(Boolean.valueOf(true))) : (string2 != null ? Component.translatable((String)"gui.banned.description.reason_id_message", (Object[])new Object[]{i, string2}).withStyle(ChatFormatting.BOLD) : Component.translatable((String)"gui.banned.description.reason_id", (Object[])new Object[]{i}).withStyle(ChatFormatting.BOLD));
            return Component.translatable((String)"gui.banned.description.reason", (Object[])new Object[]{component});
        }
        return Component.translatable((String)"gui.banned.description.unknownreason");
    }

    private static Component getBanStatusText(BanDetails banDetails) {
        if (BanNoticeScreens.isTemporaryBan(banDetails)) {
            Component component = BanNoticeScreens.getBanDurationText(banDetails);
            return Component.translatable((String)"gui.banned.description.temporary", (Object[])new Object[]{Component.translatable((String)"gui.banned.description.temporary.duration", (Object[])new Object[]{component}).withStyle(ChatFormatting.BOLD)});
        }
        return Component.translatable((String)"gui.banned.description.permanent").withStyle(ChatFormatting.BOLD);
    }

    private static Component getBanDurationText(BanDetails banDetails) {
        Duration duration = Duration.between(Instant.now(), banDetails.expires());
        long l = duration.toHours();
        if (l > 72L) {
            return CommonComponents.days((long)duration.toDays());
        }
        if (l < 1L) {
            return CommonComponents.minutes((long)duration.toMinutes());
        }
        return CommonComponents.hours((long)duration.toHours());
    }

    private static boolean isTemporaryBan(BanDetails banDetails) {
        return banDetails.expires() != null;
    }
}

