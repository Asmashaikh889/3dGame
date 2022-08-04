package com.notbestlord.core.utils;

import java.awt.*;

public class ColoredString extends Color {
	private String string;
	public ColoredString(int r, int g, int b, String str) {
		super(r, g, b);
		string = str;
	}
	public ColoredString(Color c, String str) {
		super(c.getRed(),c.getGreen(),c.getBlue());
		string = str;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}
}
