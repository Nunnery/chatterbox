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
