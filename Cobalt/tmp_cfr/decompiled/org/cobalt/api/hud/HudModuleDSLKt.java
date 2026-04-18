/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.hud;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.hud.HudElement;
import org.cobalt.api.hud.HudElementBuilder;
import org.cobalt.api.module.Module;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=2, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u001aD\u0010\u000b\u001a\u00020\n*\u00020\u00002\u0006\u0010\u0002\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u00012\b\b\u0002\u0010\u0004\u001a\u00020\u00012\u0017\u0010\t\u001a\u0013\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005\u00a2\u0006\u0002\b\b\u00a2\u0006\u0004\b\u000b\u0010\f\u00a8\u0006\r"}, d2={"Lorg/cobalt/api/module/Module;", "", "id", "name", "description", "Lkotlin/Function1;", "Lorg/cobalt/api/hud/HudElementBuilder;", "", "Lkotlin/ExtensionFunctionType;", "init", "Lorg/cobalt/api/hud/HudElement;", "hudElement", "(Lorg/cobalt/api/module/Module;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/jvm/functions/Function1;)Lorg/cobalt/api/hud/HudElement;", "cobalt"})
public final class HudModuleDSLKt {
    @NotNull
    public static final HudElement hudElement(@NotNull Module $this$hudElement, @NotNull String id, @NotNull String name, @NotNull String description, @NotNull Function1<? super HudElementBuilder, Unit> init) {
        Intrinsics.checkNotNullParameter((Object)$this$hudElement, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)id, (String)"id");
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)description, (String)"description");
        Intrinsics.checkNotNullParameter(init, (String)"init");
        HudElementBuilder builder = new HudElementBuilder(id, name, description);
        init.invoke((Object)builder);
        HudElement element = builder.build();
        $this$hudElement.addHudElement(element);
        return element;
    }

    public static /* synthetic */ HudElement hudElement$default(Module module, String string, String string2, String string3, Function1 function1, int n, Object object) {
        if ((n & 4) != 0) {
            string3 = "";
        }
        return HudModuleDSLKt.hudElement(module, string, string2, string3, (Function1<? super HudElementBuilder, Unit>)function1);
    }
}

