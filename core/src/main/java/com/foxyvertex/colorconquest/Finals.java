package com.foxyvertex.colorconquest;

/**
 * A set of predefined constant that might need to be used by any class.
 */
public class Finals {
    public static final float
            V_WIDTH = 400,
            V_HEIGHT = 225,
            PPM = 100;
    //Collision bits made to test object collsions -- MUST BE A POWER OF 2
    public static final short NOTHING_BIT = 0,
            BLOCK_BIT = 1,
            COLOR_BIT = 1,
            PLAYER_BIT = 4,
            END_LEVEL_BIT = 8,
            PLAYER_FEET_BIT = 16,
            BULLET_BIT = 32,
            EVERYTHING_BIT = BLOCK_BIT | PLAYER_BIT | END_LEVEL_BIT | PLAYER_FEET_BIT | BULLET_BIT;
    public static String TITLE = "Color Conquest";
    public static int firstLevel = 0;

    //DEBUG FEATURES
    public static final boolean SKIP_TO_GAME = true;
    public static final boolean ENABLE_BOX2D_DEBUG_RENDERER = true;
}
