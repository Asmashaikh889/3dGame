package com.notbestlord.core.rpg.skills;

import com.notbestlord.ServerLauncher;
import com.notbestlord.core.utils.Utils;
import com.notbestlord.network.packet.player.rpg.SkillUpdatePacket;
import com.notbestlord.network.server.entity.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

public class Skill {
    private SkillType skillType;
    private int level;
    private float currentExp;
    private float requiredExp;

    public static void initSkillSystem(){
        Map<Integer, Float> skillExpRequirements = new HashMap<>();
        skillExpRequirements.put(1,50f);
        skillExpRequirements.put(2,125f);
        skillExpRequirements.put(3,200f);
        skillExpRequirements.put(4,300f);
        skillExpRequirements.put(5,500f);
        skillExpRequirements.put(6,750f);
        skillExpRequirements.put(7,1000f);
        skillExpRequirements.put(8,1500f);
        skillExpRequirements.put(9,2000f);
        skillExpRequirements.put(10,3500f);
        skillExpRequirements.put(11,5000f);
        skillExpRequirements.put(12,7500f);
        skillExpRequirements.put(13,10000f);
        skillExpRequirements.put(14,15000f);
        skillExpRequirements.put(15,20000f);
        skillExpRequirements.put(16,30000f);
        skillExpRequirements.put(17,50000f);
        skillExpRequirements.put(18,75000f);
        skillExpRequirements.put(19,100000f);
        skillExpRequirements.put(20,200000f);
        skillExpRequirements.put(21,300000f);
        skillExpRequirements.put(22,400000f);
        skillExpRequirements.put(23,500000f);
        skillExpRequirements.put(24,600000f);
        skillExpRequirements.put(25,700000f);
        skillExpRequirements.put(26,-1f);
        Utils.skillExpRequirements = skillExpRequirements;
    }
    public Skill() {}
    public Skill(SkillType skillType) {
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
            skillType.triggerLevelUpEvent(player, level);
            incExp(0);
        }
        ServerLauncher.getGameServer().sendPacketToPlayer(player.getUuid(), new SkillUpdatePacket(this));
    }

    public SkillType getSkillType() {
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
