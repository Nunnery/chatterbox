package com.tealcube.minecraft.bukkit.chatterbox.items;

import com.tealcube.minecraft.bukkit.chatterbox.ChatterboxPlugin;
import com.tealcube.minecraft.bukkit.chatterbox.titles.GroupData;
import com.tealcube.minecraft.bukkit.chatterbox.titles.PlayerData;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.Cnly.BusyInv.BusyInv.events.ItemClickEvent;

public class TitleItem extends MetaBusyItem {

    private final String title;

    public TitleItem(String title, GroupData groupData) {
        super(title, new ItemStack(Material.NAME_TAG), groupData.getTitleDescription());
        this.title = title;
    }

    @Override
    public void onClick(ItemClickEvent e) {
        super.onClick(e);
        Player player = e.getPlayer();
        PlayerData playerData = ChatterboxPlugin.getInstance().getPlayerDataMap().get(player.getUniqueId());
        if (playerData == null) {
            playerData = new PlayerData(player.getUniqueId());
        }
        playerData.setTitle(title);
        playerData.setTitleGroup(ChatterboxPlugin.getInstance().getTitleGroup(player));
        MessageUtils.sendMessage(player, "<green>Your title has been changed!");
        e.setCloseDirectly(true);
    }

}
