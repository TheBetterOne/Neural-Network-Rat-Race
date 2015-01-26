package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import static game.Constants.NUM_COLORS;

public class ColorCode {
    public static final List<ColorCode> ALL_COLOR_CODES = new ArrayList<ColorCode>(NUM_COLORS);
    public static void initialize(){
        ALL_COLOR_CODES.clear();
        for (int i = 0; i < NUM_COLORS; i++) {
            ALL_COLOR_CODES.add(new ColorCode(i));
        }
        Collections.shuffle(ALL_COLOR_CODES);
        colorAssigner = ALL_COLOR_CODES.listIterator();
    }
    public static ListIterator<ColorCode> colorAssigner = ALL_COLOR_CODES.listIterator();
    public final int number;
    public ColorCode(int number){
        this.number = number;
    }
}
