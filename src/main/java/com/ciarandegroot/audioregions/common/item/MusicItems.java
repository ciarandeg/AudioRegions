package com.ciarandegroot.audioregions.common.item;

import com.ciarandegroot.audioregions.AudioRegions;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MusicItems {
    public static final DeferredRegister<Item> REGISTRY =
            DeferredRegister.create(ForgeRegistries.ITEMS, AudioRegions.MOD_ID);

    public static final RegistryObject<WandItem> MUSIC_WAND =
            REGISTRY.register("music_wand",
                    () -> new WandItem(
                            new Item.Properties()
                                    .maxStackSize(1)
                                    .rarity(Rarity.UNCOMMON)
                                    .group(ItemGroup.TOOLS)));
}