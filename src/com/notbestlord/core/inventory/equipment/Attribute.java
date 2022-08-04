package com.notbestlord.core.inventory.equipment;

import java.awt.*;

public enum Attribute {
	none(new Color(255, 255, 255, 255)),
	fire(new Color(229, 120, 74)),
	water(new Color(83, 197, 255)),
	earth(new Color(126, 94, 72)),
	wind(new Color(164, 220, 208)),
	plant(new Color(75, 183, 26)),
	ice(new Color(97, 244, 255)),
	holy(new Color(255, 237, 0)),
	shadow(new Color(52, 16, 84)),
	lightning(new Color(163, 137, 255)),
	insanity(new Color(53, 75, 61)),
	magic(new Color(0, 164, 255));
	private final Color color;

	Attribute(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}
}
