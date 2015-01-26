package game.players;

import game.Constants;

import java.awt.*;
import java.util.Map;
import java.util.Random;

public abstract class Player {
    public Random random = Constants.random;//Players use this random.

    public static Class<? extends Player> currentPlayer = RunningStar.class;

    public abstract Point takeTurn(String genome, Map<Point, Integer> vision);

    public void setRandom(Random random){//Players cannot call this.
        this.random = random;
    }

}
