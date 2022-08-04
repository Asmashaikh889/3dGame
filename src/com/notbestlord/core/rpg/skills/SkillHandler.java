package com.notbestlord.core.rpg.skills;

import com.google.gson.annotations.Expose;
import com.notbestlord.core.inventory.equipment.Equipment;
import com.notbestlord.core.inventory.equipment.EquipmentWeapon;
import com.notbestlord.core.inventory.equipment.WeaponType;
import com.notbestlord.network.server.entity.ServerPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillHandler{
    @Expose(serialize = false)
    private ServerPlayer owner;

    private Map<SkillType, Skill> skills;
    private Map<SubSkillType, SubSkill> subSkills;

    public SkillHandler(ServerPlayer owner){
        skills = new HashMap<>();
        for(SkillType type : SkillType.values()){
            skills.put(type, new Skill(type));
        }
        skills.remove(SkillType.none);
        subSkills = new HashMap<>();
        for(SubSkillType type : SubSkillType.values()){
            subSkills.put(type, new SubSkill(type));
        }
        subSkills.remove(SubSkillType.none);
        this.owner = owner;
    }

    public void init(ServerPlayer owner){
        this.owner = owner;
    }

    public Skill getSkill(SkillType type){
        return skills.getOrDefault(type, null);
    }

    public void incSkillExp(SkillType type, float exp){
        if(skills.containsKey(type)) skills.get(type).incExp(exp, owner);
    }
    public void incCombatExp(float exp, Equipment equipment){
        incCombatExp(exp, equipment.getMain_hand() instanceof EquipmentWeapon ?
                ((EquipmentWeapon) equipment.getMain_hand()).getWeaponTypes() : null);
    }
    public void incCombatExp(float exp, List<WeaponType> weaponTypes){
        if(skills.containsKey(SkillType.combat)) skills.get(SkillType.combat).incExp(exp, owner);
        if(weaponTypes == null) return;
        float mul = (float) (exp * 0.2 / weaponTypes.size());
        for(WeaponType weaponType : weaponTypes){
            switch (weaponType){
                case axe -> {
                    if(subSkills.containsKey(SubSkillType.combat_axe)) subSkills.get(SubSkillType.combat_axe).incExp(mul, owner);
                }
                case aura -> {
                    if(subSkills.containsKey(SubSkillType.combat_aura)) subSkills.get(SubSkillType.combat_aura).incExp(mul, owner);
                }
                case scythe -> {
                    if(subSkills.containsKey(SubSkillType.combat_scythe)) subSkills.get(SubSkillType.combat_scythe).incExp(mul, owner);
                }
                case ranged -> {
                    if(subSkills.containsKey(SubSkillType.combat_ranged)) subSkills.get(SubSkillType.combat_ranged).incExp(mul, owner);
                }
                case spear -> {
                    if(subSkills.containsKey(SubSkillType.combat_spear)) subSkills.get(SubSkillType.combat_spear).incExp(mul, owner);
                }
                case sword -> {
                    if(subSkills.containsKey(SubSkillType.combat_sword)) subSkills.get(SubSkillType.combat_sword).incExp(mul, owner);
                }
                default -> {}
            }
        }
    }
    public void incSubSkillExp(SubSkillType type, float exp){
        if(subSkills.containsKey(type)) subSkills.get(type).incExp(exp, owner);
    }
    public SubSkill getSubSkill(SubSkillType type){
        return subSkills.getOrDefault(type, null);
    }

    public static SkillRecord getDefaultSkillRecord(){
        Map<SkillType, Skill> skills = new HashMap<>();
        Map<SubSkillType, SubSkill> subSkills = new HashMap<>();
        skills = new HashMap<>();
        for(SkillType type : SkillType.values()){
            skills.put(type, new Skill(type));
        }
        skills.remove(SkillType.none);
        subSkills = new HashMap<>();
        for(SubSkillType type : SubSkillType.values()){
            subSkills.put(type, new SubSkill(type));
        }
        subSkills.remove(SubSkillType.none);
        return new SkillRecord(skills,subSkills);
    }

    private String getPercent(float current, float max){
        if(((current / max) * 100) % 1 == 0){
            return "" + ((int)((current / max) * 100));
        }
        return "" + ((current / max) * 100);
    }

    public HashMap<SkillType, Skill> skillList(){
        return new HashMap<>(skills);
    }

    public HashMap<SubSkillType, SubSkill> subSkillList(){
        return new HashMap<>(subSkills);
    }
}
