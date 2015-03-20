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
import com.tealcube.minecraft.bukkit.facecore.utilities.TextUtils;
import com.tealcube.minecraft.bukkit.kern.fanciful.FancyMessage;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Splitter;
import com.tealcube.minecraft.bukkit.tribes.TribesPlugin;
import com.tealcube.minecraft.bukkit.tribes.data.Member;
import com.tealcube.minecraft.bukkit.tribes.data.Tribe;
import me.barryg.EasyTitles.EasyTitles;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.List;
import java.util.Set;

public class ChatterboxPlugin extends FacePlugin implements Listener {

    private EasyTitles easyTitles;
    private TribesPlugin tribesPlugin;
    private Chat chat;

    @Override
    public void enable() {
        easyTitles = (EasyTitles) Bukkit.getPluginManager().getPlugin("EasyTitles");
        if (!setupChat()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        tribesPlugin = (TribesPlugin) Bukkit.getPluginManager().getPlugin("Tribes");
        getServer().getPluginManager().registerEvents(this, this);
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
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
        List<String> splitMessage = Splitter.on(" ").splitToList(newFormat);
        FancyMessage messageParts = new FancyMessage("");
        ItemStack hand = player.getEquipment().getItemInHand();
        HiltItemStack hHand = (hand != null && hand.getType() != Material.AIR) ? new HiltItemStack(hand) : null;
        ItemStack helmet = player.getEquipment().getHelmet();
        HiltItemStack hHelmet = (helmet != null && helmet.getType() != Material.AIR) ? new HiltItemStack(helmet) : null;
        ItemStack chest = player.getEquipment().getChestplate();
        HiltItemStack hChest = (chest != null && chest.getType() != Material.AIR) ? new HiltItemStack(chest) : null;
        ItemStack leggings = player.getEquipment().getLeggings();
        HiltItemStack hLeggings = (leggings != null && leggings.getType() != Material.AIR) ? new HiltItemStack(leggings) : null;
        ItemStack boots = player.getEquipment().getBoots();
        HiltItemStack hBoots = (boots != null && boots.getType() != Material.AIR) ? new HiltItemStack(boots) : null;
        Member member = tribesPlugin.getMemberManager().getMember(player.getUniqueId()).or(new Member(player.getUniqueId()));
        if (!tribesPlugin.getMemberManager().hasMember(member)) {
            tribesPlugin.getMemberManager().addMember(member);
        }
        Tribe tribe = null;
        if (member.getTribe() != null) {
            tribe = tribesPlugin.getTribeManager().getTribe(member.getTribe()).orNull();
        }
        ChatColor color = ChatColor.GRAY;
        for (int i = 0; i < splitMessage.size(); i++) {
            if (i == 2) {
                color = ChatColor.getByChar(splitMessage.get(i).substring(1, 2));
            }
            String s = ChatColor.stripColor(splitMessage.get(i));
            if (s.equalsIgnoreCase(player.getDisplayName() + ":")) {
                messageParts.then(s).tooltip(
                        ChatColor.WHITE + player.getName() + " - Level " + player.getLevel(),
                        ChatColor.GOLD + "Tribe: " + ChatColor.WHITE + (tribe != null ? tribe.getName() : "N/A"),
                        ChatColor.GOLD + "Rank: " + ChatColor.WHITE + chat.getPrimaryGroup(player));
            } else if (s.equalsIgnoreCase("{hand}")) {
                if (hHand != null) {
                    messageParts.then(hHand.getName().length() > 12 ? hHand.getName().substring(0, 12) : hHand.getName()).itemTooltip(hHand);
                } else {
                    messageParts.then("nothing");
                }
            } else if (s.equalsIgnoreCase("{helmet}") || s.equalsIgnoreCase("{head}")) {
                if (hHelmet != null) {
                    messageParts.then(hHelmet.getName().length() > 12 ? hHelmet.getName().substring(0, 12) : hHelmet.getName()).itemTooltip(hHelmet);
                } else {
                    messageParts.then("nothing");
                }
            } else if (s.equalsIgnoreCase("{chestplate}") || s.equalsIgnoreCase("{chest}")) {
                if (hChest != null) {
                    messageParts.then(hChest.getName().length() > 12 ? hChest.getName().substring(0, 12) : hChest.getName()).itemTooltip(hChest);
                } else {
                    messageParts.then("nothing");
                }
            } else if (s.equalsIgnoreCase("{leggings}") || s.equalsIgnoreCase("{legs}")) {
                if (hLeggings != null) {
                    messageParts.then(hLeggings.getName().length() > 12 ? hLeggings.getName().substring(0, 12) : hLeggings.getName()).itemTooltip
                            (hLeggings);
                } else {
                    messageParts.then("nothing");
                }
            } else if (s.equalsIgnoreCase("{boots}") || s.equalsIgnoreCase("{feet}")) {
                if (hBoots != null) {
                    messageParts.then(hBoots.getName().length() > 12 ? hBoots.getName().substring(0, 12) : hBoots.getName()).itemTooltip(hBoots);
                } else {
                    messageParts.then("nothing");
                }
            } else {
                messageParts.then(TextUtils.color(color + s));
            }
            if (i != splitMessage.size() - 1) {
                messageParts.then(" ");
            }
        }
        messageParts.send(Bukkit.getConsoleSender());
        messageParts.send(receivers);
    }

}
