/**
 * The MIT License
 * Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.tealcube.minecraft.bukkit.chatterbox.items;

import com.tealcube.minecraft.bukkit.chatterbox.ChatterboxPlugin;
import com.tealcube.minecraft.bukkit.chatterbox.titles.GroupData;
import com.tealcube.minecraft.bukkit.chatterbox.titles.PlayerData;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.Cnly.BusyInv.BusyInv.events.ItemClickEvent;
import io.github.Cnly.BusyInv.BusyInv.items.BusyItem;

public class TitleItem extends BusyItem {

    private final String title;

    public TitleItem(String title, GroupData groupData) {
        super(title, new ItemStack(Material.NAME_TAG),
                groupData.getRankDescription().toArray(new String[groupData.getRankDescription().size()]));
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
