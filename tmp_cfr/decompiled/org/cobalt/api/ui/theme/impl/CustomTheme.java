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
package org.cobalt.api.ui.theme.impl;

import java.awt.Color;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.ui.theme.Theme;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000-\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\bw\n\u0002\u0010\u0000\n\u0003\b\u0082\u0001\b\u0086\b\u0018\u00002\u00020\u0001B\u00cb\u0004\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0004\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0006\u0012\b\b\u0002\u0010\b\u001a\u00020\u0006\u0012\b\b\u0002\u0010\t\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u000b\u001a\u00020\n\u0012\b\b\u0002\u0010\f\u001a\u00020\n\u0012\b\b\u0002\u0010\r\u001a\u00020\n\u0012\b\b\u0002\u0010\u000e\u001a\u00020\n\u0012\b\b\u0002\u0010\u000f\u001a\u00020\n\u0012\b\b\u0002\u0010\u0010\u001a\u00020\n\u0012\b\b\u0002\u0010\u0011\u001a\u00020\n\u0012\b\b\u0002\u0010\u0012\u001a\u00020\n\u0012\b\b\u0002\u0010\u0013\u001a\u00020\n\u0012\b\b\u0002\u0010\u0014\u001a\u00020\n\u0012\b\b\u0002\u0010\u0015\u001a\u00020\n\u0012\b\b\u0002\u0010\u0016\u001a\u00020\n\u0012\b\b\u0002\u0010\u0017\u001a\u00020\n\u0012\b\b\u0002\u0010\u0018\u001a\u00020\n\u0012\b\b\u0002\u0010\u0019\u001a\u00020\n\u0012\b\b\u0002\u0010\u001a\u001a\u00020\n\u0012\b\b\u0002\u0010\u001b\u001a\u00020\n\u0012\b\b\u0002\u0010\u001c\u001a\u00020\n\u0012\b\b\u0002\u0010\u001d\u001a\u00020\n\u0012\b\b\u0002\u0010\u001e\u001a\u00020\n\u0012\b\b\u0002\u0010\u001f\u001a\u00020\n\u0012\b\b\u0002\u0010 \u001a\u00020\n\u0012\b\b\u0002\u0010!\u001a\u00020\n\u0012\b\b\u0002\u0010\"\u001a\u00020\n\u0012\b\b\u0002\u0010#\u001a\u00020\n\u0012\b\b\u0002\u0010$\u001a\u00020\n\u0012\b\b\u0002\u0010%\u001a\u00020\n\u0012\b\b\u0002\u0010&\u001a\u00020\n\u0012\b\b\u0002\u0010'\u001a\u00020\n\u0012\b\b\u0002\u0010(\u001a\u00020\n\u0012\b\b\u0002\u0010)\u001a\u00020\n\u0012\b\b\u0002\u0010*\u001a\u00020\n\u0012\b\b\u0002\u0010+\u001a\u00020\n\u0012\b\b\u0002\u0010,\u001a\u00020\n\u0012\b\b\u0002\u0010-\u001a\u00020\n\u0012\b\b\u0002\u0010.\u001a\u00020\n\u0012\b\b\u0002\u0010/\u001a\u00020\n\u0012\b\b\u0002\u00100\u001a\u00020\n\u0012\b\b\u0002\u00101\u001a\u00020\n\u0012\b\b\u0002\u00102\u001a\u00020\n\u0012\b\b\u0002\u00103\u001a\u00020\n\u0012\b\b\u0002\u00104\u001a\u00020\n\u0012\b\b\u0002\u00105\u001a\u00020\n\u0012\b\b\u0002\u00106\u001a\u00020\n\u0012\b\b\u0002\u00107\u001a\u00020\n\u0012\b\b\u0002\u00108\u001a\u00020\n\u0012\b\b\u0002\u00109\u001a\u00020\n\u0012\b\b\u0002\u0010:\u001a\u00020\n\u0012\b\b\u0002\u0010;\u001a\u00020\n\u0012\b\b\u0002\u0010<\u001a\u00020\n\u0012\b\b\u0002\u0010=\u001a\u00020\n\u0012\b\b\u0002\u0010>\u001a\u00020\n\u0012\b\b\u0002\u0010?\u001a\u00020\n\u00a2\u0006\u0004\b@\u0010AJ\u0010\u0010B\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\bB\u0010CJ\u0010\u0010D\u001a\u00020\u0004H\u00c6\u0003\u00a2\u0006\u0004\bD\u0010EJ\u0010\u0010F\u001a\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\bF\u0010GJ\u0010\u0010H\u001a\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\bH\u0010GJ\u0010\u0010I\u001a\u00020\u0006H\u00c6\u0003\u00a2\u0006\u0004\bI\u0010GJ\u0010\u0010J\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bJ\u0010KJ\u0010\u0010L\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bL\u0010KJ\u0010\u0010M\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bM\u0010KJ\u0010\u0010N\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bN\u0010KJ\u0010\u0010O\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bO\u0010KJ\u0010\u0010P\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bP\u0010KJ\u0010\u0010Q\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bQ\u0010KJ\u0010\u0010R\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bR\u0010KJ\u0010\u0010S\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bS\u0010KJ\u0010\u0010T\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bT\u0010KJ\u0010\u0010U\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bU\u0010KJ\u0010\u0010V\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bV\u0010KJ\u0010\u0010W\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bW\u0010KJ\u0010\u0010X\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bX\u0010KJ\u0010\u0010Y\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bY\u0010KJ\u0010\u0010Z\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bZ\u0010KJ\u0010\u0010[\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\b[\u0010KJ\u0010\u0010\\\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\b\\\u0010KJ\u0010\u0010]\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\b]\u0010KJ\u0010\u0010^\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\b^\u0010KJ\u0010\u0010_\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\b_\u0010KJ\u0010\u0010`\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\b`\u0010KJ\u0010\u0010a\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\ba\u0010KJ\u0010\u0010b\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bb\u0010KJ\u0010\u0010c\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bc\u0010KJ\u0010\u0010d\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bd\u0010KJ\u0010\u0010e\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\be\u0010KJ\u0010\u0010f\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bf\u0010KJ\u0010\u0010g\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bg\u0010KJ\u0010\u0010h\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bh\u0010KJ\u0010\u0010i\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bi\u0010KJ\u0010\u0010j\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bj\u0010KJ\u0010\u0010k\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bk\u0010KJ\u0010\u0010l\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bl\u0010KJ\u0010\u0010m\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bm\u0010KJ\u0010\u0010n\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bn\u0010KJ\u0010\u0010o\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bo\u0010KJ\u0010\u0010p\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bp\u0010KJ\u0010\u0010q\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bq\u0010KJ\u0010\u0010r\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\br\u0010KJ\u0010\u0010s\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bs\u0010KJ\u0010\u0010t\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bt\u0010KJ\u0010\u0010u\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bu\u0010KJ\u0010\u0010v\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bv\u0010KJ\u0010\u0010w\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bw\u0010KJ\u0010\u0010x\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bx\u0010KJ\u0010\u0010y\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\by\u0010KJ\u0010\u0010z\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\bz\u0010KJ\u0010\u0010{\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\b{\u0010KJ\u0010\u0010|\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\b|\u0010KJ\u0010\u0010}\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\b}\u0010KJ\u0010\u0010~\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\b~\u0010KJ\u0010\u0010\u007f\u001a\u00020\nH\u00c6\u0003\u00a2\u0006\u0004\b\u007f\u0010KJ\u00d7\u0004\u0010\u0080\u0001\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00042\b\b\u0002\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\b\u001a\u00020\u00062\b\b\u0002\u0010\t\u001a\u00020\u00062\b\b\u0002\u0010\u000b\u001a\u00020\n2\b\b\u0002\u0010\f\u001a\u00020\n2\b\b\u0002\u0010\r\u001a\u00020\n2\b\b\u0002\u0010\u000e\u001a\u00020\n2\b\b\u0002\u0010\u000f\u001a\u00020\n2\b\b\u0002\u0010\u0010\u001a\u00020\n2\b\b\u0002\u0010\u0011\u001a\u00020\n2\b\b\u0002\u0010\u0012\u001a\u00020\n2\b\b\u0002\u0010\u0013\u001a\u00020\n2\b\b\u0002\u0010\u0014\u001a\u00020\n2\b\b\u0002\u0010\u0015\u001a\u00020\n2\b\b\u0002\u0010\u0016\u001a\u00020\n2\b\b\u0002\u0010\u0017\u001a\u00020\n2\b\b\u0002\u0010\u0018\u001a\u00020\n2\b\b\u0002\u0010\u0019\u001a\u00020\n2\b\b\u0002\u0010\u001a\u001a\u00020\n2\b\b\u0002\u0010\u001b\u001a\u00020\n2\b\b\u0002\u0010\u001c\u001a\u00020\n2\b\b\u0002\u0010\u001d\u001a\u00020\n2\b\b\u0002\u0010\u001e\u001a\u00020\n2\b\b\u0002\u0010\u001f\u001a\u00020\n2\b\b\u0002\u0010 \u001a\u00020\n2\b\b\u0002\u0010!\u001a\u00020\n2\b\b\u0002\u0010\"\u001a\u00020\n2\b\b\u0002\u0010#\u001a\u00020\n2\b\b\u0002\u0010$\u001a\u00020\n2\b\b\u0002\u0010%\u001a\u00020\n2\b\b\u0002\u0010&\u001a\u00020\n2\b\b\u0002\u0010'\u001a\u00020\n2\b\b\u0002\u0010(\u001a\u00020\n2\b\b\u0002\u0010)\u001a\u00020\n2\b\b\u0002\u0010*\u001a\u00020\n2\b\b\u0002\u0010+\u001a\u00020\n2\b\b\u0002\u0010,\u001a\u00020\n2\b\b\u0002\u0010-\u001a\u00020\n2\b\b\u0002\u0010.\u001a\u00020\n2\b\b\u0002\u0010/\u001a\u00020\n2\b\b\u0002\u00100\u001a\u00020\n2\b\b\u0002\u00101\u001a\u00020\n2\b\b\u0002\u00102\u001a\u00020\n2\b\b\u0002\u00103\u001a\u00020\n2\b\b\u0002\u00104\u001a\u00020\n2\b\b\u0002\u00105\u001a\u00020\n2\b\b\u0002\u00106\u001a\u00020\n2\b\b\u0002\u00107\u001a\u00020\n2\b\b\u0002\u00108\u001a\u00020\n2\b\b\u0002\u00109\u001a\u00020\n2\b\b\u0002\u0010:\u001a\u00020\n2\b\b\u0002\u0010;\u001a\u00020\n2\b\b\u0002\u0010<\u001a\u00020\n2\b\b\u0002\u0010=\u001a\u00020\n2\b\b\u0002\u0010>\u001a\u00020\n2\b\b\u0002\u0010?\u001a\u00020\nH\u00c6\u0001\u00a2\u0006\u0006\b\u0080\u0001\u0010\u0081\u0001J \u0010\u0084\u0001\u001a\u00020\u00042\n\u0010\u0083\u0001\u001a\u0005\u0018\u00010\u0082\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0006\b\u0084\u0001\u0010\u0085\u0001J\u0013\u0010\u0086\u0001\u001a\u00020\nH\u00d6\u0081\u0004\u00a2\u0006\u0005\b\u0086\u0001\u0010KJ\u0013\u0010\u0087\u0001\u001a\u00020\u0002H\u00d6\u0081\u0004\u00a2\u0006\u0005\b\u0087\u0001\u0010CR&\u0010\u0003\u001a\u00020\u00028\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\u0003\u0010\u0088\u0001\u001a\u0005\b\u0089\u0001\u0010C\"\u0006\b\u008a\u0001\u0010\u008b\u0001R&\u0010\u0005\u001a\u00020\u00048\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\u0005\u0010\u008c\u0001\u001a\u0005\b\u008d\u0001\u0010E\"\u0006\b\u008e\u0001\u0010\u008f\u0001R&\u0010\u0007\u001a\u00020\u00068\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\u0007\u0010\u0090\u0001\u001a\u0005\b\u0091\u0001\u0010G\"\u0006\b\u0092\u0001\u0010\u0093\u0001R&\u0010\b\u001a\u00020\u00068\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\b\u0010\u0090\u0001\u001a\u0005\b\u0094\u0001\u0010G\"\u0006\b\u0095\u0001\u0010\u0093\u0001R&\u0010\t\u001a\u00020\u00068\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\t\u0010\u0090\u0001\u001a\u0005\b\u0096\u0001\u0010G\"\u0006\b\u0097\u0001\u0010\u0093\u0001R&\u0010\u000b\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\u000b\u0010\u0098\u0001\u001a\u0005\b\u0099\u0001\u0010K\"\u0006\b\u009a\u0001\u0010\u009b\u0001R&\u0010\f\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\f\u0010\u0098\u0001\u001a\u0005\b\u009c\u0001\u0010K\"\u0006\b\u009d\u0001\u0010\u009b\u0001R&\u0010\r\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\r\u0010\u0098\u0001\u001a\u0005\b\u009e\u0001\u0010K\"\u0006\b\u009f\u0001\u0010\u009b\u0001R&\u0010\u000e\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\u000e\u0010\u0098\u0001\u001a\u0005\b\u00a0\u0001\u0010K\"\u0006\b\u00a1\u0001\u0010\u009b\u0001R&\u0010\u000f\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\u000f\u0010\u0098\u0001\u001a\u0005\b\u00a2\u0001\u0010K\"\u0006\b\u00a3\u0001\u0010\u009b\u0001R&\u0010\u0010\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\u0010\u0010\u0098\u0001\u001a\u0005\b\u00a4\u0001\u0010K\"\u0006\b\u00a5\u0001\u0010\u009b\u0001R&\u0010\u0011\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\u0011\u0010\u0098\u0001\u001a\u0005\b\u00a6\u0001\u0010K\"\u0006\b\u00a7\u0001\u0010\u009b\u0001R&\u0010\u0012\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\u0012\u0010\u0098\u0001\u001a\u0005\b\u00a8\u0001\u0010K\"\u0006\b\u00a9\u0001\u0010\u009b\u0001R&\u0010\u0013\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\u0013\u0010\u0098\u0001\u001a\u0005\b\u00aa\u0001\u0010K\"\u0006\b\u00ab\u0001\u0010\u009b\u0001R&\u0010\u0014\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\u0014\u0010\u0098\u0001\u001a\u0005\b\u00ac\u0001\u0010K\"\u0006\b\u00ad\u0001\u0010\u009b\u0001R&\u0010\u0015\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\u0015\u0010\u0098\u0001\u001a\u0005\b\u00ae\u0001\u0010K\"\u0006\b\u00af\u0001\u0010\u009b\u0001R&\u0010\u0016\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\u0016\u0010\u0098\u0001\u001a\u0005\b\u00b0\u0001\u0010K\"\u0006\b\u00b1\u0001\u0010\u009b\u0001R&\u0010\u0017\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\u0017\u0010\u0098\u0001\u001a\u0005\b\u00b2\u0001\u0010K\"\u0006\b\u00b3\u0001\u0010\u009b\u0001R&\u0010\u0018\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\u0018\u0010\u0098\u0001\u001a\u0005\b\u00b4\u0001\u0010K\"\u0006\b\u00b5\u0001\u0010\u009b\u0001R&\u0010\u0019\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\u0019\u0010\u0098\u0001\u001a\u0005\b\u00b6\u0001\u0010K\"\u0006\b\u00b7\u0001\u0010\u009b\u0001R&\u0010\u001a\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\u001a\u0010\u0098\u0001\u001a\u0005\b\u00b8\u0001\u0010K\"\u0006\b\u00b9\u0001\u0010\u009b\u0001R&\u0010\u001b\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\u001b\u0010\u0098\u0001\u001a\u0005\b\u00ba\u0001\u0010K\"\u0006\b\u00bb\u0001\u0010\u009b\u0001R&\u0010\u001c\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\u001c\u0010\u0098\u0001\u001a\u0005\b\u00bc\u0001\u0010K\"\u0006\b\u00bd\u0001\u0010\u009b\u0001R&\u0010\u001d\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\u001d\u0010\u0098\u0001\u001a\u0005\b\u00be\u0001\u0010K\"\u0006\b\u00bf\u0001\u0010\u009b\u0001R&\u0010\u001e\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\u001e\u0010\u0098\u0001\u001a\u0005\b\u00c0\u0001\u0010K\"\u0006\b\u00c1\u0001\u0010\u009b\u0001R&\u0010\u001f\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\u001f\u0010\u0098\u0001\u001a\u0005\b\u00c2\u0001\u0010K\"\u0006\b\u00c3\u0001\u0010\u009b\u0001R&\u0010 \u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b \u0010\u0098\u0001\u001a\u0005\b\u00c4\u0001\u0010K\"\u0006\b\u00c5\u0001\u0010\u009b\u0001R&\u0010!\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b!\u0010\u0098\u0001\u001a\u0005\b\u00c6\u0001\u0010K\"\u0006\b\u00c7\u0001\u0010\u009b\u0001R&\u0010\"\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b\"\u0010\u0098\u0001\u001a\u0005\b\u00c8\u0001\u0010K\"\u0006\b\u00c9\u0001\u0010\u009b\u0001R&\u0010#\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b#\u0010\u0098\u0001\u001a\u0005\b\u00ca\u0001\u0010K\"\u0006\b\u00cb\u0001\u0010\u009b\u0001R&\u0010$\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b$\u0010\u0098\u0001\u001a\u0005\b\u00cc\u0001\u0010K\"\u0006\b\u00cd\u0001\u0010\u009b\u0001R&\u0010%\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b%\u0010\u0098\u0001\u001a\u0005\b\u00ce\u0001\u0010K\"\u0006\b\u00cf\u0001\u0010\u009b\u0001R&\u0010&\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b&\u0010\u0098\u0001\u001a\u0005\b\u00d0\u0001\u0010K\"\u0006\b\u00d1\u0001\u0010\u009b\u0001R&\u0010'\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b'\u0010\u0098\u0001\u001a\u0005\b\u00d2\u0001\u0010K\"\u0006\b\u00d3\u0001\u0010\u009b\u0001R&\u0010(\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b(\u0010\u0098\u0001\u001a\u0005\b\u00d4\u0001\u0010K\"\u0006\b\u00d5\u0001\u0010\u009b\u0001R&\u0010)\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b)\u0010\u0098\u0001\u001a\u0005\b\u00d6\u0001\u0010K\"\u0006\b\u00d7\u0001\u0010\u009b\u0001R&\u0010*\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b*\u0010\u0098\u0001\u001a\u0005\b\u00d8\u0001\u0010K\"\u0006\b\u00d9\u0001\u0010\u009b\u0001R&\u0010+\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b+\u0010\u0098\u0001\u001a\u0005\b\u00da\u0001\u0010K\"\u0006\b\u00db\u0001\u0010\u009b\u0001R&\u0010,\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b,\u0010\u0098\u0001\u001a\u0005\b\u00dc\u0001\u0010K\"\u0006\b\u00dd\u0001\u0010\u009b\u0001R&\u0010-\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b-\u0010\u0098\u0001\u001a\u0005\b\u00de\u0001\u0010K\"\u0006\b\u00df\u0001\u0010\u009b\u0001R&\u0010.\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b.\u0010\u0098\u0001\u001a\u0005\b\u00e0\u0001\u0010K\"\u0006\b\u00e1\u0001\u0010\u009b\u0001R&\u0010/\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b/\u0010\u0098\u0001\u001a\u0005\b\u00e2\u0001\u0010K\"\u0006\b\u00e3\u0001\u0010\u009b\u0001R&\u00100\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b0\u0010\u0098\u0001\u001a\u0005\b\u00e4\u0001\u0010K\"\u0006\b\u00e5\u0001\u0010\u009b\u0001R&\u00101\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b1\u0010\u0098\u0001\u001a\u0005\b\u00e6\u0001\u0010K\"\u0006\b\u00e7\u0001\u0010\u009b\u0001R&\u00102\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b2\u0010\u0098\u0001\u001a\u0005\b\u00e8\u0001\u0010K\"\u0006\b\u00e9\u0001\u0010\u009b\u0001R&\u00103\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b3\u0010\u0098\u0001\u001a\u0005\b\u00ea\u0001\u0010K\"\u0006\b\u00eb\u0001\u0010\u009b\u0001R&\u00104\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b4\u0010\u0098\u0001\u001a\u0005\b\u00ec\u0001\u0010K\"\u0006\b\u00ed\u0001\u0010\u009b\u0001R&\u00105\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b5\u0010\u0098\u0001\u001a\u0005\b\u00ee\u0001\u0010K\"\u0006\b\u00ef\u0001\u0010\u009b\u0001R&\u00106\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b6\u0010\u0098\u0001\u001a\u0005\b\u00f0\u0001\u0010K\"\u0006\b\u00f1\u0001\u0010\u009b\u0001R&\u00107\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b7\u0010\u0098\u0001\u001a\u0005\b\u00f2\u0001\u0010K\"\u0006\b\u00f3\u0001\u0010\u009b\u0001R&\u00108\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b8\u0010\u0098\u0001\u001a\u0005\b\u00f4\u0001\u0010K\"\u0006\b\u00f5\u0001\u0010\u009b\u0001R&\u00109\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b9\u0010\u0098\u0001\u001a\u0005\b\u00f6\u0001\u0010K\"\u0006\b\u00f7\u0001\u0010\u009b\u0001R&\u0010:\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b:\u0010\u0098\u0001\u001a\u0005\b\u00f8\u0001\u0010K\"\u0006\b\u00f9\u0001\u0010\u009b\u0001R&\u0010;\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b;\u0010\u0098\u0001\u001a\u0005\b\u00fa\u0001\u0010K\"\u0006\b\u00fb\u0001\u0010\u009b\u0001R&\u0010<\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b<\u0010\u0098\u0001\u001a\u0005\b\u00fc\u0001\u0010K\"\u0006\b\u00fd\u0001\u0010\u009b\u0001R&\u0010=\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b=\u0010\u0098\u0001\u001a\u0005\b\u00fe\u0001\u0010K\"\u0006\b\u00ff\u0001\u0010\u009b\u0001R&\u0010>\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b>\u0010\u0098\u0001\u001a\u0005\b\u0080\u0002\u0010K\"\u0006\b\u0081\u0002\u0010\u009b\u0001R&\u0010?\u001a\u00020\n8\u0016@\u0016X\u0096\u000e\u00a2\u0006\u0016\n\u0005\b?\u0010\u0098\u0001\u001a\u0005\b\u0082\u0002\u0010K\"\u0006\b\u0083\u0002\u0010\u009b\u0001\u00a8\u0006\u0084\u0002"}, d2={"Lorg/cobalt/api/ui/theme/impl/CustomTheme;", "Lorg/cobalt/api/ui/theme/Theme;", "", "name", "", "rainbowEnabled", "", "rainbowSpeed", "rainbowSaturation", "rainbowBrightness", "", "background", "panel", "inset", "overlay", "text", "textPrimary", "textSecondary", "textDisabled", "textPlaceholder", "textOnAccent", "accent", "accentPrimary", "accentSecondary", "selection", "controlBg", "controlBorder", "inputBg", "inputBorder", "success", "warning", "error", "info", "scrollbarThumb", "scrollbarTrack", "sliderTrack", "sliderFill", "sliderThumb", "tooltipBackground", "tooltipBorder", "tooltipText", "notificationBackground", "notificationBorder", "notificationText", "notificationTextSecondary", "infoBackground", "infoBorder", "infoIcon", "warningBackground", "warningBorder", "warningIcon", "successBackground", "successBorder", "successIcon", "errorBackground", "errorBorder", "errorIcon", "selectionText", "searchPlaceholderText", "moduleDivider", "selectedOverlay", "white", "black", "transparent", "<init>", "(Ljava/lang/String;ZFFFIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII)V", "component1", "()Ljava/lang/String;", "component2", "()Z", "component3", "()F", "component4", "component5", "component6", "()I", "component7", "component8", "component9", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component20", "component21", "component22", "component23", "component24", "component25", "component26", "component27", "component28", "component29", "component30", "component31", "component32", "component33", "component34", "component35", "component36", "component37", "component38", "component39", "component40", "component41", "component42", "component43", "component44", "component45", "component46", "component47", "component48", "component49", "component50", "component51", "component52", "component53", "component54", "component55", "component56", "component57", "component58", "copy", "(Ljava/lang/String;ZFFFIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII)Lorg/cobalt/api/ui/theme/impl/CustomTheme;", "", "other", "equals", "(Ljava/lang/Object;)Z", "hashCode", "toString", "Ljava/lang/String;", "getName", "setName", "(Ljava/lang/String;)V", "Z", "getRainbowEnabled", "setRainbowEnabled", "(Z)V", "F", "getRainbowSpeed", "setRainbowSpeed", "(F)V", "getRainbowSaturation", "setRainbowSaturation", "getRainbowBrightness", "setRainbowBrightness", "I", "getBackground", "setBackground", "(I)V", "getPanel", "setPanel", "getInset", "setInset", "getOverlay", "setOverlay", "getText", "setText", "getTextPrimary", "setTextPrimary", "getTextSecondary", "setTextSecondary", "getTextDisabled", "setTextDisabled", "getTextPlaceholder", "setTextPlaceholder", "getTextOnAccent", "setTextOnAccent", "getAccent", "setAccent", "getAccentPrimary", "setAccentPrimary", "getAccentSecondary", "setAccentSecondary", "getSelection", "setSelection", "getControlBg", "setControlBg", "getControlBorder", "setControlBorder", "getInputBg", "setInputBg", "getInputBorder", "setInputBorder", "getSuccess", "setSuccess", "getWarning", "setWarning", "getError", "setError", "getInfo", "setInfo", "getScrollbarThumb", "setScrollbarThumb", "getScrollbarTrack", "setScrollbarTrack", "getSliderTrack", "setSliderTrack", "getSliderFill", "setSliderFill", "getSliderThumb", "setSliderThumb", "getTooltipBackground", "setTooltipBackground", "getTooltipBorder", "setTooltipBorder", "getTooltipText", "setTooltipText", "getNotificationBackground", "setNotificationBackground", "getNotificationBorder", "setNotificationBorder", "getNotificationText", "setNotificationText", "getNotificationTextSecondary", "setNotificationTextSecondary", "getInfoBackground", "setInfoBackground", "getInfoBorder", "setInfoBorder", "getInfoIcon", "setInfoIcon", "getWarningBackground", "setWarningBackground", "getWarningBorder", "setWarningBorder", "getWarningIcon", "setWarningIcon", "getSuccessBackground", "setSuccessBackground", "getSuccessBorder", "setSuccessBorder", "getSuccessIcon", "setSuccessIcon", "getErrorBackground", "setErrorBackground", "getErrorBorder", "setErrorBorder", "getErrorIcon", "setErrorIcon", "getSelectionText", "setSelectionText", "getSearchPlaceholderText", "setSearchPlaceholderText", "getModuleDivider", "setModuleDivider", "getSelectedOverlay", "setSelectedOverlay", "getWhite", "setWhite", "getBlack", "setBlack", "getTransparent", "setTransparent", "cobalt"})
public final class CustomTheme
implements Theme {
    @NotNull
    private String name;
    private boolean rainbowEnabled;
    private float rainbowSpeed;
    private float rainbowSaturation;
    private float rainbowBrightness;
    private int background;
    private int panel;
    private int inset;
    private int overlay;
    private int text;
    private int textPrimary;
    private int textSecondary;
    private int textDisabled;
    private int textPlaceholder;
    private int textOnAccent;
    private int accent;
    private int accentPrimary;
    private int accentSecondary;
    private int selection;
    private int controlBg;
    private int controlBorder;
    private int inputBg;
    private int inputBorder;
    private int success;
    private int warning;
    private int error;
    private int info;
    private int scrollbarThumb;
    private int scrollbarTrack;
    private int sliderTrack;
    private int sliderFill;
    private int sliderThumb;
    private int tooltipBackground;
    private int tooltipBorder;
    private int tooltipText;
    private int notificationBackground;
    private int notificationBorder;
    private int notificationText;
    private int notificationTextSecondary;
    private int infoBackground;
    private int infoBorder;
    private int infoIcon;
    private int warningBackground;
    private int warningBorder;
    private int warningIcon;
    private int successBackground;
    private int successBorder;
    private int successIcon;
    private int errorBackground;
    private int errorBorder;
    private int errorIcon;
    private int selectionText;
    private int searchPlaceholderText;
    private int moduleDivider;
    private int selectedOverlay;
    private int white;
    private int black;
    private int transparent;

    public CustomTheme(@NotNull String name, boolean rainbowEnabled, float rainbowSpeed, float rainbowSaturation, float rainbowBrightness, int background, int panel, int inset, int overlay, int text, int textPrimary, int textSecondary, int textDisabled, int textPlaceholder, int textOnAccent, int accent, int accentPrimary, int accentSecondary, int selection, int controlBg, int controlBorder, int inputBg, int inputBorder, int success, int warning, int error, int info, int scrollbarThumb, int scrollbarTrack, int sliderTrack, int sliderFill, int sliderThumb, int tooltipBackground, int tooltipBorder, int tooltipText, int notificationBackground, int notificationBorder, int notificationText, int notificationTextSecondary, int infoBackground, int infoBorder, int infoIcon, int warningBackground, int warningBorder, int warningIcon, int successBackground, int successBorder, int successIcon, int errorBackground, int errorBorder, int errorIcon, int selectionText, int searchPlaceholderText, int moduleDivider, int selectedOverlay, int white, int black, int transparent) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        this.name = name;
        this.rainbowEnabled = rainbowEnabled;
        this.rainbowSpeed = rainbowSpeed;
        this.rainbowSaturation = rainbowSaturation;
        this.rainbowBrightness = rainbowBrightness;
        this.background = background;
        this.panel = panel;
        this.inset = inset;
        this.overlay = overlay;
        this.text = text;
        this.textPrimary = textPrimary;
        this.textSecondary = textSecondary;
        this.textDisabled = textDisabled;
        this.textPlaceholder = textPlaceholder;
        this.textOnAccent = textOnAccent;
        this.accent = accent;
        this.accentPrimary = accentPrimary;
        this.accentSecondary = accentSecondary;
        this.selection = selection;
        this.controlBg = controlBg;
        this.controlBorder = controlBorder;
        this.inputBg = inputBg;
        this.inputBorder = inputBorder;
        this.success = success;
        this.warning = warning;
        this.error = error;
        this.info = info;
        this.scrollbarThumb = scrollbarThumb;
        this.scrollbarTrack = scrollbarTrack;
        this.sliderTrack = sliderTrack;
        this.sliderFill = sliderFill;
        this.sliderThumb = sliderThumb;
        this.tooltipBackground = tooltipBackground;
        this.tooltipBorder = tooltipBorder;
        this.tooltipText = tooltipText;
        this.notificationBackground = notificationBackground;
        this.notificationBorder = notificationBorder;
        this.notificationText = notificationText;
        this.notificationTextSecondary = notificationTextSecondary;
        this.infoBackground = infoBackground;
        this.infoBorder = infoBorder;
        this.infoIcon = infoIcon;
        this.warningBackground = warningBackground;
        this.warningBorder = warningBorder;
        this.warningIcon = warningIcon;
        this.successBackground = successBackground;
        this.successBorder = successBorder;
        this.successIcon = successIcon;
        this.errorBackground = errorBackground;
        this.errorBorder = errorBorder;
        this.errorIcon = errorIcon;
        this.selectionText = selectionText;
        this.searchPlaceholderText = searchPlaceholderText;
        this.moduleDivider = moduleDivider;
        this.selectedOverlay = selectedOverlay;
        this.white = white;
        this.black = black;
        this.transparent = transparent;
    }

    public /* synthetic */ CustomTheme(String string, boolean bl, float f, float f2, float f3, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9, int n10, int n11, int n12, int n13, int n14, int n15, int n16, int n17, int n18, int n19, int n20, int n21, int n22, int n23, int n24, int n25, int n26, int n27, int n28, int n29, int n30, int n31, int n32, int n33, int n34, int n35, int n36, int n37, int n38, int n39, int n40, int n41, int n42, int n43, int n44, int n45, int n46, int n47, int n48, int n49, int n50, int n51, int n52, int n53, int n54, int n55, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n54 & 1) != 0) {
            string = "Custom";
        }
        if ((n54 & 2) != 0) {
            bl = false;
        }
        if ((n54 & 4) != 0) {
            f = 1.0f;
        }
        if ((n54 & 8) != 0) {
            f2 = 1.0f;
        }
        if ((n54 & 0x10) != 0) {
            f3 = 1.0f;
        }
        if ((n54 & 0x20) != 0) {
            n = new Color(18, 18, 18).getRGB();
        }
        if ((n54 & 0x40) != 0) {
            n2 = new Color(24, 24, 24).getRGB();
        }
        if ((n54 & 0x80) != 0) {
            n3 = new Color(30, 30, 30).getRGB();
        }
        if ((n54 & 0x100) != 0) {
            n4 = new Color(18, 18, 18, 230).getRGB();
        }
        if ((n54 & 0x200) != 0) {
            n5 = new Color(230, 230, 230).getRGB();
        }
        if ((n54 & 0x400) != 0) {
            n6 = new Color(245, 245, 245).getRGB();
        }
        if ((n54 & 0x800) != 0) {
            n7 = new Color(179, 179, 179).getRGB();
        }
        if ((n54 & 0x1000) != 0) {
            n8 = new Color(120, 120, 120).getRGB();
        }
        if ((n54 & 0x2000) != 0) {
            n9 = new Color(128, 128, 128).getRGB();
        }
        if ((n54 & 0x4000) != 0) {
            n10 = new Color(245, 245, 245).getRGB();
        }
        if ((n54 & 0x8000) != 0) {
            n11 = new Color(61, 94, 149).getRGB();
        }
        if ((n54 & 0x10000) != 0) {
            n12 = new Color(53, 85, 139).getRGB();
        }
        if ((n54 & 0x20000) != 0) {
            n13 = new Color(86, 116, 170).getRGB();
        }
        if ((n54 & 0x40000) != 0) {
            n14 = new Color(70, 130, 180, 100).getRGB();
        }
        if ((n54 & 0x80000) != 0) {
            n15 = new Color(42, 42, 42, 50).getRGB();
        }
        if ((n54 & 0x100000) != 0) {
            n16 = new Color(42, 42, 42).getRGB();
        }
        if ((n54 & 0x200000) != 0) {
            n17 = new Color(42, 42, 42, 50).getRGB();
        }
        if ((n54 & 0x400000) != 0) {
            n18 = new Color(42, 42, 42).getRGB();
        }
        if ((n54 & 0x800000) != 0) {
            n19 = new Color(34, 139, 34).getRGB();
        }
        if ((n54 & 0x1000000) != 0) {
            n20 = new Color(184, 134, 11).getRGB();
        }
        if ((n54 & 0x2000000) != 0) {
            n21 = new Color(178, 34, 34).getRGB();
        }
        if ((n54 & 0x4000000) != 0) {
            n22 = new Color(61, 94, 149).getRGB();
        }
        if ((n54 & 0x8000000) != 0) {
            n23 = new Color(61, 94, 149).getRGB();
        }
        if ((n54 & 0x10000000) != 0) {
            n24 = new Color(32, 32, 32).getRGB();
        }
        if ((n54 & 0x20000000) != 0) {
            n25 = new Color(60, 60, 60).getRGB();
        }
        if ((n54 & 0x40000000) != 0) {
            n26 = new Color(61, 94, 149).getRGB();
        }
        if ((n54 & Integer.MIN_VALUE) != 0) {
            n27 = new Color(61, 94, 149).getRGB();
        }
        if ((n55 & 1) != 0) {
            n28 = new Color(18, 18, 18, 240).getRGB();
        }
        if ((n55 & 2) != 0) {
            n29 = new Color(42, 42, 42).getRGB();
        }
        if ((n55 & 4) != 0) {
            n30 = new Color(230, 230, 230).getRGB();
        }
        if ((n55 & 8) != 0) {
            n31 = new Color(25, 25, 25).getRGB();
        }
        if ((n55 & 0x10) != 0) {
            n32 = new Color(61, 94, 149).getRGB();
        }
        if ((n55 & 0x20) != 0) {
            n33 = new Color(230, 230, 230).getRGB();
        }
        if ((n55 & 0x40) != 0) {
            n34 = new Color(179, 179, 179).getRGB();
        }
        if ((n55 & 0x80) != 0) {
            n35 = new Color(61, 94, 149, 25).getRGB();
        }
        if ((n55 & 0x100) != 0) {
            n36 = new Color(61, 94, 149, 150).getRGB();
        }
        if ((n55 & 0x200) != 0) {
            n37 = new Color(61, 94, 149, 255).getRGB();
        }
        if ((n55 & 0x400) != 0) {
            n38 = new Color(184, 134, 11, 25).getRGB();
        }
        if ((n55 & 0x800) != 0) {
            n39 = new Color(184, 134, 11, 150).getRGB();
        }
        if ((n55 & 0x1000) != 0) {
            n40 = new Color(184, 134, 11, 255).getRGB();
        }
        if ((n55 & 0x2000) != 0) {
            n41 = new Color(34, 139, 34, 25).getRGB();
        }
        if ((n55 & 0x4000) != 0) {
            n42 = new Color(34, 139, 34, 150).getRGB();
        }
        if ((n55 & 0x8000) != 0) {
            n43 = new Color(34, 139, 34, 255).getRGB();
        }
        if ((n55 & 0x10000) != 0) {
            n44 = new Color(178, 34, 34, 25).getRGB();
        }
        if ((n55 & 0x20000) != 0) {
            n45 = new Color(178, 34, 34, 150).getRGB();
        }
        if ((n55 & 0x40000) != 0) {
            n46 = new Color(178, 34, 34, 255).getRGB();
        }
        if ((n55 & 0x80000) != 0) {
            n47 = new Color(245, 245, 245).getRGB();
        }
        if ((n55 & 0x100000) != 0) {
            n48 = new Color(128, 128, 128).getRGB();
        }
        if ((n55 & 0x200000) != 0) {
            n49 = new Color(42, 42, 42).getRGB();
        }
        if ((n55 & 0x400000) != 0) {
            n50 = new Color(61, 94, 149, 50).getRGB();
        }
        if ((n55 & 0x800000) != 0) {
            n51 = new Color(255, 255, 255).getRGB();
        }
        if ((n55 & 0x1000000) != 0) {
            n52 = new Color(0, 0, 0).getRGB();
        }
        if ((n55 & 0x2000000) != 0) {
            n53 = new Color(0, 0, 0, 0).getRGB();
        }
        this(string, bl, f, f2, f3, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20, n21, n22, n23, n24, n25, n26, n27, n28, n29, n30, n31, n32, n33, n34, n35, n36, n37, n38, n39, n40, n41, n42, n43, n44, n45, n46, n47, n48, n49, n50, n51, n52, n53);
    }

    @Override
    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        this.name = string;
    }

    @Override
    public boolean getRainbowEnabled() {
        return this.rainbowEnabled;
    }

    public void setRainbowEnabled(boolean bl) {
        this.rainbowEnabled = bl;
    }

    @Override
    public float getRainbowSpeed() {
        return this.rainbowSpeed;
    }

    public void setRainbowSpeed(float f) {
        this.rainbowSpeed = f;
    }

    @Override
    public float getRainbowSaturation() {
        return this.rainbowSaturation;
    }

    public void setRainbowSaturation(float f) {
        this.rainbowSaturation = f;
    }

    @Override
    public float getRainbowBrightness() {
        return this.rainbowBrightness;
    }

    public void setRainbowBrightness(float f) {
        this.rainbowBrightness = f;
    }

    @Override
    public int getBackground() {
        return this.background;
    }

    public void setBackground(int n) {
        this.background = n;
    }

    @Override
    public int getPanel() {
        return this.panel;
    }

    public void setPanel(int n) {
        this.panel = n;
    }

    @Override
    public int getInset() {
        return this.inset;
    }

    public void setInset(int n) {
        this.inset = n;
    }

    @Override
    public int getOverlay() {
        return this.overlay;
    }

    public void setOverlay(int n) {
        this.overlay = n;
    }

    @Override
    public int getText() {
        return this.text;
    }

    public void setText(int n) {
        this.text = n;
    }

    @Override
    public int getTextPrimary() {
        return this.textPrimary;
    }

    public void setTextPrimary(int n) {
        this.textPrimary = n;
    }

    @Override
    public int getTextSecondary() {
        return this.textSecondary;
    }

    public void setTextSecondary(int n) {
        this.textSecondary = n;
    }

    @Override
    public int getTextDisabled() {
        return this.textDisabled;
    }

    public void setTextDisabled(int n) {
        this.textDisabled = n;
    }

    @Override
    public int getTextPlaceholder() {
        return this.textPlaceholder;
    }

    public void setTextPlaceholder(int n) {
        this.textPlaceholder = n;
    }

    @Override
    public int getTextOnAccent() {
        return this.textOnAccent;
    }

    public void setTextOnAccent(int n) {
        this.textOnAccent = n;
    }

    @Override
    public int getAccent() {
        return this.accent;
    }

    public void setAccent(int n) {
        this.accent = n;
    }

    @Override
    public int getAccentPrimary() {
        return this.accentPrimary;
    }

    public void setAccentPrimary(int n) {
        this.accentPrimary = n;
    }

    @Override
    public int getAccentSecondary() {
        return this.accentSecondary;
    }

    public void setAccentSecondary(int n) {
        this.accentSecondary = n;
    }

    @Override
    public int getSelection() {
        return this.selection;
    }

    public void setSelection(int n) {
        this.selection = n;
    }

    @Override
    public int getControlBg() {
        return this.controlBg;
    }

    public void setControlBg(int n) {
        this.controlBg = n;
    }

    @Override
    public int getControlBorder() {
        return this.controlBorder;
    }

    public void setControlBorder(int n) {
        this.controlBorder = n;
    }

    @Override
    public int getInputBg() {
        return this.inputBg;
    }

    public void setInputBg(int n) {
        this.inputBg = n;
    }

    @Override
    public int getInputBorder() {
        return this.inputBorder;
    }

    public void setInputBorder(int n) {
        this.inputBorder = n;
    }

    @Override
    public int getSuccess() {
        return this.success;
    }

    public void setSuccess(int n) {
        this.success = n;
    }

    @Override
    public int getWarning() {
        return this.warning;
    }

    public void setWarning(int n) {
        this.warning = n;
    }

    @Override
    public int getError() {
        return this.error;
    }

    public void setError(int n) {
        this.error = n;
    }

    @Override
    public int getInfo() {
        return this.info;
    }

    public void setInfo(int n) {
        this.info = n;
    }

    @Override
    public int getScrollbarThumb() {
        return this.scrollbarThumb;
    }

    public void setScrollbarThumb(int n) {
        this.scrollbarThumb = n;
    }

    @Override
    public int getScrollbarTrack() {
        return this.scrollbarTrack;
    }

    public void setScrollbarTrack(int n) {
        this.scrollbarTrack = n;
    }

    @Override
    public int getSliderTrack() {
        return this.sliderTrack;
    }

    public void setSliderTrack(int n) {
        this.sliderTrack = n;
    }

    @Override
    public int getSliderFill() {
        return this.sliderFill;
    }

    public void setSliderFill(int n) {
        this.sliderFill = n;
    }

    @Override
    public int getSliderThumb() {
        return this.sliderThumb;
    }

    public void setSliderThumb(int n) {
        this.sliderThumb = n;
    }

    @Override
    public int getTooltipBackground() {
        return this.tooltipBackground;
    }

    public void setTooltipBackground(int n) {
        this.tooltipBackground = n;
    }

    @Override
    public int getTooltipBorder() {
        return this.tooltipBorder;
    }

    public void setTooltipBorder(int n) {
        this.tooltipBorder = n;
    }

    @Override
    public int getTooltipText() {
        return this.tooltipText;
    }

    public void setTooltipText(int n) {
        this.tooltipText = n;
    }

    @Override
    public int getNotificationBackground() {
        return this.notificationBackground;
    }

    public void setNotificationBackground(int n) {
        this.notificationBackground = n;
    }

    @Override
    public int getNotificationBorder() {
        return this.notificationBorder;
    }

    public void setNotificationBorder(int n) {
        this.notificationBorder = n;
    }

    @Override
    public int getNotificationText() {
        return this.notificationText;
    }

    public void setNotificationText(int n) {
        this.notificationText = n;
    }

    @Override
    public int getNotificationTextSecondary() {
        return this.notificationTextSecondary;
    }

    public void setNotificationTextSecondary(int n) {
        this.notificationTextSecondary = n;
    }

    @Override
    public int getInfoBackground() {
        return this.infoBackground;
    }

    public void setInfoBackground(int n) {
        this.infoBackground = n;
    }

    @Override
    public int getInfoBorder() {
        return this.infoBorder;
    }

    public void setInfoBorder(int n) {
        this.infoBorder = n;
    }

    @Override
    public int getInfoIcon() {
        return this.infoIcon;
    }

    public void setInfoIcon(int n) {
        this.infoIcon = n;
    }

    @Override
    public int getWarningBackground() {
        return this.warningBackground;
    }

    public void setWarningBackground(int n) {
        this.warningBackground = n;
    }

    @Override
    public int getWarningBorder() {
        return this.warningBorder;
    }

    public void setWarningBorder(int n) {
        this.warningBorder = n;
    }

    @Override
    public int getWarningIcon() {
        return this.warningIcon;
    }

    public void setWarningIcon(int n) {
        this.warningIcon = n;
    }

    @Override
    public int getSuccessBackground() {
        return this.successBackground;
    }

    public void setSuccessBackground(int n) {
        this.successBackground = n;
    }

    @Override
    public int getSuccessBorder() {
        return this.successBorder;
    }

    public void setSuccessBorder(int n) {
        this.successBorder = n;
    }

    @Override
    public int getSuccessIcon() {
        return this.successIcon;
    }

    public void setSuccessIcon(int n) {
        this.successIcon = n;
    }

    @Override
    public int getErrorBackground() {
        return this.errorBackground;
    }

    public void setErrorBackground(int n) {
        this.errorBackground = n;
    }

    @Override
    public int getErrorBorder() {
        return this.errorBorder;
    }

    public void setErrorBorder(int n) {
        this.errorBorder = n;
    }

    @Override
    public int getErrorIcon() {
        return this.errorIcon;
    }

    public void setErrorIcon(int n) {
        this.errorIcon = n;
    }

    @Override
    public int getSelectionText() {
        return this.selectionText;
    }

    public void setSelectionText(int n) {
        this.selectionText = n;
    }

    @Override
    public int getSearchPlaceholderText() {
        return this.searchPlaceholderText;
    }

    public void setSearchPlaceholderText(int n) {
        this.searchPlaceholderText = n;
    }

    @Override
    public int getModuleDivider() {
        return this.moduleDivider;
    }

    public void setModuleDivider(int n) {
        this.moduleDivider = n;
    }

    @Override
    public int getSelectedOverlay() {
        return this.selectedOverlay;
    }

    public void setSelectedOverlay(int n) {
        this.selectedOverlay = n;
    }

    @Override
    public int getWhite() {
        return this.white;
    }

    public void setWhite(int n) {
        this.white = n;
    }

    @Override
    public int getBlack() {
        return this.black;
    }

    public void setBlack(int n) {
        this.black = n;
    }

    @Override
    public int getTransparent() {
        return this.transparent;
    }

    public void setTransparent(int n) {
        this.transparent = n;
    }

    @NotNull
    public final String component1() {
        return this.name;
    }

    public final boolean component2() {
        return this.rainbowEnabled;
    }

    public final float component3() {
        return this.rainbowSpeed;
    }

    public final float component4() {
        return this.rainbowSaturation;
    }

    public final float component5() {
        return this.rainbowBrightness;
    }

    public final int component6() {
        return this.background;
    }

    public final int component7() {
        return this.panel;
    }

    public final int component8() {
        return this.inset;
    }

    public final int component9() {
        return this.overlay;
    }

    public final int component10() {
        return this.text;
    }

    public final int component11() {
        return this.textPrimary;
    }

    public final int component12() {
        return this.textSecondary;
    }

    public final int component13() {
        return this.textDisabled;
    }

    public final int component14() {
        return this.textPlaceholder;
    }

    public final int component15() {
        return this.textOnAccent;
    }

    public final int component16() {
        return this.accent;
    }

    public final int component17() {
        return this.accentPrimary;
    }

    public final int component18() {
        return this.accentSecondary;
    }

    public final int component19() {
        return this.selection;
    }

    public final int component20() {
        return this.controlBg;
    }

    public final int component21() {
        return this.controlBorder;
    }

    public final int component22() {
        return this.inputBg;
    }

    public final int component23() {
        return this.inputBorder;
    }

    public final int component24() {
        return this.success;
    }

    public final int component25() {
        return this.warning;
    }

    public final int component26() {
        return this.error;
    }

    public final int component27() {
        return this.info;
    }

    public final int component28() {
        return this.scrollbarThumb;
    }

    public final int component29() {
        return this.scrollbarTrack;
    }

    public final int component30() {
        return this.sliderTrack;
    }

    public final int component31() {
        return this.sliderFill;
    }

    public final int component32() {
        return this.sliderThumb;
    }

    public final int component33() {
        return this.tooltipBackground;
    }

    public final int component34() {
        return this.tooltipBorder;
    }

    public final int component35() {
        return this.tooltipText;
    }

    public final int component36() {
        return this.notificationBackground;
    }

    public final int component37() {
        return this.notificationBorder;
    }

    public final int component38() {
        return this.notificationText;
    }

    public final int component39() {
        return this.notificationTextSecondary;
    }

    public final int component40() {
        return this.infoBackground;
    }

    public final int component41() {
        return this.infoBorder;
    }

    public final int component42() {
        return this.infoIcon;
    }

    public final int component43() {
        return this.warningBackground;
    }

    public final int component44() {
        return this.warningBorder;
    }

    public final int component45() {
        return this.warningIcon;
    }

    public final int component46() {
        return this.successBackground;
    }

    public final int component47() {
        return this.successBorder;
    }

    public final int component48() {
        return this.successIcon;
    }

    public final int component49() {
        return this.errorBackground;
    }

    public final int component50() {
        return this.errorBorder;
    }

    public final int component51() {
        return this.errorIcon;
    }

    public final int component52() {
        return this.selectionText;
    }

    public final int component53() {
        return this.searchPlaceholderText;
    }

    public final int component54() {
        return this.moduleDivider;
    }

    public final int component55() {
        return this.selectedOverlay;
    }

    public final int component56() {
        return this.white;
    }

    public final int component57() {
        return this.black;
    }

    public final int component58() {
        return this.transparent;
    }

    @NotNull
    public final CustomTheme copy(@NotNull String name, boolean rainbowEnabled, float rainbowSpeed, float rainbowSaturation, float rainbowBrightness, int background, int panel, int inset, int overlay, int text, int textPrimary, int textSecondary, int textDisabled, int textPlaceholder, int textOnAccent, int accent, int accentPrimary, int accentSecondary, int selection, int controlBg, int controlBorder, int inputBg, int inputBorder, int success, int warning, int error, int info, int scrollbarThumb, int scrollbarTrack, int sliderTrack, int sliderFill, int sliderThumb, int tooltipBackground, int tooltipBorder, int tooltipText, int notificationBackground, int notificationBorder, int notificationText, int notificationTextSecondary, int infoBackground, int infoBorder, int infoIcon, int warningBackground, int warningBorder, int warningIcon, int successBackground, int successBorder, int successIcon, int errorBackground, int errorBorder, int errorIcon, int selectionText, int searchPlaceholderText, int moduleDivider, int selectedOverlay, int white, int black, int transparent) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        return new CustomTheme(name, rainbowEnabled, rainbowSpeed, rainbowSaturation, rainbowBrightness, background, panel, inset, overlay, text, textPrimary, textSecondary, textDisabled, textPlaceholder, textOnAccent, accent, accentPrimary, accentSecondary, selection, controlBg, controlBorder, inputBg, inputBorder, success, warning, error, info, scrollbarThumb, scrollbarTrack, sliderTrack, sliderFill, sliderThumb, tooltipBackground, tooltipBorder, tooltipText, notificationBackground, notificationBorder, notificationText, notificationTextSecondary, infoBackground, infoBorder, infoIcon, warningBackground, warningBorder, warningIcon, successBackground, successBorder, successIcon, errorBackground, errorBorder, errorIcon, selectionText, searchPlaceholderText, moduleDivider, selectedOverlay, white, black, transparent);
    }

    public static /* synthetic */ CustomTheme copy$default(CustomTheme customTheme, String string, boolean bl, float f, float f2, float f3, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9, int n10, int n11, int n12, int n13, int n14, int n15, int n16, int n17, int n18, int n19, int n20, int n21, int n22, int n23, int n24, int n25, int n26, int n27, int n28, int n29, int n30, int n31, int n32, int n33, int n34, int n35, int n36, int n37, int n38, int n39, int n40, int n41, int n42, int n43, int n44, int n45, int n46, int n47, int n48, int n49, int n50, int n51, int n52, int n53, int n54, int n55, Object object) {
        if ((n54 & 1) != 0) {
            string = customTheme.name;
        }
        if ((n54 & 2) != 0) {
            bl = customTheme.rainbowEnabled;
        }
        if ((n54 & 4) != 0) {
            f = customTheme.rainbowSpeed;
        }
        if ((n54 & 8) != 0) {
            f2 = customTheme.rainbowSaturation;
        }
        if ((n54 & 0x10) != 0) {
            f3 = customTheme.rainbowBrightness;
        }
        if ((n54 & 0x20) != 0) {
            n = customTheme.background;
        }
        if ((n54 & 0x40) != 0) {
            n2 = customTheme.panel;
        }
        if ((n54 & 0x80) != 0) {
            n3 = customTheme.inset;
        }
        if ((n54 & 0x100) != 0) {
            n4 = customTheme.overlay;
        }
        if ((n54 & 0x200) != 0) {
            n5 = customTheme.text;
        }
        if ((n54 & 0x400) != 0) {
            n6 = customTheme.textPrimary;
        }
        if ((n54 & 0x800) != 0) {
            n7 = customTheme.textSecondary;
        }
        if ((n54 & 0x1000) != 0) {
            n8 = customTheme.textDisabled;
        }
        if ((n54 & 0x2000) != 0) {
            n9 = customTheme.textPlaceholder;
        }
        if ((n54 & 0x4000) != 0) {
            n10 = customTheme.textOnAccent;
        }
        if ((n54 & 0x8000) != 0) {
            n11 = customTheme.accent;
        }
        if ((n54 & 0x10000) != 0) {
            n12 = customTheme.accentPrimary;
        }
        if ((n54 & 0x20000) != 0) {
            n13 = customTheme.accentSecondary;
        }
        if ((n54 & 0x40000) != 0) {
            n14 = customTheme.selection;
        }
        if ((n54 & 0x80000) != 0) {
            n15 = customTheme.controlBg;
        }
        if ((n54 & 0x100000) != 0) {
            n16 = customTheme.controlBorder;
        }
        if ((n54 & 0x200000) != 0) {
            n17 = customTheme.inputBg;
        }
        if ((n54 & 0x400000) != 0) {
            n18 = customTheme.inputBorder;
        }
        if ((n54 & 0x800000) != 0) {
            n19 = customTheme.success;
        }
        if ((n54 & 0x1000000) != 0) {
            n20 = customTheme.warning;
        }
        if ((n54 & 0x2000000) != 0) {
            n21 = customTheme.error;
        }
        if ((n54 & 0x4000000) != 0) {
            n22 = customTheme.info;
        }
        if ((n54 & 0x8000000) != 0) {
            n23 = customTheme.scrollbarThumb;
        }
        if ((n54 & 0x10000000) != 0) {
            n24 = customTheme.scrollbarTrack;
        }
        if ((n54 & 0x20000000) != 0) {
            n25 = customTheme.sliderTrack;
        }
        if ((n54 & 0x40000000) != 0) {
            n26 = customTheme.sliderFill;
        }
        if ((n54 & Integer.MIN_VALUE) != 0) {
            n27 = customTheme.sliderThumb;
        }
        if ((n55 & 1) != 0) {
            n28 = customTheme.tooltipBackground;
        }
        if ((n55 & 2) != 0) {
            n29 = customTheme.tooltipBorder;
        }
        if ((n55 & 4) != 0) {
            n30 = customTheme.tooltipText;
        }
        if ((n55 & 8) != 0) {
            n31 = customTheme.notificationBackground;
        }
        if ((n55 & 0x10) != 0) {
            n32 = customTheme.notificationBorder;
        }
        if ((n55 & 0x20) != 0) {
            n33 = customTheme.notificationText;
        }
        if ((n55 & 0x40) != 0) {
            n34 = customTheme.notificationTextSecondary;
        }
        if ((n55 & 0x80) != 0) {
            n35 = customTheme.infoBackground;
        }
        if ((n55 & 0x100) != 0) {
            n36 = customTheme.infoBorder;
        }
        if ((n55 & 0x200) != 0) {
            n37 = customTheme.infoIcon;
        }
        if ((n55 & 0x400) != 0) {
            n38 = customTheme.warningBackground;
        }
        if ((n55 & 0x800) != 0) {
            n39 = customTheme.warningBorder;
        }
        if ((n55 & 0x1000) != 0) {
            n40 = customTheme.warningIcon;
        }
        if ((n55 & 0x2000) != 0) {
            n41 = customTheme.successBackground;
        }
        if ((n55 & 0x4000) != 0) {
            n42 = customTheme.successBorder;
        }
        if ((n55 & 0x8000) != 0) {
            n43 = customTheme.successIcon;
        }
        if ((n55 & 0x10000) != 0) {
            n44 = customTheme.errorBackground;
        }
        if ((n55 & 0x20000) != 0) {
            n45 = customTheme.errorBorder;
        }
        if ((n55 & 0x40000) != 0) {
            n46 = customTheme.errorIcon;
        }
        if ((n55 & 0x80000) != 0) {
            n47 = customTheme.selectionText;
        }
        if ((n55 & 0x100000) != 0) {
            n48 = customTheme.searchPlaceholderText;
        }
        if ((n55 & 0x200000) != 0) {
            n49 = customTheme.moduleDivider;
        }
        if ((n55 & 0x400000) != 0) {
            n50 = customTheme.selectedOverlay;
        }
        if ((n55 & 0x800000) != 0) {
            n51 = customTheme.white;
        }
        if ((n55 & 0x1000000) != 0) {
            n52 = customTheme.black;
        }
        if ((n55 & 0x2000000) != 0) {
            n53 = customTheme.transparent;
        }
        return customTheme.copy(string, bl, f, f2, f3, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20, n21, n22, n23, n24, n25, n26, n27, n28, n29, n30, n31, n32, n33, n34, n35, n36, n37, n38, n39, n40, n41, n42, n43, n44, n45, n46, n47, n48, n49, n50, n51, n52, n53);
    }

    @NotNull
    public String toString() {
        return "CustomTheme(name=" + this.name + ", rainbowEnabled=" + this.rainbowEnabled + ", rainbowSpeed=" + this.rainbowSpeed + ", rainbowSaturation=" + this.rainbowSaturation + ", rainbowBrightness=" + this.rainbowBrightness + ", background=" + this.background + ", panel=" + this.panel + ", inset=" + this.inset + ", overlay=" + this.overlay + ", text=" + this.text + ", textPrimary=" + this.textPrimary + ", textSecondary=" + this.textSecondary + ", textDisabled=" + this.textDisabled + ", textPlaceholder=" + this.textPlaceholder + ", textOnAccent=" + this.textOnAccent + ", accent=" + this.accent + ", accentPrimary=" + this.accentPrimary + ", accentSecondary=" + this.accentSecondary + ", selection=" + this.selection + ", controlBg=" + this.controlBg + ", controlBorder=" + this.controlBorder + ", inputBg=" + this.inputBg + ", inputBorder=" + this.inputBorder + ", success=" + this.success + ", warning=" + this.warning + ", error=" + this.error + ", info=" + this.info + ", scrollbarThumb=" + this.scrollbarThumb + ", scrollbarTrack=" + this.scrollbarTrack + ", sliderTrack=" + this.sliderTrack + ", sliderFill=" + this.sliderFill + ", sliderThumb=" + this.sliderThumb + ", tooltipBackground=" + this.tooltipBackground + ", tooltipBorder=" + this.tooltipBorder + ", tooltipText=" + this.tooltipText + ", notificationBackground=" + this.notificationBackground + ", notificationBorder=" + this.notificationBorder + ", notificationText=" + this.notificationText + ", notificationTextSecondary=" + this.notificationTextSecondary + ", infoBackground=" + this.infoBackground + ", infoBorder=" + this.infoBorder + ", infoIcon=" + this.infoIcon + ", warningBackground=" + this.warningBackground + ", warningBorder=" + this.warningBorder + ", warningIcon=" + this.warningIcon + ", successBackground=" + this.successBackground + ", successBorder=" + this.successBorder + ", successIcon=" + this.successIcon + ", errorBackground=" + this.errorBackground + ", errorBorder=" + this.errorBorder + ", errorIcon=" + this.errorIcon + ", selectionText=" + this.selectionText + ", searchPlaceholderText=" + this.searchPlaceholderText + ", moduleDivider=" + this.moduleDivider + ", selectedOverlay=" + this.selectedOverlay + ", white=" + this.white + ", black=" + this.black + ", transparent=" + this.transparent + ")";
    }

    public int hashCode() {
        int result = this.name.hashCode();
        result = result * 31 + Boolean.hashCode(this.rainbowEnabled);
        result = result * 31 + Float.hashCode(this.rainbowSpeed);
        result = result * 31 + Float.hashCode(this.rainbowSaturation);
        result = result * 31 + Float.hashCode(this.rainbowBrightness);
        result = result * 31 + Integer.hashCode(this.background);
        result = result * 31 + Integer.hashCode(this.panel);
        result = result * 31 + Integer.hashCode(this.inset);
        result = result * 31 + Integer.hashCode(this.overlay);
        result = result * 31 + Integer.hashCode(this.text);
        result = result * 31 + Integer.hashCode(this.textPrimary);
        result = result * 31 + Integer.hashCode(this.textSecondary);
        result = result * 31 + Integer.hashCode(this.textDisabled);
        result = result * 31 + Integer.hashCode(this.textPlaceholder);
        result = result * 31 + Integer.hashCode(this.textOnAccent);
        result = result * 31 + Integer.hashCode(this.accent);
        result = result * 31 + Integer.hashCode(this.accentPrimary);
        result = result * 31 + Integer.hashCode(this.accentSecondary);
        result = result * 31 + Integer.hashCode(this.selection);
        result = result * 31 + Integer.hashCode(this.controlBg);
        result = result * 31 + Integer.hashCode(this.controlBorder);
        result = result * 31 + Integer.hashCode(this.inputBg);
        result = result * 31 + Integer.hashCode(this.inputBorder);
        result = result * 31 + Integer.hashCode(this.success);
        result = result * 31 + Integer.hashCode(this.warning);
        result = result * 31 + Integer.hashCode(this.error);
        result = result * 31 + Integer.hashCode(this.info);
        result = result * 31 + Integer.hashCode(this.scrollbarThumb);
        result = result * 31 + Integer.hashCode(this.scrollbarTrack);
        result = result * 31 + Integer.hashCode(this.sliderTrack);
        result = result * 31 + Integer.hashCode(this.sliderFill);
        result = result * 31 + Integer.hashCode(this.sliderThumb);
        result = result * 31 + Integer.hashCode(this.tooltipBackground);
        result = result * 31 + Integer.hashCode(this.tooltipBorder);
        result = result * 31 + Integer.hashCode(this.tooltipText);
        result = result * 31 + Integer.hashCode(this.notificationBackground);
        result = result * 31 + Integer.hashCode(this.notificationBorder);
        result = result * 31 + Integer.hashCode(this.notificationText);
        result = result * 31 + Integer.hashCode(this.notificationTextSecondary);
        result = result * 31 + Integer.hashCode(this.infoBackground);
        result = result * 31 + Integer.hashCode(this.infoBorder);
        result = result * 31 + Integer.hashCode(this.infoIcon);
        result = result * 31 + Integer.hashCode(this.warningBackground);
        result = result * 31 + Integer.hashCode(this.warningBorder);
        result = result * 31 + Integer.hashCode(this.warningIcon);
        result = result * 31 + Integer.hashCode(this.successBackground);
        result = result * 31 + Integer.hashCode(this.successBorder);
        result = result * 31 + Integer.hashCode(this.successIcon);
        result = result * 31 + Integer.hashCode(this.errorBackground);
        result = result * 31 + Integer.hashCode(this.errorBorder);
        result = result * 31 + Integer.hashCode(this.errorIcon);
        result = result * 31 + Integer.hashCode(this.selectionText);
        result = result * 31 + Integer.hashCode(this.searchPlaceholderText);
        result = result * 31 + Integer.hashCode(this.moduleDivider);
        result = result * 31 + Integer.hashCode(this.selectedOverlay);
        result = result * 31 + Integer.hashCode(this.white);
        result = result * 31 + Integer.hashCode(this.black);
        result = result * 31 + Integer.hashCode(this.transparent);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CustomTheme)) {
            return false;
        }
        CustomTheme customTheme = (CustomTheme)other;
        if (!Intrinsics.areEqual((Object)this.name, (Object)customTheme.name)) {
            return false;
        }
        if (this.rainbowEnabled != customTheme.rainbowEnabled) {
            return false;
        }
        if (Float.compare(this.rainbowSpeed, customTheme.rainbowSpeed) != 0) {
            return false;
        }
        if (Float.compare(this.rainbowSaturation, customTheme.rainbowSaturation) != 0) {
            return false;
        }
        if (Float.compare(this.rainbowBrightness, customTheme.rainbowBrightness) != 0) {
            return false;
        }
        if (this.background != customTheme.background) {
            return false;
        }
        if (this.panel != customTheme.panel) {
            return false;
        }
        if (this.inset != customTheme.inset) {
            return false;
        }
        if (this.overlay != customTheme.overlay) {
            return false;
        }
        if (this.text != customTheme.text) {
            return false;
        }
        if (this.textPrimary != customTheme.textPrimary) {
            return false;
        }
        if (this.textSecondary != customTheme.textSecondary) {
            return false;
        }
        if (this.textDisabled != customTheme.textDisabled) {
            return false;
        }
        if (this.textPlaceholder != customTheme.textPlaceholder) {
            return false;
        }
        if (this.textOnAccent != customTheme.textOnAccent) {
            return false;
        }
        if (this.accent != customTheme.accent) {
            return false;
        }
        if (this.accentPrimary != customTheme.accentPrimary) {
            return false;
        }
        if (this.accentSecondary != customTheme.accentSecondary) {
            return false;
        }
        if (this.selection != customTheme.selection) {
            return false;
        }
        if (this.controlBg != customTheme.controlBg) {
            return false;
        }
        if (this.controlBorder != customTheme.controlBorder) {
            return false;
        }
        if (this.inputBg != customTheme.inputBg) {
            return false;
        }
        if (this.inputBorder != customTheme.inputBorder) {
            return false;
        }
        if (this.success != customTheme.success) {
            return false;
        }
        if (this.warning != customTheme.warning) {
            return false;
        }
        if (this.error != customTheme.error) {
            return false;
        }
        if (this.info != customTheme.info) {
            return false;
        }
        if (this.scrollbarThumb != customTheme.scrollbarThumb) {
            return false;
        }
        if (this.scrollbarTrack != customTheme.scrollbarTrack) {
            return false;
        }
        if (this.sliderTrack != customTheme.sliderTrack) {
            return false;
        }
        if (this.sliderFill != customTheme.sliderFill) {
            return false;
        }
        if (this.sliderThumb != customTheme.sliderThumb) {
            return false;
        }
        if (this.tooltipBackground != customTheme.tooltipBackground) {
            return false;
        }
        if (this.tooltipBorder != customTheme.tooltipBorder) {
            return false;
        }
        if (this.tooltipText != customTheme.tooltipText) {
            return false;
        }
        if (this.notificationBackground != customTheme.notificationBackground) {
            return false;
        }
        if (this.notificationBorder != customTheme.notificationBorder) {
            return false;
        }
        if (this.notificationText != customTheme.notificationText) {
            return false;
        }
        if (this.notificationTextSecondary != customTheme.notificationTextSecondary) {
            return false;
        }
        if (this.infoBackground != customTheme.infoBackground) {
            return false;
        }
        if (this.infoBorder != customTheme.infoBorder) {
            return false;
        }
        if (this.infoIcon != customTheme.infoIcon) {
            return false;
        }
        if (this.warningBackground != customTheme.warningBackground) {
            return false;
        }
        if (this.warningBorder != customTheme.warningBorder) {
            return false;
        }
        if (this.warningIcon != customTheme.warningIcon) {
            return false;
        }
        if (this.successBackground != customTheme.successBackground) {
            return false;
        }
        if (this.successBorder != customTheme.successBorder) {
            return false;
        }
        if (this.successIcon != customTheme.successIcon) {
            return false;
        }
        if (this.errorBackground != customTheme.errorBackground) {
            return false;
        }
        if (this.errorBorder != customTheme.errorBorder) {
            return false;
        }
        if (this.errorIcon != customTheme.errorIcon) {
            return false;
        }
        if (this.selectionText != customTheme.selectionText) {
            return false;
        }
        if (this.searchPlaceholderText != customTheme.searchPlaceholderText) {
            return false;
        }
        if (this.moduleDivider != customTheme.moduleDivider) {
            return false;
        }
        if (this.selectedOverlay != customTheme.selectedOverlay) {
            return false;
        }
        if (this.white != customTheme.white) {
            return false;
        }
        if (this.black != customTheme.black) {
            return false;
        }
        return this.transparent == customTheme.transparent;
    }

    public CustomTheme() {
        this(null, false, 0.0f, 0.0f, 0.0f, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0x3FFFFFF, null);
    }
}

