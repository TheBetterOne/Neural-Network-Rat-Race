package game;

import java.util.ArrayList;
import java.util.List;

import static game.Constants.NUMBER_OF_BOARDS;
import static game.Constants.NUMBER_OF_TURNS;

public class GameData {

    private int totalPoints = 1;
    private int maxFitness = 0;
    private int totalFitness = 0;
    private List<Row> rows = new ArrayList<>();
    private int turnNumber = 0;
    private int id;

    public GameData(int id) {
        this.id = id;
    }

    public void addRow(double time /*, int totalPoints*/, int population /*, double averageFitness, int maxFitness*/) {
        rows.add(new Row(turnNumber++ * 100, time, totalPoints, population, totalFitness * 1.0/population, maxFitness));
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void updateFitness(int fitness){
        if (fitness > maxFitness){
            maxFitness = fitness;
        }
        totalFitness += fitness;
    }

    public int getTotalFitness() {
        return totalFitness;
    }

    public int getMaxFitness() {
        return maxFitness;
    }

    public void restart() {
        totalFitness = 0;
    }

    public void addPoints(int points) {
        totalPoints += points;
    }

    public Row getEntry(int index){
        return rows.get(index);
    }

    public class Row {
        private final int maxFitness;
        private final double averageFitness;
        private final int population;
        private final int totalPoints;
        private final double time;
        private final int turnNumber;

        private Row(int turnNumber, double time, int totalPoints, int population, double averageFitness, int maxFitness) {
            this.turnNumber = turnNumber;
            this.time = time;
            this.totalPoints = totalPoints;
            this.population = population;
            this.averageFitness = averageFitness;
            this.maxFitness = maxFitness;
        }

        public double getTime() {
            return time;
        }

        public int getTotalPoints() {
            return totalPoints;
        }

        public int getPopulation() {
            return population;
        }

        public double getAverageFitness() {
            return averageFitness;
        }

        public int getMaxFitness() {
            return maxFitness;
        }

        public int getTurnNumber() {
            return turnNumber;
        }
    }


    @Override
    public String toString(){
        String result = "Running board #"+(id+1)+"/"+NUMBER_OF_BOARDS + "\n";
        for (Row row : rows){
            result += String.format("%3d%% ", row.getTurnNumber() * 100 / NUMBER_OF_TURNS);
            result += String.format("%5.4f sec ", row.getTime());
            result += String.format("%10d ", row.getTotalPoints());
            result += String.format("Pop %5d ", row.getPopulation());
            result += String.format("Fit ");
            result += String.format("Avg %11.3f ", row.getAverageFitness());
            result += String.format("Max %5d ", row.getMaxFitness());
            result += "\n";
        }
        result += "Your bot got " + getTotalPoints() + " points." + "\n";
        return result;
    }
}
