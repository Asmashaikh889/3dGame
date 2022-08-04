package com.notbestlord.core.rpg.skills;

import com.notbestlord.core.event.SkillLevelUpEvent;
import com.notbestlord.core.rpg.stats.Stat;
import com.notbestlord.core.utils.Utils;
import com.notbestlord.network.server.entity.ServerPlayer;

import java.util.function.Consumer;
import java.util.function.Function;

public enum SkillType {
    none(null, null, "", null),
    foraging(null, event -> event.getPlayer().getStatHandler().incStat(Stat.luck, 0.1f),
            "Gain Foraging experience by cutting down trees.",
            level -> "+0.1 Luck per Level. (+"+(0.1*level)+")"),
    mining(null, event -> event.getPlayer().getStatHandler().incStat(Stat.luck, 0.1f),
            "Gain Mining experience by mining or killing mining-based mobs.",
            level -> "+0.1 Luck / Level. (+"+(0.1*level)+")"),
    combat(new SubSkillType[]{SubSkillType.combat_aura, SubSkillType.combat_axe, SubSkillType.combat_ranged,
            SubSkillType.combat_spear, SubSkillType.combat_sword, SubSkillType.combat_scythe},
        event -> {
            event.getPlayer().getStatHandler().incStat(Stat.strength, 0.25f);
            event.getPlayer().getStatHandler().incStat(Stat.critical_chance, 2f);
        },
            "Gain Combat experience by killing mobs.",
            level -> "+0.25 Strength / Level. (+"+(0.25*level)+") +2 Critical Chance / Level. (+"+(2*level)+")"),
    magic(new SubSkillType[]{SubSkillType.magic_alchemy, SubSkillType.magic_mana_manipulation, SubSkillType.magic_spell_casting},
            event -> {
                event.getPlayer().getStatHandler().incStat(Stat.mana_max, 5f);
                event.getPlayer().getStatHandler().incStatMul(Stat.mana_recovery, 0.01f);
            }, "Gain Magic experience by using magic or by increasing your knowledge of magic.",
            level -> "+5 Max Mana / Level. (+"+(5*level)+") +0.01 Mana Recovery / Level. (+"+(0.01*level)+")"),
    craftsmanship(new SubSkillType[]{SubSkillType.craftsmanship_gem_refining, SubSkillType.craftsmanship_ore_processing},
            event -> event.getPlayer().getStatHandler().incStat(Stat.dexterity, 0.1f),
            "Gain Craftsmanship experience by crafting items or processing materials.",
            level -> "+0.1 Dexterity / Level. (+"+(0.1*level)+")"),
    fishing(null, event -> event.getPlayer().getStatHandler().incStat(Stat.luck, 0.1f),
            "Gain Fishing experience by fishing.",
            level -> "+0.1 Luck / Level. (+"+(0.1*level)+")"),
    farming(null, event -> event.getPlayer().getStatHandler().incStat(Stat.luck, 0.1f),
            "Gain Farming experience by harvesting crops or butchering animals.",
            level -> "+0.1 Luck / Level. (+"+(0.1*level)+")");
    private final SubSkillType[] subSkills;
    private final Consumer<SkillLevelUpEvent> levelUpEvent;
    private final String description;
    private final Function<Integer, String> benefits;

    SkillType(SubSkillType[] subSkills, Consumer<SkillLevelUpEvent> levelUpEvent, String description, Function<Integer, String> benefits) {
        this.subSkills = subSkills;
        this.levelUpEvent = levelUpEvent;
        this.description = description;
        this.benefits = benefits;
    }

    public void triggerLevelUpEvent(ServerPlayer player, int level){
        if(levelUpEvent != null) levelUpEvent.accept(new SkillLevelUpEvent(level, player));
    }

    public String getBenefits(int level){
        return benefits != null ? benefits.apply(level) : "";
    }

    public boolean doesHaveSubSkills(){
        return subSkills != null;
    }

    public SubSkillType[] getSubSkills(){
        return subSkills;
    }

    public String getStr(){
        return Utils.UpperCaseStart(name());
    }

    public String getDescription(){
        return description;
    }
}
