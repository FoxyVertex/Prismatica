package com.foxyvertex.colorconquest.system;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.foxyvertex.colorconquest.Finals;
import com.foxyvertex.colorconquest.Globals;
import com.foxyvertex.colorconquest.component.Animation;
import com.foxyvertex.colorconquest.component.Bullet;
import com.foxyvertex.colorconquest.component.Player;
import com.foxyvertex.colorconquest.input.DesktopController;
import com.foxyvertex.colorconquest.input.MobileController;
import com.foxyvertex.colorconquest.tools.Utilities;
import com.kotcrab.vis.runtime.component.Layer;
import com.kotcrab.vis.runtime.component.Origin;
import com.kotcrab.vis.runtime.component.OriginalRotation;
import com.kotcrab.vis.runtime.component.PhysicsBody;
import com.kotcrab.vis.runtime.component.Renderable;
import com.kotcrab.vis.runtime.component.Tint;
import com.kotcrab.vis.runtime.component.Transform;
import com.kotcrab.vis.runtime.component.Variables;
import com.kotcrab.vis.runtime.component.VisSprite;
import com.kotcrab.vis.runtime.system.CameraManager;
import com.kotcrab.vis.runtime.system.VisIDManager;
import com.kotcrab.vis.runtime.system.physics.PhysicsSystem;
import com.kotcrab.vis.runtime.util.AfterSceneInit;

/**
 * Created by aidan on 2/12/2017.
 */

public class PlayerSystem extends BaseSystem implements AfterSceneInit {
    VisIDManager idManager;
    ComponentMapper<VisSprite> spriteCm;
    ComponentMapper<Transform> transformCm;
    ComponentMapper<PhysicsBody> bodyCm;

    CameraManager cameraManager;

    public int currentColorIndex = 0;
    public float speedMultiplier = 1f;
    public boolean jumpPressed, forwardPressed, backwardPressed, downPressed, debugSuperAbilityPressed, debugSpawnpointPressed, debugZoomInPressed, debugZoomOutPressed, debugNextLevelPressed, firingModePressed;
    private boolean jumpPressedPrev, forwardPressedPrev, backwardPressedPrev, downPressedPrev, debugSuperAbilityPressedPrev, debugSpawnpointPressedPrev, debugZoomInPressedPrev, debugZoomOutPressedPrev, debugNextLevelPressedPrev, firingModePressedPrev;
    private float currentJumpLength = 0;
    private boolean canJump = true;
    private boolean inAir = false;
    private boolean jumpReleased = false;

    private boolean animationStarted = false;

    private float forceScale = 0.01f;
    private Vector2 initialBulletImpulse = new Vector2(4, 2);
    private Vector2 iInitialBulletImpulse = new Vector2(-4, 2);

    public Entity player;
    public Player playerComp;
    private VisSprite sprite;
    private Transform transform;
    public Body body;

    private enum FacingDIRECTION {LEFT, RIGHT}
    private FacingDIRECTION facingDIRECTION = FacingDIRECTION.RIGHT;

    private boolean shouldFlipX = false;
    private boolean shouldFlipY = false;

    private MobileController mobileController;
    private DesktopController desktopController;

    public boolean isGamePaused = false;

    private TextureRegion bulletTextureRegion;


    @Override
    protected void processSystem() {
        if (!isGamePaused) {
            if (Globals.isMobile) {
                mobileController.handleInput();
            } else {
                desktopController.handleInput(getWorld().getDelta());
            }

            switch (currentColorIndex) {
                case 0:
                    playerComp.selectedColor = Color.RED;
                    break;
                case 1:
                    playerComp.selectedColor = Color.GREEN;
                    break;
                case 2:
                    playerComp.selectedColor = Color.BLUE;
                    break;
            }

            float maxJumpForceLength = 0.2f;

            if (Gdx.input.isKeyPressed(Input.Keys.B)) {
                player.getComponent(Player.class).blue += 1;
            }
            if (debugNextLevelPressed) {
                Globals.gameScreen.nextLevel();
            }
            if (debugSuperAbilityPressed) {
                playerComp.runSpeed = playerComp.maxRunSpeed;
                playerComp.jumpForce = playerComp.maxJumpForce;
                debugSuperAbilityPressedPrev = true;
            } else if (debugSuperAbilityPressedPrev) {
                playerComp.runSpeed = playerComp.minRunSpeed;
                playerComp.jumpForce = playerComp.minJumpForce;
                debugSuperAbilityPressedPrev = false;
            }

            if (debugZoomInPressed)
                cameraManager.getCamera().zoom += 1.3f * getWorld().getDelta();
            if (debugZoomOutPressed)
                cameraManager.getCamera().zoom -= 1.3f * getWorld().getDelta();

            if (firingModePressed) {
                // Firing Mode pressed logic
                firingModePressedPrev = true;
            } else if (firingModePressedPrev) {
                // Firing Mode unpressed logic
                firingModePressedPrev = false;
            }

            player.getComponent(Player.class).isFiring = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);

            if (jumpPressed && !jumpReleased) {
                currentJumpLength += world.getDelta();

                if (currentJumpLength >= maxJumpForceLength) canJump = false;
                if (currentJumpLength < maxJumpForceLength) canJump = true;

                jumpPressedPrev = true;
            } else if (body.getLinearVelocity().y <= 0.0001 && body.getLinearVelocity().y >= -0.0001) {
                jumpReleased = false;
            } else {
                if (jumpPressedPrev && !jumpPressed) {
                    jumpReleased = true;
                    currentJumpLength = 0f;
                }
                jumpPressedPrev = false;
            }

            if (downPressed)
                body.applyLinearImpulse(new Vector2(0, -10f), body.getWorldCenter(), true);

            if (forwardPressed && body.getLinearVelocity().x <= 2 * speedMultiplier) {
                body.applyLinearImpulse(new Vector2(playerComp.runSpeed * speedMultiplier * forceScale, 0), body.getWorldCenter(), true);
                forwardPressedPrev = true;
                if (player.getComponent(Animation.class) != null) world.getSystem(AnimationSystem.class).changeAnimState(player, "walk", false, false, true);
                facingDIRECTION = FacingDIRECTION.RIGHT;
            } else if (!backwardPressed && forwardPressedPrev) {
                forwardPressedPrev = false;
            }

            if (backwardPressed && body.getLinearVelocity().x >= -2 * speedMultiplier) {
                body.applyLinearImpulse(new Vector2(-playerComp.runSpeed * speedMultiplier * forceScale, 0), body.getWorldCenter(), true);
                if (player.getComponent(Animation.class) != null) world.getSystem(AnimationSystem.class).changeAnimState(player, "walk", true, false, true);
                facingDIRECTION = FacingDIRECTION.LEFT;
                backwardPressedPrev = true;

            } else if (!backwardPressed && backwardPressedPrev) {
                backwardPressedPrev = false;
            }
            if (body.getLinearVelocity().y < -0.01) {
                if (player.getComponent(Animation.class) != null) world.getSystem(AnimationSystem.class).changeAnimState(player, "fall", shouldFlipX, false, true);
            }

            switch (facingDIRECTION) {
                case RIGHT:
                    shouldFlipX = false;
                    break;
                case LEFT:
                    shouldFlipX = true;
                    break;
            }

            if (!backwardPressed && !forwardPressed && !downPressed && !jumpPressed && (body.getLinearVelocity().y >= -0.001) && (body.getLinearVelocity().y <= 0.001f)) {
                if (player.getComponent(Animation.class) != null) world.getSystem(AnimationSystem.class).changeAnimState(player, "idle", shouldFlipX, shouldFlipY, true);
            }



            if (currentJumpLength > 0 && canJump && !jumpReleased) {
                body.applyLinearImpulse(new Vector2(0, player.getComponent(Player.class).jumpForce * world.getDelta()), body.getWorldCenter(), true);
                if (player.getComponent(Animation.class) != null) world.getSystem(AnimationSystem.class).changeAnimState(player, "jumpstart", shouldFlipX, false, false);
                inAir = true;
            }
        } else {
            Gdx.input.setInputProcessor(Globals.pauseMenuStage.stage);
            Globals.pauseMenuStage.stage.act();
        }
    }


    @Override
    public void afterSceneInit() {
        player = idManager.get("player");
        player.edit().add(new Player());
        playerComp = player.getComponent(Player.class);
        playerComp.colors = new Array<>();
        playerComp.colors.add(Color.RED);
        playerComp.colors.add(Color.GREEN);
        playerComp.colors.add(Color.BLUE);
        player.getComponent(Variables.class).put("collisionCat", "player");


        sprite = spriteCm.get(player);
        transform = transformCm.get(player);
        body = bodyCm.get(player).body;
        Filter filter = new Filter();
        filter.categoryBits = Finals.PLAYER_BIT;
        body.getFixtureList().get(0).setFilterData(filter);

        if (Globals.isMobile) {
            mobileController = new MobileController(this);
            Globals.gameScreen.drawables.add(mobileController);
            Gdx.input.setInputProcessor(mobileController);
        } else {
            desktopController = new DesktopController(this);
            Gdx.input.setInputProcessor(desktopController);
        }

        Pixmap pixmap = new Pixmap(20, 20, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fillCircle(pixmap.getWidth() / 2, pixmap.getHeight() / 2, 20 / 2);
        bulletTextureRegion = new TextureRegion(new Texture(pixmap));
    }

    public void shoot(Vector2 clickPoint) {
        if (playerComp.selectedColor == Color.RED)
            if (playerComp.red <= 0)
                return;
            else
                playerComp.red--;
        else if (playerComp.selectedColor == Color.GREEN)
            if (playerComp.green <= 0)
                return;
            else
                playerComp.green--;
        else if (playerComp.selectedColor == Color.BLUE)
            if (playerComp.blue <= 0)
                return;
            else
                playerComp.blue--;
        else
            return;
        updateHud();

        Bullet bulletComp = new Bullet();
        bulletComp.color = playerComp.selectedColor;

        float bulletStartXValue;
        Vector2 impulse;

        if (facingDIRECTION == FacingDIRECTION.LEFT) {
            bulletStartXValue = -0.2f;
            impulse = iInitialBulletImpulse;
        } else {
            bulletStartXValue = 0.75f;
            impulse = initialBulletImpulse;
        }

        Transform transformComp = new Transform(body.getPosition().x+bulletStartXValue, body.getPosition().y+1f);
        Variables variables = new Variables();
        variables.put("collisionCat", "bullet");

        Entity thisBullet = world.createEntity().edit()
                .add(new Renderable(0))
                .add(new Layer(Globals.gameScreen.scene.getLayerDataByName("Foreground").id))
                .add(transformComp)
                .add(new Tint(playerComp.selectedColor))
                .add(bulletComp)
                .add(variables)
                .getEntity();


        Vector2 worldPos = new Vector2(transformComp.getX(), transformComp.getY());

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(worldPos);

        Body body = getWorld().getSystem(PhysicsSystem.class).getPhysicsWorld().createBody(bodyDef);
        body.setType(BodyDef.BodyType.DynamicBody);
        body.setUserData(thisBullet);

        body.setGravityScale(1f);
        body.setBullet(true);
        body.setFixedRotation(true);
        body.setSleepingAllowed(false);
        CircleShape shape = new CircleShape();
        shape.setRadius(0.1f);
        shape.setPosition(new Vector2(0.1f, 0.1f));

        body.applyLinearImpulse(impulse, body.getWorldCenter(), true);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        body.createFixture(fd);
        shape.dispose();


        VisSprite visSpriteComp = new VisSprite(bulletTextureRegion);
        visSpriteComp.setSize(0.2f, 0.2f);

        Origin origin = new Origin();
        origin.setOrigin(0, 0);
        origin.setDirty(true);

        thisBullet.edit()
                .add(new PhysicsBody(body))
                .add(new OriginalRotation(transform.getRotation()))
                .add(visSpriteComp)
                .add(origin);
    }

    public void updateHud() {
        getWorld().getSystem(HudSystem.class).updateColorMeter();
    }
}
