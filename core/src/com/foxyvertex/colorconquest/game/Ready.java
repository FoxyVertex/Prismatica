package com.foxyvertex.colorconquest.game;

import com.foxyvertex.colorconquest.Globals;

/**
 * Created by aidan on 1/23/17.
 */

public class Ready extends GameState {

    @Override
    public void update(float delta) {
        Globals.gameReadyScene.tick(delta);
    }

    @Override
    public void render() {
        Globals.game.batch.setProjectionMatrix(Globals.gameReadyScene.stage.getCamera().combined);
        Globals.gameReadyScene.stage.draw();
    }

    @Override
    public void start() {
        Globals.gameReadyScene.show();
    }

    @Override
    public void stop() {

    }

    @Override
    public void dispose() {

    }

}
