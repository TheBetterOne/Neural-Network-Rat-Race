package game.traps;

import java.util.Random;

import static game.Constants.NUM_WALL_COLORS;

public class WallTraps {

    public WallTraps(Traps traps, Random random){
        for (int i = 0; i < NUM_WALL_COLORS; i++) {
            traps.allTraps.put(traps.colorCodes.colorAssigner.next(), new WallTrap());
        }
    }

}
