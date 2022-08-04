package com.notbestlord.core.utils;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Consts {
    public static final float SPECULAR_POWER = 10f;

    public static final float FOV = (float) Math.toRadians(60);
    public static final float Z_Near = 0.01f;
    public static final float Z_Far = 1000f;

    public static float deltaTime = 0;
    public static float trueDeltaTime = 0;

    public static final float PLAYER_MOVE_SPEED = 0.04f;
    public static final float PLAYER_EYE_HEIGHT = 0.5f;

    public static final float TERMINAL_VELOCITY_Y = 0.01f;
    public static final float GRAVITY_ACCEL = 0.00025f;

    public static final int MAX_POINT_LIGHTS = 5;
    public static final int MAX_SPOT_LIGHTS = 5;


    public static final String TITLE = "Game ";

    public static final float MOUSE_SENSITIVITY = 0.25f;

    public static final Vector4f DEFAULT_COLOR = new Vector4f(1f,1f,1f,1f);

    public static final Vector3f AMBIENT_LIGHT = new Vector3f(0f, 0f, 0f);
}
