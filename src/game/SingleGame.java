package game;

import game.display.Display;
import game.players.Player;

import java.awt.*;
import java.io.PrintStream;
import java.util.*;
import java.util.List;

import static game.Constants.*;

public class SingleGame implements Runnable{


    private Player player;
    private Board board;
    private Random random;

    private List<PrintStream> outs = new ArrayList<>();

    private GameData data;

    private boolean showDisplay;

    private boolean close = false;
    private boolean running = false;
    private boolean finished = false;

    private final int id;

    public SingleGame(int id, Player player, Board board, Random random, boolean showDisplay){
        this.id = id;
        this.player = player;
        this.board = board;
        this.random = random;
        this.showDisplay = showDisplay;
        player.setRandom(random);
        data = new GameData(id);
    }

    public void addOutputStream(PrintStream stream){
        outs.add(stream);
    }

    @Override
    public void run(){
        if (finished){
            throw new IllegalStateException();
        }
        running = true;
        long startTime = System.nanoTime();
        Display display = null;
        if (showDisplay){
            display = new Display(board);
        }
        printf("Running board #"+(id+1)+"/"+NUMBER_OF_BOARDS + "\n");
        for (int turnNumber = 0; turnNumber < NUMBER_OF_TURNS && !close; turnNumber++){
            data.addPoints(takeTurn(board, turnNumber, player));
            if (!lifeExists(board))
                break;
            breed(board, turnNumber, REPRODUCTION_RATE);
            board.updateSpecimen();
            if (showDisplay) {
                display.repaint();
            }
            if (turnNumber % (NUMBER_OF_TURNS/100) == 0){
                int population = 0;
                for (Point point: board.getSpecimenLocations()){
                    population += board.getSpecimen(point).size();
                }
                data.addRow((System.nanoTime() - startTime) / 1000000000.0, population);

                printf("%3d%% ", turnNumber * 100 / NUMBER_OF_TURNS);
                printf("%5.4f sec ", (System.nanoTime() - startTime) / 1000000000.0);
                printf("%10d ", data.getTotalPoints());
                printf("Pop %5d ", population);
                printf("Fit ");
                printf("Avg %11.3f ", data.getTotalFitness() * 1.0 / population);
                printf("Max %5d ", data.getMaxFitness());
                println();
            }
        }
        for (Point coordinate: board.getSpecimenLocations()){
            if (Board.atFinish(coordinate)){
                data.addPoints(board.getSpecimen(coordinate).size());
            }
        }
        println("Your bot got " + data.getTotalPoints() + " points");
        if (showDisplay){
            display.dispose();
        }
        finished = true;
        running = false;
    }

    public GameData getResults(){
        if (finished) {
            return data;
        }
        throw new IllegalStateException("Hasn't been ran yet.");
    }

    public void close(){
        close = true;
    }

    public boolean isFinished(){
        return finished;
    }

    public boolean isRunning(){
        return running;
    }

    private void println(String s) {
        for (PrintStream stream : outs){
            stream.println(s);
        }
    }

    private void println() {
        for (PrintStream stream : outs){
            stream.println();
        }
    }

    private void printf(String s, Object... objects) {
        for (PrintStream stream : outs){
            stream.printf(s, objects);
        }
    }

    public int takeTurn(Board board, int turnNumber, Player player){
        int points = 0;
        for (Point location: board.getSpecimenLocations()){
            if (Board.atFinish(location)){
                for (Specimen specimen: board.getSpecimen(location)){
                    Point newLocation = Utils.pickOne(board.startingSquares, random);
                    specimen.birthTurn = turnNumber;
                    specimen.bonusFitness += BOARD_WIDTH;
                    board.addSpecimen(specimen, newLocation);
                    points += 1;
                }
                continue;
            }
            for (Specimen specimen: board.getSpecimen(location)){
                if (turnNumber == specimen.birthTurn + SPECIMEN_LIFESPAN){
                    continue;
                }
                Map<Point, Integer> colors = new HashMap<Point, Integer>();
                for (Point offset:Utils.createArea(VISION_WIDTH)){
                    colors.put(offset, board.getSquare(Utils.add(offset, location)).colorCode.number);
                }
                Point direction = player.takeTurn(specimen.genome, colors);
                if (direction.x*direction.x + direction.y*direction.y > 2){
                    throw new RuntimeException("Direction out of bounds");
                }
                Point newLocation = Utils.add(direction, location);
                Square newSquare = board.getSquare(newLocation);
                if (newSquare.isWall) {
                    newSquare = board.getSquare(location);
                    newLocation = location;
                }
                Point teleported = Utils.add(newSquare.teleportsTo, newLocation);
                if (board.getSquare(teleported).kills){
                    continue;
                }
                board.addSpecimen(specimen, teleported);
            }
        }
        return points;
    }
    public boolean lifeExists(Board board){
        if (board.getSpecimenLocations().size() > NUM_PARENTS)
            return true;
        int population = 0;
        for (Point point: board.getSpecimenLocations()){
            population += board.getSpecimen(point).size();
            if (population >= NUM_PARENTS){
                return true;
            }
        }
        return false;
    }

    public int scoreSpecimen(Point coordinate, Specimen specimen){
        return coordinate.x + specimen.bonusFitness + 1;
    }

    public void breed(Board board, int turnNumber, int numberOffspring){
        data.restart();
        for (Point point: board.getSpecimenLocations()){
            for (Specimen specimen: board.getSpecimen(point)){
                int fitness = scoreSpecimen(point, specimen);
                data.updateFitness(fitness);
            }
        }
        for (int i = 0; i < numberOffspring; i++){
            List<Specimen> selectedSpecimen = new ArrayList<Specimen>(NUM_PARENTS);
            long remainingTotal = data.getTotalFitness();
            for (int j = 0; j < NUM_PARENTS; j++){
                long countDown;
                try {
                    countDown = random.nextLong()%remainingTotal;
                } catch (IllegalArgumentException e){
                    println(data.getTotalFitness() + "");
                    throw e;
                }
                for (Point point: board.getSpecimenLocations()){
                    for (Specimen specimen: board.getSpecimen(point)){
                        if (selectedSpecimen.contains(specimen))
                            continue;
                        int score = scoreSpecimen(point, specimen);
                        countDown -= score;
                        if (countDown <= 0){
                            selectedSpecimen.add(specimen);
                            remainingTotal -= score;
                            break;
                        }
                    }
                    if (countDown <= 0){
                        break;
                    }
                }
            }
            Specimen currentParent = Utils.pickOne(selectedSpecimen, random);
            StringBuilder newGenome = new StringBuilder();
            for (int j = 0; j < GENOME_LENGTH; j++){
                if (random.nextDouble() < GENOME_CROSSOVER_RATE){
                    currentParent = Utils.pickOne(selectedSpecimen, random);
                }
                int bit = currentParent.bitAt(j);
                if (random.nextDouble() < GENOME_MUTATION_RATE){
                    bit = -bit+1;
                }
                newGenome.append(bit);
            }
            board.addSpecimen(new Specimen(newGenome.toString(), turnNumber), Utils.pickOne(board.startingSquares, random));
        }
    }
}
