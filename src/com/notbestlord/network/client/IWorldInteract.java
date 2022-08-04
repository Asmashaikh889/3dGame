package com.notbestlord.network.client;

import com.notbestlord.core.physics.Collider;
import com.notbestlord.network.data.MouseButton;
import com.notbestlord.network.server.entity.ServerPlayer;

public interface IWorldInteract {
	void Interact(ServerPlayer player, MouseButton mouseButton);

	Collider getCollider();
}
