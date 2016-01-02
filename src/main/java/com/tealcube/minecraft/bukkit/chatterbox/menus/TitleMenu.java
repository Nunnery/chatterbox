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
import com.tealcube.minecraft.bukkit.chatterbox.items.TitleItem;
import com.tealcube.minecraft.bukkit.chatterbox.titles.GroupData;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import io.github.Cnly.BusyInv.BusyInv.items.MenuOpenItem;
import io.github.Cnly.BusyInv.BusyInv.menus.ChestMenu;

public class TitleMenu extends ChestMenu {

    private final ChatterboxPlugin plugin;

    public TitleMenu(ChatterboxPlugin plugin, GroupData groupData) {
        super(ChatColor.BLACK + "Title Select!", null, ChestSize.fit(groupData.getTitles().size()));
        this.plugin = plugin;
        int counter = 1;
        for (String title : groupData.getTitles()) {
            setItem(counter++, new TitleItem(title));
        }
    }

    @Override
    public void onMenuOpen(InventoryOpenEvent e) {
        super.onMenuOpen(e);
        GroupMenu groupMenu = plugin.getPlayerGroupMenuMap().get(e.getPlayer().getUniqueId());
        setItem(0, new MenuOpenItem(groupMenu, "Go Back", new ItemStack(Material.REDSTONE)));
    }
}
