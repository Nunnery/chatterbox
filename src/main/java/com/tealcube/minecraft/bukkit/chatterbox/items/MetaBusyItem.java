package com.tealcube.minecraft.bukkit.chatterbox.items;

import org.bukkit.inventory.ItemStack;

import java.util.List;

import io.github.Cnly.BusyInv.BusyInv.items.BusyItem;

public class MetaBusyItem extends BusyItem {
    public MetaBusyItem(String displayName, ItemStack icon, String... lore) {
        super(displayName, icon, lore);
    }

    public MetaBusyItem(String displayName, ItemStack icon, List<String> lore) {
        this(displayName, icon, lore.toArray(new String[lore.size()]));
    }
}