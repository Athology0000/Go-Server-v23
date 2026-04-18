/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.ui.util;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import org.cobalt.api.util.ui.NVGRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0010\u0007\n\u0002\b'\n\u0002\u0010\t\n\u0002\b\u0005\b\u0000\u0018\u00002\u00020\u0001B\u001b\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0006\u0010\u0007J\r\u0010\b\u001a\u00020\u0002\u00a2\u0006\u0004\b\b\u0010\tJ\u0015\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\u0002\u00a2\u0006\u0004\b\f\u0010\rJ\u0015\u0010\u000f\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00020\u0002\u00a2\u0006\u0004\b\u000f\u0010\rJ\r\u0010\u0010\u001a\u00020\u000b\u00a2\u0006\u0004\b\u0010\u0010\u0011J\r\u0010\u0012\u001a\u00020\u000b\u00a2\u0006\u0004\b\u0012\u0010\u0011J\u0015\u0010\u0015\u001a\u00020\u000b2\u0006\u0010\u0014\u001a\u00020\u0013\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u0015\u0010\u0017\u001a\u00020\u000b2\u0006\u0010\u0014\u001a\u00020\u0013\u00a2\u0006\u0004\b\u0017\u0010\u0016J\u0015\u0010\u0018\u001a\u00020\u000b2\u0006\u0010\u0014\u001a\u00020\u0013\u00a2\u0006\u0004\b\u0018\u0010\u0016J\u0015\u0010\u0019\u001a\u00020\u000b2\u0006\u0010\u0014\u001a\u00020\u0013\u00a2\u0006\u0004\b\u0019\u0010\u0016J\r\u0010\u001a\u001a\u00020\u000b\u00a2\u0006\u0004\b\u001a\u0010\u0011J\u000f\u0010\u001b\u001a\u0004\u0018\u00010\u0002\u00a2\u0006\u0004\b\u001b\u0010\tJ\u000f\u0010\u001c\u001a\u0004\u0018\u00010\u0002\u00a2\u0006\u0004\b\u001c\u0010\tJ%\u0010!\u001a\u00020\u000b2\u0006\u0010\u001e\u001a\u00020\u001d2\u0006\u0010\u001f\u001a\u00020\u001d2\u0006\u0010 \u001a\u00020\u001d\u00a2\u0006\u0004\b!\u0010\"J%\u0010#\u001a\u00020\u000b2\u0006\u0010\u001e\u001a\u00020\u001d2\u0006\u0010\u001f\u001a\u00020\u001d2\u0006\u0010 \u001a\u00020\u001d\u00a2\u0006\u0004\b#\u0010\"J-\u0010'\u001a\u00020\u000b2\u0006\u0010\u001e\u001a\u00020\u001d2\u0006\u0010$\u001a\u00020\u001d2\u0006\u0010%\u001a\u00020\u001d2\u0006\u0010&\u001a\u00020\u0004\u00a2\u0006\u0004\b'\u0010(J5\u0010)\u001a\u00020\u000b2\u0006\u0010\u001e\u001a\u00020\u001d2\u0006\u0010$\u001a\u00020\u001d2\u0006\u0010%\u001a\u00020\u001d2\u0006\u0010 \u001a\u00020\u001d2\u0006\u0010&\u001a\u00020\u0004\u00a2\u0006\u0004\b)\u0010*J\u001d\u0010,\u001a\u00020\u000b2\u0006\u0010+\u001a\u00020\u001d2\u0006\u0010 \u001a\u00020\u001d\u00a2\u0006\u0004\b,\u0010-J\r\u0010.\u001a\u00020\u001d\u00a2\u0006\u0004\b.\u0010/J\u000f\u00100\u001a\u00020\u0013H\u0002\u00a2\u0006\u0004\b0\u00101J\u000f\u00102\u001a\u00020\u000bH\u0002\u00a2\u0006\u0004\b2\u0010\u0011J\u000f\u00103\u001a\u00020\u000bH\u0002\u00a2\u0006\u0004\b3\u0010\u0011J\u0017\u00104\u001a\u00020\u00022\u0006\u0010\n\u001a\u00020\u0002H\u0002\u00a2\u0006\u0004\b4\u00105J\u0017\u00107\u001a\u00020\u00042\u0006\u00106\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b7\u00108J\u000f\u00109\u001a\u00020\u000bH\u0002\u00a2\u0006\u0004\b9\u0010\u0011J\u0017\u0010:\u001a\u00020\u00022\u0006\u00106\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b:\u0010;J'\u0010<\u001a\u00020\u00042\u0006\u0010\u001e\u001a\u00020\u001d2\u0006\u0010\u001f\u001a\u00020\u001d2\u0006\u0010 \u001a\u00020\u001dH\u0002\u00a2\u0006\u0004\b<\u0010=J\u000f\u0010>\u001a\u00020\u000bH\u0002\u00a2\u0006\u0004\b>\u0010\u0011R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0005\u0010?R\u0016\u0010@\u001a\u00020\u00028\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b@\u0010AR\u0016\u0010B\u001a\u00020\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bB\u0010?R\u0016\u0010C\u001a\u00020\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bC\u0010?R\u0016\u0010D\u001a\u00020\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bD\u0010?R\u0016\u0010F\u001a\u00020E8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bF\u0010GR\u0016\u0010H\u001a\u00020\u001d8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bH\u0010I\u00a8\u0006J"}, d2={"Lorg/cobalt/internal/ui/util/TextInputHandler;", "", "", "initialText", "", "maxLength", "<init>", "(Ljava/lang/String;I)V", "getText", "()Ljava/lang/String;", "value", "", "setText", "(Ljava/lang/String;)V", "input", "insertText", "backspace", "()V", "delete", "", "shift", "moveCursorLeft", "(Z)V", "moveCursorRight", "moveCursorToStart", "moveCursorToEnd", "selectAll", "copy", "cut", "", "x", "textX", "fontSize", "startSelection", "(FFF)V", "updateSelection", "y", "height", "color", "renderCursor", "(FFFI)V", "renderSelection", "(FFFFI)V", "viewWidth", "updateScroll", "(FF)V", "getTextOffset", "()F", "hasSelection", "()Z", "deleteSelection", "clearSelection", "limitText", "(Ljava/lang/String;)Ljava/lang/String;", "index", "clampIndex", "(I)I", "normalizeState", "textPrefix", "(I)Ljava/lang/String;", "getCursorPosFromX", "(FFF)I", "resetBlink", "I", "text", "Ljava/lang/String;", "cursorPos", "selectionStart", "selectionEnd", "", "cursorBlinkTime", "J", "textOffset", "F", "cobalt"})
@SourceDebugExtension(value={"SMAP\nTextInputHandler.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TextInputHandler.kt\norg/cobalt/internal/ui/util/TextInputHandler\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,227:1\n1#2:228\n*E\n"})
public final class TextInputHandler {
    private final int maxLength;
    @NotNull
    private String text;
    private int cursorPos;
    private int selectionStart;
    private int selectionEnd;
    private long cursorBlinkTime;
    private float textOffset;

    public TextInputHandler(@NotNull String initialText, int maxLength) {
        Intrinsics.checkNotNullParameter((Object)initialText, (String)"initialText");
        this.maxLength = maxLength;
        this.text = this.limitText(initialText);
        this.cursorPos = this.text.length();
        this.selectionStart = -1;
        this.selectionEnd = -1;
    }

    public /* synthetic */ TextInputHandler(String string, int n, int n2, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n2 & 1) != 0) {
            string = "";
        }
        if ((n2 & 2) != 0) {
            n = Integer.MAX_VALUE;
        }
        this(string, n);
    }

    @NotNull
    public final String getText() {
        return this.text;
    }

    public final void setText(@NotNull String value) {
        Intrinsics.checkNotNullParameter((Object)value, (String)"value");
        this.text = this.limitText(value);
        this.cursorPos = this.text.length();
        this.clearSelection();
    }

    public final void insertText(@NotNull String input) {
        String toInsert;
        Intrinsics.checkNotNullParameter((Object)input, (String)"input");
        if (this.hasSelection()) {
            this.deleteSelection();
        }
        if (((CharSequence)(toInsert = StringsKt.take((String)input, (int)RangesKt.coerceAtLeast((int)(this.maxLength - this.text.length()), (int)0)))).length() == 0) {
            return;
        }
        String string = this.text.substring(0, this.cursorPos);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"substring(...)");
        String string2 = this.text.substring(this.cursorPos);
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"substring(...)");
        this.text = string + toInsert + string2;
        this.cursorPos += toInsert.length();
        this.clearSelection();
        this.resetBlink();
    }

    public final void backspace() {
        if (this.hasSelection()) {
            this.deleteSelection();
        } else if (this.cursorPos > 0) {
            String string = this.text.substring(0, this.cursorPos - 1);
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"substring(...)");
            String string2 = this.text.substring(this.cursorPos);
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"substring(...)");
            this.text = string + string2;
            int n = this.cursorPos;
            this.cursorPos = n + -1;
            this.resetBlink();
        }
    }

    public final void delete() {
        if (this.hasSelection()) {
            this.deleteSelection();
        } else if (this.cursorPos < this.text.length()) {
            String string = this.text.substring(0, this.cursorPos);
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"substring(...)");
            String string2 = this.text.substring(this.cursorPos + 1);
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"substring(...)");
            this.text = string + string2;
            this.resetBlink();
        }
    }

    public final void moveCursorLeft(boolean shift) {
        if (shift) {
            if (!this.hasSelection()) {
                this.selectionStart = this.cursorPos;
            }
            this.selectionEnd = this.cursorPos = Math.max(0, this.cursorPos - 1);
        } else {
            this.cursorPos = this.hasSelection() ? Math.min(this.selectionStart, this.selectionEnd) : Math.max(0, this.cursorPos - 1);
            this.clearSelection();
        }
        this.resetBlink();
    }

    public final void moveCursorRight(boolean shift) {
        if (shift) {
            if (!this.hasSelection()) {
                this.selectionStart = this.cursorPos;
            }
            this.selectionEnd = this.cursorPos = Math.min(this.text.length(), this.cursorPos + 1);
        } else {
            this.cursorPos = this.hasSelection() ? Math.max(this.selectionStart, this.selectionEnd) : Math.min(this.text.length(), this.cursorPos + 1);
            this.clearSelection();
        }
        this.resetBlink();
    }

    public final void moveCursorToStart(boolean shift) {
        if (shift) {
            if (!this.hasSelection()) {
                this.selectionStart = this.cursorPos;
            }
            this.selectionEnd = 0;
        }
        this.cursorPos = 0;
        if (!shift) {
            this.clearSelection();
        }
        this.resetBlink();
    }

    public final void moveCursorToEnd(boolean shift) {
        if (shift) {
            if (!this.hasSelection()) {
                this.selectionStart = this.cursorPos;
            }
            this.selectionEnd = this.text.length();
        }
        this.cursorPos = this.text.length();
        if (!shift) {
            this.clearSelection();
        }
        this.resetBlink();
    }

    public final void selectAll() {
        this.selectionStart = 0;
        this.selectionEnd = this.text.length();
        this.cursorPos = this.text.length();
    }

    @Nullable
    public final String copy() {
        String string;
        if (this.hasSelection()) {
            String string2 = this.text.substring(Math.min(this.selectionStart, this.selectionEnd), Math.max(this.selectionStart, this.selectionEnd));
            string = string2;
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"substring(...)");
        } else {
            string = null;
        }
        return string;
    }

    @Nullable
    public final String cut() {
        String string;
        String string2 = this.copy();
        if (string2 != null) {
            String string3;
            String it = string3 = string2;
            boolean bl = false;
            this.deleteSelection();
            string = string3;
        } else {
            string = null;
        }
        return string;
    }

    public final void startSelection(float x, float textX, float fontSize) {
        this.selectionStart = this.cursorPos = this.getCursorPosFromX(x, textX, fontSize);
        this.selectionEnd = this.cursorPos;
        this.resetBlink();
    }

    public final void updateSelection(float x, float textX, float fontSize) {
        this.selectionEnd = this.cursorPos = this.getCursorPosFromX(x, textX, fontSize);
    }

    public final void renderCursor(float x, float y, float height, int color) {
        this.normalizeState();
        if ((System.currentTimeMillis() - this.cursorBlinkTime) % (long)1000 < 500L) {
            float cursorX = x + NVGRenderer.textWidth$default(this.textPrefix(this.cursorPos), height, null, 4, null) - this.textOffset;
            NVGRenderer.rect(cursorX, y, 1.0f, height, color);
        }
    }

    public final void renderSelection(float x, float y, float height, float fontSize, int color) {
        if (!this.hasSelection()) {
            return;
        }
        int start = Math.min(this.selectionStart, this.selectionEnd);
        int end = Math.max(this.selectionStart, this.selectionEnd);
        float startX = x + NVGRenderer.textWidth$default(this.textPrefix(start), fontSize, null, 4, null) - this.textOffset;
        float endX = x + NVGRenderer.textWidth$default(this.textPrefix(end), fontSize, null, 4, null) - this.textOffset;
        NVGRenderer.rect(startX, y, endX - startX, height, color);
    }

    public final void updateScroll(float viewWidth, float fontSize) {
        this.normalizeState();
        float cursorX = NVGRenderer.textWidth$default(this.textPrefix(this.cursorPos), fontSize, null, 4, null);
        float padding = 10.0f;
        this.textOffset = cursorX - this.textOffset > viewWidth - padding ? cursorX - viewWidth + padding : (cursorX - this.textOffset < padding ? Math.max(0.0f, cursorX - padding) : this.textOffset);
    }

    public final float getTextOffset() {
        return this.textOffset;
    }

    private final boolean hasSelection() {
        this.normalizeState();
        return this.selectionStart >= 0 && this.selectionEnd >= 0 && this.selectionStart != this.selectionEnd;
    }

    private final void deleteSelection() {
        if (!this.hasSelection()) {
            return;
        }
        int start = Math.min(this.selectionStart, this.selectionEnd);
        int end = Math.max(this.selectionStart, this.selectionEnd);
        String string = this.text.substring(0, start);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"substring(...)");
        String string2 = this.text.substring(end);
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"substring(...)");
        this.text = string + string2;
        this.cursorPos = start;
        this.clearSelection();
        this.resetBlink();
    }

    private final void clearSelection() {
        this.selectionStart = -1;
        this.selectionEnd = -1;
    }

    private final String limitText(String value) {
        return StringsKt.take((String)value, (int)RangesKt.coerceAtLeast((int)this.maxLength, (int)0));
    }

    private final int clampIndex(int index) {
        return RangesKt.coerceIn((int)index, (int)0, (int)this.text.length());
    }

    private final void normalizeState() {
        this.cursorPos = this.clampIndex(this.cursorPos);
        if (this.selectionStart < 0 || this.selectionEnd < 0) {
            this.clearSelection();
            return;
        }
        this.selectionStart = this.clampIndex(this.selectionStart);
        this.selectionEnd = this.clampIndex(this.selectionEnd);
    }

    private final String textPrefix(int index) {
        String string = this.text.substring(0, this.clampIndex(index));
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"substring(...)");
        return string;
    }

    private final int getCursorPosFromX(float x, float textX, float fontSize) {
        int closestPos = 0;
        float closestDist = Float.MAX_VALUE;
        int i = 0;
        int n = this.text.length();
        if (i <= n) {
            while (true) {
                String string = this.text.substring(0, i);
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"substring(...)");
                float textWidth = NVGRenderer.textWidth$default(string, fontSize, null, 4, null);
                float dist = Math.abs(x - (textX + textWidth - this.textOffset));
                if (dist < closestDist) {
                    closestDist = dist;
                    closestPos = i;
                }
                if (i == n) break;
                ++i;
            }
        }
        return closestPos;
    }

    private final void resetBlink() {
        this.cursorBlinkTime = System.currentTimeMillis();
    }

    public TextInputHandler() {
        this(null, 0, 3, null);
    }
}

