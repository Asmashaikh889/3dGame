package com.notbestlord;
import com.notbestlord.core.EngineManager;
import com.notbestlord.core.ILogic;
import com.notbestlord.core.WindowManager;
import com.notbestlord.core.utils.Consts;

public class ClientLauncher {

    private static WindowManager window;
    private static GameClient game;

    public static void main(String[] args){
        window = new WindowManager(Consts.TITLE, 1280,720,true);
        EngineManager engine = new EngineManager();
        game = new GameClient();
        try {
            engine.start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static WindowManager getWindow(){
        return window;
    }

    public static GameClient getGame() {
        return game;
    }
}
