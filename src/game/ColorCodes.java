package game;

import java.util.*;

import static game.Constants.NUM_COLORS;

public class ColorCodes {

    public final List<ColorCode> ALL_COLOR_CODES = new ArrayList<>(NUM_COLORS);

    public ListIterator<ColorCode> colorAssigner = ALL_COLOR_CODES.listIterator();

    public ColorCodes(Random random){
        for (int i = 0; i < NUM_COLORS; i++) {
            ALL_COLOR_CODES.add(new ColorCode(i));
        }
        Collections.shuffle(ALL_COLOR_CODES, random);
        colorAssigner = ALL_COLOR_CODES.listIterator();
    }

}
