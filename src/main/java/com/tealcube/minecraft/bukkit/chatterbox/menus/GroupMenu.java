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
        super(ChatColor.BLACK + "Pick a Group!", null, ChestSize.fit(plugin.getGroupDataMap().size()));

        int counter = 0;
        List<GroupData> groupDataList = plugin.getGroups(player);
        for (GroupData data : groupDataList) {
            TitleMenu titleMenu = ChatterboxPlugin.getInstance().getTitleMenu(data.getKey());
            if (titleMenu == null) {
                continue;
            }
            setItem(counter++, new GroupItem(data, titleMenu));
        }
    }

}
