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

import com.tealcube.minecraft.bukkit.CaselessMap;
import com.tealcube.minecraft.bukkit.TextUtils;
import com.tealcube.minecraft.bukkit.chatterbox.menus.GroupMenu;
import com.tealcube.minecraft.bukkit.chatterbox.menus.TitleMenu;
import com.tealcube.minecraft.bukkit.chatterbox.titles.GroupData;
import com.tealcube.minecraft.bukkit.chatterbox.titles.PlayerData;
import com.tealcube.minecraft.bukkit.config.MasterConfiguration;
import com.tealcube.minecraft.bukkit.config.SmartConfiguration;
import com.tealcube.minecraft.bukkit.config.SmartYamlConfiguration;
import com.tealcube.minecraft.bukkit.config.VersionedConfiguration;
import com.tealcube.minecraft.bukkit.config.VersionedSmartYamlConfiguration;
import com.tealcube.minecraft.bukkit.facecore.apache.validator.routines.UrlValidator;
import com.tealcube.minecraft.bukkit.facecore.logging.PluginLogger;
import com.tealcube.minecraft.bukkit.facecore.plugin.FacePlugin;
import com.tealcube.minecraft.bukkit.hilt.HiltItemStack;
import com.tealcube.minecraft.bukkit.shade.fanciful.FancyMessage;
import com.tealcube.minecraft.bukkit.shade.google.common.base.Splitter;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import se.ranzdo.bukkit.methodcommand.CommandHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class ChatterboxPlugin extends FacePlugin implements Listener {

    private static ChatterboxPlugin _INSTANCE;

    private Chat chat;
    private UrlValidator validator;
    private MasterConfiguration settings;
    private VersionedSmartYamlConfiguration groupsYaml;
    private SmartConfiguration dataYaml;
    private Map<UUID, GroupMenu> playerGroupMenuMap = new HashMap<>();
    private Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    private Map<String, GroupData> groupDataMap = new CaselessMap<>();
    private Map<String, TitleMenu> titleMenuMap = new CaselessMap<>();
    private PluginLogger debugPrinter;

    public static ChatterboxPlugin getInstance() {
        return _INSTANCE;
    }

    @Override
    public void enable() {
        debugPrinter = new PluginLogger(this);
        _INSTANCE = this;

        VersionedSmartYamlConfiguration configYaml = new VersionedSmartYamlConfiguration(
                new File(getDataFolder(), "config.yml"), getResource("config.yml"),
                VersionedConfiguration.VersionUpdateType.BACKUP_AND_UPDATE);
        if (configYaml.update()) {
            getLogger().info("Updating config.yml...");
            debug("Updating config.yml...");
        }
        groupsYaml = new VersionedSmartYamlConfiguration(
                new File(getDataFolder(), "groups.yml"), getResource("groups.yml"),
                VersionedConfiguration.VersionUpdateType.BACKUP_AND_UPDATE);
        if (groupsYaml.update()) {
            getLogger().info("Updating groups.yml...");
            debug("Updating groups.yml...");
        }
        dataYaml = new SmartYamlConfiguration(new File(getDataFolder(), "data.yml"));

        loadGroupData(groupsYaml);
        loadPlayerData(dataYaml);

        for (GroupData groupData : groupDataMap.values()) {
            titleMenuMap.put(groupData.getKey(), new TitleMenu(this, groupData));
        }

        settings = MasterConfiguration.loadFromFiles(configYaml);
        if (!setupChat()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getServer().getPluginManager().registerEvents(this, this);
        validator = new UrlValidator();

        CommandHandler commandHandler = new CommandHandler(this);
        commandHandler.registerCommands(new TitleCommand(this));

        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                saveGroupData(groupsYaml);
                savePlayerData(dataYaml);
            }
        }, 20L * 600, 20L * 600);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerData playerData = getPlayerDataMap().get(event.getPlayer().getUniqueId());
        if (playerData == null) {
            playerData = new PlayerData(event.getPlayer().getUniqueId());
            playerData.setTitle(settings.getString("config.default-title"));
            playerData.setTitleGroup(getTitleGroup(event.getPlayer()));
        }
        getPlayerDataMap().put(event.getPlayer().getUniqueId(), playerData);
        getPlayerGroupMenuMap().put(event.getPlayer().getUniqueId(), new GroupMenu(this, event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        getPlayerGroupMenuMap().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        getPlayerGroupMenuMap().remove(event.getPlayer().getUniqueId());
    }

    private void savePlayerData(SmartConfiguration configuration) {
        for (String key : configuration.getKeys(true)) {
            configuration.set(key, null);
        }
        for (Map.Entry<UUID, PlayerData> entry : playerDataMap.entrySet()) {
            configuration.set("titles." + entry.getKey().toString() + ".title", entry.getValue().getTitle());
            configuration.set("titles." + entry.getKey().toString() + ".title-group", entry.getValue().getTitleGroup());
            configuration.set("titles." + entry.getKey().toString() + ".ignore-list", entry.getValue().getIgnoreList());
        }
        configuration.save();
    }

    private void saveGroupData(SmartConfiguration configuration) {
        for (String key : configuration.getKeys(true)) {
            if (key.equals("version")) {
                continue;
            }
            configuration.set(key, null);
        }
        for (Map.Entry<String, GroupData> entry : groupDataMap.entrySet()) {
            configuration.set("groups." + entry.getKey() + ".titles", entry.getValue().getTitles());
            configuration.set("groups." + entry.getKey() + ".title-color", TextUtils.convertTag(entry.getValue()
                    .getTitleColor()));
            configuration.set("groups." + entry.getKey() + ".chat-color", TextUtils.convertTag(entry.getValue()
                    .getChatColor()));
            configuration.set("groups." + entry.getKey() + ".rank-description", entry.getValue().getRankDescription());
            configuration.set("groups." + entry.getKey() + ".title-description", entry.getValue().getTitleDescription());
            configuration.set("groups." + entry.getKey() + ".weight", entry.getValue().getWeight());
        }
    }

    private void loadPlayerData(SmartConfiguration configuration) {
        playerDataMap.clear();
        if (!configuration.isConfigurationSection("titles")) {
            return;
        }
        ConfigurationSection titlesSection = configuration.getConfigurationSection("titles");
        Map<UUID, PlayerData> data = new HashMap<>();
        for (String key : titlesSection.getKeys(false)) {
            ConfigurationSection titleSection = titlesSection.getConfigurationSection(key);
            UUID uuid = UUID.fromString(key);
            PlayerData playerData = new PlayerData(uuid);
            playerData.setTitle(titleSection.getString("title"));
            playerData.setTitleGroup(titleSection.getString("title-group"));
            playerData.setIgnoreList(titleSection.getStringList("ignore-list"));
            data.put(uuid, playerData);
        }
        debug("Loaded players: " + data.size());
        playerDataMap.putAll(data);
    }

    private void loadGroupData(SmartConfiguration configuration) {
        groupDataMap.clear();
        if (!configuration.isConfigurationSection("groups")) {
            return;
        }
        ConfigurationSection groupsSection = configuration.getConfigurationSection("groups");
        Map<String, GroupData> data = new HashMap<>();
        for (String key : groupsSection.getKeys(false)) {
            ConfigurationSection groupSection = groupsSection.getConfigurationSection(key);
            GroupData groupData = new GroupData(key);
            groupData.setTitleColor(TextUtils.convertTag(groupSection.getString("title-color")));
            groupData.setChatColor(TextUtils.convertTag(groupSection.getString("chat-color")));
            groupData.setRankDescription(groupSection.getStringList("rank-description"));
            groupData.setTitleDescription(groupSection.getStringList("title-description"));
            groupData.setWeight(groupSection.getInt("weight"));
            groupData.setTitles(groupSection.getStringList("titles"));
            data.put(key, groupData);
        }
        debug("Loaded groups: " + data.size());
        groupDataMap.putAll(data);
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }

    public void debug(String... messages) {
        debug(Level.INFO, messages);
    }

    public void debug(Level level, String... messages) {
        if (debugPrinter != null && (settings == null || settings.getBoolean("config.debug", false))) {
            debugPrinter.log(level, Arrays.asList(messages));
        }
    }

    @Override
    public void disable() {
        saveGroupData(groupsYaml);
        savePlayerData(dataYaml);
    }

    public TitleMenu getTitleMenu(String key) {
        if (titleMenuMap.containsKey(key)) {
            return titleMenuMap.get(key);
        }
        return null;
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
        String newFormat = formatMessage(player, message);
        GroupData group = getGroupData(player);
        List<String> splitMessage = Splitter.on(" ").splitToList(newFormat);
        FancyMessage messageParts = new FancyMessage("");
        ChatColor color = ChatColor.GRAY;
        String title = playerDataMap.containsKey(player.getUniqueId()) ? playerDataMap.get(player.getUniqueId())
                .getTitle() : settings.getString("config.default-title");
        for (int i = 0; i < splitMessage.size(); i++) {
            if (i == 2) {
                color = ChatColor.getByChar(splitMessage.get(i).substring(1, 2));
            }
            String s = splitMessage.get(i);
            String str = ChatColor.stripColor(s);
            if (str.equalsIgnoreCase(player.getDisplayName() + ":")) {
                String lev = ChatColor.WHITE + player.getName() + " - Level " + player.getLevel();
                String[] rankDesc = TextUtils.color(group.getRankDescription()).toArray(
                        new String[group.getRankDescription().size()]);
                String[] titleDesc = TextUtils.color(group.getTitleDescription()).toArray(
                        new String[group.getTitleDescription().size()]);
                messageParts.then(s).tooltip(StringUtils.concat(StringUtils.concat(lev, rankDesc), titleDesc));
            } else if (str.equalsIgnoreCase(title)) {
                messageParts.then(TextUtils.findFirstColor(s) + "[" + s + "]").tooltip(StringUtils.concat(group
                        .getRankDescription(), group.getTitleDescription()));
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
        for (Player receiver : receivers) {
            PlayerData playerData = getPlayerDataMap().get(receiver.getUniqueId());
            if (playerData == null) {
                continue;
            }
            if (playerData.getIgnoreList().contains(player.getUniqueId().toString())) {
                continue;
            }
            messageParts.send(receiver);
        }
    }

    public Map<UUID, PlayerData> getPlayerDataMap() {
        return playerDataMap;
    }

    public Map<String, GroupData> getGroupDataMap() {
        return groupDataMap;
    }

    private GroupData getGroupData(Player player) {
        GroupData group = null;
        for (GroupData data : getGroups(player)) {
            if (group == null) {
                group = data;
                continue;
            }
            if (data.getWeight() > group.getWeight()) {
                group = data;
            }
        }
        return group;
    }

    private String formatMessage(Player player, String message) {
        GroupData group = getGroupData(player);
        String title = playerDataMap.containsKey(player.getUniqueId()) ? playerDataMap.get(player.getUniqueId())
                .getTitle() : settings.getString("config.default-title");
        String template = settings.getString("config.format");
        return TextUtils.args(template, new String[][]{
                {"%name%", player.getDisplayName()},
                {"%message%", message},
                {"%title-color%", group.getTitleColor() + ""},
                {"%chatcolor%", group.getChatColor() + ""},
                {"%title%", title}
        });
    }

    public String getTitleGroup(Player player) {
        String titleGroup = "";
        int lastWeight = 0;
        for (Map.Entry<String, GroupData> entry : getGroupDataMap().entrySet()) {
            if (player.hasPermission("easytitles.group." + entry.getKey()) && entry.getValue().getWeight() > lastWeight) {
                titleGroup = entry.getKey();
                lastWeight = entry.getValue().getWeight();
            }
        }
        return titleGroup;
    }

    public List<GroupData> getGroups(Player player) {
        List<GroupData> groupDatas = new ArrayList<>();
        for (Map.Entry<String, GroupData> entry : groupDataMap.entrySet()) {
            if (!player.hasPermission("easytitles.group." + entry.getKey())) {
                continue;
            }
            groupDatas.add(entry.getValue());
        }
        return groupDatas;
    }

    public Map<UUID, GroupMenu> getPlayerGroupMenuMap() {
        return playerGroupMenuMap;
    }
}
