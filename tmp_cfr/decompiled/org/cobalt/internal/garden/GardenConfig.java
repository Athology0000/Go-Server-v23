/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.garden;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00005\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\t\n\u0002\b\t\n\u0002\u0010\u0006\n\u0003\b\u00af\u0001\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\"\u0010\u0005\u001a\u00020\u00048\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0005\u0010\u0006\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\"\u0010\u000b\u001a\u00020\u00048\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u000b\u0010\u0006\u001a\u0004\b\f\u0010\b\"\u0004\b\r\u0010\nR\"\u0010\u000e\u001a\u00020\u00048\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u000e\u0010\u0006\u001a\u0004\b\u000f\u0010\b\"\u0004\b\u0010\u0010\nR\"\u0010\u0011\u001a\u00020\u00048\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0011\u0010\u0006\u001a\u0004\b\u0012\u0010\b\"\u0004\b\u0013\u0010\nR\"\u0010\u0015\u001a\u00020\u00148\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0015\u0010\u0016\u001a\u0004\b\u0017\u0010\u0018\"\u0004\b\u0019\u0010\u001aR\"\u0010\u001c\u001a\u00020\u001b8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u001c\u0010\u001d\u001a\u0004\b\u001e\u0010\u001f\"\u0004\b \u0010!R\"\u0010\"\u001a\u00020\u00148\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\"\u0010\u0016\u001a\u0004\b#\u0010\u0018\"\u0004\b$\u0010\u001aR\"\u0010&\u001a\u00020%8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b&\u0010'\u001a\u0004\b(\u0010)\"\u0004\b*\u0010+R\"\u0010,\u001a\u00020\u00148\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b,\u0010\u0016\u001a\u0004\b-\u0010\u0018\"\u0004\b.\u0010\u001aR\"\u00100\u001a\u00020/8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b0\u00101\u001a\u0004\b2\u00103\"\u0004\b4\u00105R\"\u00106\u001a\u00020\u001b8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b6\u0010\u001d\u001a\u0004\b7\u0010\u001f\"\u0004\b8\u0010!R\"\u00109\u001a\u00020\u00148\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b9\u0010\u0016\u001a\u0004\b:\u0010\u0018\"\u0004\b;\u0010\u001aR\"\u0010<\u001a\u00020\u00148\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b<\u0010\u0016\u001a\u0004\b=\u0010\u0018\"\u0004\b>\u0010\u001aR\"\u0010?\u001a\u00020\u00148\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b?\u0010\u0016\u001a\u0004\b@\u0010\u0018\"\u0004\bA\u0010\u001aR\"\u0010B\u001a\u00020\u00048\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bB\u0010\u0006\u001a\u0004\bC\u0010\b\"\u0004\bD\u0010\nR\"\u0010E\u001a\u00020\u00148\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bE\u0010\u0016\u001a\u0004\bF\u0010\u0018\"\u0004\bG\u0010\u001aR\"\u0010H\u001a\u00020\u001b8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bH\u0010\u001d\u001a\u0004\bI\u0010\u001f\"\u0004\bJ\u0010!R\"\u0010K\u001a\u00020\u00148\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bK\u0010\u0016\u001a\u0004\bL\u0010\u0018\"\u0004\bM\u0010\u001aR\"\u0010N\u001a\u00020\u00148\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bN\u0010\u0016\u001a\u0004\bO\u0010\u0018\"\u0004\bP\u0010\u001aR\"\u0010Q\u001a\u00020\u00148\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bQ\u0010\u0016\u001a\u0004\bR\u0010\u0018\"\u0004\bS\u0010\u001aR\"\u0010T\u001a\u00020\u001b8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bT\u0010\u001d\u001a\u0004\bU\u0010\u001f\"\u0004\bV\u0010!R\"\u0010W\u001a\u00020\u001b8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bW\u0010\u001d\u001a\u0004\bX\u0010\u001f\"\u0004\bY\u0010!R\"\u0010Z\u001a\u00020\u001b8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bZ\u0010\u001d\u001a\u0004\b[\u0010\u001f\"\u0004\b\\\u0010!R\"\u0010]\u001a\u00020\u00148\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b]\u0010\u0016\u001a\u0004\b^\u0010\u0018\"\u0004\b_\u0010\u001aR\"\u0010`\u001a\u00020\u00048\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b`\u0010\u0006\u001a\u0004\ba\u0010\b\"\u0004\bb\u0010\nR\"\u0010c\u001a\u00020\u00048\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bc\u0010\u0006\u001a\u0004\bd\u0010\b\"\u0004\be\u0010\nR\"\u0010f\u001a\u00020\u00048\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bf\u0010\u0006\u001a\u0004\bg\u0010\b\"\u0004\bh\u0010\nR\"\u0010i\u001a\u00020%8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bi\u0010'\u001a\u0004\bj\u0010)\"\u0004\bk\u0010+R\"\u0010l\u001a\u00020\u00148\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bl\u0010\u0016\u001a\u0004\bm\u0010\u0018\"\u0004\bn\u0010\u001aR\"\u0010o\u001a\u00020\u00148\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bo\u0010\u0016\u001a\u0004\bp\u0010\u0018\"\u0004\bq\u0010\u001aR\"\u0010r\u001a\u00020\u00148\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\br\u0010\u0016\u001a\u0004\bs\u0010\u0018\"\u0004\bt\u0010\u001aR\"\u0010u\u001a\u00020%8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bu\u0010'\u001a\u0004\bv\u0010)\"\u0004\bw\u0010+R\"\u0010x\u001a\u00020\u00148\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bx\u0010\u0016\u001a\u0004\by\u0010\u0018\"\u0004\bz\u0010\u001aR\"\u0010{\u001a\u00020\u00048\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b{\u0010\u0006\u001a\u0004\b|\u0010\b\"\u0004\b}\u0010\nR#\u0010~\u001a\u00020\u001b8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0013\n\u0004\b~\u0010\u001d\u001a\u0004\b\u007f\u0010\u001f\"\u0005\b\u0080\u0001\u0010!R&\u0010\u0081\u0001\u001a\u00020\u00148\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u0081\u0001\u0010\u0016\u001a\u0005\b\u0082\u0001\u0010\u0018\"\u0005\b\u0083\u0001\u0010\u001aR&\u0010\u0084\u0001\u001a\u00020\u001b8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u0084\u0001\u0010\u001d\u001a\u0005\b\u0085\u0001\u0010\u001f\"\u0005\b\u0086\u0001\u0010!R&\u0010\u0087\u0001\u001a\u00020\u001b8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u0087\u0001\u0010\u001d\u001a\u0005\b\u0088\u0001\u0010\u001f\"\u0005\b\u0089\u0001\u0010!R&\u0010\u008a\u0001\u001a\u00020%8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u008a\u0001\u0010'\u001a\u0005\b\u008b\u0001\u0010)\"\u0005\b\u008c\u0001\u0010+R&\u0010\u008d\u0001\u001a\u00020\u00148\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u008d\u0001\u0010\u0016\u001a\u0005\b\u008e\u0001\u0010\u0018\"\u0005\b\u008f\u0001\u0010\u001aR&\u0010\u0090\u0001\u001a\u00020\u00148\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u0090\u0001\u0010\u0016\u001a\u0005\b\u0091\u0001\u0010\u0018\"\u0005\b\u0092\u0001\u0010\u001aR&\u0010\u0093\u0001\u001a\u00020\u00048\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u0093\u0001\u0010\u0006\u001a\u0005\b\u0094\u0001\u0010\b\"\u0005\b\u0095\u0001\u0010\nR&\u0010\u0096\u0001\u001a\u00020\u001b8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u0096\u0001\u0010\u001d\u001a\u0005\b\u0097\u0001\u0010\u001f\"\u0005\b\u0098\u0001\u0010!R&\u0010\u0099\u0001\u001a\u00020%8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u0099\u0001\u0010'\u001a\u0005\b\u009a\u0001\u0010)\"\u0005\b\u009b\u0001\u0010+R&\u0010\u009c\u0001\u001a\u00020\u00148\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u009c\u0001\u0010\u0016\u001a\u0005\b\u009d\u0001\u0010\u0018\"\u0005\b\u009e\u0001\u0010\u001aR&\u0010\u009f\u0001\u001a\u00020\u00048\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u009f\u0001\u0010\u0006\u001a\u0005\b\u00a0\u0001\u0010\b\"\u0005\b\u00a1\u0001\u0010\nR&\u0010\u00a2\u0001\u001a\u00020\u00148\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u00a2\u0001\u0010\u0016\u001a\u0005\b\u00a3\u0001\u0010\u0018\"\u0005\b\u00a4\u0001\u0010\u001aR&\u0010\u00a5\u0001\u001a\u00020%8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u00a5\u0001\u0010'\u001a\u0005\b\u00a6\u0001\u0010)\"\u0005\b\u00a7\u0001\u0010+R&\u0010\u00a8\u0001\u001a\u00020%8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u00a8\u0001\u0010'\u001a\u0005\b\u00a9\u0001\u0010)\"\u0005\b\u00aa\u0001\u0010+R&\u0010\u00ab\u0001\u001a\u00020%8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u00ab\u0001\u0010'\u001a\u0005\b\u00ac\u0001\u0010)\"\u0005\b\u00ad\u0001\u0010+R&\u0010\u00ae\u0001\u001a\u00020%8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u00ae\u0001\u0010'\u001a\u0005\b\u00af\u0001\u0010)\"\u0005\b\u00b0\u0001\u0010+R&\u0010\u00b1\u0001\u001a\u00020%8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u00b1\u0001\u0010'\u001a\u0005\b\u00b2\u0001\u0010)\"\u0005\b\u00b3\u0001\u0010+R&\u0010\u00b4\u0001\u001a\u00020\u00148\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u00b4\u0001\u0010\u0016\u001a\u0005\b\u00b5\u0001\u0010\u0018\"\u0005\b\u00b6\u0001\u0010\u001aR&\u0010\u00b7\u0001\u001a\u00020\u00148\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u00b7\u0001\u0010\u0016\u001a\u0005\b\u00b8\u0001\u0010\u0018\"\u0005\b\u00b9\u0001\u0010\u001aR&\u0010\u00ba\u0001\u001a\u00020\u00148\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u00ba\u0001\u0010\u0016\u001a\u0005\b\u00bb\u0001\u0010\u0018\"\u0005\b\u00bc\u0001\u0010\u001aR&\u0010\u00bd\u0001\u001a\u00020\u001b8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u00bd\u0001\u0010\u001d\u001a\u0005\b\u00be\u0001\u0010\u001f\"\u0005\b\u00bf\u0001\u0010!R&\u0010\u00c0\u0001\u001a\u00020%8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u00c0\u0001\u0010'\u001a\u0005\b\u00c1\u0001\u0010)\"\u0005\b\u00c2\u0001\u0010+R&\u0010\u00c3\u0001\u001a\u00020%8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u00c3\u0001\u0010'\u001a\u0005\b\u00c4\u0001\u0010)\"\u0005\b\u00c5\u0001\u0010+R&\u0010\u00c6\u0001\u001a\u00020%8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u00c6\u0001\u0010'\u001a\u0005\b\u00c7\u0001\u0010)\"\u0005\b\u00c8\u0001\u0010+R&\u0010\u00c9\u0001\u001a\u00020%8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u00c9\u0001\u0010'\u001a\u0005\b\u00ca\u0001\u0010)\"\u0005\b\u00cb\u0001\u0010+R&\u0010\u00cc\u0001\u001a\u00020%8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u00cc\u0001\u0010'\u001a\u0005\b\u00cd\u0001\u0010)\"\u0005\b\u00ce\u0001\u0010+R&\u0010\u00cf\u0001\u001a\u00020%8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u00cf\u0001\u0010'\u001a\u0005\b\u00d0\u0001\u0010)\"\u0005\b\u00d1\u0001\u0010+R&\u0010\u00d2\u0001\u001a\u00020\u00048\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u00d2\u0001\u0010\u0006\u001a\u0005\b\u00d3\u0001\u0010\b\"\u0005\b\u00d4\u0001\u0010\nR&\u0010\u00d5\u0001\u001a\u00020\u00048\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u00d5\u0001\u0010\u0006\u001a\u0005\b\u00d6\u0001\u0010\b\"\u0005\b\u00d7\u0001\u0010\nR&\u0010\u00d8\u0001\u001a\u00020\u00048\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u00d8\u0001\u0010\u0006\u001a\u0005\b\u00d9\u0001\u0010\b\"\u0005\b\u00da\u0001\u0010\nR&\u0010\u00db\u0001\u001a\u00020\u00148\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0015\n\u0005\b\u00db\u0001\u0010\u0016\u001a\u0005\b\u00dc\u0001\u0010\u0018\"\u0005\b\u00dd\u0001\u0010\u001a\u00a8\u0006\u00de\u0001"}, d2={"Lorg/cobalt/internal/garden/GardenConfig;", "", "<init>", "()V", "", "farmScript", "Ljava/lang/String;", "getFarmScript", "()Ljava/lang/String;", "setFarmScript", "(Ljava/lang/String;)V", "pestScript", "getPestScript", "setPestScript", "returnScript", "getReturnScript", "setReturnScript", "visitorScript", "getVisitorScript", "setVisitorScript", "", "autoRestartStoppedScript", "Z", "getAutoRestartStoppedScript", "()Z", "setAutoRestartStoppedScript", "(Z)V", "", "pestThreshold", "I", "getPestThreshold", "()I", "setPestThreshold", "(I)V", "triggerPestOnChat", "getTriggerPestOnChat", "setTriggerPestOnChat", "", "pestChatTriggerDelayMs", "J", "getPestChatTriggerDelayMs", "()J", "setPestChatTriggerDelayMs", "(J)V", "aotvEnabled", "getAotvEnabled", "setAotvEnabled", "", "roofPitch", "D", "getRoofPitch", "()D", "setRoofPitch", "(D)V", "aotvRoofPitchHumanization", "getAotvRoofPitchHumanization", "setAotvRoofPitchHumanization", "breakBlocksBeforeAotv", "getBreakBlocksBeforeAotv", "setBreakBlocksBeforeAotv", "enablePlotTpRewarp", "getEnablePlotTpRewarp", "setEnablePlotTpRewarp", "holdWUntilWall", "getHoldWUntilWall", "setHoldWUntilWall", "plotTpNumber", "getPlotTpNumber", "setPlotTpNumber", "delayPestForCropFever", "getDelayPestForCropFever", "setDelayPestForCropFever", "visitorThreshold", "getVisitorThreshold", "setVisitorThreshold", "autoWardrobeEnabled", "getAutoWardrobeEnabled", "setAutoWardrobeEnabled", "autoWardrobePest", "getAutoWardrobePest", "setAutoWardrobePest", "autoWardrobeVisitor", "getAutoWardrobeVisitor", "setAutoWardrobeVisitor", "farmingWardrobeSlot", "getFarmingWardrobeSlot", "setFarmingWardrobeSlot", "pestWardrobeSlot", "getPestWardrobeSlot", "setPestWardrobeSlot", "visitorWardrobeSlot", "getVisitorWardrobeSlot", "setVisitorWardrobeSlot", "autoEquipment", "getAutoEquipment", "setAutoEquipment", "farmingEquipment", "getFarmingEquipment", "setFarmingEquipment", "pestEquipment", "getPestEquipment", "setPestEquipment", "visitorEquipment", "getVisitorEquipment", "setVisitorEquipment", "swapDelayMs", "getSwapDelayMs", "setSwapDelayMs", "autoRodPestCd", "getAutoRodPestCd", "setAutoRodPestCd", "autoRodPestSpawn", "getAutoRodPestSpawn", "setAutoRodPestSpawn", "autoRodReturnToFarm", "getAutoRodReturnToFarm", "setAutoRodReturnToFarm", "rodSwapDelayMs", "getRodSwapDelayMs", "setRodSwapDelayMs", "autoGeorgeSell", "getAutoGeorgeSell", "setAutoGeorgeSell", "georgeRarity", "getGeorgeRarity", "setGeorgeRarity", "georgeSellThreshold", "getGeorgeSellThreshold", "setGeorgeSellThreshold", "autoBookCombine", "getAutoBookCombine", "setAutoBookCombine", "bookCombineLevel", "getBookCombineLevel", "setBookCombineLevel", "bookThreshold", "getBookThreshold", "setBookThreshold", "bookCombineDelayMs", "getBookCombineDelayMs", "setBookCombineDelayMs", "alwaysActiveCombine", "getAlwaysActiveCombine", "setAlwaysActiveCombine", "autoDropJunk", "getAutoDropJunk", "setAutoDropJunk", "junkItems", "getJunkItems", "setJunkItems", "junkThreshold", "getJunkThreshold", "setJunkThreshold", "junkItemDropDelayMs", "getJunkItemDropDelayMs", "setJunkItemDropDelayMs", "autoBoosterCookie", "getAutoBoosterCookie", "setAutoBoosterCookie", "boosterCookieItems", "getBoosterCookieItems", "setBoosterCookieItems", "autoStashManager", "getAutoStashManager", "setAutoStashManager", "bazaarRefreshSecs", "getBazaarRefreshSecs", "setBazaarRefreshSecs", "workDurationMins", "getWorkDurationMins", "setWorkDurationMins", "workOffsetMins", "getWorkOffsetMins", "setWorkOffsetMins", "breakDurationMins", "getBreakDurationMins", "setBreakDurationMins", "breakOffsetMins", "getBreakOffsetMins", "setBreakOffsetMins", "persistSessionTimer", "getPersistSessionTimer", "setPersistSessionTimer", "autoResumeAfterDynamicRest", "getAutoResumeAfterDynamicRest", "setAutoResumeAfterDynamicRest", "autoRecoverUnexpectedDisconnect", "getAutoRecoverUnexpectedDisconnect", "setAutoRecoverUnexpectedDisconnect", "maxRecoveryAttempts", "getMaxRecoveryAttempts", "setMaxRecoveryAttempts", "reconnectDelayMin", "getReconnectDelayMin", "setReconnectDelayMin", "reconnectDelayMax", "getReconnectDelayMax", "setReconnectDelayMax", "rotationTimeMs", "getRotationTimeMs", "setRotationTimeMs", "guiClickDelayMs", "getGuiClickDelayMs", "setGuiClickDelayMs", "additionalRandomDelayMs", "getAdditionalRandomDelayMs", "setAdditionalRandomDelayMs", "gardenWarpDelayMs", "getGardenWarpDelayMs", "setGardenWarpDelayMs", "unflyMode", "getUnflyMode", "setUnflyMode", "petTrackerList", "getPetTrackerList", "setPetTrackerList", "cookieItem", "getCookieItem", "setCookieItem", "guiOnlyInGarden", "getGuiOnlyInGarden", "setGuiOnlyInGarden", "cobalt"})
public final class GardenConfig {
    @NotNull
    public static final GardenConfig INSTANCE = new GardenConfig();
    @NotNull
    private static volatile String farmScript = "netherwart:1";
    @NotNull
    private static volatile String pestScript = "misc:pestCleaner";
    @NotNull
    private static volatile String returnScript = "misc:visitor";
    @NotNull
    private static volatile String visitorScript = "misc:visitor";
    private static volatile boolean autoRestartStoppedScript;
    private static volatile int pestThreshold;
    private static volatile boolean triggerPestOnChat;
    private static volatile long pestChatTriggerDelayMs;
    private static volatile boolean aotvEnabled;
    private static volatile double roofPitch;
    private static volatile int aotvRoofPitchHumanization;
    private static volatile boolean breakBlocksBeforeAotv;
    private static volatile boolean enablePlotTpRewarp;
    private static volatile boolean holdWUntilWall;
    @NotNull
    private static volatile String plotTpNumber;
    private static volatile boolean delayPestForCropFever;
    private static volatile int visitorThreshold;
    private static volatile boolean autoWardrobeEnabled;
    private static volatile boolean autoWardrobePest;
    private static volatile boolean autoWardrobeVisitor;
    private static volatile int farmingWardrobeSlot;
    private static volatile int pestWardrobeSlot;
    private static volatile int visitorWardrobeSlot;
    private static volatile boolean autoEquipment;
    @NotNull
    private static volatile String farmingEquipment;
    @NotNull
    private static volatile String pestEquipment;
    @NotNull
    private static volatile String visitorEquipment;
    private static volatile long swapDelayMs;
    private static volatile boolean autoRodPestCd;
    private static volatile boolean autoRodPestSpawn;
    private static volatile boolean autoRodReturnToFarm;
    private static volatile long rodSwapDelayMs;
    private static volatile boolean autoGeorgeSell;
    @NotNull
    private static volatile String georgeRarity;
    private static volatile int georgeSellThreshold;
    private static volatile boolean autoBookCombine;
    private static volatile int bookCombineLevel;
    private static volatile int bookThreshold;
    private static volatile long bookCombineDelayMs;
    private static volatile boolean alwaysActiveCombine;
    private static volatile boolean autoDropJunk;
    @NotNull
    private static volatile String junkItems;
    private static volatile int junkThreshold;
    private static volatile long junkItemDropDelayMs;
    private static volatile boolean autoBoosterCookie;
    @NotNull
    private static volatile String boosterCookieItems;
    private static volatile boolean autoStashManager;
    private static volatile long bazaarRefreshSecs;
    private static volatile long workDurationMins;
    private static volatile long workOffsetMins;
    private static volatile long breakDurationMins;
    private static volatile long breakOffsetMins;
    private static volatile boolean persistSessionTimer;
    private static volatile boolean autoResumeAfterDynamicRest;
    private static volatile boolean autoRecoverUnexpectedDisconnect;
    private static volatile int maxRecoveryAttempts;
    private static volatile long reconnectDelayMin;
    private static volatile long reconnectDelayMax;
    private static volatile long rotationTimeMs;
    private static volatile long guiClickDelayMs;
    private static volatile long additionalRandomDelayMs;
    private static volatile long gardenWarpDelayMs;
    @NotNull
    private static volatile String unflyMode;
    @NotNull
    private static volatile String petTrackerList;
    @NotNull
    private static volatile String cookieItem;
    private static volatile boolean guiOnlyInGarden;

    private GardenConfig() {
    }

    @NotNull
    public final String getFarmScript() {
        return farmScript;
    }

    public final void setFarmScript(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        farmScript = string;
    }

    @NotNull
    public final String getPestScript() {
        return pestScript;
    }

    public final void setPestScript(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        pestScript = string;
    }

    @NotNull
    public final String getReturnScript() {
        return returnScript;
    }

    public final void setReturnScript(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        returnScript = string;
    }

    @NotNull
    public final String getVisitorScript() {
        return visitorScript;
    }

    public final void setVisitorScript(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        visitorScript = string;
    }

    public final boolean getAutoRestartStoppedScript() {
        return autoRestartStoppedScript;
    }

    public final void setAutoRestartStoppedScript(boolean bl) {
        autoRestartStoppedScript = bl;
    }

    public final int getPestThreshold() {
        return pestThreshold;
    }

    public final void setPestThreshold(int n) {
        pestThreshold = n;
    }

    public final boolean getTriggerPestOnChat() {
        return triggerPestOnChat;
    }

    public final void setTriggerPestOnChat(boolean bl) {
        triggerPestOnChat = bl;
    }

    public final long getPestChatTriggerDelayMs() {
        return pestChatTriggerDelayMs;
    }

    public final void setPestChatTriggerDelayMs(long l) {
        pestChatTriggerDelayMs = l;
    }

    public final boolean getAotvEnabled() {
        return aotvEnabled;
    }

    public final void setAotvEnabled(boolean bl) {
        aotvEnabled = bl;
    }

    public final double getRoofPitch() {
        return roofPitch;
    }

    public final void setRoofPitch(double d) {
        roofPitch = d;
    }

    public final int getAotvRoofPitchHumanization() {
        return aotvRoofPitchHumanization;
    }

    public final void setAotvRoofPitchHumanization(int n) {
        aotvRoofPitchHumanization = n;
    }

    public final boolean getBreakBlocksBeforeAotv() {
        return breakBlocksBeforeAotv;
    }

    public final void setBreakBlocksBeforeAotv(boolean bl) {
        breakBlocksBeforeAotv = bl;
    }

    public final boolean getEnablePlotTpRewarp() {
        return enablePlotTpRewarp;
    }

    public final void setEnablePlotTpRewarp(boolean bl) {
        enablePlotTpRewarp = bl;
    }

    public final boolean getHoldWUntilWall() {
        return holdWUntilWall;
    }

    public final void setHoldWUntilWall(boolean bl) {
        holdWUntilWall = bl;
    }

    @NotNull
    public final String getPlotTpNumber() {
        return plotTpNumber;
    }

    public final void setPlotTpNumber(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        plotTpNumber = string;
    }

    public final boolean getDelayPestForCropFever() {
        return delayPestForCropFever;
    }

    public final void setDelayPestForCropFever(boolean bl) {
        delayPestForCropFever = bl;
    }

    public final int getVisitorThreshold() {
        return visitorThreshold;
    }

    public final void setVisitorThreshold(int n) {
        visitorThreshold = n;
    }

    public final boolean getAutoWardrobeEnabled() {
        return autoWardrobeEnabled;
    }

    public final void setAutoWardrobeEnabled(boolean bl) {
        autoWardrobeEnabled = bl;
    }

    public final boolean getAutoWardrobePest() {
        return autoWardrobePest;
    }

    public final void setAutoWardrobePest(boolean bl) {
        autoWardrobePest = bl;
    }

    public final boolean getAutoWardrobeVisitor() {
        return autoWardrobeVisitor;
    }

    public final void setAutoWardrobeVisitor(boolean bl) {
        autoWardrobeVisitor = bl;
    }

    public final int getFarmingWardrobeSlot() {
        return farmingWardrobeSlot;
    }

    public final void setFarmingWardrobeSlot(int n) {
        farmingWardrobeSlot = n;
    }

    public final int getPestWardrobeSlot() {
        return pestWardrobeSlot;
    }

    public final void setPestWardrobeSlot(int n) {
        pestWardrobeSlot = n;
    }

    public final int getVisitorWardrobeSlot() {
        return visitorWardrobeSlot;
    }

    public final void setVisitorWardrobeSlot(int n) {
        visitorWardrobeSlot = n;
    }

    public final boolean getAutoEquipment() {
        return autoEquipment;
    }

    public final void setAutoEquipment(boolean bl) {
        autoEquipment = bl;
    }

    @NotNull
    public final String getFarmingEquipment() {
        return farmingEquipment;
    }

    public final void setFarmingEquipment(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        farmingEquipment = string;
    }

    @NotNull
    public final String getPestEquipment() {
        return pestEquipment;
    }

    public final void setPestEquipment(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        pestEquipment = string;
    }

    @NotNull
    public final String getVisitorEquipment() {
        return visitorEquipment;
    }

    public final void setVisitorEquipment(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        visitorEquipment = string;
    }

    public final long getSwapDelayMs() {
        return swapDelayMs;
    }

    public final void setSwapDelayMs(long l) {
        swapDelayMs = l;
    }

    public final boolean getAutoRodPestCd() {
        return autoRodPestCd;
    }

    public final void setAutoRodPestCd(boolean bl) {
        autoRodPestCd = bl;
    }

    public final boolean getAutoRodPestSpawn() {
        return autoRodPestSpawn;
    }

    public final void setAutoRodPestSpawn(boolean bl) {
        autoRodPestSpawn = bl;
    }

    public final boolean getAutoRodReturnToFarm() {
        return autoRodReturnToFarm;
    }

    public final void setAutoRodReturnToFarm(boolean bl) {
        autoRodReturnToFarm = bl;
    }

    public final long getRodSwapDelayMs() {
        return rodSwapDelayMs;
    }

    public final void setRodSwapDelayMs(long l) {
        rodSwapDelayMs = l;
    }

    public final boolean getAutoGeorgeSell() {
        return autoGeorgeSell;
    }

    public final void setAutoGeorgeSell(boolean bl) {
        autoGeorgeSell = bl;
    }

    @NotNull
    public final String getGeorgeRarity() {
        return georgeRarity;
    }

    public final void setGeorgeRarity(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        georgeRarity = string;
    }

    public final int getGeorgeSellThreshold() {
        return georgeSellThreshold;
    }

    public final void setGeorgeSellThreshold(int n) {
        georgeSellThreshold = n;
    }

    public final boolean getAutoBookCombine() {
        return autoBookCombine;
    }

    public final void setAutoBookCombine(boolean bl) {
        autoBookCombine = bl;
    }

    public final int getBookCombineLevel() {
        return bookCombineLevel;
    }

    public final void setBookCombineLevel(int n) {
        bookCombineLevel = n;
    }

    public final int getBookThreshold() {
        return bookThreshold;
    }

    public final void setBookThreshold(int n) {
        bookThreshold = n;
    }

    public final long getBookCombineDelayMs() {
        return bookCombineDelayMs;
    }

    public final void setBookCombineDelayMs(long l) {
        bookCombineDelayMs = l;
    }

    public final boolean getAlwaysActiveCombine() {
        return alwaysActiveCombine;
    }

    public final void setAlwaysActiveCombine(boolean bl) {
        alwaysActiveCombine = bl;
    }

    public final boolean getAutoDropJunk() {
        return autoDropJunk;
    }

    public final void setAutoDropJunk(boolean bl) {
        autoDropJunk = bl;
    }

    @NotNull
    public final String getJunkItems() {
        return junkItems;
    }

    public final void setJunkItems(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        junkItems = string;
    }

    public final int getJunkThreshold() {
        return junkThreshold;
    }

    public final void setJunkThreshold(int n) {
        junkThreshold = n;
    }

    public final long getJunkItemDropDelayMs() {
        return junkItemDropDelayMs;
    }

    public final void setJunkItemDropDelayMs(long l) {
        junkItemDropDelayMs = l;
    }

    public final boolean getAutoBoosterCookie() {
        return autoBoosterCookie;
    }

    public final void setAutoBoosterCookie(boolean bl) {
        autoBoosterCookie = bl;
    }

    @NotNull
    public final String getBoosterCookieItems() {
        return boosterCookieItems;
    }

    public final void setBoosterCookieItems(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        boosterCookieItems = string;
    }

    public final boolean getAutoStashManager() {
        return autoStashManager;
    }

    public final void setAutoStashManager(boolean bl) {
        autoStashManager = bl;
    }

    public final long getBazaarRefreshSecs() {
        return bazaarRefreshSecs;
    }

    public final void setBazaarRefreshSecs(long l) {
        bazaarRefreshSecs = l;
    }

    public final long getWorkDurationMins() {
        return workDurationMins;
    }

    public final void setWorkDurationMins(long l) {
        workDurationMins = l;
    }

    public final long getWorkOffsetMins() {
        return workOffsetMins;
    }

    public final void setWorkOffsetMins(long l) {
        workOffsetMins = l;
    }

    public final long getBreakDurationMins() {
        return breakDurationMins;
    }

    public final void setBreakDurationMins(long l) {
        breakDurationMins = l;
    }

    public final long getBreakOffsetMins() {
        return breakOffsetMins;
    }

    public final void setBreakOffsetMins(long l) {
        breakOffsetMins = l;
    }

    public final boolean getPersistSessionTimer() {
        return persistSessionTimer;
    }

    public final void setPersistSessionTimer(boolean bl) {
        persistSessionTimer = bl;
    }

    public final boolean getAutoResumeAfterDynamicRest() {
        return autoResumeAfterDynamicRest;
    }

    public final void setAutoResumeAfterDynamicRest(boolean bl) {
        autoResumeAfterDynamicRest = bl;
    }

    public final boolean getAutoRecoverUnexpectedDisconnect() {
        return autoRecoverUnexpectedDisconnect;
    }

    public final void setAutoRecoverUnexpectedDisconnect(boolean bl) {
        autoRecoverUnexpectedDisconnect = bl;
    }

    public final int getMaxRecoveryAttempts() {
        return maxRecoveryAttempts;
    }

    public final void setMaxRecoveryAttempts(int n) {
        maxRecoveryAttempts = n;
    }

    public final long getReconnectDelayMin() {
        return reconnectDelayMin;
    }

    public final void setReconnectDelayMin(long l) {
        reconnectDelayMin = l;
    }

    public final long getReconnectDelayMax() {
        return reconnectDelayMax;
    }

    public final void setReconnectDelayMax(long l) {
        reconnectDelayMax = l;
    }

    public final long getRotationTimeMs() {
        return rotationTimeMs;
    }

    public final void setRotationTimeMs(long l) {
        rotationTimeMs = l;
    }

    public final long getGuiClickDelayMs() {
        return guiClickDelayMs;
    }

    public final void setGuiClickDelayMs(long l) {
        guiClickDelayMs = l;
    }

    public final long getAdditionalRandomDelayMs() {
        return additionalRandomDelayMs;
    }

    public final void setAdditionalRandomDelayMs(long l) {
        additionalRandomDelayMs = l;
    }

    public final long getGardenWarpDelayMs() {
        return gardenWarpDelayMs;
    }

    public final void setGardenWarpDelayMs(long l) {
        gardenWarpDelayMs = l;
    }

    @NotNull
    public final String getUnflyMode() {
        return unflyMode;
    }

    public final void setUnflyMode(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        unflyMode = string;
    }

    @NotNull
    public final String getPetTrackerList() {
        return petTrackerList;
    }

    public final void setPetTrackerList(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        petTrackerList = string;
    }

    @NotNull
    public final String getCookieItem() {
        return cookieItem;
    }

    public final void setCookieItem(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        cookieItem = string;
    }

    public final boolean getGuiOnlyInGarden() {
        return guiOnlyInGarden;
    }

    public final void setGuiOnlyInGarden(boolean bl) {
        guiOnlyInGarden = bl;
    }

    static {
        pestThreshold = 4;
        triggerPestOnChat = true;
        roofPitch = -80.0;
        aotvRoofPitchHumanization = 3;
        plotTpNumber = "0";
        visitorThreshold = 5;
        autoWardrobePest = true;
        farmingWardrobeSlot = 1;
        pestWardrobeSlot = 2;
        visitorWardrobeSlot = 3;
        autoEquipment = true;
        farmingEquipment = "lotus, blossom";
        pestEquipment = "pesthunter, pest vest";
        visitorEquipment = "";
        swapDelayMs = 300L;
        rodSwapDelayMs = 100L;
        georgeRarity = "LEGENDARY";
        georgeSellThreshold = 3;
        bookCombineLevel = 5;
        bookThreshold = 7;
        bookCombineDelayMs = 300L;
        junkItems = "Fruit Bowl,Farming Exp Boost,Sunder VI";
        junkThreshold = 3;
        junkItemDropDelayMs = 300L;
        autoBoosterCookie = true;
        boosterCookieItems = "Atmospheric Filter,Squeaky Toy,Beady Eyes,Clipped Wings,Overclocker,Mantid Claw,Flowering Bouquet,Bookworm,Chirping Stereo,Firefly,Capsule,Vinyl";
        bazaarRefreshSecs = 120L;
        workDurationMins = 60L;
        workOffsetMins = 5L;
        breakDurationMins = 10L;
        breakOffsetMins = 2L;
        persistSessionTimer = true;
        autoResumeAfterDynamicRest = true;
        autoRecoverUnexpectedDisconnect = true;
        maxRecoveryAttempts = 15;
        reconnectDelayMin = 30L;
        reconnectDelayMax = 60L;
        rotationTimeMs = 500L;
        guiClickDelayMs = 500L;
        gardenWarpDelayMs = 3000L;
        unflyMode = "DOUBLE_TAP_SPACE";
        petTrackerList = "PET_ROSE_DRAGON:Rose Dragon:200:LEGENDARY";
        cookieItem = "";
        guiOnlyInGarden = true;
    }
}

