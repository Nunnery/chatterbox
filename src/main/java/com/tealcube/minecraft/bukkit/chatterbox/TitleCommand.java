package com.tealcube.minecraft.bukkit.chatterbox;

import com.tealcube.minecraft.bukkit.chatterbox.titles.GroupData;
import com.tealcube.minecraft.bukkit.chatterbox.titles.PlayerData;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;

import org.bukkit.ChatColor;
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

    @Command(identifier = "title list", permissions = "chatterbox.commands.list", onlyPlayers = true)
    public void listCommand(Player sender, @Arg(name = "page", def = "1", verifiers = "min[0]") int page) {
        List<String> titles = getTitles(sender);
        if (titles.isEmpty()) {
            MessageUtils.sendMessage(sender, "<red>You have no titles.");
            return;
        }
        int listWidth = 5;
        int listHeight = 9;
        int totalPages = titles.size() / (listWidth * listHeight);
        int curPage = Math.min(page, totalPages);
        MessageUtils.sendMessage(sender, "<gold> --== <darkred>Chatterbox <white>Page %curPage% / %totalPages% " +
                "<gold>==--", new String[][]{{"%curPage%", curPage + ""}, {"%totalPages%", totalPages + ""}});
        curPage *= listWidth * listHeight;
        int count = 1;
        String message = "";
        for (int i = curPage; i < curPage + listWidth * listHeight; ++i) {
            if (i < titles.size()) {
                message = message + ChatColor.WHITE + i + ": " + ChatColor.GREEN + titles.get(i) + " ";
            }

            if (count == listWidth) {
                if (!message.equals("")) {
                    MessageUtils.sendMessage(sender, message);
                }
                if (i > titles.size()) {
                    break;
                }

                message = "";
                count = 1;
            } else {
                count++;
            }
        }
    }

    @Command(identifier = "title use", permissions = "chatterbox.commands.use", onlyPlayers = true)
    public void useCommand(Player sender, @Arg(name = "title", verifiers = "min[0]") int title) {
        List<String> titles = getTitles(sender);
        if (titles.isEmpty()) {
            MessageUtils.sendMessage(sender, "<red>You have no titles.");
            return;
        }
        int chosenTitle = Math.max(title, titles.size());
        PlayerData playerData = plugin.getPlayerDataMap().get(sender.getUniqueId());
        if (playerData == null) {
            playerData = new PlayerData(sender.getUniqueId());
        }
        playerData.setTitle(titles.get(chosenTitle));
        playerData.setTitleGroup(getTitleGroup(sender));
        plugin.getPlayerDataMap().put(sender.getUniqueId(), playerData);
        MessageUtils.sendMessage(sender, "<green>Your title was changed to <white>%title%<green>!", new
                String[][]{{"%title%", playerData.getTitle()}});
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
