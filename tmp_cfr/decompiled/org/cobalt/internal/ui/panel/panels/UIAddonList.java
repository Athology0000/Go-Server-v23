/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.StringsKt
 *  net.fabricmc.loader.api.FabricLoader
 *  net.fabricmc.loader.api.ModContainer
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.panel.panels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.cobalt.api.addon.Addon;
import org.cobalt.api.addon.AddonMetadata;
import org.cobalt.api.hud.modules.CommissionMacroModule;
import org.cobalt.api.hud.modules.MiningHudModule;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.ModuleManager;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.combat.CombatMacroModule;
import org.cobalt.internal.garden.GardenAnalyzerModule;
import org.cobalt.internal.garden.GardenMacroModule;
import org.cobalt.internal.loader.AddonLoader;
import org.cobalt.internal.mining.AutoLanternModule;
import org.cobalt.internal.mining.CommissionHudModule;
import org.cobalt.internal.mining.FairyModule;
import org.cobalt.internal.mining.MiningMacroModule;
import org.cobalt.internal.mining.MiningModule;
import org.cobalt.internal.mining.RoutesModule;
import org.cobalt.internal.mining.VeinDirectionModule;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.components.UIAddonEntry;
import org.cobalt.internal.ui.components.UITopbar;
import org.cobalt.internal.ui.panel.UIPanel;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.cobalt.internal.ui.util.GridLayout;
import org.cobalt.internal.ui.util.ScrollHandler;
import org.cobalt.internal.visual.BlockOutlineModule;
import org.cobalt.internal.visual.BlockOverlayModule;
import org.cobalt.internal.visual.DarkModeModule;
import org.cobalt.internal.visual.FreecamModule;
import org.cobalt.internal.visual.FullBrightModule;
import org.cobalt.internal.visual.HotbarOverlayModule;
import org.cobalt.internal.visual.OrbitFreecamModule;
import org.cobalt.internal.visual.PetDisplayModule;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0000\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H\u0016\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u001f\u0010\n\u001a\u00020\t2\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0015\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\r0\fH\u0002\u00a2\u0006\u0004\b\u000e\u0010\u000fR\u0014\u0010\u0011\u001a\u00020\u00108\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0011\u0010\u0012R\u001a\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\r0\f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0013\u0010\u0014R\u001c\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\r0\f8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u0015\u0010\u0014R\u0014\u0010\u0017\u001a\u00020\u00168\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0017\u0010\u0018R\u0014\u0010\u001a\u001a\u00020\u00198\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001a\u0010\u001b\u00a8\u0006\u001c"}, d2={"Lorg/cobalt/internal/ui/panel/panels/UIAddonList;", "Lorg/cobalt/internal/ui/panel/UIPanel;", "<init>", "()V", "", "render", "", "horizontalAmount", "verticalAmount", "", "mouseScrolled", "(DD)Z", "", "Lorg/cobalt/internal/ui/components/UIAddonEntry;", "buildAddonEntries", "()Ljava/util/List;", "Lorg/cobalt/internal/ui/components/UITopbar;", "topBar", "Lorg/cobalt/internal/ui/components/UITopbar;", "allEntries", "Ljava/util/List;", "entries", "Lorg/cobalt/internal/ui/util/GridLayout;", "gridLayout", "Lorg/cobalt/internal/ui/util/GridLayout;", "Lorg/cobalt/internal/ui/util/ScrollHandler;", "scrollHandler", "Lorg/cobalt/internal/ui/util/ScrollHandler;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nUIAddonList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UIAddonList.kt\norg/cobalt/internal/ui/panel/panels/UIAddonList\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,190:1\n1915#2,2:191\n1586#2:193\n1661#2,3:194\n1391#2:197\n1480#2,5:198\n777#2:203\n873#2,2:204\n777#2:206\n873#2,2:207\n777#2:209\n873#2,2:210\n777#2:212\n873#2,2:213\n777#2:215\n873#2,2:216\n777#2:218\n873#2,2:219\n777#2:221\n873#2,2:222\n*S KotlinDebug\n*F\n+ 1 UIAddonList.kt\norg/cobalt/internal/ui/panel/panels/UIAddonList\n*L\n86#1:191,2\n102#1:193\n102#1:194,3\n104#1:197\n104#1:198,5\n105#1:203\n105#1:204,2\n107#1:206\n107#1:207,2\n113#1:209\n113#1:210,2\n117#1:212\n117#1:213,2\n125#1:215\n125#1:216,2\n129#1:218\n129#1:219,2\n66#1:221\n66#1:222,2\n*E\n"})
public final class UIAddonList
extends UIPanel {
    @NotNull
    private final UITopbar topBar = new UITopbar("Addons");
    @NotNull
    private final List<UIAddonEntry> allEntries = this.buildAddonEntries();
    @NotNull
    private List<UIAddonEntry> entries = this.allEntries;
    @NotNull
    private final GridLayout gridLayout = new GridLayout(3, 270.0f, 70.0f, 20.0f);
    @NotNull
    private final ScrollHandler scrollHandler = new ScrollHandler(0.0f, 1, null);

    public UIAddonList() {
        super(0.0f, 0.0f, 890.0f, 600.0f);
        this.getComponents().addAll((Collection<UIComponent>)this.allEntries);
        this.getComponents().add(this.topBar);
        this.topBar.searchChanged((Function1<? super String, Unit>)((Function1)arg_0 -> UIAddonList._init_$lambda$0(this, arg_0)));
    }

    @Override
    public void render() {
        NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ThemeManager.INSTANCE.getCurrentTheme().getBackground(), 10.0f);
        this.topBar.updateBounds(this.getX(), this.getY()).render();
        float startY = this.getY() + this.topBar.getHeight();
        float visibleHeight = this.getHeight() - this.topBar.getHeight();
        this.scrollHandler.setMaxScroll(this.gridLayout.contentHeight(this.entries.size()) + 20.0f, visibleHeight);
        NVGRenderer.pushScissor(this.getX(), startY, this.getWidth(), visibleHeight);
        float scrollOffset = this.scrollHandler.getOffset();
        this.gridLayout.layout(this.getX() + 20.0f, startY + 20.0f - scrollOffset, this.entries);
        Iterable $this$forEach$iv = this.entries;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            UIComponent p0 = (UIComponent)element$iv;
            boolean bl = false;
            p0.render();
        }
        NVGRenderer.popScissor();
    }

    @Override
    public boolean mouseScrolled(double horizontalAmount, double verticalAmount) {
        if (ExtensionsKt.isHoveringOver(this.getX(), this.getY(), this.getWidth(), this.getHeight())) {
            this.scrollHandler.handleScroll(verticalAmount);
            return true;
        }
        return false;
    }

    /*
     * WARNING - void declaration
     */
    private final List<UIAddonEntry> buildAddonEntries() {
        void $this$filterTo$iv$iv;
        void $this$filterTo$iv$iv2;
        void $this$filterTo$iv$iv3;
        void $this$filterTo$iv$iv4;
        void $this$filterTo$iv$iv5;
        void $this$filterTo$iv$iv6;
        void $this$flatMapTo$iv$iv;
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        List entries = new ArrayList();
        Iterable iterable = AddonLoader.INSTANCE.getAddons();
        List list = entries;
        boolean $i$f$map = false;
        void var4_6 = $this$map$iv;
        Iterable destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            Pair pair = (Pair)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(new UIAddonEntry((AddonMetadata)it.getFirst(), (Addon)it.getSecond()));
        }
        list.addAll((List)destination$iv$iv);
        Iterable $this$flatMap$iv = AddonLoader.INSTANCE.getAddons();
        boolean $i$f$flatMap = false;
        destination$iv$iv = $this$flatMap$iv;
        Iterable destination$iv$iv2 = new ArrayList();
        boolean $i$f$flatMapTo = false;
        for (Object element$iv$iv : $this$flatMapTo$iv$iv) {
            Pair it = (Pair)element$iv$iv;
            boolean bl = false;
            Iterable list$iv$iv = ((Addon)it.getSecond()).getModules();
            CollectionsKt.addAll((Collection)destination$iv$iv2, (Iterable)list$iv$iv);
        }
        Set addonModules = CollectionsKt.toSet((Iterable)((List)destination$iv$iv2));
        Iterable $this$filter$iv = ModuleManager.getModules();
        boolean $i$f$filter = false;
        destination$iv$iv2 = $this$filter$iv;
        Iterable destination$iv$iv3 = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv6) {
            Module it = (Module)element$iv$iv;
            boolean bl = false;
            if (!(!addonModules.contains(it) && !Intrinsics.areEqual((Object)it, (Object)CommissionHudModule.INSTANCE))) continue;
            destination$iv$iv3.add(element$iv$iv);
        }
        List builtinModules = (List)destination$iv$iv3;
        Iterable $this$filter$iv2 = builtinModules;
        boolean $i$f$filter2 = false;
        destination$iv$iv3 = $this$filter$iv2;
        Iterable destination$iv$iv4 = new ArrayList();
        boolean $i$f$filterTo2 = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv5) {
            Module it = (Module)element$iv$iv;
            boolean bl = false;
            if (!(Intrinsics.areEqual((Object)it, (Object)MiningModule.INSTANCE) || Intrinsics.areEqual((Object)it, (Object)MiningHudModule.INSTANCE) || Intrinsics.areEqual((Object)it, (Object)MiningMacroModule.INSTANCE) || Intrinsics.areEqual((Object)it, (Object)FairyModule.INSTANCE) || Intrinsics.areEqual((Object)it, (Object)RoutesModule.INSTANCE) || Intrinsics.areEqual((Object)it, (Object)CommissionMacroModule.INSTANCE) || Intrinsics.areEqual((Object)it, (Object)VeinDirectionModule.INSTANCE) || Intrinsics.areEqual((Object)it, (Object)AutoLanternModule.INSTANCE))) continue;
            destination$iv$iv4.add(element$iv$iv);
        }
        List miningModules = (List)destination$iv$iv4;
        Iterable $this$filter$iv3 = builtinModules;
        boolean $i$f$filter3 = false;
        destination$iv$iv4 = $this$filter$iv3;
        Iterable destination$iv$iv5 = new ArrayList();
        boolean $i$f$filterTo3 = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv4) {
            Module it = (Module)element$iv$iv;
            boolean bl = false;
            if (!(Intrinsics.areEqual((Object)it, (Object)CombatMacroModule.INSTANCE) || StringsKt.equals((String)it.getName(), (String)"Combat HUD", (boolean)true))) continue;
            destination$iv$iv5.add(element$iv$iv);
        }
        List combatModules = (List)destination$iv$iv5;
        Iterable $this$filter$iv4 = builtinModules;
        boolean $i$f$filter4 = false;
        destination$iv$iv5 = $this$filter$iv4;
        Iterable destination$iv$iv6 = new ArrayList();
        boolean $i$f$filterTo4 = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv3) {
            Module it = (Module)element$iv$iv;
            boolean bl = false;
            if (!(Intrinsics.areEqual((Object)it, (Object)FullBrightModule.INSTANCE) || Intrinsics.areEqual((Object)it, (Object)DarkModeModule.INSTANCE) || Intrinsics.areEqual((Object)it, (Object)FreecamModule.INSTANCE) || Intrinsics.areEqual((Object)it, (Object)OrbitFreecamModule.INSTANCE) || Intrinsics.areEqual((Object)it, (Object)BlockOverlayModule.INSTANCE) || Intrinsics.areEqual((Object)it, (Object)BlockOutlineModule.INSTANCE) || Intrinsics.areEqual((Object)it, (Object)HotbarOverlayModule.INSTANCE) || Intrinsics.areEqual((Object)it, (Object)PetDisplayModule.INSTANCE) || StringsKt.equals((String)it.getName(), (String)"Watermark", (boolean)true) || StringsKt.equals((String)it.getName(), (String)"Inventory HUD", (boolean)true))) continue;
            destination$iv$iv6.add(element$iv$iv);
        }
        List visualModules = (List)destination$iv$iv6;
        Iterable $this$filter$iv5 = builtinModules;
        boolean $i$f$filter5 = false;
        destination$iv$iv6 = $this$filter$iv5;
        Iterable destination$iv$iv7 = new ArrayList();
        boolean $i$f$filterTo5 = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv2) {
            Module it = (Module)element$iv$iv;
            boolean bl = false;
            if (!(Intrinsics.areEqual((Object)it, (Object)GardenMacroModule.INSTANCE) || Intrinsics.areEqual((Object)it, (Object)GardenAnalyzerModule.INSTANCE))) continue;
            destination$iv$iv7.add(element$iv$iv);
        }
        List gardenModules = (List)destination$iv$iv7;
        Iterable $this$filter$iv6 = builtinModules;
        boolean $i$f$filter6 = false;
        destination$iv$iv7 = $this$filter$iv6;
        Collection destination$iv$iv8 = new ArrayList();
        boolean $i$f$filterTo6 = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            Module it = (Module)element$iv$iv;
            boolean bl = false;
            if (!(!miningModules.contains(it) && !combatModules.contains(it) && !visualModules.contains(it) && !gardenModules.contains(it))) continue;
            destination$iv$iv8.add(element$iv$iv);
        }
        List coreModules = (List)destination$iv$iv8;
        String version = FabricLoader.getInstance().getModContainer("cobalt").map(arg_0 -> UIAddonList.buildAddonEntries$lambda$9(UIAddonList::buildAddonEntries$lambda$8, arg_0)).orElse("builtin");
        if (!((Collection)coreModules).isEmpty()) {
            Intrinsics.checkNotNull((Object)version);
            AddonMetadata meta = new AddonMetadata("cobalt", "Core", version, CollectionsKt.emptyList(), CollectionsKt.emptyList(), null, 32, null);
            entries.add(0, new UIAddonEntry(meta, new Addon(coreModules){
                final /* synthetic */ List<Module> $coreModules;
                {
                    this.$coreModules = $coreModules;
                }

                public void onLoad() {
                }

                public void onUnload() {
                }

                public List<Module> getModules() {
                    return this.$coreModules;
                }
            }));
        }
        if (!((Collection)visualModules).isEmpty()) {
            Intrinsics.checkNotNull((Object)version);
            AddonMetadata meta = new AddonMetadata("cobalt-visuals", "Visuals", version, CollectionsKt.emptyList(), CollectionsKt.emptyList(), null, 32, null);
            entries.add(0, new UIAddonEntry(meta, new Addon(visualModules){
                final /* synthetic */ List<Module> $visualModules;
                {
                    this.$visualModules = $visualModules;
                }

                public void onLoad() {
                }

                public void onUnload() {
                }

                public List<Module> getModules() {
                    return this.$visualModules;
                }
            }));
        }
        if (!((Collection)combatModules).isEmpty()) {
            Intrinsics.checkNotNull((Object)version);
            AddonMetadata meta = new AddonMetadata("cobalt-combat", "Combat", version, CollectionsKt.emptyList(), CollectionsKt.emptyList(), null, 32, null);
            entries.add(0, new UIAddonEntry(meta, new Addon(combatModules){
                final /* synthetic */ List<Module> $combatModules;
                {
                    this.$combatModules = $combatModules;
                }

                public void onLoad() {
                }

                public void onUnload() {
                }

                public List<Module> getModules() {
                    return this.$combatModules;
                }
            }));
        }
        if (!((Collection)gardenModules).isEmpty()) {
            Intrinsics.checkNotNull((Object)version);
            AddonMetadata meta = new AddonMetadata("cobalt-garden", "Garden", version, CollectionsKt.emptyList(), CollectionsKt.emptyList(), null, 32, null);
            entries.add(0, new UIAddonEntry(meta, new Addon(gardenModules){
                final /* synthetic */ List<Module> $gardenModules;
                {
                    this.$gardenModules = $gardenModules;
                }

                public void onLoad() {
                }

                public void onUnload() {
                }

                public List<Module> getModules() {
                    return this.$gardenModules;
                }
            }));
        }
        if (!((Collection)miningModules).isEmpty()) {
            Intrinsics.checkNotNull((Object)version);
            AddonMetadata meta = new AddonMetadata("cobalt-mining", "Mining", version, CollectionsKt.emptyList(), CollectionsKt.emptyList(), null, 32, null);
            entries.add(0, new UIAddonEntry(meta, new Addon(miningModules){
                final /* synthetic */ List<Module> $miningModules;
                {
                    this.$miningModules = $miningModules;
                }

                public void onLoad() {
                }

                public void onUnload() {
                }

                public List<Module> getModules() {
                    return this.$miningModules;
                }
            }));
        }
        return entries;
    }

    /*
     * WARNING - void declaration
     */
    private static final Unit _init_$lambda$0(UIAddonList this$0, String searchText) {
        List list;
        Intrinsics.checkNotNullParameter((Object)searchText, (String)"searchText");
        UIAddonList uIAddonList = this$0;
        if (((CharSequence)searchText).length() == 0) {
            list = this$0.allEntries;
        } else {
            void $this$filterTo$iv$iv;
            void $this$filter$iv;
            Iterable iterable = this$0.allEntries;
            UIAddonList uIAddonList2 = uIAddonList;
            boolean $i$f$filter = false;
            void var4_5 = $this$filter$iv;
            Collection destination$iv$iv = new ArrayList();
            boolean $i$f$filterTo = false;
            for (Object element$iv$iv : $this$filterTo$iv$iv) {
                UIAddonEntry it = (UIAddonEntry)element$iv$iv;
                boolean bl = false;
                String string = it.getMetadata().getName().toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
                CharSequence charSequence = string;
                String string2 = searchText.toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
                if (!StringsKt.contains$default((CharSequence)charSequence, (CharSequence)string2, (boolean)false, (int)2, null)) continue;
                destination$iv$iv.add(element$iv$iv);
            }
            list = (List)destination$iv$iv;
            uIAddonList = uIAddonList2;
        }
        uIAddonList.entries = list;
        return Unit.INSTANCE;
    }

    private static final String buildAddonEntries$lambda$8(ModContainer it) {
        return it.getMetadata().getVersion().getFriendlyString();
    }

    private static final String buildAddonEntries$lambda$9(Function1 $tmp0, Object p0) {
        return (String)$tmp0.invoke(p0);
    }
}

