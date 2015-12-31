package com.tealcube.minecraft.bukkit.chatterbox.menus;

import com.tealcube.minecraft.bukkit.chatterbox.ChatterboxPlugin;
import com.tealcube.minecraft.bukkit.chatterbox.items.TitleItem;
import com.tealcube.minecraft.bukkit.chatterbox.titles.GroupData;

import io.github.Cnly.BusyInv.BusyInv.menus.ChestMenu;

public class TitleMenu extends ChestMenu {

    public TitleMenu(ChatterboxPlugin plugin, GroupData groupData) {
        super("Click a Title!", null, groupData.getTitles().size());

        int counter = 1;
        for (String title : groupData.getTitles()) {
            setItem(counter++, new TitleItem(title, groupData));
        }
    }

}
