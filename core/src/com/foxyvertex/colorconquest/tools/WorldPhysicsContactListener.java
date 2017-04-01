package com.foxyvertex.colorconquest.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;
import com.foxyvertex.colorconquest.Finals;
import com.foxyvertex.colorconquest.Globals;
import com.foxyvertex.colorconquest.entities.Barrier;
import com.foxyvertex.colorconquest.entities.Block;
import com.foxyvertex.colorconquest.entities.Bullet;
import com.foxyvertex.colorconquest.entities.Interactant;
import com.foxyvertex.colorconquest.entities.Player;
import com.foxyvertex.colorconquest.entities.Slitherikter;
import com.foxyvertex.colorconquest.entities.SpriteBody;
import com.foxyvertex.colorconquest.game.GameManager;
import com.foxyvertex.colorconquest.managers.Levels;

/**
 * Created by seth on 11/26/2016.
 * <p>
 * This class handles all collisions between any two box2d fixtures within the world
 */

public class WorldPhysicsContactListener implements ContactListener {

    public static Array<Body> deadBodies;

    /**
     * Instantiates an instance of a worldContactlistener for the world to use.
     *
     * @param gameManager Used in order to access other objects within the current tiledMap loaded.
     */
    public WorldPhysicsContactListener(GameManager gameManager) {
        deadBodies = new Array<Body>();
    }

    /**
     * This method is called by the world at the beginning of a between any two fixtures
     *
     * @param contact this is all of the data for two fixtures.
     */
    @Override
    public void beginContact(Contact contact) {

        //Store the fixtures from the collsion
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        //An integer that tells the catagoryBits of the two fixtures
        int collisionDefinition = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;
        //Checks to see if a select two kinds of fixtures collide.
        switch (collisionDefinition) {
            case Finals.PLAYER_BIT | Finals.END_LEVEL_BIT:

                Levels.Level nextLevel = Globals.gameMan.currentLevel.nextLevel;
                Globals.gameMan.switchLevel(nextLevel);
                break;
            case Finals.SLITHERIKTER_BIT | Finals.BLOCK_BIT:
            case Finals.PLAYER_BIT | Finals.SLIME_BIT:
            case Finals.PLAYER_BIT | Finals.BLOCK_BIT:
                //Determines which fixture is the player and which is the block
                // This can be shortened to 1 line!
                Fixture objectCollidedWith = (fixtureA.getUserData() instanceof Interactant) ? fixtureB : fixtureA;
                Interactant objectTouched = (fixtureA.getUserData() instanceof Interactant) ? (Interactant) fixtureA.getUserData() : (Interactant) fixtureB.getUserData();
                Color blockColor = ((SpriteBody) objectCollidedWith.getUserData()).color;
                if (blockColor != null) {
                    float RGBColors[] = {blockColor.r, blockColor.g, blockColor.b};

                    switch (Utilities.findBiggestIndex(RGBColors)) {
                        case 0:
                            objectTouched.runSpeed = objectTouched.minRunSpeed + blockColor.r * (objectTouched.maxRunSpeed - objectTouched.minRunSpeed);
                            break;
                        case 1:
                            objectTouched.jumpForce = objectTouched.minJumpForce + blockColor.g * (objectTouched.maxJumpForce - objectTouched.minJumpForce);
                            break;
                        case 2:
                            if (objectCollidedWith.getFilterData().categoryBits == Finals.BLOCK_BIT)
                                objectTouched.primaryFixture.setRestitution(0);
                            else
                                Globals.gameMan.player.primaryFixture.setRestitution(0);

                    }
                }
                break;

            case Finals.BLOCK_BIT | Finals.BULLET_BIT:
                //initialize the objects to their proper collision fixtures
                Block attackedBlock = (fixtureA.getUserData() instanceof Block) ? (Block) fixtureA.getUserData() : (Block) fixtureB.getUserData();
                Bullet bullet = (fixtureA.getUserData() instanceof Bullet) ? (Bullet) fixtureA.getUserData() : (Bullet) fixtureB.getUserData();

                //Determine which color the bullet is (Because Java sucks (Because there is no pass by reference))
                float RGBColors[] = {bullet.color.r, bullet.color.g, bullet.color.b};
                switch (Utilities.findBiggestIndex(RGBColors)) {
                    case 0:
                        attackedBlock.color.r = Utilities.clamp(attackedBlock.color.r + Utilities.map(10, 0, 255, 0, 1), 0, 1);
                        attackedBlock.tintTexture();
                        break;
                    case 1:
                        attackedBlock.color.g = Utilities.clamp(attackedBlock.color.g + Utilities.map(10, 0, 255, 0, 1), 0, 1);
                        attackedBlock.tintTexture();
                        break;
                    case 2:
                        attackedBlock.color.b = Utilities.clamp(attackedBlock.color.b + Utilities.map(10, 0, 255, 0, 1), 0, 1);
                        attackedBlock.tintTexture();
                        break;
                }
                attackedBlock.color.a = Utilities.clamp(attackedBlock.color.a + Utilities.map(5, 0, 100, 0, 1), 0, 1);

                attackedBlock.rainbow = (attackedBlock.color.r == 1 && attackedBlock.color.g == 1 && attackedBlock.color.b == 1 && attackedBlock.color.a == 1 || attackedBlock.rainbow);

                //attackedBlock.setColor(attackedBlock.color);

                bullet.body.applyLinearImpulse(new Vector2(0, 5), bullet.body.getWorldCenter(), false);

                bullet.setToDestroy = true;
                break;

            case Finals.BULLET_BIT:
                if (fixtureA.getShape().getRadius() > fixtureB.getShape().getRadius()) {
                    fixtureA.getShape().setRadius(fixtureA.getShape().getRadius() + fixtureB.getShape().getRadius());
                    ((Bullet) fixtureA.getUserData()).reDraw();
                    deadBodies.add(fixtureB.getBody());
                } else {
                    fixtureB.getShape().setRadius(fixtureA.getShape().getRadius() + fixtureB.getShape().getRadius());
                    ((Bullet) fixtureB.getUserData()).reDraw();
                    deadBodies.add(fixtureA.getBody());
                }
                break;
            case Finals.BULLET_BIT | Finals.SLITHERIKTER_BIT:
                if (fixtureA.getUserData() instanceof Slitherikter)
                    ((Slitherikter) fixtureA.getUserData()).attacked((Bullet) fixtureB.getUserData());
                else
                    ((Slitherikter) fixtureB.getUserData()).attacked((Bullet) fixtureA.getUserData());
                break;
            case Finals.ENEMY_BUFFER_BIT | Finals.BLOCK_BIT:

                if (fixtureA.getUserData() instanceof Slitherikter) {
                    ((Slitherikter) fixtureA.getUserData()).shouldFlip = !((Slitherikter) fixtureA.getUserData()).shouldFlip;
                } else {
                    ((Slitherikter) fixtureB.getUserData()).shouldFlip = !((Slitherikter) fixtureB.getUserData()).shouldFlip;
                }
                break;
            case Finals.SLITHERIKTER_BIT | Finals.SLIME_BIT:

                ((Slitherikter) ((fixtureA.getFilterData().categoryBits == Finals.SLITHERIKTER_BIT) ? fixtureA.getUserData() : fixtureB.getUserData())).isColidingWithSlime = true;
                break;
            case Finals.PLAYER_BIT | Finals.BARRIER_BIT:
                //IF the player's Downward force is greater than or equal to the force required to break the barrier
                Gdx.app.log("", "" + ((Player) ((fixtureA.getUserData() instanceof Player) ? fixtureA.getUserData() : fixtureB.getUserData())).body.getLinearVelocity().y);
                if (((Player) ((fixtureA.getUserData() instanceof Player) ? fixtureA.getUserData() : fixtureB.getUserData())).body.getLinearVelocity().y <= ((Barrier) ((fixtureA.getUserData() instanceof Barrier) ? fixtureA.getUserData() : fixtureB.getUserData())).requiredBreakingForce) {    //Break the Barrier
                    ((Barrier) ((fixtureA.getUserData() instanceof Barrier) ? fixtureA.getUserData() : fixtureB.getUserData())).setToDestroy = true;
                    Gdx.app.log("", "f");
                }
                break;

        }
    }

    //This method is called when collision is finished It basically just resets everything
    @Override
    public void endContact(Contact contact) {
        //Store the fixtures from the collsion
        Fixture fixtureA = contact.getFixtureA(), fixtureB = contact.getFixtureB();
        //An integer that tells the catagoryBits of the two fixtures
        int collisionDefinition = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;
        //Checks to see if a select two kinds of fixtures collide.
        switch (collisionDefinition) {
            case Finals.PLAYER_BIT | Finals.SLIME_BIT:
            case Finals.PLAYER_BIT | Finals.BLOCK_BIT:
                //Determines which fixture is the player and which is the block
                // This can be shortened to 1 line!
                Fixture objectCollidedWith = (fixtureA.getUserData() instanceof Interactant) ? fixtureB : fixtureA;
                Interactant objectTouched = (fixtureA.getUserData() instanceof Interactant) ? (Interactant) fixtureA.getUserData() : (Interactant) fixtureB.getUserData();

                Color blockColor = ((SpriteBody) objectCollidedWith.getUserData()).color;
                if (blockColor != null) {
                    float RGBColors[] = {blockColor.r, blockColor.g, blockColor.b};

                    switch (Utilities.findBiggestIndex(RGBColors)) {
                        case 0:
                            objectTouched.runSpeed = objectTouched.minRunSpeed;
                            break;
                        case 1:
                            objectTouched.jumpForce = objectTouched.minJumpForce;
                            break;
                        case 2:
                            //objectTouched.primaryFixture.setRestitution(0);
                            break;
                    }
                } else {
                    Globals.gameMan.player.runSpeed = Globals.gameMan.player.minRunSpeed;
                    Globals.gameMan.player.jumpForce = Globals.gameMan.player.minJumpForce;
                }
                break;
            case Finals.SLITHERIKTER_BIT | Finals.SLIME_BIT:
                ((Slitherikter) ((fixtureA.getFilterData().categoryBits == Finals.SLITHERIKTER_BIT) ? fixtureA.getUserData() : fixtureB.getUserData())).isColidingWithSlime = false;
        }

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}