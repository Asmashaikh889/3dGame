package com.notbestlord.core.rpg.skills;

import com.notbestlord.core.utils.Utils;

import java.util.function.Function;

public enum SubSkillType {
    combat_aura("Gain Aura-Combat experience by killing enemies using aura-based abilities."),
    combat_axe("Gain Axe-Combat experience by killing enemies using an axe."),
    combat_ranged("Gain Ranged-Combat experience by killing enemies using a ranged weapon."),
    combat_scythe("Gain Scythe-Combat experience by killing enemies using a scythe."),
    combat_spear("Gain Spear-Combat experience by killing enemies using a spear."),
    combat_sword("Gain Sword-Combat experience by killing enemies using a sword."),
    craftsmanship_gem_refining("Gain Gem Refining experience by refining gems."),
    craftsmanship_ore_processing("Gain Ore Processing experience by processing ores."),
    magic_alchemy("Gain Alchemy experience by concocting potions and poisons."),
    magic_dark_magic("Gain Dark-Magic experience by using dark-magic based abilities."),
    magic_holy_magic("Gain Holy-Magic experience by using holy-magic based abilities."),
    magic_mana_manipulation("Gain Mana-Manipulation experience by using mana-manipulation based abilities."),
    magic_spell_casting("Gain Spell-Casting experience by casting magic spells."),
    none("");

    private final String description;
    private final Function<Integer, String> benefits;

    SubSkillType(String description) {
        this.description = description;
        this.benefits = null;
    }

    SubSkillType(String description, Function<Integer, String> benefits) {
        this.description = description;
        this.benefits = benefits;
    }

    public String getBenefits(int level) {
        return this.benefits != null ? benefits.apply(level) : "";
    }

    public String getStr() {
        return Utils.UpperCaseStart(name().substring(name().indexOf('_') + 1));
    }

    public String getDescription() {
        return description;
    }
}
