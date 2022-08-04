package com.notbestlord.core.rpg;

import com.notbestlord.ServerLauncher;
import com.notbestlord.core.inventory.ItemManager;
import com.notbestlord.core.physics.DamageBox;
import com.notbestlord.core.physics.DamageBoxType;
import com.notbestlord.core.rpg.skills.Skill;
import com.notbestlord.core.rpg.skills.SkillType;
import com.notbestlord.core.rpg.skills.SubSkill;
import com.notbestlord.core.rpg.skills.SubSkillType;
import com.notbestlord.core.rpg.stats.Stat;
import com.notbestlord.core.rpg.stats.StatusEffect;
import com.notbestlord.core.rpg.stats.StatusEffectType;
import com.notbestlord.core.utils.Consts;
import com.notbestlord.network.server.entity.ServerPlayer;

public class RPGUtils {

	public static boolean doesMeetRequirements(IRPGEntity entity, String requirements){
		for(String req : requirements.split("&&")){
			String[] args0 = req.split("\\s+");
			String[] args1 = args0[0].split("\\.");
			try {
				switch (args1[0]) {
					case "stat" -> {
						float stat = entity.getStat(Stat.valueOf(args1[1]));
						float reqStat = Float.parseFloat(args0[2]);
						switch (args0[1]) {
							case "==" -> {
								if (stat != reqStat) return false;
							}
							case ">=" -> {
								if (stat < reqStat) return false;
							}
							case "<=" -> {
								if (stat > reqStat) return false;
							}
						}
					}
					case "status_effect" -> {
						if(!entity.getStatHandler().hasStatusEffect(StatusEffectType.valueOf(args1[1]))){
							return false;
						}
					}
					case "skills" -> {
						if(entity instanceof ServerPlayer player) {
							int lvl = 0;
							if (args1[1].equalsIgnoreCase("skill")) {
								lvl = player.getRPGHandler().skillHandler.getSkill(SkillType.valueOf(args1[2])).getLevel();
							} else if (args1[1].equalsIgnoreCase("subskill")) {
								lvl = player.getRPGHandler().skillHandler.getSubSkill(SubSkillType.valueOf(args1[2])).getLevel();
							}
							int reqLvl = Integer.parseInt(args0[2]);
							switch (args0[1]) {
								case "==" -> {
									if (lvl != reqLvl) return false;
								}
								case ">=" -> {
									if (lvl < reqLvl) return false;
								}
								case "<=" -> {
									if (lvl > reqLvl) return false;
								}
							}
						}
					}
					case "ability" -> {
						if(entity instanceof ServerPlayer player) {
							if (args1[1].equalsIgnoreCase("active")) {
								if (!player.getRPGHandler().abilityHandler.hasActiveAbility(args1[2])) {
									return false;
								}
							} else if (args1[1].equalsIgnoreCase("passive")) {
								if (!player.getRPGHandler().abilityHandler.hasPassiveAbility(args1[2])) {
									return false;
								}
							}
						}
					}
					case "inv" -> {
						if(entity instanceof ServerPlayer player) {
							if (args1[1].equalsIgnoreCase("coins")) {
								int reqCoins = Integer.parseInt(args0[2]);
								int coins = player.getInventory().getCoins();
								switch (args0[1]) {
									case "==" -> {
										if (coins != reqCoins) return false;
									}
									case ">=" -> {
										if (coins < reqCoins) return false;
									}
									case "<=" -> {
										if (coins > reqCoins) return false;
									}
								}
							} else if (args1[1].equalsIgnoreCase("item")) {
								if (!player.getInventory().containsAtLeast(args1[2], Integer.parseInt(args0[1]))) {
									return false;
								}
							}
						}
					}
				}
			}
			catch (Exception ignored){
				return false;
			}
		}
		return true;
	}

	public static boolean triggerEvent(IRPGEntity player, String event){
		if(event.contains("?")) {
			String[] arg0 = event.split("\\?");
			if (arg0.length >= 2 && doesMeetRequirements(player, arg0[0])) {
				System.out.println(arg0[1]);
				applyEventEffect(player, arg0[1]);
				return true;
			}
		}
		else{
			applyEventEffect(player, event);
			return true;
		}
		return false;
	}

	public static void applyEventEffect(IRPGEntity entity, String event){
		// stat.[stat] [+-] [n] && skill.[skill/subskill].[name] [+-] [n]
		for(String req : event.split("&&")){  // ["stat.[stat] [+-] [n]" , "skill.[skill/subskill].[name] [+-] [n]"]
			String[] args0 = req.split("\\s+"); // ["stat.[stat]" ,"[+-*/]" ,"[n]"]
			String[] args1 = args0[0].split("\\."); // ["stat" ,"[stat]"]
			try {
				switch (args1[0]) {
					case "stat" -> {
						Stat stat =  Stat.valueOf(args1[1]);
						float n = Float.parseFloat(args0[2]);
						if(args0.length > 3 && args0[3].equals("dt")){
							n *= Consts.deltaTime;
						}
						else if(args0.length > 3 && args0[3].equals("trueDt")){
							n *= Consts.trueDeltaTime;
						}
						switch (args0[1]) {
							case "+" -> entity.getStatHandler().incStat(stat, n);
							case "-" -> entity.getStatHandler().incStat(stat, -n);
							case "*" -> entity.getStatHandler().incStatMul(stat, n);
							case "/" -> entity.getStatHandler().incStatMul(stat, 1f / n);
						}
					}
					case "status_effect" -> entity.getStatHandler().addStatusEffect(new StatusEffect(StatusEffectType.valueOf(args1[1]), Float.parseFloat(args0[1])));
					case "skills" -> {
						if(entity instanceof ServerPlayer player) {
							if (args1[1].equalsIgnoreCase("skill")) {
								Skill skill = player.getRPGHandler().skillHandler.getSkill(SkillType.valueOf(args1[2]));
								switch (args0[1]) {
									case "+" -> skill.incExp(Float.parseFloat(args0[2]));
									case "-" -> skill.incExp(-Float.parseFloat(args0[2]));
								}
							} else if (args1[1].equalsIgnoreCase("subskill")) {
								SubSkill skill = player.getRPGHandler().skillHandler.getSubSkill(SubSkillType.valueOf(args1[2]));
								switch (args0[1]) {
									case "+" -> skill.incExp(Float.parseFloat(args0[2]));
									case "-" -> skill.incExp(-Float.parseFloat(args0[2]));
								}
							}
						}
					}
					case "ability" -> {
						if(entity instanceof ServerPlayer player) {
							if (args1[1].equalsIgnoreCase("active")) {
								if (!player.getRPGHandler().abilityHandler.hasActiveAbility(args1[2])) {
									player.getRPGHandler().abilityHandler.addActiveAbility(args1[2]);
								}
							} else if (args1[1].equalsIgnoreCase("passive")) {
								if (!player.getRPGHandler().abilityHandler.hasPassiveAbility(args1[2])) {
									player.getRPGHandler().abilityHandler.addPassiveAbility(args1[2]);
								}
							}
						}
					}
					case "inv" -> {
						if(entity instanceof ServerPlayer player) {
							if (args1[1].equalsIgnoreCase("coins")) {
								switch (args0[1]) {
									case "+" -> player.getInventory().incCoins(Integer.parseInt(args0[2]));
									case "-" -> player.getInventory().incCoins(-Integer.parseInt(args0[2]));
								}
							} else if (args1[1].equalsIgnoreCase("item")) {
								player.getInventory().addItem(ItemManager.getItem(args1[2], Integer.parseInt(args1[3])));
							}
						}
					}
					case "incQi" -> entity.getStatHandler().incInnerQi(Float.parseFloat(args0[1]));
					case "damage_box" -> {
						if(entity instanceof ServerPlayer player){
							ServerLauncher.getGameServer().getPhysics().addDamageBox(new DamageBox(
									DamageBoxType.valueOf(args0[1]),
									Integer.parseInt(args0[2]),
									entity.getPosition(),
									args0[3].replace("$", " "),
									player.getCameraRotation().y, player.getCameraRotation().x));
						}
					}
					case "recipes" -> {
						if(entity instanceof ServerPlayer player){
							if(args1[1].equalsIgnoreCase("station")) {
								switch (args0[1]) {
									case "add" -> player.getInventory().addCraftingStation(args1[2]);
									case "remove" -> player.getInventory().removeCraftingStation(args1[2]);
								}
								player.getInventory().refreshUnlockedRecipes();
							}
						}
					}
				}
			}
			catch (Exception ignored) {}
		}
	}
}
