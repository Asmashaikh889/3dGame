package com.notbestlord.core;

import com.notbestlord.core.event.KeyCaptureEvent;
import com.notbestlord.core.utils.Consts;
import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLUtil;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class WindowManager {
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private String glslVer = null;
    private GuiManager guiManager;

    private final String title;
    private int width,height;
    private long window;
    private boolean UiOpen, UiFocused;

    private List<KeyCaptureEvent> keyCaptures = new ArrayList<>();

    private boolean resize,vSync;

    private long audioContext;
    private long audioDevice;

    private final Matrix4f projectionMatrix;

    public WindowManager(String title, int width, int height, boolean vSync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync;
        this.projectionMatrix = new Matrix4f();
        this.guiManager = new GuiManager();
    }

    public void init(){
        GLFWErrorCallback.createPrint(System.err).set();
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);

        boolean maximised = false;
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if(width == 0 || height == 0){
            width = 100;
            height = 100;
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
            maximised = true;
        }
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetFramebufferSizeCallback(window, (window,width,height) ->{
           this.width = width;
           this.height = height;
           this.setResize(true);
        });
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if( action == GLFW_RELEASE && !keyCaptures.isEmpty() ){
                keyCaptures.forEach(event -> {
                    event.capture(key);
                });
            }
        });

        if(maximised){
            glfwMaximizeWindow(window);
        }
        else{
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - width) / 2,
                    (vidmode.height() - height) / 2
            );
        }
        glfwMakeContextCurrent(window);

        if(isvSync()){
            glfwSwapInterval(1);
        }

        glfwShowWindow(window);
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);

        //
        audioDevice = ALC10.alcOpenDevice(alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER));

        ALCCapabilities deviceCaps = ALC.createCapabilities(audioDevice);

        audioContext = ALC10.alcCreateContext(audioDevice, new int[] {0});

        ALC10.alcMakeContextCurrent(audioContext);

        AL.createCapabilities(deviceCaps);

        //
        GL.createCapabilities();
        glfwMakeContextCurrent(window);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glClearColor(0.0f,0.0f,0.0f,1.0f);
        GL11.glEnable(GL_DEPTH_TEST);
        GL11.glEnable(GL_STENCIL_TEST);
        GL11.glEnable(GL_CULL_FACE);
        GL11.glCullFace(GL_BACK);

        // setup ImGui
        ImGui.createContext();
        ImGui.getIO().addConfigFlags(ImGuiConfigFlags.DockingEnable);

        glslVer = "#version 130";
        imGuiGlfw.init(this.window, true);
        imGuiGl3.init(glslVer);
    }

    public void update(){
        imGuiGlfw.newFrame();
        ImGui.newFrame();

        guiManager.renderGuis();
        if(!guiManager.isInteractGuiClosed()){
            UiFocused = guiManager.renderInteractiveGuis();
            UiOpen = true;
            if(glfwGetInputMode(window, GLFW_CURSOR) != GLFW_CURSOR_NORMAL)
                glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }
        else if(UiOpen){
            UiOpen = false;
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        }

        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        if(ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)){
            final long backupWindow = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupWindow);
        }

        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    public void cleanup(){
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
        ALC.destroy();
        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);
        glfwDestroyWindow(window);
    }

    public void setClearColor(float r, float g, float b, float a){
        GL11.glClearColor(r,g,b,a);
    }

    public boolean isKeyPressed(int keyCode){
        return glfwGetKey(window, keyCode) == GLFW_PRESS && !UiOpen;
    }

    public boolean isKeyReleased(int keyCode){
        return glfwGetKey(window, keyCode) == GLFW_RELEASE && !UiOpen;
    }

    public boolean isGuiKeyPressed(int keyCode){
        return glfwGetKey(window, keyCode) == GLFW_PRESS && !UiFocused;
    }

    public boolean windowShouldClose(){
        return glfwWindowShouldClose(window);
    }

    public boolean isResize() {
        return resize;
    }

    public void setResize(boolean resize) {
        this.resize = resize;
    }

    public boolean isvSync() {
        return vSync;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getWindowHandler() {
        return window;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title){
        glfwSetWindowTitle(window, title);
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public boolean isUiOpen() {
        return UiOpen;
    }

    public Matrix4f updateProjectionMatrix(){
        float aspectRatio = (float) width/height;
        return projectionMatrix.setPerspective(Consts.FOV,aspectRatio,Consts.Z_Near,Consts.Z_Far);
    }
    public Matrix4f getProjectionMatrix(){
        return projectionMatrix;
    }
    public Matrix4f updateProjectionMatrix(Matrix4f matrix,  int width, int height){
        float aspectRatio = (float) width/height;
        return matrix.setPerspective(Consts.FOV,aspectRatio,Consts.Z_Near,Consts.Z_Far);
    }

    public void addKeyCaptureEvent(KeyCaptureEvent event){
        keyCaptures.add(event);
    }
    public void removeKeyCaptureEvent(KeyCaptureEvent event){
        keyCaptures.remove(event);
    }
}
