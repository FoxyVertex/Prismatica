package com.foxyvertex.colorconquest.entities;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by seth on 3/24/2017.
 */

public abstract class Interactant extends SpriteBody {

    public float maxJumpForce = 300;
    public float minJumpForce = 55;
    public float jumpForce    = minJumpForce;
    public float maxRunSpeed  = 10f;
    public float minRunSpeed  = 0.2f;
    public float runSpeed     = minRunSpeed;
    public int health = 20;
    public boolean doFallDamage = true;
    public boolean isInvulnerable = false;

    Interactant(Vector2 spawnPoint) {
        super(spawnPoint);
        reInitVars();
    }

    public abstract void reInitVars();

    public abstract void attacked(SpriteBody attacker);

    public void die() {
        if(!isInvulnerable)
        setToDestroy = true;
    }
}
