package game.traps;

import game.ColorCode;
import game.ColorCodes;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Traps {

    public ColorCodes colorCodes;
    DeathTraps deathTraps;
    public EmptyTraps emptyTraps;
    TeleportationTraps teleportationTraps;
    WallTraps wallTraps;

    public final Map<ColorCode, Trap> allTraps = new HashMap<>();

    public Traps(Random random){
        colorCodes = new ColorCodes(random);
        deathTraps = new DeathTraps(this, random);
        emptyTraps = new EmptyTraps(this, random);
        teleportationTraps = new TeleportationTraps(this, random);
        wallTraps = new WallTraps(this, random);
    }

}
