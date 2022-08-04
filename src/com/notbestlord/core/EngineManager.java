package com.notbestlord.core;


import com.notbestlord.ClientLauncher;
import com.notbestlord.core.audio.AudioManager;
import com.notbestlord.core.utils.Consts;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class EngineManager {
    public static final long NANOSECOND = 1000000000l;
    public static final float FRAMERATE = 1000;

    private static int fps;
    private static float frametime = 1.0f / FRAMERATE;
    private boolean isRunning;

    private WindowManager window;
    private MouseInput mouseInput;
    private GLFWErrorCallback errorCallback;
    private ILogic gameLogic;
    private AudioManager audioManager;

    private void init() {
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        window = ClientLauncher.getWindow();
        audioManager = new AudioManager();
        gameLogic = ClientLauncher.getGame();
        mouseInput = new MouseInput();
        window.init();
        audioManager.init();
        mouseInput.init();
    }

    public void start() throws Exception{
        init();
        if(isRunning)
            return;
        run();
    }

    public void run() throws Exception {
        gameLogic.init();
        this.isRunning = true;
        int frames = 0;
        long frameCounter = 0;
        long lastTime = System.nanoTime();
        double unprocessedTime = 0;

        while (isRunning){
            boolean render = false;
            long startTime = System.nanoTime();
            long passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime / (double) NANOSECOND;
            frameCounter += passedTime;

            //input
            input();

            while(unprocessedTime > frametime){
                render = true;
                unprocessedTime -= frametime;
                if(window.windowShouldClose())
                    stop();

                if(frameCounter >= NANOSECOND){
                    setFps(frames);
                    window.setTitle(Consts.TITLE + getFps());
                    frames = 0;
                    frameCounter = 0;
                }
            }
            if(render){
                Consts.deltaTime = (float) passedTime / NANOSECOND;
                update();
                render();
                frames++;
            }
        }
        cleanup();
    }

    private void stop() {
        if(!isRunning)
            return;
        isRunning = false;
    }
    private void input() {
        mouseInput.input();
        gameLogic.input();
    }
    private void render() {
        gameLogic.render();
        window.update();
    }
    private void update() {
        gameLogic.update(mouseInput);
    }

    private void cleanup() {
        Config.saveConfig();
        window.cleanup();
        audioManager.cleanup();
        gameLogic.cleanup();
        errorCallback.free();
        GLFW.glfwTerminate();
    }

    public static int getFps() {
        return fps;
    }

    public static void setFps(int fps) {
        EngineManager.fps = fps;
    }
}
