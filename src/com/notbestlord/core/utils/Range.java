package com.notbestlord.core.utils;

public class Range<T> {
	private T min;
	private T max;

	public Range(T min, T max) {
		this.min = min;
		this.max = max;
	}

	public T getMin() {
		return min;
	}

	public void setMin(T min) {
		this.min = min;
	}

	public T getMax() {
		return max;
	}

	public void setMax(T max) {
		this.max = max;
	}

	public static int randomInt(int min, int max){
		return (int) (Math.random() * (max - min)) + min;
	}
	public static float randomFloat(float min, float max){
		return (float) ((Math.random() * (max - min)) + min);
	}
}
