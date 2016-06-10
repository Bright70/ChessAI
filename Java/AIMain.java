/*
    Pits two AI's against each other for self-improvement, experimental
 */

package chessai;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

public class AIMain {

    public static void main(String[] args){

        Board board;
        ChessAI control;
        ChessAI test;

        // for AI self improvement
        //double[] pieceVals;
        double[][] miscArray; // first dim is dropoff and lifetime, second array items in scoreweight (aiThread)

        while(true) { // will play self until program is terminated

            System.out.println("Starting new game");

            //pieceVals = new double[6]; // in alphabetical order

            miscArray = getValues(false); // get previously stored values
            board = new Board(); // init board
            control = new ChessAI(true, miscArray); // control AI, uses values defined in file

            miscArray = getValues(true); // modify values from text file\
            test = new ChessAI(true, miscArray); // test AI, uses modified values from text file

            boolean checkmate = false; // control for game ending

            int startTime = (int)System.currentTimeMillis() / 1000; // timer for games, to see how long

            while (!checkmate) { // plays until one side loses
                Move controlMove = control.aiMakeMove(board);

                if (controlMove == null) // no legal moves, game over
                    checkmate = true;
                else
                    board.makeMove(controlMove);

                System.out.println("Turn " + board.turnCount);
                board.displayBoard(board.board);

                Move testMove = test.aiMakeMove(board);
                if (testMove == null) // no legal moves, game over
                    checkmate = true;
                else
                    board.makeMove(testMove);

                System.out.println("Turn " + board.turnCount);
                board.displayBoard(board.board);
            }

            System.out.println("Game took " + (double)(((System.currentTimeMillis() / 1000) - startTime) / 3600) + "h"); // displays time in hours

            if (board.turnCount % 2 == 0) { // black checkmated white | control AI won
                System.out.println("Control AI won");
                // nothing changes
            } else { // white checkmated black | test AI won
                System.out.println("Testing AI won");
                setValues(miscArray);
                // modify static values to values used by testing AI
            }

            ChessAI.sleep(10000); // sleep for 10s between games
        }
    }

    private static double[][] getValues(boolean test){
        //double[] pieceVals = new double[6];
        double[][] miscVals = new double[2][3];
        Random rand = new Random();
        List<String> lines = null;

        try {
            lines = Files.readAllLines(Paths.get("src/files/miscValues"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("No such file exists");
            return null;
        }

        // removed piece value modifier due to technical difficulties implementing the change of values on one board instance

        if(test){ // modify values from file without modifying file, as test
            /*
            for (int i = 0; i < lines.size(); i++){
                pieceVals[i] = Double.valueOf(lines.get(i));
                if(rand.nextInt(2) == 0)
                    pieceVals[i] += 0.1;
                else
                    pieceVals[i] -= 0.1;
            }
            return pieceVals;
            */

            for (int i = 0; i < lines.size(); i++){
                miscVals[(i > 1 ? 1 : 0)][i % (i > 2 ? 3 : 2)] = Double.valueOf(lines.get(i));
                if(rand.nextBoolean())
                    miscVals[(i > 1 ? 1 : 0)][i % (i > 2 ? 3 : 2)] += (i > 2 ? 0.1 : 1d);
                else
                    miscVals[(i > 1 ? 1 : 0)][i % (i > 2 ? 3 : 2)] -= (i > 2 ? 0.1 : 1d);
            }

        }
        else{ // grab values from file
            /*
            for (int i = 0; i < lines.size(); i++){
                pieceVals[i] = Double.valueOf(lines.get(i));
            }
            return pieceVals;
            */
            for (int i = 0; i < lines.size(); i++){
                miscVals[(i > 1 ? 1 : 0)][i % (i > 2 ? 3 : 2)] = Double.valueOf(lines.get(i));
            }
        }
        return miscVals;
    }

    private static void setValues(double[][] miscVals){
        PrintWriter writer = null;

        // writing board
        try {
            Files.deleteIfExists(Paths.get("/src/files/miscValues")); // delete to prevent reading errors
            writer = new PrintWriter("src/files/miscValues", "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for(int i = 0; i < 5; i++){
            writer.println(miscVals[(i > 1 ? 1 : 0)][i % (i > 2 ? 3 : 2)]);
        }
    }
}