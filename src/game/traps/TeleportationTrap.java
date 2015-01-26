package game.traps;

import java.awt.*;


public class TeleportationTrap extends Trap{

    public final Point direction;
    public TeleportationTrap(Point direction){
        this.direction = direction;
    }
}
