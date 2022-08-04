package com.notbestlord.core.event;

public class KeyCaptureEvent {
    public String tag = "";
    private boolean captured = false;
    public int keyCode = 0;

    public void reset(){
        tag = "";
        captured = false;
        keyCode = 0;
    }

    public void capture(int keyCode){
        captured = true;
        this.keyCode = keyCode;
    }

    public boolean isCaptured() {
        return captured;
    }
}
