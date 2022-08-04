package com.notbestlord.core.entity;

import com.notbestlord.ClientLauncher;
import com.notbestlord.core.Camera;
import com.notbestlord.core.gui.PlayerBars;
import com.notbestlord.core.gui.PlayerLog;
import com.notbestlord.core.gui.PlayerPauseMenu;
import com.notbestlord.core.rpg.npcs.NPC_ID;
import com.notbestlord.core.rpg.npcs.NpcConversingHandler;
import com.notbestlord.core.rpg.stats.Stat;
import com.notbestlord.core.rpg.stats.StatusEffectType;
import com.notbestlord.core.utils.Consts;
import com.notbestlord.network.client.ClientPlayerInventory;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player extends Entity {
    private final Camera camera;
    private float eyeHeight;

    private final PlayerLog log;
    private final PlayerBars playerBars;
    private final PlayerPauseMenu playerPauseMenu;
    private ClientPlayerInventory inventory;
    private NpcConversingHandler npcConversingHandler;

    public Player(Entity entity, Camera camera) {
        super(entity.getModel(), entity.getPos(), entity.getRotation(), entity.getScale());

        this.camera = camera;
        this.eyeHeight = Consts.PLAYER_EYE_HEIGHT;

        inventory = new ClientPlayerInventory();

        this.log = new PlayerLog();

        this.playerBars = new PlayerBars(this);

        this.playerPauseMenu = new PlayerPauseMenu();

        npcConversingHandler = new NpcConversingHandler(this);
    }

    public void startGui(){
        ClientLauncher.getWindow().getGuiManager().addGui(log);
        ClientLauncher.getWindow().getGuiManager().addGui(playerBars);
    }
    public void endGui(){

        ClientLauncher.getWindow().getGuiManager().removeGui(log);
        ClientLauncher.getWindow().getGuiManager().removeGui(playerBars);
    }

    public ClientPlayerInventory getInventory() {
        return inventory;
    }

    public void frameUpdate(){
        npcConversingHandler.frameUpdate();
        Map<StatusEffectType, Float> map = new HashMap<>(inventory.gui.statHandler.effects);
        for(StatusEffectType type : map.keySet()){
            if(inventory.gui.statHandler.effects.get(type) != -1){
                inventory.gui.statHandler.effects.put(type, inventory.gui.statHandler.effects.get(type) - Consts.deltaTime);
                if(inventory.gui.statHandler.effects.get(type) < 0){
                    inventory.gui.statHandler.effects.remove(type);
                }
            }
        }
    }

    public void updatePosition(){
        camera.setPosition(new Vector3f(this.getPos()).add(0,eyeHeight,0));
    }

    public void updatePosition(Vector3f position){
        setPos(position);
        updatePosition();
    }

    public void moveRotation(float x, float y){
        camera.moveRotation(x,y, 0);
    }

    public float getStat(Stat stat){
        return inventory.gui.statHandler.stats.containsKey(stat) ? inventory.gui.statHandler.stats.get(stat) : -1;
    }

    public void setCurrentNPCDialogue(NPC_ID id, List<String> dialogue){
        npcConversingHandler.setCurrentDialogue(id, dialogue);
    }

    public Camera getCamera() {
        return camera;
    }

    public void addToLog(String str){
        log.addLog(str);
    }

    public void clearLog(){
        log.clearLog();
    }

    public void openPauseMenu(){
        playerPauseMenu.toggle();
    }
    public void closePauseMenu(){
        playerPauseMenu.close();
    }

    @Override
    public EntityType getEntityType(){
        return EntityType.player;
    }
}
