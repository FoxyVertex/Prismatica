package com.thefoxarmy.rainbowwarrior.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.thefoxarmy.rainbowwarrior.managers.Assets;
import com.thefoxarmy.rainbowwarrior.screens.GameScreen;
import com.thefoxarmy.rainbowwarrior.screens.MenuScreen;

/**
 * Created by aidan on 11/26/2016.
 *
 * This scene allows the game to have a pause menu.
 */

public class PauseMenu implements Disposable {

    public Stage stage;
    private Viewport viewport;

    //Scene2D widgets
    private TextButton btnResume;
    private TextButton btnQuit;

    /**
     * This sets up the pause menu's stage and lets it be amazing
     * @param sb the sprite batch is used to setup the stage
     * @param screen the GameScreen is used to access the `game` variable to quit the game
     */
    public PauseMenu(SpriteBatch sb, final GameScreen screen){
        viewport = new ScreenViewport(new OrthographicCamera());
        stage = new Stage(viewport, sb);

        btnResume = new TextButton("Resume", Assets.guiSkin);
        btnQuit = new TextButton("Quit", Assets.guiSkin);
        btnResume.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent i, float x, float y) {
                GameScreen.gameState = GameScreen.GameState.RUNNING;
                Assets.playSound(Assets.clickSound);
            }
        });
        btnQuit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent i, float x, float y) {
                screen.game.setScreen(new MenuScreen(screen.game));
                Assets.playSound(Assets.clickSound);
            }
        });

        Table table = new Table();
        table.top();
        table.setFillParent(true);
        table.add(btnResume).expandX().padTop(50);
        table.row();
        table.add(btnQuit).expandX().padTop(10);


        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    /**
     * This method is called to switch the input adapter to the pausemenu's when shown.
     */
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }
}