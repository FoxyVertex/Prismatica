package com.foxyvertex.colorconquest;

import com.foxyvertex.colorconquest.scenes.Scene;
import com.foxyvertex.colorconquest.screens.GameScreen;
import com.foxyvertex.colorconquest.screens.MenuScreen;

/**
 * Created by aidan on 11/27/2016.
 */

public class Globals {
    public static ColorConquest game;
    public static int currentGameSave = 45;

    // Screen Cache:
    public static MenuScreen menuScreen;
    public static GameScreen gameScreen;

    // Scene Cache:
    public static Scene titleScreenScene;
    public static Scene pauseMenuScene;
    public static Scene hudScene;
    public static Scene optionsMenuScreen;
    public static Scene playMenuScene;
    public static Scene gameReadyScene;
}