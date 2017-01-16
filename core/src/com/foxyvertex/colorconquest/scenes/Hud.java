package com.foxyvertex.colorconquest.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.foxyvertex.colorconquest.Finals;
import com.foxyvertex.colorconquest.Globals;
import com.foxyvertex.colorconquest.managers.Assets;

/**
 * Created by aidan on 11/26/2016.
 * <p>
 * This class represents the heads up display and is currently housing a placeholder for it.
 */

public class Hud extends Scene {

    //Mario score/time Tracking Variables
    private Integer worldTimer;
    private Integer score;

    //Scene2D widgets
    private Label countdownLabel;
    private Label scoreLabel;
    private Label timeLabel;
    private Label levelLabel;
    private Label marioLabel;

    /**
     * The constructor sets up the stage.
     *
     * @param screen the screen is passed to super
     */
    public Hud(final Screen screen) {
        super(screen);

        //define our tracking variables
        worldTimer = 300;
        score = 0;

        //define a table used to organize our hud's labels
        Table table = new Table();
        //Top-Align table
        table.top();
        //make the table fill the entire stage
        table.setFillParent(true);

        //define our labels using the String, and a Label style consisting of a font and color
        countdownLabel = new Label(String.format("%03d", worldTimer), Assets.guiSkin);
        scoreLabel = new Label(String.format("%06d", score), Assets.guiSkin);
        timeLabel = new Label("TIME", Assets.guiSkin);
        levelLabel = new Label("1-1", Assets.guiSkin);
        Label worldLabel = new Label("WORLD", Assets.guiSkin);
        marioLabel = new Label("MARIO", Assets.guiSkin);

        countdownLabel.setFontScale(1.5f);
        scoreLabel.setFontScale(1.5f);
        timeLabel.setFontScale(1.5f);
        levelLabel.setFontScale(1.5f);
        worldLabel.setFontScale(1.5f);
        marioLabel.setFontScale(1.5f);

        //add our labels to our table, padding the top, and giving them all equal width with expandX
        table.add(marioLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        //add a second row to our table
        table.row();
        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countdownLabel).expandX();

        //Bottom of the HUD that displays color information

        Pixmap colorIndicator = new Pixmap(85, 255, Pixmap.Format.RGB888);
        colorIndicator.setColor(Color.RED);
        colorIndicator.fillRectangle(0, colorIndicator.getHeight() - Globals.gameScreen.player.red, colorIndicator.getWidth() / 3, Globals.gameScreen.player.red);
        colorIndicator.setColor(Color.GREEN);
        colorIndicator.fillRectangle(colorIndicator.getWidth() / 3, colorIndicator.getHeight() - Globals.gameScreen.player.green, colorIndicator.getWidth() / 3, Globals.gameScreen.player.green);
        colorIndicator.setColor(Color.BLUE);
        colorIndicator.fillRectangle(colorIndicator.getWidth() / 3 * 2, colorIndicator.getHeight() - Globals.gameScreen.player.blue, colorIndicator.getWidth() / 3, Globals.gameScreen.player.blue);
        Image colorINT = new Image(new Texture(colorIndicator));
        Table bottomHud = new Table(Assets.guiSkin);
        bottomHud.setFillParent(true);
        bottomHud.setScale(Finals.PPM);
        bottomHud.right().bottom().scaleBy(Finals.PPM);
        bottomHud.add(colorINT).pad(10);
        //add our table to the stage
        stage.addActor(table);
        stage.addActor(bottomHud);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void show() {

    }

    public void tick(float delta) {
        stage.act();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }


}