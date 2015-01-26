package game.traps;

import game.ColorCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static game.Constants.NUM_SAFE_COLORS;

public class EmptyTraps {

    public final Map<ColorCode, EmptyTrap> EMPTY_TRAPS = new HashMap<>(NUM_SAFE_COLORS);

    public EmptyTraps(Traps traps, Random random){
        EmptyTrap emptyTrap = new EmptyTrap();
        for (int i = 0; i < NUM_SAFE_COLORS; i++) {
            ColorCode nextColor = traps.colorCodes.colorAssigner.next();
            traps.allTraps.put(nextColor, emptyTrap);
            EMPTY_TRAPS.put(nextColor, emptyTrap);
        }
    }

}
