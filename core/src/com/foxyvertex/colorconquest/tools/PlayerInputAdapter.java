package com.foxyvertex.colorconquest.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.foxyvertex.colorconquest.Finals;
import com.foxyvertex.colorconquest.Globals;
import com.foxyvertex.colorconquest.managers.Levels;

/**
 * Processes the input to move the player
 */
public class PlayerInputAdapter extends InputAdapter implements InputProcessor {

    private float currentJumpLength = 0;
    private boolean isSpacePreviousPressed = false;

    private boolean backKeyPrev = false;
    private boolean forwardKeyPrev = false;
    private boolean canJump = true;

    /**
     * Sets the classes player variable to the player
     */
    public PlayerInputAdapter() {
    }

    /**
     * Handles all of the input
     *
     * @param delta a float that is the amount of time in seconds since the last frame
     */
    public void handleInput(float delta) {

        float maxJumpForceLength = 0.2f;

        //DEBUG JUNK
        if (Gdx.input.isKeyPressed(Input.Keys.EQUALS))
            Globals.gameScreen.cam.zoom += 3 / Finals.PPM;
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS))
            Globals.gameScreen.cam.zoom -= 3 / Finals.PPM;
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            Globals.gameScreen.player.body.setLinearVelocity(new Vector2(0, 0));
            Globals.gameScreen.player.body.setTransform(Globals.gameScreen.player.spawnPoint, Globals.gameScreen.player.body.getAngle());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            Levels.Level nextLevel = Globals.gameScreen.currentLevel.nextLevel;
            Globals.gameScreen.switchLevel(nextLevel);
        }



        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            currentJumpLength += delta;

            if (currentJumpLength >= maxJumpForceLength) canJump = false;

            isSpacePreviousPressed = true;
        } else {
            currentJumpLength = 0f;
            canJump = Globals.gameScreen.player.body.getLinearVelocity().y == 0 && !isSpacePreviousPressed;
            isSpacePreviousPressed = false;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)) {
            Globals.gameScreen.player.runSpeed = Globals.gameScreen.player.maxRunSpeed;
            Globals.gameScreen.player.jumpForce = Globals.gameScreen.player.maxJumpForce;
        }


        if (Gdx.input.isKeyJustPressed(Input.Keys.S))
            Globals.gameScreen.player.body.applyLinearImpulse(new Vector2(0, -10f), Globals.gameScreen.player.body.getWorldCenter(), true);

        if (Gdx.input.isKeyPressed(Input.Keys.D) && Globals.gameScreen.player.body.getLinearVelocity().x <= 2) {
            Globals.gameScreen.player.body.applyLinearImpulse(new Vector2(Globals.gameScreen.player.runSpeed, 0), Globals.gameScreen.player.body.getWorldCenter(), true);
            forwardKeyPrev = true;
        } else if (!Gdx.input.isKeyPressed(Input.Keys.A) && forwardKeyPrev) {
            Globals.gameScreen.player.body.applyLinearImpulse(new Vector2(-Globals.gameScreen.player.runSpeed, 0), Globals.gameScreen.player.body.getWorldCenter(), true);
            forwardKeyPrev = false;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A) && Globals.gameScreen.player.body.getLinearVelocity().x >= -2) {
            Globals.gameScreen.player.body.applyLinearImpulse(new Vector2(-Globals.gameScreen.player.runSpeed, 0), Globals.gameScreen.player.body.getWorldCenter(), true);
            backKeyPrev = true;
        } else if (!Gdx.input.isKeyPressed(Input.Keys.A) && backKeyPrev) {
            Globals.gameScreen.player.body.applyLinearImpulse(new Vector2(-Globals.gameScreen.player.runSpeed, 0), Globals.gameScreen.player.body.getWorldCenter(), true);
            backKeyPrev = false;
        }


        if (!(currentJumpLength >= maxJumpForceLength) && currentJumpLength > 0 && canJump)
            Globals.gameScreen.player.body.applyLinearImpulse(new Vector2(0, Globals.gameScreen.player.jumpForce * delta), Globals.gameScreen.player.body.getWorldCenter(), true);

    }
}