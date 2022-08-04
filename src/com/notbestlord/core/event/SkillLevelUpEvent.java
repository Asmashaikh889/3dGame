package com.notbestlord.core.event;

import com.notbestlord.network.server.entity.ServerPlayer;

public class SkillLevelUpEvent {
    private int level;
    private ServerPlayer player;

    public int getLevel() {
        return level;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public SkillLevelUpEvent(int level, ServerPlayer player) {
        this.level = level;
        this.player = player;
    }
}
