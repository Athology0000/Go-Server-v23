/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.PackOutput$PathProvider
 *  net.minecraft.data.PackOutput$Target
 *  net.minecraft.resources.Identifier
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.equipment.EquipmentAsset
 *  net.minecraft.world.item.equipment.EquipmentAssets
 */
package net.minecraft.client.data.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

@Environment(value=EnvType.CLIENT)
public class EquipmentAssetProvider
implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public EquipmentAssetProvider(PackOutput packOutput) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "equipment");
    }

    private static void bootstrap(BiConsumer<ResourceKey<EquipmentAsset>, EquipmentClientInfo> biConsumer) {
        ResourceKey resourceKey;
        DyeColor dyeColor;
        biConsumer.accept((ResourceKey<EquipmentAsset>)EquipmentAssets.LEATHER, EquipmentClientInfo.builder().addHumanoidLayers(Identifier.withDefaultNamespace((String)"leather"), true).addHumanoidLayers(Identifier.withDefaultNamespace((String)"leather_overlay"), false).addLayers(EquipmentClientInfo.LayerType.HORSE_BODY, EquipmentClientInfo.Layer.leatherDyeable(Identifier.withDefaultNamespace((String)"leather"), true), EquipmentClientInfo.Layer.leatherDyeable(Identifier.withDefaultNamespace((String)"leather_overlay"), false)).build());
        biConsumer.accept((ResourceKey<EquipmentAsset>)EquipmentAssets.CHAINMAIL, EquipmentAssetProvider.onlyHumanoid("chainmail"));
        biConsumer.accept((ResourceKey<EquipmentAsset>)EquipmentAssets.COPPER, EquipmentAssetProvider.humanoidAndMountArmor("copper"));
        biConsumer.accept((ResourceKey<EquipmentAsset>)EquipmentAssets.IRON, EquipmentAssetProvider.humanoidAndMountArmor("iron"));
        biConsumer.accept((ResourceKey<EquipmentAsset>)EquipmentAssets.GOLD, EquipmentAssetProvider.humanoidAndMountArmor("gold"));
        biConsumer.accept((ResourceKey<EquipmentAsset>)EquipmentAssets.DIAMOND, EquipmentAssetProvider.humanoidAndMountArmor("diamond"));
        biConsumer.accept((ResourceKey<EquipmentAsset>)EquipmentAssets.TURTLE_SCUTE, EquipmentClientInfo.builder().addMainHumanoidLayer(Identifier.withDefaultNamespace((String)"turtle_scute"), false).build());
        biConsumer.accept((ResourceKey<EquipmentAsset>)EquipmentAssets.NETHERITE, EquipmentAssetProvider.humanoidAndMountArmor("netherite"));
        biConsumer.accept((ResourceKey<EquipmentAsset>)EquipmentAssets.ARMADILLO_SCUTE, EquipmentClientInfo.builder().addLayers(EquipmentClientInfo.LayerType.WOLF_BODY, EquipmentClientInfo.Layer.onlyIfDyed(Identifier.withDefaultNamespace((String)"armadillo_scute"), false)).addLayers(EquipmentClientInfo.LayerType.WOLF_BODY, EquipmentClientInfo.Layer.onlyIfDyed(Identifier.withDefaultNamespace((String)"armadillo_scute_overlay"), true)).build());
        biConsumer.accept((ResourceKey<EquipmentAsset>)EquipmentAssets.ELYTRA, EquipmentClientInfo.builder().addLayers(EquipmentClientInfo.LayerType.WINGS, new EquipmentClientInfo.Layer(Identifier.withDefaultNamespace((String)"elytra"), Optional.empty(), true)).build());
        EquipmentClientInfo.Layer layer = new EquipmentClientInfo.Layer(Identifier.withDefaultNamespace((String)"saddle"));
        biConsumer.accept((ResourceKey<EquipmentAsset>)EquipmentAssets.SADDLE, EquipmentClientInfo.builder().addLayers(EquipmentClientInfo.LayerType.PIG_SADDLE, layer).addLayers(EquipmentClientInfo.LayerType.STRIDER_SADDLE, layer).addLayers(EquipmentClientInfo.LayerType.CAMEL_SADDLE, layer).addLayers(EquipmentClientInfo.LayerType.CAMEL_HUSK_SADDLE, layer).addLayers(EquipmentClientInfo.LayerType.HORSE_SADDLE, layer).addLayers(EquipmentClientInfo.LayerType.DONKEY_SADDLE, layer).addLayers(EquipmentClientInfo.LayerType.MULE_SADDLE, layer).addLayers(EquipmentClientInfo.LayerType.SKELETON_HORSE_SADDLE, layer).addLayers(EquipmentClientInfo.LayerType.ZOMBIE_HORSE_SADDLE, layer).addLayers(EquipmentClientInfo.LayerType.NAUTILUS_SADDLE, layer).build());
        for (Map.Entry entry : EquipmentAssets.HARNESSES.entrySet()) {
            dyeColor = (DyeColor)entry.getKey();
            resourceKey = (ResourceKey)entry.getValue();
            biConsumer.accept((ResourceKey<EquipmentAsset>)resourceKey, EquipmentClientInfo.builder().addLayers(EquipmentClientInfo.LayerType.HAPPY_GHAST_BODY, EquipmentClientInfo.Layer.onlyIfDyed(Identifier.withDefaultNamespace((String)(dyeColor.getSerializedName() + "_harness")), false)).build());
        }
        for (Map.Entry entry : EquipmentAssets.CARPETS.entrySet()) {
            dyeColor = (DyeColor)entry.getKey();
            resourceKey = (ResourceKey)entry.getValue();
            biConsumer.accept((ResourceKey<EquipmentAsset>)resourceKey, EquipmentClientInfo.builder().addLayers(EquipmentClientInfo.LayerType.LLAMA_BODY, new EquipmentClientInfo.Layer(Identifier.withDefaultNamespace((String)dyeColor.getSerializedName()))).build());
        }
        biConsumer.accept((ResourceKey<EquipmentAsset>)EquipmentAssets.TRADER_LLAMA, EquipmentClientInfo.builder().addLayers(EquipmentClientInfo.LayerType.LLAMA_BODY, new EquipmentClientInfo.Layer(Identifier.withDefaultNamespace((String)"trader_llama"))).build());
    }

    private static EquipmentClientInfo onlyHumanoid(String string) {
        return EquipmentClientInfo.builder().addHumanoidLayers(Identifier.withDefaultNamespace((String)string)).build();
    }

    private static EquipmentClientInfo humanoidAndMountArmor(String string) {
        return EquipmentClientInfo.builder().addHumanoidLayers(Identifier.withDefaultNamespace((String)string)).addLayers(EquipmentClientInfo.LayerType.HORSE_BODY, EquipmentClientInfo.Layer.leatherDyeable(Identifier.withDefaultNamespace((String)string), false)).addLayers(EquipmentClientInfo.LayerType.NAUTILUS_BODY, EquipmentClientInfo.Layer.leatherDyeable(Identifier.withDefaultNamespace((String)string), false)).build();
    }

    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        HashMap map = new HashMap();
        EquipmentAssetProvider.bootstrap((resourceKey, equipmentClientInfo) -> {
            if (map.putIfAbsent(resourceKey, equipmentClientInfo) != null) {
                throw new IllegalStateException("Tried to register equipment asset twice for id: " + String.valueOf(resourceKey));
            }
        });
        return DataProvider.saveAll((CachedOutput)cachedOutput, EquipmentClientInfo.CODEC, arg_0 -> ((PackOutput.PathProvider)this.pathProvider).json(arg_0), map);
    }

    public String getName() {
        return "Equipment Asset Definitions";
    }
}

