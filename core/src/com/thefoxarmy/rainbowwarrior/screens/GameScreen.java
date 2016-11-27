package com.thefoxarmy.rainbowwarrior.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.thefoxarmy.rainbowwarrior.Globals;
import com.thefoxarmy.rainbowwarrior.RainbowWarrior;
import com.thefoxarmy.rainbowwarrior.scenes.Hud;
import com.thefoxarmy.rainbowwarrior.scenes.PauseMenu;
import com.thefoxarmy.rainbowwarrior.sprites.Player;
import com.thefoxarmy.rainbowwarrior.tools.PlayerInputAdapter;
import com.thefoxarmy.rainbowwarrior.tools.Utilities;
import com.thefoxarmy.rainbowwarrior.tools.WorldPhysicsContactListener;
import com.thefoxarmy.rainbowwarrior.tools.WorldPhysicsCreator;

import static com.badlogic.gdx.Gdx.input;

/**
 * Handles level loading, all of the objects in the world, and anything outside of a menu
 */
public class GameScreen implements Screen {

    public static GameState gameState;

    public TiledMap level;
    private Player player;
    public RainbowWarrior game;
    //Camera stuff
    private OrthographicCamera cam;
    private Viewport viewport;
    private TiledMapRenderer mapRenderer;
    private World world;
    private Box2DDebugRenderer b2dRenderer;
    private Preferences prefs;

    private Hud hud;
    private PauseMenu pauseMenu;

    public float timeSinceStartLevel = 0;

    /**
     * Initializes the current level and sets up the playing screen
     *
     * @param game the main game class
     * @param path path to the `tmx` level
     */
    GameScreen(RainbowWarrior game, String path) {

        this.game = game;
        this.prefs = Gdx.app.getPreferences("User Data");
        //Camera stuff
        cam = new OrthographicCamera();
        viewport = new StretchViewport(Globals.V_WIDTH / Globals.PPM, Globals.V_HEIGHT / Globals.PPM, cam);

        level = new TmxMapLoader().load(path);
        mapRenderer = new OrthogonalTiledMapRenderer(level, 1 / Globals.PPM);

        cam.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        world = new World(new Vector2(0, -9.89f), true);
        world.setContactListener(new WorldPhysicsContactListener(this));
        b2dRenderer = new Box2DDebugRenderer();

        new WorldPhysicsCreator(this);

        //Spawns the player at a location designated on the map
        player = new Player(this,
                new PlayerInputAdapter(player),
                new Vector2(level.getLayers().get("triggerPoints").getObjects().get("p1SpawnPoint").getProperties().get("x", Float.class),
                        level.getLayers().get("triggerPoints").getObjects().get("p1SpawnPoint").getProperties().get("y", Float.class)
                )
        );
        cam.position.y = player.body.getPosition().y;
        gameState = GameState.READY;
        hud = new Hud(game.batch);
        pauseMenu = new PauseMenu(game.batch, this);
    }

    /**
     * Currently, does nothing
     */
    @Override
    public void show() {

    }

    /**
     * Calculates all physics on behalf of the world
     *
     * @param delta a float that is the amount of time in seconds since the last frame
     */
    private void tick(float delta) {
        switch (gameState) {
            case READY:
                updateReady();
                break;
            case RUNNING:
                updateRunning(delta);
                break;
            case PAUSED:
                updatePaused();
                break;
            case LEVEL_END:
                updateLevelEnd();
                break;
            case OVER:
                updateGameOver();
                break;
        }
    }

    /**
     * This is called to update physics for the READY state
     */
    public void updateReady() {
        if (input.justTouched()) {
            gameState = GameState.RUNNING;
        }
    }
    /**
     * This is called to show stuff for the READY state
     */
    public void presentReady() {

    }

    /**
     * This is called to update physics for the RUNNING state
     */
    public void updateRunning(float delta) {
        if (input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gameState = GameState.PAUSED;
            pauseMenu.show();
            return;
        }

        timeSinceStartLevel += delta;
        world.step(1 / 60f, 6, 2);
        cam.position.x = player.body.getPosition().x;
        cam.position.y = player.body.getPosition().y;
        MapProperties levelProps = level.getProperties();
        int mapPixelWidth = levelProps.get("width", Integer.class) * levelProps.get("tilewidth", Integer.class);
        int mapPixelHeight = levelProps.get("height", Integer.class) * levelProps.get("tileheight", Integer.class);
        cam.position.x = Utilities.clamp(player.body.getPosition().x, cam.viewportWidth / 2, (mapPixelWidth / Globals.PPM) - (cam.viewportWidth / 2));
        cam.position.y = Utilities.clamp(player.body.getPosition().y, cam.viewportHeight / 2, (mapPixelHeight / Globals.PPM) - (cam.viewportHeight / 2));



        player.tick(delta);
        cam.update();
        mapRenderer.setView(cam);
        hud.stage.act();
    }
    /**
     * This is called to show stuff for the READY state
     */
    public void presentRunning() {
        mapRenderer.render();
        b2dRenderer.render(world, cam.combined);
        game.batch.setProjectionMatrix(cam.combined);
        game.batch.begin();
        player.draw(game.batch);
        game.batch.end();
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    /**
     * This is called to update physics for the PAUSED state
     */
    public void updatePaused() {

        if (input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gameState = GameState.RUNNING;
            Gdx.input.setInputProcessor(player.input);
            return;
        }

    }
    /**
     * This is called to show stuff for the PAUSED state
     */
    public void presentPaused() {
        game.batch.setProjectionMatrix(pauseMenu.stage.getCamera().combined);
        pauseMenu.stage.act();
        pauseMenu.stage.draw();
    }

    /**
     * This is called to update physics for the LEVEL_END state
     */
    public void updateLevelEnd() {

    }
    /**
     * This is called to show stuff for the LEVEL_END state
     */
    public void presentLevelEnd() {

    }

    /**
     * This is called to update physics for the SHOW state
     */
    public void updateGameOver() {

    }
    /**
     * This is called to show stuff for the SHOW state
     */
    public void presentGameOver() {

    }


    /**
     * Renders all the things
     *
     * @param delta a float that is the amount of time in seconds since the last frame
     */
    @Override
    public void render(float delta) {
        tick(delta);
        switch (gameState) {
            case READY:
                presentReady();
                break;
            case RUNNING:
                presentRunning();
                break;
            case PAUSED:
                presentPaused();
                break;
            case LEVEL_END:
                presentLevelEnd();
                break;
            case OVER:
                presentGameOver();
                break;
        }

    }

    /**
     * Updates the screen's width and height when resized
     *
     * @param width  new screen width
     * @param height new screen height
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    /**
     * Currently, does nothing
     */
    @Override
    public void pause() {

    }

    /**
     * Currently, does nothing
     */
    @Override
    public void resume() {

    }

    /**
     * Currently, does nothing
     */
    @Override
    public void hide() {

    }

    /**
     * Discards all textures in memory
     */
    @Override
    public void dispose() {
        world.dispose();
        b2dRenderer.dispose();
        player.dispose();
    }

    /**
     * Gets the world for anyone who needs it
     *
     * @return the world
     */
    public World getWorld() {
        return world;
    }

    /**
    * Update the user's preferences file and create a new playScreen based on the nextLevel property of the current TiledMap.
    */
    public void switchLevel() {
        if (!level.getProperties().get("hasCutscene", Boolean.class)) {
            prefs.putString("Level", level.getProperties().get("nextLevel", String.class));
            prefs.flush();
            game.setScreen(new GameScreen(game, prefs.getString("Level")));
            timeSinceStartLevel = 0;
        } else {
            //Play Cutscene or whatever...
        }

    }

    /**
     * This houses the different game states.
     */
    public enum GameState {
        READY,
        RUNNING,
        PAUSED,
        LEVEL_END,
        OVER
    }
}

