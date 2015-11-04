package com.tealcube.minecraft.bukkit.chatterbox.titles;

import com.google.common.base.Objects;

import org.bukkit.ChatColor;

import java.util.List;

public class GroupData {

    private final String key;
    private List<String> titles;
    private ChatColor titleColor;
    private ChatColor chatColor;
    private List<String> rankDescription;
    private List<String> titleDescription;
    private int weight;

    public GroupData(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupData)) return false;
        GroupData groupData = (GroupData) o;
        return Objects.equal(key, groupData.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }

    public List<String> getTitles() {
        return titles;
    }

    public void setTitles(List<String> titles) {
        this.titles = titles;
    }

    public ChatColor getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(ChatColor titleColor) {
        this.titleColor = titleColor;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public void setChatColor(ChatColor chatColor) {
        this.chatColor = chatColor;
    }

    public List<String> getRankDescription() {
        return rankDescription;
    }

    public void setRankDescription(List<String> rankDescription) {
        this.rankDescription = rankDescription;
    }

    public List<String> getTitleDescription() {
        return titleDescription;
    }

    public void setTitleDescription(List<String> titleDescription) {
        this.titleDescription = titleDescription;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

}
