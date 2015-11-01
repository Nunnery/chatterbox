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

import com.tealcube.minecraft.bukkit.TextUtils;
import com.tealcube.minecraft.bukkit.facecore.apache.validator.routines.UrlValidator;
import com.tealcube.minecraft.bukkit.facecore.plugin.FacePlugin;
import com.tealcube.minecraft.bukkit.hilt.HiltItemStack;
import com.tealcube.minecraft.bukkit.shade.fanciful.FancyMessage;
import com.tealcube.minecraft.bukkit.shade.google.common.base.Splitter;
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
    private UrlValidator validator;

    @Override
    public void enable() {
        easyTitles = (EasyTitles) Bukkit.getPluginManager().getPlugin("EasyTitles");
        if (!setupChat()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        tribesPlugin = (TribesPlugin) Bukkit.getPluginManager().getPlugin("Tribes");
        getServer().getPluginManager().registerEvents(this, this);
        validator = new UrlValidator();
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
        if (event.isCancelled()) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        Set<Player> receivers = event.getRecipients();
        String message = event.getMessage();
        String newFormat = easyTitles.formatMessage(player);
        newFormat = String.format(newFormat, player.getDisplayName(), message);
        List<String> splitMessage = Splitter.on(" ").splitToList(newFormat);
        FancyMessage messageParts = new FancyMessage("");
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
            String s = splitMessage.get(i);
            String str = ChatColor.stripColor(s);
            if (str.equalsIgnoreCase(player.getDisplayName() + ":")) {
                messageParts.then(s).tooltip(
                        ChatColor.WHITE + player.getName() + " - Level " + player.getLevel(),
                        ChatColor.GOLD + "Guild: " + ChatColor.WHITE + (tribe != null ? tribe.getName() : "None"),
                        ChatColor.GOLD + "Rank: " + ChatColor.WHITE + chat.getPrimaryGroup(player));
            } else if (str.startsWith("{")) {
                if (str.equalsIgnoreCase("{hand}") || str.equalsIgnoreCase("{item}") || str.equalsIgnoreCase("{link}")) {
                    ItemStack hand = player.getEquipment().getItemInHand();
                    HiltItemStack hHand = (hand != null && hand.getType() != Material.AIR) ? new HiltItemStack(hand) : null;
                    if (hHand != null) {
                        if (hHand.getName().contains("\u00A7")) {
                            messageParts.then(hHand.getName().substring(0, 2) + "[Item]").itemTooltip(hHand);
                        } else {
                            messageParts.then("[Item]").itemTooltip(hHand);
                        }
                    } else {
                        messageParts.then("nothing");
                    }
                } else if (str.equalsIgnoreCase("{helmet}") || str.equalsIgnoreCase("{head}") || str.equalsIgnoreCase
                        ("{hat}")) {
                    ItemStack helmet = player.getEquipment().getHelmet();
                    HiltItemStack hHelmet = (helmet != null && helmet.getType() != Material.AIR) ? new HiltItemStack(helmet) : null;
                    if (hHelmet != null) {
                        if (hHelmet.getName().contains("\u00A7")) {
                            messageParts.then(hHelmet.getName().substring(0, 2) + "[Item]").itemTooltip(hHelmet);
                        } else {
                            messageParts.then("[Item]").itemTooltip(hHelmet);
                        }
                    } else {
                        messageParts.then("nothing");
                    }
                } else if (str.equalsIgnoreCase("{chestplate}") || str.equalsIgnoreCase("{chest}") || str
                        .equalsIgnoreCase("{body}")) {
                    ItemStack chest = player.getEquipment().getChestplate();
                    HiltItemStack hChest = (chest != null && chest.getType() != Material.AIR) ? new HiltItemStack(chest) : null;
                    if (hChest != null) {
                        if (hChest.getName().contains("\u00A7")) {
                            messageParts.then(hChest.getName().substring(0, 2) + "[Item]").itemTooltip(hChest);
                        } else {
                            messageParts.then("[Item]").itemTooltip(hChest);
                        }
                    } else {
                        messageParts.then("nothing");
                    }
                } else if (str.equalsIgnoreCase("{leggings}") || str.equalsIgnoreCase("{legs}") || str.equalsIgnoreCase
                        ("{pants}")) {
                    ItemStack leggings = player.getEquipment().getLeggings();
                    HiltItemStack hLeggings = (leggings != null && leggings.getType() != Material.AIR) ? new HiltItemStack(leggings) : null;
                    if (hLeggings != null) {
                        if (hLeggings.getName().contains("\u00A7")) {
                            messageParts.then(hLeggings.getName().substring(0, 2) + "[Item]").itemTooltip(hLeggings);
                        } else {
                            messageParts.then("[Item]").itemTooltip(hLeggings);
                        }
                    } else {
                        messageParts.then("nothing");
                    }
                } else if (str.equalsIgnoreCase("{boots}") || str.equalsIgnoreCase("{feet}") || str.equalsIgnoreCase
                        ("{shoes}")) {
                    ItemStack boots = player.getEquipment().getBoots();
                    HiltItemStack hBoots = (boots != null && boots.getType() != Material.AIR) ? new HiltItemStack(boots) : null;
                    if (hBoots != null) {
                        if (hBoots.getName().contains("\u00A7")) {
                            messageParts.then(hBoots.getName().substring(0, 2) + "[Item]").itemTooltip(hBoots);
                        } else {
                            messageParts.then("[Item]").itemTooltip(hBoots);
                        }
                    } else {
                        messageParts.then("nothing");
                    }
                }
            } else if (validator.isValid(str)) {
                messageParts.then("[Link]").color(ChatColor.AQUA).link(str).tooltip(str);
            } else if (validator.isValid("http://" + str)) {
                messageParts.then("[Link]").color(ChatColor.AQUA).link("http://" + str).tooltip("http://" + str);
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
