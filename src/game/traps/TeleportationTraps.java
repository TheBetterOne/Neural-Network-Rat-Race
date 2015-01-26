package game.traps;

import game.Utils;

import java.awt.*;
import java.util.Collections;
import java.util.Random;

import static game.Constants.NUM_TELEPORTER_COLORS;

public class TeleportationTraps {

    public TeleportationTraps(Traps traps, Random random){
        java.util.List<Point> possibleDirections = Utils.createArea(9);
        Collections.shuffle(possibleDirections, random);
        java.util.List<Point> directionAssigner = possibleDirections.subList(0, NUM_TELEPORTER_COLORS);
        for (int i = 0; i < NUM_TELEPORTER_COLORS; i+=2) {
            Point direction = Utils.pickOne(directionAssigner, random);
            Point oppositeDirection = new Point(-1*direction.x, -1*direction.y);
            traps.allTraps.put(traps.colorCodes.colorAssigner.next(), new TeleportationTrap(direction));
            traps.allTraps.put(traps.colorCodes.colorAssigner.next(), new TeleportationTrap(oppositeDirection));
        }
    }

}
