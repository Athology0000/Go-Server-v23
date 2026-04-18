/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.item.crafting.RecipeAccess
 *  net.minecraft.world.item.crafting.RecipePropertySet
 *  net.minecraft.world.item.crafting.SelectableRecipe$SingleInputSet
 *  net.minecraft.world.item.crafting.StonecutterRecipe
 */
package net.minecraft.client.multiplayer;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;

@Environment(value=EnvType.CLIENT)
public class ClientRecipeContainer
implements RecipeAccess {
    private final Map<ResourceKey<RecipePropertySet>, RecipePropertySet> itemSets;
    private final SelectableRecipe.SingleInputSet<StonecutterRecipe> stonecutterRecipes;

    public ClientRecipeContainer(Map<ResourceKey<RecipePropertySet>, RecipePropertySet> map, SelectableRecipe.SingleInputSet<StonecutterRecipe> singleInputSet) {
        this.itemSets = map;
        this.stonecutterRecipes = singleInputSet;
    }

    public RecipePropertySet propertySet(ResourceKey<RecipePropertySet> resourceKey) {
        return this.itemSets.getOrDefault(resourceKey, RecipePropertySet.EMPTY);
    }

    public SelectableRecipe.SingleInputSet<StonecutterRecipe> stonecutterRecipes() {
        return this.stonecutterRecipes;
    }
}

