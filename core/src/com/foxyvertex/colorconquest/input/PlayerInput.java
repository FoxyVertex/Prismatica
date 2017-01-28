package com.foxyvertex.colorconquest.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.foxyvertex.colorconquest.Finals;
import com.foxyvertex.colorconquest.Globals;
import com.foxyvertex.colorconquest.managers.Levels;

/**
 * Created by aidan on 1/23/17.
 */

public class PlayerInput extends InputMultiplexer {

    public DesktopController desktopController;
    public MobileController mobileController;

    boolean jumpPressed, forwardPressed, backwardPressed, downPressed, debugSuperAbilityPressed, debugSpawnpointPressed, debugZoomInPressed, debugZoomOutPressed, debugNextLevelPressed;
    boolean jumpPressedPrev, forwardPressedPrev, backwardPressedPrev, downPressedPrev, debugSuperAbilityPressedPrev, debugSpawnpointPressedPrev, debugZoomInPressedPrev, debugZoomOutPressedPrev, debugNextLevelPressedPrev;
    public int currentColorIndex = 0;
    private float currentJumpLength = 0;
    private boolean canJump = true;

    public float speedMultiplier = 1f;


    public PlayerInput() {
        super();
        Gdx.input.setInputProcessor(this);
        if (Globals.isMobileApp) {
            mobileController = new MobileController(this);
            addProcessor(mobileController.stage);
            addProcessor(mobileController);
            Globals.gameMan.running.drawables.add(mobileController);
        } else {
            desktopController = new DesktopController(this);
            addProcessor(desktopController);
        }
    }

    public void handleInput(float delta) {
        //Gdx.app.log("asdf", "" + Gdx.input.getInputProcessor().getClass());
        Gdx.input.setInputProcessor(this);
        if (Globals.isMobileApp && Gdx.input.getInputProcessor() != mobileController) {
            Gdx.input.setInputProcessor(mobileController.stage);
        } else if (Gdx.input.getInputProcessor() != desktopController) {
            Gdx.input.setInputProcessor(desktopController);
        }

        if (!Globals.isMobileApp) {
            desktopController.handleInput(delta);
        } else {
            mobileController.handleInput();
        }

        float maxJumpForceLength = 0.2f;

        //DEBUG JUNK
        if (Gdx.input.isKeyPressed(Input.Keys.B)) {
            Globals.gameMan.player.blue += 1;
            Globals.hudScene.updateData();
        }
        if (debugZoomInPressed)
            Globals.gameMan.cam.zoom += 3 / Finals.PPM;
        if (debugZoomOutPressed)
            Globals.gameMan.cam.zoom -= 3 / Finals.PPM;
        if (debugSpawnpointPressed) {
            Globals.gameMan.player.body.setLinearVelocity(new Vector2(0, 0));
            Globals.gameMan.player.body.setTransform(Globals.gameMan.player.spawnPoint, Globals.gameMan.player.body.getAngle());
        }
        if (debugNextLevelPressed) {
            Levels.Level nextLevel = Globals.gameMan.currentLevel.nextLevel;
            Globals.gameMan.switchLevel(nextLevel);
        }

        Globals.gameMan.player.isFiring = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);

        if (jumpPressed) {
            currentJumpLength += delta;

            if (currentJumpLength >= maxJumpForceLength) canJump = false;

            jumpPressedPrev = true;
        } else {
            currentJumpLength = 0f;
            canJump = Globals.gameMan.player.body.getLinearVelocity().y == 0 && !jumpPressedPrev;
            jumpPressedPrev = false;
        }

        if (debugSuperAbilityPressed) {
            Globals.gameMan.player.runSpeed = Globals.gameMan.player.maxRunSpeed;
            Globals.gameMan.player.jumpForce = Globals.gameMan.player.maxJumpForce;
        }


        if (downPressed)
            Globals.gameMan.player.body.applyLinearImpulse(new Vector2(0, -10f), Globals.gameMan.player.body.getWorldCenter(), true);

        if (forwardPressed && Globals.gameMan.player.body.getLinearVelocity().x <= 2*speedMultiplier) {
            Globals.gameMan.player.body.applyLinearImpulse(new Vector2(Globals.gameMan.player.runSpeed*speedMultiplier, 0), Globals.gameMan.player.body.getWorldCenter(), true);
            forwardPressedPrev = true;
        } else if (!backwardPressed && forwardPressedPrev) {
            Globals.gameMan.player.body.applyLinearImpulse(new Vector2(-Globals.gameMan.player.runSpeed*speedMultiplier, 0), Globals.gameMan.player.body.getWorldCenter(), true);
            forwardPressedPrev = false;
        }

        if (backwardPressed && Globals.gameMan.player.body.getLinearVelocity().x >= -2*speedMultiplier) {
            Globals.gameMan.player.body.applyLinearImpulse(new Vector2(-Globals.gameMan.player.runSpeed*speedMultiplier, 0), Globals.gameMan.player.body.getWorldCenter(), true);
            backwardPressedPrev = true;
        } else if (!Gdx.input.isKeyPressed(Input.Keys.A) && backwardPressedPrev) {
            Globals.gameMan.player.body.applyLinearImpulse(new Vector2(-Globals.gameMan.player.runSpeed*speedMultiplier, 0), Globals.gameMan.player.body.getWorldCenter(), true);
            backwardPressedPrev = false;
        }


        if (!(currentJumpLength >= maxJumpForceLength) && currentJumpLength > 0 && canJump)
            Globals.gameMan.player.body.applyLinearImpulse(new Vector2(0, Globals.gameMan.player.jumpForce * delta), Globals.gameMan.player.body.getWorldCenter(), true);

        Globals.gameMan.player.setColor(Globals.gameMan.player.colors.get(currentColorIndex));
        Globals.gameMan.player.setSelectedColor(Globals.gameMan.player.colors.get(currentColorIndex));
    }

}
