package com.foxyvertex.colorconquest.managers;

import com.foxyvertex.colorconquest.Globals;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aidan on 12/19/2016.
 */

public class Levels {
    public static List<Level> levels       = new ArrayList<Level>();
    public static int         currentLevel = 0;
    public static String      pathPrefix   = "levels/";

    public static void load() {
        Level level1 = new Level();
        Level level2 = new Level();
        Level level3 = new Level();
        Level level4 = new Level();

        level1.path = pathPrefix + "DebugLevel.tmx";
        level2.path = pathPrefix + "test.tmx";
        level3.path = pathPrefix + "level2.tmx";
        level4.path = pathPrefix + "lvl3.tmx";

        level1.name = "level1";
        level2.name = "level2";
        level3.name = "level3";
        level4.name = "level4";

        level1.hasCutscene = false;
        level2.hasCutscene = false;
        level3.hasCutscene = false;
        level4.hasCutscene = false;

        level1.nextLevel = level2;
        level2.nextLevel = level3;
        level3.nextLevel = level4;
        level4.nextLevel = level1;

        level1.index = 0;
        level2.index = 1;
        level3.index = 2;
        level4.index = 3;

        levels.add(level1);
        levels.add(level2);
        levels.add(level3);
        levels.add(level4);

        currentLevel = UserPrefs.getLevel(Globals.currentGameSave);
    }


    public static class Level {
        public String  path;
        public Level   nextLevel;
        public boolean hasCutscene;
        public int     index;
        public String  name;
    }
}
