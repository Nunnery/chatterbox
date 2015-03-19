/*
 * This file is part of Loot, licensed under the ISC License.
 *
 * Copyright (c) 2015 Richard Harrah
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted,
 * provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
 * THIS SOFTWARE.
 */
package com.tealcube.minecraft.bukkit.chatterbox;

import com.tealcube.minecraft.bukkit.facecore.plugin.FacePlugin;
import com.tealcube.minecraft.bukkit.facecore.shade.hilt.HiltItemStack;
import com.tealcube.minecraft.bukkit.kern.fanciful.FancyMessage;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Splitter;
import me.barryg.EasyTitles.EasyTitles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class ChatterboxPlugin extends FacePlugin implements Listener {

    private static final Pattern PATTERN = Pattern.compile("\\{([Hh][Aa][Nn][Dd]*?)\\}");
    private EasyTitles easyTitles;

    @Override
    public void enable() {
        easyTitles = (EasyTitles) Bukkit.getPluginManager().getPlugin("EasyTitles");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void disable() {

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        Set<Player> receivers = event.getRecipients();
        String message = event.getMessage();
        String newFormat = easyTitles.formatMessage(player);
        newFormat = String.format(newFormat, player.getDisplayName(), message);
        List<String> splitMessage = Splitter.on(PATTERN).splitToList(newFormat);
        Bukkit.getLogger().info("size: " + splitMessage.size());
        if (splitMessage.size() <= 1) {
            for (Player p : receivers) {
                p.sendMessage(newFormat);
            }
            Bukkit.getConsoleSender().sendMessage(newFormat);
            return;
        }
        FancyMessage messageParts = new FancyMessage(splitMessage.get(0));
        if (player.getItemInHand().getType() == Material.AIR) {
            messageParts.then(ChatColor.WHITE + "AIR");
        } else {
            HiltItemStack his = new HiltItemStack(player.getItemInHand());
            messageParts.then(his.getName()).itemTooltip(his);
        }
        messageParts.then(splitMessage.get(1));
        messageParts.send(Bukkit.getConsoleSender());
        messageParts.send(receivers);
    }

}
