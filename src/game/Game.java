package game;

import game.players.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static game.Constants.*;

public class Game {

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        run();
    }
    public static void run() throws IllegalAccessException, InstantiationException {
        List<GameData> gameRecords = new ArrayList<>(NUMBER_OF_BOARDS);

        Board[] boards = new Board[NUMBER_OF_BOARDS];
        SingleGame[] games = new SingleGame[NUMBER_OF_BOARDS];

        if (LOG_EACH_GAME){
            new File(LOG_DIRECTORY_NAME).mkdirs();
        }

        for (int boardNumber = 0; boardNumber < NUMBER_OF_BOARDS; boardNumber++){
            boards[boardNumber] = new Board(random);
            games[boardNumber] = new SingleGame(boardNumber, Player.currentPlayer.newInstance(), boards[boardNumber], new Random(random.nextLong()), SHOW_DISPLAY);
            if (USE_SYSTEM_OUT){
                games[boardNumber].addOutputStream(System.out);
            }
            if (LOG_EACH_GAME){
                String fileName = LOG_DIRECTORY_NAME + File.separator + "game" + boardNumber + ".txt";
                File file = new File(fileName);
                try {
                    games[boardNumber].addOutputStream(new PrintStream(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        ExecutorService executer = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        for (SingleGame game : games){
            executer.submit(game);
        }
        executer.shutdown();
        while (true){
            try {
                if (executer.awaitTermination(1, TimeUnit.MINUTES)){
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (SingleGame game : games){
            gameRecords.add(game.getResults());
        }

        String output = "";

        if (NUMBER_OF_BOARDS > 1){
            output += "=========================================\n";
            output += "Individual scores: ";
            double total = 0;
            for (GameData score: gameRecords){
                output += score.getTotalPoints()+", ";
                total += Math.log(score.getTotalPoints());
            }
            output += "\nYour final score is "+Math.exp(total / gameRecords.size()) + "\n";
            System.out.print(output);
        }

        if (SAVE_RESULTS){
            PrintStream out = null;
            try {
                out = new PrintStream(RESULTS_FILE_NAME);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            for (GameData data : gameRecords){
                out.print(data);
            }
            out.print(output);
        }

    }

}
