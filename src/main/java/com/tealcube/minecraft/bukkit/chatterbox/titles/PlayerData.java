package com.tealcube.minecraft.bukkit.chatterbox.titles;

import com.google.common.base.Objects;

import java.util.UUID;

public class PlayerData {

    private final UUID uniqueId;
    private String title;
    private String titleGroup;

    public PlayerData(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleGroup() {
        return titleGroup;
    }

    public void setTitleGroup(String titleGroup) {
        this.titleGroup = titleGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerData)) return false;
        PlayerData that = (PlayerData) o;
        return Objects.equal(getUniqueId(), that.getUniqueId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUniqueId());
    }

}
