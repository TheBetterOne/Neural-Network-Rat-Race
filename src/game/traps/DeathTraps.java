package game.traps;

import game.Utils;

import java.awt.*;
import java.util.Collections;
import java.util.Random;

import static game.Constants.NUM_KILLER_COLORS;

public class DeathTraps{

    public DeathTraps(Traps traps, Random random){
        java.util.List<Point> possibleDirections = Utils.createArea(3);
        Collections.shuffle(possibleDirections, random);
        java.util.List<Point> directionAssigner = possibleDirections.subList(0, NUM_KILLER_COLORS);
        for (int i = 0; i < NUM_KILLER_COLORS; i++) {
            traps.allTraps.put(traps.colorCodes.colorAssigner.next(), new DeathTrap(Utils.pickOne(directionAssigner, random)));
        }
    }

}
