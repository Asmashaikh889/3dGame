package com.notbestlord.core.rpg.classes;

public enum ClassType {
	/*
	Main classes can be switched using a [Class Tome].
	When a class reaches max lvl (determined by difficulty, basic, intermediate, etc.), player can choose another class to use, the previous classes abilities will become permanent.
	When choosing a new class, either a basic class or a higher-tier available class can be chosen, example:
	Player[
	Class have:
	Healer lvl Max
	Tank lvl Max
	Priest lvl Max

	Next Class Options:
	Basic Class not max lvl.
	Paladin (Healer + Tank).
	Saint (Priest).
	]
	 */

	// Basic Classes (max lvl 10)
	warrior, // sword--
	magician, // magick
	tank, // meat shield
	healer, // heal
	bard, // music buff and aoe
	rouge, // throw
	ranger, // bow and gun

	// Intermediate Classes (max lvl 25)
	aura_fighter, // aura better
	poet, // bard++
	priest, // healer++
	paladin, // tank + healer
	swordsman, // sword
	alchemist, // brew potions
	thief, // steal
	gunner, // gun
	archer, // bow
	enchanter, // buff & de-buff

	// Advanced Classes (max lvl 50)
	saint,  // healer + holy
	knight, // sword++
	aura_knight, // aura better with weapon
	witch, // alchemist++
	assassin, // sneaky
	illusionist, // illusion mage
	sniper, // sniper
	summoner, // summon

	// Master Classes (max lvl 100)
	swords_master, // sword Max
	aura_master, // aura Max
	pope, // holy + healer Max
	idol, // bard Max
	wizard, // mage Max
	bow_master, // bow Max
	gun_master, // sex pistols

	// Unique Classes (max lvl class specific), just special case classes, non-tree progression
	jack_of_all_trades,
	/*
	 no max lvl.
	 at lvl 10, (Passive) Jack Of All Trades I : gain 20% more skill exp, 10% boost to all stats.
	 at lvl 25, (Passive) Jack Of All Trades II : gain 20% more skill exp, 10% boost to all stats.
	 at lvl 50, (Passive) Jack Of All Trades III : gain 20% more skill exp, 10% boost to all stats.
	 at lvl 75, (Passive) Jack Of All Trades IV : gain 20% more skill exp, 10% boost to all stats.
	 at lvl 100, (Passive) Jack Of All Trades V : gain 20% more skill exp, 10% boost to all stats.
	*/
	spy, // sneaky style - max lvl 75 - abilities cap at 25, every lvl after 25 gain +5% stealth - abilities scale with stealth.
	engineer, // gun + turrets - no max lvl - abilities cap at 50, every 5 levels after 50, gain 1 more turret slot & 5% more stats for engineering.
	scholar, // knowledge is power - max lvl 5 - gain exp when using a spell for the first time.
	tamer, // tame animals and spirits - max lvl 25 - gain exp when taming an animal / spirit.
	morpher, // gain the skills and stats of slain enemies - max lvl 100.
	mech_rider, // Mech - max lvl 75.
	vampire, // Use health as trigger for vampire abilities - max lvl 50
	demon_weapon, // soul eater human weapons - max lvl 100 -
	/*
	 different types :
	 gun - gun.
	 stationary - trap / summon.
	 multi-form - aoe.
	 any type of combat weapon - any attacks gives exp for sub skill of type.

	 universal abilities:
	 soul resonance - special attack
	 full-weapon transformation - turn into weapon, special skills unlocked while weapon.
	 */

	// CLASS HARD LEVEL CAP - 999

	// undecided:
	botanist, // inspect and collect plants
	farmer, // harvest plants (use scythe)

	;



}
