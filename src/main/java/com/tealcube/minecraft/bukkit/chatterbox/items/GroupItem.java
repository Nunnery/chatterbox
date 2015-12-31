package com.tealcube.minecraft.bukkit.chatterbox.items;

import com.tealcube.minecraft.bukkit.chatterbox.ChatterboxPlugin;
import com.tealcube.minecraft.bukkit.chatterbox.menus.TitleMenu;
import com.tealcube.minecraft.bukkit.chatterbox.titles.GroupData;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.Cnly.BusyInv.BusyInv.events.ItemClickEvent;

public class GroupItem extends MetaBusyItem {

    private final GroupData groupData;

    public GroupItem(GroupData groupData) {
        super(groupData.getKey(), new ItemStack(Material.CHEST), groupData.getRankDescription());
        this.groupData = groupData;
    }

    @Override
    public void onClick(ItemClickEvent e) {
        super.onClick(e);
        Player player = e.getPlayer();
        TitleMenu titleMenu = ChatterboxPlugin.getInstance().getTitleMenu(groupData.getKey());
        if (titleMenu == null) {
            return;
        }
        titleMenu.openFor(player);
    }

}
