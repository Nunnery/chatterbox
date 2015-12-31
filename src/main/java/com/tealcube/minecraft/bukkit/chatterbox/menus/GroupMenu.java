package com.tealcube.minecraft.bukkit.chatterbox.menus;

import com.tealcube.minecraft.bukkit.chatterbox.ChatterboxPlugin;
import com.tealcube.minecraft.bukkit.chatterbox.items.GroupItem;
import com.tealcube.minecraft.bukkit.chatterbox.titles.GroupData;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

import io.github.Cnly.BusyInv.BusyInv.menus.ChestMenu;

public class GroupMenu extends ChestMenu {

    public GroupMenu(ChatterboxPlugin plugin, Player player) {
        super(ChatColor.BLACK + "Click a Group!", null, plugin.getGroupDataMap().size());

        int counter = 0;
        List<GroupData> groupDataList = plugin.getGroups(player);
        for (GroupData data : groupDataList) {
            setItem(counter++, new GroupItem(data));
        }
    }

}
