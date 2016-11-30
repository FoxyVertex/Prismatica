package com.thefoxarmy.rainbowwarrior.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by aidan on 11/26/2016.
 *
 * Manages and houses all of our assets
 */

public class Assets {

    public static Animation playerIdleAnim;
    public static Animation playerWalkAnim;
    public static Animation playerFallAnim;

    public static Skin guiSkin;

    public static AssetManager manager;
    public static TextureAtlas mainAtlas;

    public static Sound clickSound;

    /**
     * Called on game start. Loads all assets into easily accessible variables.
     */
    public static void load() {
        manager = new AssetManager();

        manager.load("click.wav", Sound.class);

        manager.finishLoading();
        clickSound = manager.get("click.wav", Sound.class);
        Assets.mainAtlas = new TextureAtlas("GreyGuy.pack");

        playerIdleAnim = new Animation(1 / 16f, Assets.mainAtlas.findRegions("idle"), Animation.PlayMode.LOOP);
        playerWalkAnim = new Animation(1 / 16f, Assets.mainAtlas.findRegions("walk"), Animation.PlayMode.LOOP);
        playerFallAnim = new Animation(1 / 16f, Assets.mainAtlas.findRegions("fall"), Animation.PlayMode.LOOP);

        guiSkin = new Skin(Gdx.files.internal("skin/clean-crispy-ui.json"));
    }

    public static void playSound (Sound sound) {
        if (UserPrefs.isSoundEnabled()) sound.play(1);
    }
}
