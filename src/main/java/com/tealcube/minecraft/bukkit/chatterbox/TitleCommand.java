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
package com.tealcube.minecraft.bukkit.chatterbox;

import com.tealcube.minecraft.bukkit.chatterbox.menus.GroupMenu;
import com.tealcube.minecraft.bukkit.chatterbox.titles.GroupData;
import com.tealcube.minecraft.bukkit.chatterbox.titles.PlayerData;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;

import org.bukkit.entity.Player;

import se.ranzdo.bukkit.methodcommand.Arg;
import se.ranzdo.bukkit.methodcommand.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TitleCommand {

    private ChatterboxPlugin plugin;

    public TitleCommand(ChatterboxPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(identifier = "title", onlyPlayers = true)
    public void baseCommand(Player sender) {
        GroupMenu menu = plugin.getPlayerGroupMenuMap().get(sender.getUniqueId());
        if (menu == null) {
            MessageUtils.sendMessage(sender, "<red>Something just went terribly wrong.");
            return;
        }
        menu.openFor(sender);
    }

    @Command(identifier = "ignore", permissions = "chatterbox.commands.ignore", onlyPlayers = true)
    public void ignoreCommand(Player sender, @Arg(name = "player") Player target) {
        PlayerData playerData = plugin.getPlayerDataMap().get(sender.getUniqueId());
        if (playerData == null) {
            playerData = new PlayerData(sender.getUniqueId());
        }
        List<String> ignores = playerData.getIgnoreList();
        if (ignores.contains(target.getUniqueId().toString())) {
            ignores.remove(target.getUniqueId().toString());
            MessageUtils.sendMessage(sender, "<green>You have unignored <white>%player%<green>.",
                    new String[][]{{"%player%", target.getDisplayName()}});
        } else {
            ignores.add(target.getUniqueId().toString());
            MessageUtils.sendMessage(sender, "<green>You have ignored <white>%player%<green>.",
                    new String[][]{{"%player%", target.getDisplayName()}});
        }
        playerData.setIgnoreList(ignores);
        plugin.getPlayerDataMap().put(sender.getUniqueId(), playerData);
    }

    private List<String> getTitles(Player player) {
        List<String> titles = new ArrayList<>();
        for (Map.Entry<String, GroupData> entry : plugin.getGroupDataMap().entrySet()) {
            if (player.hasPermission("easytitles.group." + entry.getKey())) {
                titles.addAll(entry.getValue().getTitles());
            }
        }
        return titles;
    }

    private String getTitleGroup(Player player) {
        String titleGroup = "";
        int lastWeight = 0;
        for (Map.Entry<String, GroupData> entry : plugin.getGroupDataMap().entrySet()) {
            if (player.hasPermission("easytitles.group." + entry.getKey()) && entry.getValue().getWeight() > lastWeight) {
                titleGroup = entry.getKey();
                lastWeight = entry.getValue().getWeight();
            }
        }
        return titleGroup;
    }

}
