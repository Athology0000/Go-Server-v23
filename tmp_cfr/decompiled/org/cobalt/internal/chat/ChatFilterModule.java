/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.SetsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.Regex
 *  kotlin.text.RegexOption
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.chat;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.Regex;
import kotlin.text.RegexOption;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.cobalt.api.event.impl.client.ChatEvent;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.SettingKt;
import org.cobalt.api.module.setting.impl.CheckboxSetting;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b)\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0010 \n\u0002\b\u001e\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\"\n\u0002\b\u0013\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b\f\u0010\rJ\u0017\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\tH\u0002\u00a2\u0006\u0004\b\u000e\u0010\rR\u0014\u0010\u000f\u001a\u00020\t8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u000f\u0010\u0010R\u0014\u0010\u0011\u001a\u00020\t8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0011\u0010\u0010R\u0014\u0010\u0012\u001a\u00020\t8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0012\u0010\u0010R\u0014\u0010\u0013\u001a\u00020\t8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0013\u0010\u0010R\u0014\u0010\u0014\u001a\u00020\t8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0014\u0010\u0010R\u0014\u0010\u0015\u001a\u00020\t8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0015\u0010\u0010R\u0014\u0010\u0016\u001a\u00020\t8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0016\u0010\u0010R\u0014\u0010\u0017\u001a\u00020\t8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0017\u0010\u0010R\u0014\u0010\u0018\u001a\u00020\t8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0018\u0010\u0010R\u0014\u0010\u0019\u001a\u00020\t8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0019\u0010\u0010R\u0014\u0010\u001a\u001a\u00020\t8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u001a\u0010\u0010R\u0014\u0010\u001c\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001c\u0010\u001dR\u0014\u0010\u001e\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001e\u0010\u001dR\u0014\u0010\u001f\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001f\u0010\u001dR\u0014\u0010 \u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b \u0010\u001dR\u0014\u0010!\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b!\u0010\u001dR\u0014\u0010\"\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\"\u0010\u001dR\u0014\u0010#\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b#\u0010\u001dR\u0014\u0010$\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b$\u0010\u001dR\u0014\u0010%\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b%\u0010\u001dR\u0014\u0010&\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b&\u0010\u001dR\u0014\u0010'\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b'\u0010\u001dR\u0014\u0010(\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b(\u0010\u001dR\u0014\u0010)\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b)\u0010\u001dR\u0014\u0010*\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b*\u0010\u001dR\u0014\u0010+\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b+\u0010\u001dR\u0014\u0010,\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b,\u0010\u001dR\u0014\u0010-\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b-\u0010\u001dR\u0014\u0010.\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b.\u0010\u001dR\u0014\u0010/\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b/\u0010\u001dR\u0014\u00100\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b0\u0010\u001dR\u0014\u00101\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b1\u0010\u001dR\u0014\u00102\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b2\u0010\u001dR\u0014\u00103\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b3\u0010\u001dR\u0014\u00104\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b4\u0010\u001dR\u0014\u00105\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b5\u0010\u001dR\u0014\u00106\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b6\u0010\u001dR\u0014\u00107\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b7\u0010\u001dR\u0014\u00108\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b8\u0010\u001dR\u0014\u00109\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b9\u0010\u001dR\u0014\u0010:\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b:\u0010\u001dR\u0014\u0010;\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b;\u0010\u001dR\u0014\u0010<\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b<\u0010\u001dR\u0014\u0010=\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b=\u0010\u001dR\u0014\u0010>\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b>\u0010\u001dR\u0014\u0010?\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b?\u0010\u001dR\u0014\u0010@\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b@\u0010\u001dR\u0014\u0010A\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bA\u0010\u001dR\u0014\u0010B\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bB\u0010\u001dR\u0014\u0010C\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bC\u0010\u001dR\u0014\u0010D\u001a\u00020\u001b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bD\u0010\u001dR\u0014\u0010F\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bF\u0010GR\u0014\u0010H\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bH\u0010GR\u0014\u0010I\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bI\u0010GR\u0014\u0010J\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bJ\u0010GR\u0014\u0010K\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bK\u0010GR\u0014\u0010L\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bL\u0010GR\u0014\u0010M\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bM\u0010GR\u0014\u0010N\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bN\u0010GR\u0014\u0010O\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bO\u0010GR\u0014\u0010P\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bP\u0010GR\u0014\u0010Q\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bQ\u0010GR\u0014\u0010R\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bR\u0010GR\u0014\u0010S\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bS\u0010GR\u0014\u0010T\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bT\u0010GR\u0014\u0010U\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bU\u0010GR\u0014\u0010V\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bV\u0010GR\u001a\u0010X\u001a\b\u0012\u0004\u0012\u00020E0W8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bX\u0010YR\u001a\u0010Z\u001a\b\u0012\u0004\u0012\u00020E0W8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bZ\u0010YR\u001a\u0010[\u001a\b\u0012\u0004\u0012\u00020E0W8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b[\u0010YR\u001a\u0010\\\u001a\b\u0012\u0004\u0012\u00020E0W8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\\\u0010YR\u0014\u0010]\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b]\u0010GR\u0014\u0010^\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b^\u0010GR\u001a\u0010_\u001a\b\u0012\u0004\u0012\u00020E0W8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b_\u0010YR\u001a\u0010`\u001a\b\u0012\u0004\u0012\u00020E0W8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b`\u0010YR\u001a\u0010a\u001a\b\u0012\u0004\u0012\u00020E0W8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\ba\u0010YR\u0014\u0010b\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bb\u0010GR\u0014\u0010c\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bc\u0010GR\u001a\u0010d\u001a\b\u0012\u0004\u0012\u00020E0W8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bd\u0010YR\u001a\u0010e\u001a\b\u0012\u0004\u0012\u00020E0W8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\be\u0010YR\u0014\u0010f\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bf\u0010GR\u001a\u0010g\u001a\b\u0012\u0004\u0012\u00020\t0W8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bg\u0010YR\u001a\u0010h\u001a\b\u0012\u0004\u0012\u00020\t0W8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bh\u0010YR\u0014\u0010i\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bi\u0010GR\u0014\u0010j\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bj\u0010GR\u001a\u0010k\u001a\b\u0012\u0004\u0012\u00020E0W8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bk\u0010YR\u0014\u0010l\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bl\u0010GR\u001a\u0010m\u001a\b\u0012\u0004\u0012\u00020E0W8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bm\u0010YR\u001a\u0010n\u001a\b\u0012\u0004\u0012\u00020E0W8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bn\u0010YR\u0014\u0010o\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bo\u0010GR\u0014\u0010p\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bp\u0010GR\u0014\u0010q\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bq\u0010GR\u0014\u0010r\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\br\u0010GR\u0014\u0010s\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bs\u0010GR\u0016\u0010t\u001a\u00020\u000b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bt\u0010uR\u0016\u0010w\u001a\u00020v8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bw\u0010xR\u0016\u0010y\u001a\u00020\u000b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\by\u0010uR\u001a\u0010{\u001a\b\u0012\u0004\u0012\u00020\t0z8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b{\u0010|R\u001a\u0010}\u001a\b\u0012\u0004\u0012\u00020\t0z8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b}\u0010|R\u001a\u0010~\u001a\b\u0012\u0004\u0012\u00020\t0z8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b~\u0010|R\u001a\u0010\u007f\u001a\b\u0012\u0004\u0012\u00020\t0z8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u007f\u0010|R\u001c\u0010\u0080\u0001\u001a\b\u0012\u0004\u0012\u00020\t0z8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0080\u0001\u0010|R\u001c\u0010\u0081\u0001\u001a\b\u0012\u0004\u0012\u00020\t0z8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0081\u0001\u0010|R\u001c\u0010\u0082\u0001\u001a\b\u0012\u0004\u0012\u00020\t0z8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0082\u0001\u0010|R\u001c\u0010\u0083\u0001\u001a\b\u0012\u0004\u0012\u00020\t0z8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0083\u0001\u0010|R\u001c\u0010\u0084\u0001\u001a\b\u0012\u0004\u0012\u00020E0W8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0084\u0001\u0010YR\u001c\u0010\u0085\u0001\u001a\b\u0012\u0004\u0012\u00020\t0z8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0085\u0001\u0010|R\u001c\u0010\u0086\u0001\u001a\b\u0012\u0004\u0012\u00020\t0z8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0086\u0001\u0010|R\u001c\u0010\u0087\u0001\u001a\b\u0012\u0004\u0012\u00020\t0z8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0087\u0001\u0010|R\u001c\u0010\u0088\u0001\u001a\b\u0012\u0004\u0012\u00020\t0z8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0088\u0001\u0010|R\u001c\u0010\u0089\u0001\u001a\b\u0012\u0004\u0012\u00020\t0z8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u0089\u0001\u0010|R\u0016\u0010\u008a\u0001\u001a\u00020E8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u008a\u0001\u0010GR\u001c\u0010\u008b\u0001\u001a\b\u0012\u0004\u0012\u00020\t0z8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u008b\u0001\u0010|R\u001c\u0010\u008c\u0001\u001a\b\u0012\u0004\u0012\u00020E0W8\u0002X\u0082\u0004\u00a2\u0006\u0007\n\u0005\b\u008c\u0001\u0010Y\u00a8\u0006\u008d\u0001"}, d2={"Lorg/cobalt/internal/chat/ChatFilterModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lorg/cobalt/api/event/impl/client/ChatEvent$Receive;", "event", "", "onChat", "(Lorg/cobalt/api/event/impl/client/ChatEvent$Receive;)V", "", "msg", "", "shouldCancel", "(Ljava/lang/String;)Z", "isWinterGift", "FORMAT", "Ljava/lang/String;", "SPARKLE", "SLAYER_ARROW", "RUNE_DIAMOND", "SNOWMAN", "HOT_SPRING", "CLOVER", "COMET", "POINTER", "PHONE", "BAR", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "filterLobbyJoins", "Lorg/cobalt/api/module/setting/impl/CheckboxSetting;", "filterMysteryBox", "filterPrototype", "filterTournament", "filterWarping", "filterWelcome", "filterProfileJoin", "filterKillCombo", "filterSlayer", "filterSlayerDrops", "filterUselessDrops", "filterSoloClass", "filterFairy", "filterArachne", "filterAnnoyingSpam", "filterBzAhMinis", "filterBzOrders", "filterSacrifice", "filterLegacyItems", "filterGardenNoPest", "filterJacobFortune", "filterParkour", "filterTeleportPad", "filterGuildExp", "filterWinterGift", "filterWinterIsland", "filterEventLevelUp", "filterFireSale", "filterRewardBundles", "filterFactoryUpgrade", "filterHoppityEggs", "filterSkyMall", "filterLottery", "filterWatchdog", "filterUselessWarn", "filterUselessNotifs", "filterPartyDivider", "filterAhDivider", "filterEmpty", "filterDungeonStats", "Lkotlin/text/Regex;", "lobbyJoinRx", "Lkotlin/text/Regex;", "hypixelSmpRx", "snowParticlesRx", "mysteryBoxRx", "mysteryDustRx", "petConsumRx", "warpingRx", "warpedToRx", "guildExpRx", "killComboRx", "killComboExpRx", "slayerStartRx", "slayerEndRx", "slayerXpRx", "slayerLvlRx", "slayerQuestRx", "", "slayerDropRxList", "Ljava/util/List;", "uselessDropRxList", "annoyingSpamRxList", "bzOrderRxList", "winterIslandRx", "fireSaleMainRx", "fireSaleRxList", "eventLvlRxList", "factoryUpgradeRxList", "hoppityAppearRx", "hoppityBeginRx", "sacrificeRxList", "rewardBundleRxList", "jacobFortuneRx", "skymallRxList", "lotteryRxList", "soloClassRx", "soloStatsRx", "fairyRxList", "dungeonRareDropRx", "noPestRxList", "parkourRxList", "teleportPadRx", "arachneCallingRx", "arachneCrystalRx", "arachneSpawnRx", "arachneVenomRx", "inWatchdog", "Z", "", "watchdogBlockedLines", "I", "hideArachneDeadMsg", "", "lobbyExact", "Ljava/util/Set;", "warpingExact", "lobbyContains", "warpingContains", "bzAhMiniExact", "slayerExact", "uselessDropExact", "uselessNotifExact", "uselessNotifPatterns", "uselessWarningExact", "annoyingSpamExact", "fireSaleExact", "ahDividerExact", "powderMiningExact", "legacyItemsRx", "teleportPadExact", "winterGiftRxList", "cobalt"})
@SourceDebugExtension(value={"SMAP\nChatFilterModule.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ChatFilterModule.kt\norg/cobalt/internal/chat/ChatFilterModule\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,566:1\n1807#2,3:567\n777#2:570\n873#2,2:571\n1807#2,3:573\n1807#2,3:576\n1807#2,3:579\n1807#2,3:582\n1807#2,3:585\n1807#2,3:588\n1807#2,3:591\n1807#2,3:594\n1807#2,3:597\n1807#2,3:600\n1807#2,3:603\n1807#2,3:606\n1807#2,3:609\n1807#2,3:612\n1807#2,3:615\n*S KotlinDebug\n*F\n+ 1 ChatFilterModule.kt\norg/cobalt/internal/chat/ChatFilterModule\n*L\n413#1:567,3\n414#1:570\n414#1:571,2\n414#1:573,3\n453#1:576,3\n458#1:579,3\n465#1:582,3\n470#1:585,3\n478#1:588,3\n487#1:591,3\n494#1:594,3\n512#1:597,3\n519#1:600,3\n523#1:603,3\n526#1:606,3\n535#1:609,3\n539#1:612,3\n564#1:615,3\n*E\n"})
public final class ChatFilterModule
extends Module {
    @NotNull
    public static final ChatFilterModule INSTANCE = new ChatFilterModule();
    @NotNull
    private static final String FORMAT = "\u00a7";
    @NotNull
    private static final String SPARKLE = "\u2726";
    @NotNull
    private static final String SLAYER_ARROW = "\u00bb";
    @NotNull
    private static final String RUNE_DIAMOND = "\u25c6";
    @NotNull
    private static final String SNOWMAN = "\u2603";
    @NotNull
    private static final String HOT_SPRING = "\u2668";
    @NotNull
    private static final String CLOVER = "\u2618";
    @NotNull
    private static final String COMET = "\u2604";
    @NotNull
    private static final String POINTER = "\u27a4";
    @NotNull
    private static final String PHONE = "\u2706";
    @NotNull
    private static final String BAR = "\u25ac";
    @NotNull
    private static final CheckboxSetting filterLobbyJoins = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Lobby Joins", "Hide '... joined the lobby!' messages.", false), "Lobby");
    @NotNull
    private static final CheckboxSetting filterMysteryBox = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Mystery Boxes", "Hide mystery box / mystery dust messages.", false), "Lobby");
    @NotNull
    private static final CheckboxSetting filterPrototype = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Prototype Lobby", "Hide prototype lobby welcome & Hype messages.", false), "Lobby");
    @NotNull
    private static final CheckboxSetting filterTournament = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Tournaments", "Hide Hypixel tournament announcement spam.", false), "Lobby");
    @NotNull
    private static final CheckboxSetting filterWarping = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Warping", "Hide 'Sending to server...' / 'Warping...' lines.", false), "Warping");
    @NotNull
    private static final CheckboxSetting filterWelcome = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Welcome", "Hide 'Welcome to Hypixel SkyBlock!'.", false), "Warping");
    @NotNull
    private static final CheckboxSetting filterProfileJoin = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Profile Join", "Hide 'You are playing on profile...' lines.", false), "Warping");
    @NotNull
    private static final CheckboxSetting filterKillCombo = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Kill Combo", "Hide kill combo progress messages.", false), "Combat");
    @NotNull
    private static final CheckboxSetting filterSlayer = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Slayer Quest", "Hide slayer quest start / end messages.", false), "Combat");
    @NotNull
    private static final CheckboxSetting filterSlayerDrops = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Slayer Drops", "Hide slayer rare drop messages.", false), "Combat");
    @NotNull
    private static final CheckboxSetting filterUselessDrops = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Useless Drops", "Hide worthless rare drop messages.", false), "Combat");
    @NotNull
    private static final CheckboxSetting filterSoloClass = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Solo Class", "Hide 'stats doubled' solo class messages.", false), "Combat");
    @NotNull
    private static final CheckboxSetting filterFairy = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Fairy", "Hide fairy death / revive messages.", false), "Combat");
    @NotNull
    private static final CheckboxSetting filterArachne = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Arachne", "Hide Arachne calling / crystal / venom spam.", false), "Combat");
    @NotNull
    private static final CheckboxSetting filterAnnoyingSpam = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Annoying Spam", "Hide Implosion/spirit sceptre hit messages.", false), "Combat");
    @NotNull
    private static final CheckboxSetting filterBzAhMinis = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("BZ / AH Ops", "Hide 'Processing bid...' / 'Claiming order...' etc.", false), "Economy");
    @NotNull
    private static final CheckboxSetting filterBzOrders = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("BZ Orders", "Hide buy/sell order setup confirmation lines.", false), "Economy");
    @NotNull
    private static final CheckboxSetting filterSacrifice = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Sacrifice", "Hide dragon sacrifice broadcast messages.", false), "Economy");
    @NotNull
    private static final CheckboxSetting filterLegacyItems = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Legacy Items", "Hide legacy item exchange warnings.", false), "Economy");
    @NotNull
    private static final CheckboxSetting filterGardenNoPest = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("No Pest Msg", "Hide 'There are no pests in your garden' spam.", true), "Garden");
    @NotNull
    private static final CheckboxSetting filterJacobFortune = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Jacob Fortune", "Hide Jacob NPC fortune talisman messages.", false), "Garden");
    @NotNull
    private static final CheckboxSetting filterParkour = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Parkour", "Hide parkour start/checkpoint/finish messages.", false), "Garden");
    @NotNull
    private static final CheckboxSetting filterTeleportPad = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Teleport Pads", "Hide teleport pad warp / error messages.", false), "Garden");
    @NotNull
    private static final CheckboxSetting filterGuildExp = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Guild / Event EXP", "Hide GEXP and Event EXP earned messages.", false), "Events");
    @NotNull
    private static final CheckboxSetting filterWinterGift = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Winter Gift", "Hide winter gift reward spam.", false), "Events");
    @NotNull
    private static final CheckboxSetting filterWinterIsland = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Winter Island", "Hide Snow Cannon mount messages.", false), "Events");
    @NotNull
    private static final CheckboxSetting filterEventLevelUp = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Event Level Up", "Hide event level-up broadcast messages.", false), "Events");
    @NotNull
    private static final CheckboxSetting filterFireSale = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Fire Sale", "Hide fire sale announcement spam.", false), "Events");
    @NotNull
    private static final CheckboxSetting filterRewardBundles = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Reward Bundles", "Hide unclaimed seasonal reward reminder spam.", false), "Events");
    @NotNull
    private static final CheckboxSetting filterFactoryUpgrade = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Factory Upgrade", "Hide Chocolate Factory upgrade messages.", false), "Events");
    @NotNull
    private static final CheckboxSetting filterHoppityEggs = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Hoppity Eggs", "Hide Hoppity's Hunt egg-appeared messages.", false), "Events");
    @NotNull
    private static final CheckboxSetting filterSkyMall = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Sky Mall", "Hide Sky Mall buff-changed messages.", false), "Events");
    @NotNull
    private static final CheckboxSetting filterLottery = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Lottery", "Hide Lottery buff-changed messages.", false), "Events");
    @NotNull
    private static final CheckboxSetting filterWatchdog = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Watchdog", "Hide multi-line Watchdog ban announcement blocks.", false), "Misc");
    @NotNull
    private static final CheckboxSetting filterUselessWarn = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Useless Warnings", "Hide 'You can't use this in combat' etc.", false), "Misc");
    @NotNull
    private static final CheckboxSetting filterUselessNotifs = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Useless Notifs", "Hide mining speed, tipping, bank interest spam.", false), "Misc");
    @NotNull
    private static final CheckboxSetting filterPartyDivider = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Party Dividers", "Hide \u00a79\u00a7m--- party separator lines.", false), "Misc");
    @NotNull
    private static final CheckboxSetting filterAhDivider = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("AH Dividers", "Hide \u00a7b--- auction house separator lines.", false), "Misc");
    @NotNull
    private static final CheckboxSetting filterEmpty = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Empty Lines", "Hide completely empty chat messages.", false), "Misc");
    @NotNull
    private static final CheckboxSetting filterDungeonStats = (CheckboxSetting)SettingKt.inGroup((Setting)new CheckboxSetting("Dungeon Stats", "Hide solo-stats and rare-drop dungeon messages.", false), "Misc");
    @NotNull
    private static final Regex lobbyJoinRx = new Regex("(?: \u00a7b>\u00a7c>\u00a7a>\u00a7r \u00a7r)?.* \u00a76(?:joined|(?:spooked|slid) into) the lobby!(?:\u00a7r \u00a7a<\u00a7c<\u00a7b<)?");
    @NotNull
    private static final Regex hypixelSmpRx = new Regex("\u00a72[\\s]*?\u00a7aYou can now create your own Hypixel SMP server![\\s]*?");
    @NotNull
    private static final Regex snowParticlesRx = new Regex("[\\s]*?.*\u00a7bFor the best experience.*Snow.*Particles.*", RegexOption.DOT_MATCHES_ALL);
    @NotNull
    private static final Regex mysteryBoxRx = new Regex("\u00a7b\u2726 \u00a7r.* \u00a7r\u00a77found (?:a|an) \u00a7r.*(?:Mystery Box|in a \u00a7r\u00a7a(?:Holiday )?Mystery Box)\u00a7r\u00a77!");
    @NotNull
    private static final Regex mysteryDustRx = new Regex("\u00a7b\u00a7b\u2726 \u00a7r\u00a77You earned \u00a7r\u00a7b\\d+ \u00a7r\u00a77Mystery Dust!");
    @NotNull
    private static final Regex petConsumRx = new Regex("\u00a7b\u00a7b\u2726 \u00a7r\u00a77You earned \u00a7a\\d+ \u00a77Pet Consumables items!");
    @NotNull
    private static final Regex warpingRx = new Regex("\u00a77(?:Sending to server|Request join for (?:Hub|Dungeon Hub #)) .*\\.\\.\\.");
    @NotNull
    private static final Regex warpedToRx = new Regex("\u00a7dWarped to (.*)\u00a7r\u00a7d!");
    @NotNull
    private static final Regex guildExpRx = new Regex("\u00a7aYou earned \u00a7r\u00a7[0-9a-f][\\d,]+ (?:GEXP|Event EXP) (?:\u00a7r\u00a7a\\+ \u00a7r\u00a7[0-9a-f][\\d,]+ Event EXP )?\u00a7r\u00a7afrom playing SkyBlock!");
    @NotNull
    private static final Regex killComboRx = new Regex("\u00a7.\u00a7l\\+(.*) Kill Combo(.*)");
    @NotNull
    private static final Regex killComboExpRx = new Regex("\u00a7cYour Kill Combo has expired! You reached a (.*) Kill Combo!");
    @NotNull
    private static final Regex slayerStartRx = new Regex(" {2}\u00a7r\u00a75\u00a7lSLAYER QUEST STARTED!");
    @NotNull
    private static final Regex slayerEndRx = new Regex(" {2}\u00a7r\u00a7a\u00a7lSLAYER QUEST COMPLETE!");
    @NotNull
    private static final Regex slayerXpRx = new Regex(" {3}\u00a7r\u00a75\u00a7l\u00bb \u00a7r\u00a77Talk to Maddox to claim your (.*) Slayer XP!");
    @NotNull
    private static final Regex slayerLvlRx = new Regex(" {3}\u00a7r\u00a7e(.*)Slayer LVL 9 \u00a7r\u00a75- \u00a7r\u00a7a\u00a7lLVL MAXED OUT!");
    @NotNull
    private static final Regex slayerQuestRx = new Regex(" {3}\u00a75\u00a7l\u00bb \u00a77Slay \u00a7c(.*) Combat XP \u00a77worth of (.*)\u00a77\\.");
    @NotNull
    private static final List<Regex> slayerDropRxList;
    @NotNull
    private static final List<Regex> uselessDropRxList;
    @NotNull
    private static final List<Regex> annoyingSpamRxList;
    @NotNull
    private static final List<Regex> bzOrderRxList;
    @NotNull
    private static final Regex winterIslandRx;
    @NotNull
    private static final Regex fireSaleMainRx;
    @NotNull
    private static final List<Regex> fireSaleRxList;
    @NotNull
    private static final List<Regex> eventLvlRxList;
    @NotNull
    private static final List<Regex> factoryUpgradeRxList;
    @NotNull
    private static final Regex hoppityAppearRx;
    @NotNull
    private static final Regex hoppityBeginRx;
    @NotNull
    private static final List<Regex> sacrificeRxList;
    @NotNull
    private static final List<Regex> rewardBundleRxList;
    @NotNull
    private static final Regex jacobFortuneRx;
    @NotNull
    private static final List<String> skymallRxList;
    @NotNull
    private static final List<String> lotteryRxList;
    @NotNull
    private static final Regex soloClassRx;
    @NotNull
    private static final Regex soloStatsRx;
    @NotNull
    private static final List<Regex> fairyRxList;
    @NotNull
    private static final Regex dungeonRareDropRx;
    @NotNull
    private static final List<Regex> noPestRxList;
    @NotNull
    private static final List<Regex> parkourRxList;
    @NotNull
    private static final Regex teleportPadRx;
    @NotNull
    private static final Regex arachneCallingRx;
    @NotNull
    private static final Regex arachneCrystalRx;
    @NotNull
    private static final Regex arachneSpawnRx;
    @NotNull
    private static final Regex arachneVenomRx;
    private static boolean inWatchdog;
    private static int watchdogBlockedLines;
    private static boolean hideArachneDeadMsg;
    @NotNull
    private static final Set<String> lobbyExact;
    @NotNull
    private static final Set<String> warpingExact;
    @NotNull
    private static final Set<String> lobbyContains;
    @NotNull
    private static final Set<String> warpingContains;
    @NotNull
    private static final Set<String> bzAhMiniExact;
    @NotNull
    private static final Set<String> slayerExact;
    @NotNull
    private static final Set<String> uselessDropExact;
    @NotNull
    private static final Set<String> uselessNotifExact;
    @NotNull
    private static final List<Regex> uselessNotifPatterns;
    @NotNull
    private static final Set<String> uselessWarningExact;
    @NotNull
    private static final Set<String> annoyingSpamExact;
    @NotNull
    private static final Set<String> fireSaleExact;
    @NotNull
    private static final Set<String> ahDividerExact;
    @NotNull
    private static final Set<String> powderMiningExact;
    @NotNull
    private static final Regex legacyItemsRx;
    @NotNull
    private static final Set<String> teleportPadExact;
    @NotNull
    private static final List<Regex> winterGiftRxList;

    private ChatFilterModule() {
        super("Chat Filter");
    }

    @SubscribeEvent
    public final void onChat(@NotNull ChatEvent.Receive event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        String string = event.getMessage();
        if (string == null) {
            return;
        }
        String msg = string;
        if (this.shouldCancel(msg)) {
            event.setCancelled(true);
        }
    }

    /*
     * Exception decompiling
     */
    private final boolean shouldCancel(String msg) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Can't sort instructions [@NONE, blocks:[5] lbl34 : CaseStatement: default:\u000a, @NONE, blocks:[5] lbl34 : CaseStatement: default:\u000a]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.CompareByIndex.compare(CompareByIndex.java:25)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.CompareByIndex.compare(CompareByIndex.java:8)
         *     at java.base/java.util.TimSort.countRunAndMakeAscending(TimSort.java:360)
         *     at java.base/java.util.TimSort.sort(TimSort.java:220)
         *     at java.base/java.util.Arrays.sort(Arrays.java:1304)
         *     at java.base/java.util.ArrayList.sortRange(ArrayList.java:1817)
         *     at java.base/java.util.ArrayList.sort(ArrayList.java:1810)
         *     at java.base/java.util.Collections.sort(Collections.java:178)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.buildSwitchCases(SwitchReplacer.java:271)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.replaceRawSwitch(SwitchReplacer.java:258)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.replaceRawSwitches(SwitchReplacer.java:66)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:517)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private final boolean isWinterGift(String msg) {
        boolean bl;
        block3: {
            Iterable $this$any$iv = winterGiftRxList;
            boolean $i$f$any = false;
            if ($this$any$iv instanceof Collection && ((Collection)$this$any$iv).isEmpty()) {
                bl = false;
            } else {
                for (Object element$iv : $this$any$iv) {
                    Regex it = (Regex)element$iv;
                    boolean bl2 = false;
                    if (!it.containsMatchIn((CharSequence)msg)) continue;
                    bl = true;
                    break block3;
                }
                bl = false;
            }
        }
        return bl;
    }

    static {
        Object[] objectArray = new Regex[]{new Regex("\u00a7b\u00a7lRARE DROP! \u00a7r\u00a77\\(\u00a7r\u00a7f\u00a7r\u00a77(.*)x \u00a7r\u00a7f\u00a7r\u00a79(?:Revenant Viscera|Foul Flesh|Toxic Arrow Poison|Twilight Arrow Poison)\u00a7r\u00a77\\) (.*)"), new Regex("\u00a7b\u00a7lRARE DROP! \u00a7r\u00a77\\(\u00a7r\u00a7f\u00a7r\u00a79(?:Revenant Viscera|Foul Flesh)\u00a7r\u00a77\\) (.*)"), new Regex("\u00a76\u00a7lRARE DROP! \u00a7r\u00a75Golden Powder (.*)"), new Regex("\u00a7[59]\u00a7lVERY RARE DROP! {2}\u00a7r\u00a77\\(.*\\) (.*)"), new Regex("\u00a76\u00a7lRARE DROP! \u00a7r\u00a79Arachne's Keeper Fragment (.+)"), new Regex("\u00a76\u00a7lRARE DROP! \u00a7r\u00a75Travel Scroll to Spider's Den Top of Nest (.+)"), new Regex("\u00a79\u00a7lVERY RARE DROP! {2}\u00a7r\u00a77\\(\u00a7r\u00a7f\u00a7r\u00a7a\u25c6 Bite Rune I\u00a7r\u00a77\\) (.+)"), new Regex("\u00a75\u00a7lVERY RARE DROP! {2}\u00a7r\u00a77\\(\u00a7r\u00a79Bane of Arthropods VI\u00a7r\u00a77\\) (.+)"), new Regex("\u00a79\u00a7lVERY RARE DROP! {2}\u00a7r\u00a77\\(\u00a7r\u00a7f\u00a7r\u00a77\\d+x \u00a7r\u00a7f\u00a7r\u00a79(?:Glowstone|Blaze Rod|Magma Cream|Nether Wart) Distillate\u00a7r\u00a77\\) (.*)"), new Regex("\u00a7d\u00a7lCRAZY RARE DROP! {2}\u00a7r\u00a77\\(\u00a7r\u00a7f\u00a7r\u00a7fPocket Espresso Machine\u00a7r\u00a77\\) (.*)"), new Regex("\u00a75\u00a7lVERY RARE DROP! {2}\u00a7r\u00a77\\(\u00a7r\u00a7f\u00a7r\u00a75(?:\u25c6 End Rune I|Sinful Dice|Revenant Catalyst|Undead Catalyst|\u25c6 Endersnake Rune I|Transmission Tuner)\u00a7r\u00a77\\) (.*)"), new Regex("\u00a79\u00a7lVERY RARE DROP! {2}\u00a7r\u00a77\\(\u00a7r\u00a7f\u00a7r\u00a79(?:Null Atom|Mana Steal I)\u00a7r\u00a77\\) (.*)"), new Regex("\u00a76\u00a7lRARE DROP! \u00a7r\u00a75Bundle of Magma Arrows\u00a7r\u00a77\\) (.*)"), new Regex("\u00a79\u00a7lVERY RARE DROP! {2}\u00a7r\u00a77\\(\u00a7r\u00a7f\u00a7r\u00a7fWisp's Ice-Flavored Water I Splash Potion\u00a7r\u00a77\\) (.*)"), new Regex("\u00a75\u00a7lVERY RARE DROP! {2}\u00a7r\u00a77\\(\u00a7r\u00a7f\u00a7r\u00a76Hazmat Enderman\u00a7r\u00a77\\) .*")};
        slayerDropRxList = CollectionsKt.listOf((Object[])objectArray);
        objectArray = new Regex[]{new Regex("\u00a76\u00a7lRARE DROP! \u00a7r\u00a7aEnchanted Ender Pearl (.*)"), new Regex("\u00a76\u00a7lRARE DROP! \u00a7r\u00a7f(?:Carrot|Potato) (.*)"), new Regex("\u00a76\u00a7lRARE DROP! \u00a7r\u00a79Machine Gun Bow (.*)"), new Regex("\u00a76\u00a7lRARE DROP! \u00a7r\u00a75(?:Earth Shard|Zombie Lord Chestplate) (.*)")};
        uselessDropRxList = CollectionsKt.listOf((Object[])objectArray);
        objectArray = new Regex[]{new Regex("\u00a77Your (?:Implosion|Molten Wave|Spirit Sceptre) hit (.*) for \u00a7r\u00a7c(.*) \u00a7r\u00a77damage\\."), new Regex("\u00a7cYou need a tool with a \u00a7r\u00a7aBreaking Power \u00a7r\u00a7cof \u00a7r\u00a76(\\d)\u00a7r\u00a7c to mine .*")};
        annoyingSpamRxList = CollectionsKt.listOf((Object[])objectArray);
        objectArray = new Regex[]{new Regex("\u00a7eBuy Order Setup! \u00a7r\u00a7a(.*)\u00a7r\u00a77x (.*) \u00a7r\u00a77for \u00a7r\u00a76(.*) coins\u00a7r\u00a77\\."), new Regex("\u00a7eSell Offer Setup! \u00a7r\u00a7a(.*)\u00a7r\u00a77x (.*) \u00a7r\u00a77for \u00a7r\u00a76(.*) coins\u00a7r\u00a77\\."), new Regex("\u00a7cCancelled! \u00a7r\u00a77Refunded \u00a7r\u00a76(.*) coins \u00a7r\u00a77from cancelling buy order!"), new Regex("\u00a7cCancelled! \u00a7r\u00a77Refunded \u00a7r\u00a7a(.*)\u00a7r\u00a77x (.*) \u00a7r\u00a77from cancelling sell offer!")};
        bzOrderRxList = CollectionsKt.listOf((Object[])objectArray);
        winterIslandRx = new Regex("\u00a7r\u00a7f\u2603 \u00a7r\u00a77\u00a7r(.*) \u00a7r\u00a77mounted a \u00a7r\u00a7fSnow Cannon\u00a7r\u00a77!");
        fireSaleMainRx = new Regex("\u00a76\u00a7k\u00a7lA\u00a7r \u00a7c\u00a7lFIRE SALE \u00a7r\u00a76\u00a7k\u00a7lA", RegexOption.DOT_MATCHES_ALL);
        objectArray = new Regex[]{new Regex("\u00a7c\u2668 \u00a7eFire Sales? for .* \u00a7e(?:are|is) starting soon!"), new Regex("\u00a7c\\s*\u2668 .* (?:Skin|Rune|Dye) \u00a7e(?:for a limited time )?\\(.* \u00a7eleft\\)(?:\u00a7c|!)"), new Regex("\u00a7c\u2668 \u00a7eVisit the Community Shop in the next \u00a7c.* \u00a7eto grab yours! \u00a7a\u00a7l\\[WARP]"), new Regex("\u00a7c\u2668 \u00a7eA Fire Sale for .* \u00a7eis starting soon!"), new Regex("\u00a7c\u2668 \u00a7r\u00a7eFire Sales? for .* \u00a7r\u00a7eended!"), new Regex("\u00a7c {3}\u2668 \u00a7eAnd \\d+ more!")};
        fireSaleRxList = CollectionsKt.listOf((Object[])objectArray);
        objectArray = new Regex[]{new Regex("(?:\u00a7f)? +\u00a7r\u00a77You are now \u00a7r\u00a7.Event Level \u00a7r\u00a7.*\u00a7r\u00a77!"), new Regex("(?:\u00a7f)? +\u00a7r\u00a77You earned \u00a7r\u00a7.* Event Silver\u00a7r\u00a77!"), new Regex("(?:\u00a7f)? +\u00a7r\u00a7.\u00a7k#\u00a7r\u00a7. LEVEL UP! \u00a7r\u00a7.\u00a7k#")};
        eventLvlRxList = CollectionsKt.listOf((Object[])objectArray);
        objectArray = new Regex[]{new Regex(".* \u00a7r\u00a77has been promoted to \u00a7r\u00a77\\[.*\u00a7r\u00a77] \u00a7r\u00a7.*\u00a7r\u00a77!"), new Regex("\u00a77Your \u00a7r\u00a7aRabbit Barn \u00a7r\u00a77capacity has been increased to \u00a7r\u00a7a.* Rabbits\u00a7r\u00a77!"), new Regex("\u00a77You will now produce \u00a7r\u00a76.* Chocolate \u00a7r\u00a77per click!"), new Regex("\u00a77You upgraded to \u00a7r\u00a7d.*?\u00a7r\u00a77!")};
        factoryUpgradeRxList = CollectionsKt.listOf((Object[])objectArray);
        hoppityAppearRx = new Regex("\u00a7d\u00a7lHOPPITY'S HUNT \u00a7r\u00a7dA .* \u00a7r\u00a7dhas appeared!");
        hoppityBeginRx = new Regex("\u00a7dHoppity's Hunt \u00a7r\u00a7ehas begun!.*");
        objectArray = new Regex[]{new Regex("\u00a7c\u00a7lSACRIFICE! (.*) \u00a7r\u00a7eturned (.*) \u00a7r\u00a7einto (.*) Dragon Essence\u00a7r\u00a7e!"), new Regex("\u00a7c\u00a7lBONUS LOOT! \u00a7r\u00a7eThey also received (.*) \u00a7r\u00a7efrom their sacrifice!")};
        sacrificeRxList = CollectionsKt.listOf((Object[])objectArray);
        objectArray = new Regex[]{new Regex("(?:\u00a7.)*You haven't claimed your (?:\u00a7.)*\\w+ Rewards (?:\u00a7.)*yet!"), new Regex("(?:\u00a7.)*Talk to the (?:\u00a7.)*.+(?:\u00a7.)*in the (?:\u00a7.)*.+(?:\u00a7.)*!")};
        rewardBundleRxList = CollectionsKt.listOf((Object[])objectArray);
        jacobFortuneRx = new Regex("\u00a7e\\[NPC] Jacob\u00a7f: \u00a7rYour \u00a79Anita's \\w+ \u00a7fis giving you \u00a76\\+\\d{1,2}\u2618 .+ Fortune \u00a7fduring the contest!");
        objectArray = new String[]{"\u00a7bNew day! \u00a7r\u00a7eYour \u00a7r\u00a72Sky Mall \u00a7r\u00a7ebuff changed!", "\u00a78\u00a7oYou can disable this messaging by toggling Sky Mall in your /hotm!"};
        skymallRxList = CollectionsKt.listOf((Object[])objectArray);
        objectArray = new String[]{"\u00a7bNew day! \u00a7r\u00a7eYour \u00a7r\u00a72Lottery \u00a7r\u00a7ebuff changed!", "\u00a78\u00a7oYou can disable this messaging by toggling Lottery in your /hotf!"};
        lotteryRxList = CollectionsKt.listOf((Object[])objectArray);
        soloClassRx = new Regex("\u00a76Your \u00a7r\u00a7a(Healer|Mage|Berserk|Archer|Tank) \u00a7r\u00a76stats are doubled because you are the only player using this class!");
        soloStatsRx = new Regex("\u00a7a\\[(Healer|Mage|Berserk|Archer|Tank)].*");
        objectArray = new Regex[]{new Regex("\u00a7d[\\w']+ the Fairy\u00a7r\u00a7f: You killed me! Take this \u00a7r\u00a76Revive Stone \u00a7r\u00a7fso that my death is not in vain!"), new Regex("\u00a7d[\\w']+ the Fairy\u00a7r\u00a7f: You killed me! I'll revive you so that my death is not in vain!"), new Regex("\u00a7d[\\w']+ the Fairy\u00a7r\u00a7f: Have a great life!")};
        fairyRxList = CollectionsKt.listOf((Object[])objectArray);
        dungeonRareDropRx = new Regex("\u00a76\u00a7lRARE REWARD! \u00a7r\u00a7bLeebys \u00a7r\u00a7efound a (.*) \u00a7r\u00a7ein their (.*) Chest\u00a7r\u00a7e!");
        objectArray = new Regex[]{new Regex("\u00a7aNo pests are currently in your garden!"), new Regex("\u00a7eThere are no pests in your garden!"), new Regex("\u00a7cThere are no pests currently in your garden", RegexOption.IGNORE_CASE)};
        noPestRxList = CollectionsKt.listOf((Object[])objectArray);
        objectArray = new Regex[]{new Regex("\u00a7aStarted parkour (.*)!"), new Regex("\u00a7aFinished parkour (.*) in (.*)!"), new Regex("\u00a7aReached checkpoint #(.*) for parkour (.*)!"), new Regex("\u00a74Wrong checkpoint for parkour (.*)!"), new Regex("\u00a74You haven't reached all checkpoints for parkour (.*)!")};
        parkourRxList = CollectionsKt.listOf((Object[])objectArray);
        teleportPadRx = new Regex("\u00a7aWarped from the (.*) \u00a7r\u00a7ato the (.*)\u00a7r\u00a7a!");
        arachneCallingRx = new Regex("\u00a74\u2604 \u00a7r.* \u00a7r\u00a7eplaced an \u00a7r\u00a79Arachne's Calling\u00a7r\u00a7e!.*");
        arachneCrystalRx = new Regex("\u00a74\u2604 \u00a7r.* \u00a7r\u00a7eplaced an Arachne Crystal! Something is awakening!");
        arachneSpawnRx = new Regex("\u00a7c\\[BOSS] Arachne\u00a7r\u00a7f: (?:The Era of Spiders begins now\\.|Ahhhh\\.\\.\\.A Calling\\.\\.\\.)");
        arachneVenomRx = new Regex("\u00a7dArachne(?:'s (?:Keeper|Brood))? used \u00a7r\u00a72Venom Shot \u00a7r\u00a7don you hitting you for \u00a7r\u00a7c[\\d.,]+ damage \u00a7r\u00a7dand infecting you with venom\\.");
        lobbyExact = SetsKt.setOf((Object)"  \u00a7r\u00a7f\u00a7l\u27a4 \u00a7r\u00a76You have reached your Hype limit! Add Hype to Prototype Lobby minigames by right-clicking with the Hype Diamond!");
        objectArray = new String[]{"\u00a77Warping...", "\u00a77Warping you to your SkyBlock island...", "\u00a77Warping using transfer token...", "\u00a77Finding player...", "\u00a77Sending a visit request..."};
        warpingExact = SetsKt.setOf((Object[])objectArray);
        objectArray = new String[]{"\u00a7r\u00a76\u00a7lWelcome to the Prototype Lobby\u00a7r", "\u00a7r\u00a7e\u00a76\u00a7lHYPIXEL\u00a7e is hosting a \u00a7b\u00a7lBED WARS DOUBLES\u00a7e tournament!", "\u00a7r\u00a7e\u00a76\u00a7lHYPIXEL BED WARS DOUBLES\u00a7e tournament is live!", "\u00a7r\u00a7e\u00a76\u00a7lHYPIXEL\u00a7e is hosting a \u00a7b\u00a7lTNT RUN\u00a7e tournament!", "\u00a7aYou are still radiating with \u00a7bGenerosity\u00a7r\u00a7a!"};
        lobbyContains = SetsKt.setOf((Object[])objectArray);
        warpingContains = SetsKt.emptySet();
        objectArray = new String[]{"\u00a77Putting item in escrow...", "\u00a77Putting coins in escrow...", "\u00a77Setting up the auction...", "\u00a77Processing purchase...", "\u00a77Processing bid...", "\u00a77Claiming BIN auction...", "\u00a76[Bazaar] \u00a7r\u00a77Submitting sell offer...", "\u00a76[Bazaar] \u00a7r\u00a77Submitting buy order...", "\u00a76[Bazaar] \u00a7r\u00a77Executing instant sell...", "\u00a76[Bazaar] \u00a7r\u00a77Executing instant buy...", "\u00a76[Bazaar] \u00a7r\u00a77Cancelling order...", "\u00a76[Bazaar] \u00a7r\u00a77Claiming order...", "\u00a76[Bazaar] \u00a7r\u00a77Putting goods in escrow...", "\u00a78Depositing coins...", "\u00a78Withdrawing coins..."};
        bzAhMiniExact = SetsKt.setOf((Object[])objectArray);
        objectArray = new String[]{"  \u00a7r\u00a76\u00a7lNICE! SLAYER BOSS SLAIN!", "\u00a7eYou received kill credit for assisting on a slayer miniboss!"};
        slayerExact = SetsKt.setOf((Object[])objectArray);
        objectArray = new String[]{"\u00a76\u00a7lRARE DROP! \u00a7r\u00a7aEnchanted Ender Pearl", "\u00a76\u00a7lRARE DROP! \u00a7r\u00a7aEnchanted End Stone", "\u00a76\u00a7lRARE DROP! \u00a7r\u00a75Crystal Fragment"};
        uselessDropExact = SetsKt.setOf((Object[])objectArray);
        objectArray = new String[]{"\u00a7eYour previous \u00a7r\u00a76Plasmaflux Power Orb \u00a7r\u00a7ewas removed!", "\u00a7aYou used your \u00a7r\u00a76Mining Speed Boost \u00a7r\u00a7aPickaxe Ability!", "\u00a7cYour Mining Speed Boost has expired!", "\u00a7a\u00a7r\u00a76Mining Speed Boost \u00a7r\u00a7ais now available!", "\u00a7aYou have just received \u00a7r\u00a760 coins \u00a7r\u00a7aas interest in your personal bank account!", "\u00a7aSince you've been away you earned \u00a7r\u00a760 coins \u00a7r\u00a7aas interest in your personal bank account!", "\u00a7aYou have just received \u00a7r\u00a760 coins \u00a7r\u00a7aas interest in your co-op bank account!"};
        uselessNotifExact = SetsKt.setOf((Object[])objectArray);
        uselessNotifPatterns = CollectionsKt.listOf((Object)new Regex("(?:\u00a7a)?\u00a7aYou tipped \\d+ players? in \\d+(?: different)? games?!"));
        objectArray = new String[]{"\u00a7cYou are sending commands too fast! Please slow down.", "\u00a7cYou can't use this while in combat!", "\u00a7cYou can not modify your equipped armor set!", "\u00a7cPlease wait a few seconds between refreshing!", "\u00a7cThis item is not salvageable!", "\u00a7cPlace a Dungeon weapon or armor piece above the anvil to salvage it!", "\u00a7cWhoa! Slow down there!", "\u00a7cWait a moment before confirming!", "\u00a7cYou cannot open the SkyBlock menu while in combat!"};
        uselessWarningExact = SetsKt.setOf((Object[])objectArray);
        objectArray = new String[]{"\u00a7cThere are blocks in the way!", "\u00a7aYour Blessing enchant got you double drops!", "\u00a7cYou can't use the wardrobe in combat!", "\u00a76\u00a7lGOOD CATCH! \u00a7r\u00a7bYou found a \u00a7r\u00a7fFish Bait\u00a7r\u00a7b.", "\u00a76\u00a7lGOOD CATCH! \u00a7r\u00a7bYou found a \u00a7r\u00a7aGrand Experience Bottle\u00a7r\u00a7b.", "\u00a76\u00a7lGOOD CATCH! \u00a7r\u00a7bYou found a \u00a7r\u00a7aBlessed Bait\u00a7r\u00a7b.", "\u00a76\u00a7lGOOD CATCH! \u00a7r\u00a7bYou found a \u00a7r\u00a7fDark Bait\u00a7r\u00a7b.", "\u00a76\u00a7lGOOD CATCH! \u00a7r\u00a7bYou found a \u00a7r\u00a7fLight Bait\u00a7r\u00a7b.", "\u00a76\u00a7lGOOD CATCH! \u00a7r\u00a7bYou found a \u00a7r\u00a7aHot Bait\u00a7r\u00a7b.", "\u00a76\u00a7lGOOD CATCH! \u00a7r\u00a7bYou found a \u00a7r\u00a7fSpooky Bait\u00a7r\u00a7b.", "\u00a7e[NPC] Jacob\u00a7f: \u00a7rMy contest has started!", "\u00a7eObtain a \u00a7r\u00a76Booster Cookie \u00a7r\u00a7efrom the community shop in the hub!"};
        annoyingSpamExact = SetsKt.setOf((Object[])objectArray);
        objectArray = new String[]{"\u00a76\u00a7k\u00a7lA\u00a7r \u00a7c\u00a7lFIRE SALE \u00a7r\u00a76\u00a7k\u00a7lA", "\u00a7c\u2668 \u00a7eSelling multiple items for a limited time!"};
        fireSaleExact = SetsKt.setOf((Object[])objectArray);
        objectArray = new String[]{"\u00a7b-----------------------------------------------------", "\u00a7eVisit the Auction House to collect your item!"};
        ahDividerExact = SetsKt.setOf((Object[])objectArray);
        objectArray = new String[]{"\u00a7aYou uncovered a treasure chest!", "\u00a7aYou received \u00a7r\u00a7f1 \u00a7r\u00a7aWishing Compass\u00a7r\u00a7a.", "\u00a7aYou received \u00a7r\u00a7f1 \u00a7r\u00a79Ascension Rope\u00a7r\u00a7a.", "\u00a7aYou received \u00a7r\u00a7f1 \u00a7r\u00a7aOil Barrel\u00a7r\u00a7a.", "\u00a76You have successfully picked the lock on this chest!"};
        powderMiningExact = SetsKt.setOf((Object[])objectArray);
        legacyItemsRx = new Regex("\u00a7cYou currently have one or more Legacy Items in your inventory or sacks.*");
        teleportPadExact = SetsKt.setOf((Object)"\u00a74This Teleport Pad does not have a destination set!");
        objectArray = new Setting[]{filterLobbyJoins, filterMysteryBox, filterPrototype, filterTournament, filterWarping, filterWelcome, filterProfileJoin, filterKillCombo, filterSlayer, filterSlayerDrops, filterUselessDrops, filterSoloClass, filterFairy, filterArachne, filterAnnoyingSpam, filterBzAhMinis, filterBzOrders, filterSacrifice, filterLegacyItems, filterGardenNoPest, filterJacobFortune, filterParkour, filterTeleportPad, filterGuildExp, filterWinterGift, filterWinterIsland, filterEventLevelUp, filterFireSale, filterRewardBundles, filterFactoryUpgrade, filterHoppityEggs, filterSkyMall, filterLottery, filterWatchdog, filterUselessWarn, filterUselessNotifs, filterPartyDivider, filterAhDivider, filterEmpty, filterDungeonStats};
        INSTANCE.addSetting((Setting<?>[])objectArray);
        EventBus.register(INSTANCE);
        objectArray = new Regex[]{new Regex("\u00a7aYou received \u00a7r\u00a76\\d[\\d,.]* Coins \u00a7r\u00a7afrom a (?:Great|Good|Normal|Perfect|Magical)? ?Winter Gift!"), new Regex("\u00a7aYou received \u00a7r\u00a7b\\d[\\d,.]* \u00a7r\u00a76SkyBlock XP \u00a7r\u00a7afrom a .*Winter Gift!"), new Regex("\u00a7aYou received \u00a7r\u00a7b\\d[\\d,.]* \u00a7r\u00a7bNorth Stars \u00a7r\u00a7afrom a .*Winter Gift!"), new Regex("\u00a7aYou received .* \u00a7r\u00a7afrom a .*Winter Gift!"), new Regex("\u00a77Right-click the gift to open it!"), new Regex("\u00a7a\\+\\d+ (?:Farming|Mining|Combat|Foraging|Fishing|Enchanting|Alchemy) XP")};
        winterGiftRxList = CollectionsKt.listOf((Object[])objectArray);
    }
}

