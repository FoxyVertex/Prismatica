package com.foxyvertex.colorconquest.tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.foxyvertex.colorconquest.Finals;
import com.foxyvertex.colorconquest.entities.Barrier;
import com.foxyvertex.colorconquest.entities.Block;
import com.foxyvertex.colorconquest.entities.EntityController;
import com.foxyvertex.colorconquest.entities.Slitherikter;

/**
 * This class reads object layers from the currently loaded tiled map and creates box2d fixtures for them in order to create collision for the player.
 */
public class WorldPhysicsCreator {
    /**
     * Loops though each layer and creates fixtures that the player can collide with, and sets up TriggerPoints.
     *
     * @param screen this instance is used to get the current tiled map.
     */
    private PolygonShape polygon;

    public WorldPhysicsCreator(World world, TiledMap map) {
        polygon = new PolygonShape();
        //For every rectangular object in the "blocks" object layer of the tile map, initialize a rectangle to create a physical fixture.
        for (MapObject object : map.getLayers().get("blocks").getObjects()) {
            new Block(object);
        }
        for (MapObject object : map.getLayers().get("EditableBlocks").getObjects()) {
            new Block(object);
        }
        //Generate fixtures for the endpoints in the triggerPoints object layer of the tiled map so that the player can collide with it to go to the next tiledMap.
        initializeRect(world, Finals.END_LEVEL_BIT, map.getLayers().get("triggerPoints").getObjects().get("EndPoint"));
        //Create the slitherikters
        if (map.getLayers().get("Slitherikters") != null) {
            for (MapObject object : map.getLayers().get("Slitherikters").getObjects()) {
                new Slitherikter(
                        new Vector2(
                                object.getProperties().get("x", Float.class) / Finals.PPM,
                                object.getProperties().get("y", Float.class) / Finals.PPM
                        ), new Color(Utilities.map(object.getProperties().get("r", Float.class), 0, 255, 0, 1),
                        Utilities.map(object.getProperties().get("g", Float.class), 0, 255, 0, 1),
                        Utilities.map(object.getProperties().get("b", Float.class), 0, 255, 0, 1),
                        1)
                );
            }
        }
        if (map.getLayers().get("Barriers") != null) {
            for (MapObject object : map.getLayers().get("Barriers").getObjects()) {
                EntityController.entities.add(
                        new Barrier(new Vector2(
                                        object.getProperties().get("x", Float.class) / Finals.PPM,
                                        object.getProperties().get("y", Float.class) / Finals.PPM
                                ), object
                        ));
            }
        }
    }

    /**
     * This method creates a fixture based on a specified tiledMap object. object
     *
     * @param world       needed for accessing the current tiledMap loaded and b2dWorld.
     * @param categoryBit The "type" of fixture being created. Used in collsions.
     * @param object      The map object that will be used to create the rectangular fixtures.
     */
    private Body initializeRect(World world, short categoryBit, MapObject object) {
        Rectangle rect = ((RectangleMapObject) object).getRectangle();
        BodyDef   bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((rect.getX() + rect.getWidth() / 2) / Finals.PPM, (rect.getY() + rect.getHeight() / 2) / Finals.PPM);
        Body       body = world.createBody(bdef);
        FixtureDef fdef = new FixtureDef();
        polygon.setAsBox((rect.getWidth() / 2) / Finals.PPM, (rect.getHeight() / 2) / Finals.PPM);
        fdef.shape = polygon;
        fdef.filter.categoryBits = categoryBit;
        body.createFixture(fdef);
        return body;
    }
}
