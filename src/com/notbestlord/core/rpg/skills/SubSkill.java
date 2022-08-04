package com.notbestlord.core.rpg.skills;

import com.notbestlord.ServerLauncher;
import com.notbestlord.core.utils.Utils;
import com.notbestlord.network.packet.player.rpg.SubSkillUpdatePacket;
import com.notbestlord.network.server.entity.ServerPlayer;

public class SubSkill {
    private SubSkillType skillType;
    private int level;
    private float currentExp;
    private float requiredExp;

    public SubSkill() {}

    public SubSkill(SubSkillType skillType) {
        this.skillType = skillType;
        level = 0;
        currentExp = 0;
        requiredExp = Utils.skillExpRequirements.get(1);
    }

    public void incExp(float inc){
        currentExp += inc;
        if(currentExp >= requiredExp && requiredExp != -1f){
            currentExp -= requiredExp;
            level++;
            requiredExp = Utils.skillExpRequirements.get(level+1);
            incExp(0);
        }
    }
    public void incExp(float inc, ServerPlayer player){
        currentExp += inc;
        if(currentExp >= requiredExp && requiredExp != -1f){
            currentExp -= requiredExp;
            level++;
            requiredExp = Utils.skillExpRequirements.get(level+1);
            incExp(0);
        }
        ServerLauncher.getGameServer().sendPacketToPlayer(player.getUuid(), new SubSkillUpdatePacket(this));
    }
    public SubSkillType getSkillType() {
        return skillType;
    }

    public int getLevel() {
        return level;
    }

    public float getCurrentExp() {
        return currentExp;
    }

    public float getRequiredExp() {
        return requiredExp;
    }
}
