/*
    Actual AI for ChessAI.
    Chess is a zero-sum game, so only one evaluation is required.
    Factors to consider:
        Piece values
        Piece mobility
        Space advantage
        King safety
        Whose turn it is
        Pawn structure
*/

package chessai;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//main AI
public class ChessAI {
    //variables
    static double[] scores;
    static boolean[] threadDead;
    static private double THRESHOLD = 0.25;
    private Random rand = new Random();
    public static boolean debug;

    //initialize vars
    public ChessAI(boolean debug) {
        ChessAI.debug = debug;
    }

    //choose a move by evaulating multiple positions with threads
    public Move aiMakeMove(Board game) {
        //vars
        long startTime = System.currentTimeMillis();
        int possibleMoves = 0, color = game.turnCount % 2 == 0 ? 1 : -1;
        Move[] moves = new Move[128];
        
        //lambda for next block
        java.util.function.BiFunction<Move, Integer, Boolean> operateMove = (m, i) -> {
            if(game.isLegal(m, true)) {
                moves[i] = m;
                return true;
            }
            else return false;
        };
        
        //get all possible moves
        for(int x = 0; x < 8; x++) {
            for(int y = 0; y < 8; y++) {
                if(game.board[x][y].name != ' ' && game.board[x][y].color == 
                        (game.turnCount % 2 == 0 ? Color.WHITE : Color.BLACK)) {
                    //find all legal moves
                    switch(game.board[x][y].name) {
                        case 'P': 
                            if(operateMove.apply(new Move(x, x, y, y-color, game.board[x][y], game.board[x][y-color]), possibleMoves))
                                possibleMoves++;
                            if(y != (color > 0 ? 1 : 6) && operateMove.apply(new Move(x, x, y, y-2*color, game.board[x][y], game.board[x][y-2*color]), possibleMoves))
                                possibleMoves++;
                            if(x > 0 && operateMove.apply(new Move(x, x-1, y, y-color, game.board[x][y], game.board[x-1][y-color]), possibleMoves))
                                possibleMoves++;
                            if(x < 7 && operateMove.apply(new Move(x, x+1, y, y-color, game.board[x][y], game.board[x+1][y-color]), possibleMoves))
                                possibleMoves++;
                            break;
                        case 'N':
                            if(x > 1 && y > 0 && operateMove.apply(new Move(x, x-2, y, y-1, game.board[x][y], game.board[x-2][y-1]), possibleMoves))
                                possibleMoves++;
                            if(x > 1 && y < 7 && operateMove.apply(new Move(x, x-2, y, y+1, game.board[x][y], game.board[x-2][y+1]), possibleMoves))
                                possibleMoves++;
                            if(x < 6 && y > 0 && operateMove.apply(new Move(x, x+2, y, y-1, game.board[x][y], game.board[x+2][y-1]), possibleMoves))
                                possibleMoves++;
                            if(x < 6 && y < 7 && operateMove.apply(new Move(x, x+2, y, y+1, game.board[x][y], game.board[x+2][y+1]), possibleMoves))
                                possibleMoves++;
                            if(x > 0 && y > 1 && operateMove.apply(new Move(x, x-1, y, y-2, game.board[x][y], game.board[x-1][y-2]), possibleMoves))
                                possibleMoves++;
                            if(x > 0 && y < 6 && operateMove.apply(new Move(x, x-1, y, y+2, game.board[x][y], game.board[x-1][y+2]), possibleMoves))
                                possibleMoves++;
                            if(x < 7 && y > 1 && operateMove.apply(new Move(x, x+1, y, y-2, game.board[x][y], game.board[x+1][y-2]), possibleMoves))
                                possibleMoves++;
                            if(x < 7 && y < 6 && operateMove.apply(new Move(x, x+1, y, y+2, game.board[x][y], game.board[x+1][y+2]), possibleMoves))
                                possibleMoves++;
                            break;
                        case 'B':
                            for(int ex = x-1, ey = y-1; ex >= 0 && ey >= 0; ex--, ey--)
                                if(operateMove.apply(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]), possibleMoves))
                                    possibleMoves++;
                                else break;
                            for(int ex = x-1, ey = y+1; ex >= 0 && ey < 8; ex--, ey++)
                                if(operateMove.apply(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]), possibleMoves))
                                    possibleMoves++;
                                else break;
                            for(int ex = x+1, ey = y-1; ex < 8 && ey >= 0; ex++, ey--)
                                if(operateMove.apply(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]), possibleMoves))
                                    possibleMoves++;
                                else break;
                            for(int ex = x+1, ey = y+1; ex < 8 && ey < 8; ex++, ey++)
                                if(operateMove.apply(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]), possibleMoves))
                                    possibleMoves++;
                                else break;
                            break;
                        case 'R':
                            for(int ex = x-1; ex >= 0; ex--)
                                if(operateMove.apply(new Move(x, ex, y, y, game.board[x][y], game.board[ex][y]), possibleMoves))
                                    possibleMoves++;
                                else break;
                            for(int ex = x+1; ex < 8; ex++)
                                if(operateMove.apply(new Move(x, ex, y, y, game.board[x][y], game.board[ex][y]), possibleMoves))
                                    possibleMoves++;
                                else break;
                            for(int ey = y-1; ey >= 0; ey--)
                                if(operateMove.apply(new Move(x, x, y, ey, game.board[x][y], game.board[x][ey]), possibleMoves))
                                    possibleMoves++;
                                else break;
                            for(int ey = y+1; ey < 8; ey++)
                                if(operateMove.apply(new Move(x, x, y, ey, game.board[x][y], game.board[x][ey]), possibleMoves))
                                    possibleMoves++;
                                else break;
                            break;
                        case 'Q':
                            for(int ex = x-1, ey = y-1; ex >= 0 && ey >= 0; ex--, ey--)
                                if(operateMove.apply(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]), possibleMoves))
                                    possibleMoves++;
                                else break;
                            for(int ex = x-1, ey = y+1; ex >= 0 && ey < 8; ex--, ey++)
                                if(operateMove.apply(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]), possibleMoves))
                                    possibleMoves++;
                                else break;
                            for(int ex = x+1, ey = y-1; ex < 8 && ey >= 0; ex++, ey--)
                                if(operateMove.apply(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]), possibleMoves))
                                    possibleMoves++;
                                else break;
                            for(int ex = x+1, ey = y+1; ex < 8 && ey < 8; ex++, ey++)
                                if(operateMove.apply(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]), possibleMoves))
                                    possibleMoves++;
                                else break;
                            for(int ex = x-1; ex >= 0; ex--)
                                if(operateMove.apply(new Move(x, ex, y, y, game.board[x][y], game.board[ex][y]), possibleMoves))
                                    possibleMoves++;
                                else break;
                            for(int ex = x+1; ex < 8; ex++)
                                if(operateMove.apply(new Move(x, ex, y, y, game.board[x][y], game.board[ex][y]), possibleMoves))
                                    possibleMoves++;
                                else break;
                            for(int ey = y-1; ey >= 0; ey--)
                                if(operateMove.apply(new Move(x, x, y, ey, game.board[x][y], game.board[x][ey]), possibleMoves))
                                    possibleMoves++;
                                else break;
                            for(int ey = y+1; ey < 8; ey++)
                                if(operateMove.apply(new Move(x, x, y, ey, game.board[x][y], game.board[x][ey]), possibleMoves))
                                    possibleMoves++;
                                else break;
                            break;
                        case 'K':
                            if(x > 0 && y > 0 && operateMove.apply(new Move(x, x-1, y, y-1, game.board[x][y], game.board[x-1][y-1]), possibleMoves))
                                possibleMoves++;
                            if(x > 0 && y > 0 && operateMove.apply(new Move(x, x-1, y, y, game.board[x][y], game.board[x-1][y]), possibleMoves))
                                possibleMoves++;
                            if(x > 0 && y < 7 && operateMove.apply(new Move(x, x-1, y, y+1, game.board[x][y], game.board[x-1][y+1]), possibleMoves))
                                possibleMoves++;
                            if(y > 0 && operateMove.apply(new Move(x, x, y, y-1, game.board[x][y], game.board[x][y-1]), possibleMoves))
                                possibleMoves++;
                            if(y < 7 && operateMove.apply(new Move(x, x, y, y+1, game.board[x][y], game.board[x][y+1]), possibleMoves))
                                possibleMoves++;
                            if(x < 7 && y > 0 && operateMove.apply(new Move(x, x+1, y, y-1, game.board[x][y], game.board[x+1][y-1]), possibleMoves))
                                possibleMoves++;
                            if(x < 7 && operateMove.apply(new Move(x, x+1, y, y, game.board[x][y], game.board[x+1][y]), possibleMoves))
                                possibleMoves++;
                            if(x < 7 && y < 7 && operateMove.apply(new Move(x, x+1, y, y+1, game.board[x][y], game.board[x+1][y+1]), possibleMoves))
                                possibleMoves++;
                            if(x == 4 && operateMove.apply(new Move(x, x+2, y, y, game.board[x][y], game.board[x+2][y]), possibleMoves))
                                possibleMoves++;
                            if(x == 4 && operateMove.apply(new Move(x, x-2, y, y, game.board[x][y], game.board[x-2][y]), possibleMoves))
                                possibleMoves++;
                            break;
                        default: System.out.print("Error."); //should never happen
                    }
                }
            }
        }

        if(possibleMoves == 0){ // checkmate or stalemate
            if(game.isInCheck(game.turnCount % 2 == 0 ? Color.BLACK : Color.WHITE))
                System.out.println((game.turnCount % 2 == 0 ? "Black" : "White") + " Checkmate");
            else
                System.out.println((game.turnCount % 2 == 0 ? "Black" : "White") + " Stalemate");
            return null;
        }
        
        Move[] nMoves = new Move[possibleMoves];
        System.arraycopy(moves, 0, nMoves, 0, possibleMoves);
        scores = new double[possibleMoves];
        threadDead = new boolean[possibleMoves];

        //evaluate positions
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for(int i = 0; i < possibleMoves; i++)
            threadPool.execute(new aiThread(i, game, nMoves[i]));

        //wait until threads are done

        while(true) {
            sleep(1000);
            int dead = 0;
            for(int x = 0; x < possibleMoves; x++)
                if(threadDead[x]) {
                    dead++;
                }
            if(dead == possibleMoves){
                threadPool.shutdown();
                if(debug)
                    System.out.println("AI played");
                break;
            }
            if(debug)
                System.out.println((possibleMoves - dead) + " threads live");
        }

        //quicksort moves based on score
        quickSort(scores, nMoves, 0, possibleMoves - 1);

        int topMoves = 0;
        // randomizer, will pick top 25% of moves best for the current player, and choose one randomly
        /*

        for(int x = 0; x < possibleMoves; x++) {
            if(game.turnCount % 2 == 0)
                if(scores[possibleMoves - 1] < scores[x] + THRESHOLD)
                    topMoves++;
            if(game.turnCount % 2 == 1)
                if(scores[0] > scores[x] - THRESHOLD)
                    topMoves++;
        }


        // choose top 25% of viable moves
        topMoves /= 4;
        // choose a random one
        topMoves = topMoves > 0 ? rand.nextInt(topMoves): 0;
        */
        if(debug) {
            System.out.println("Computer evaluation: " + scores[(game.turnCount % 2 == 0 ? possibleMoves - (topMoves + 1) : topMoves)]);
            System.out.println("Computation took " + (float) (System.currentTimeMillis() - startTime) / 1000.0 + "s");
        }

        for(int x = 0; x < possibleMoves; x++)
            System.out.println(scores[x]);

        return nMoves[(game.turnCount % 2 == 0 ? possibleMoves - (topMoves + 1) : topMoves)];
    }

    public static void sleep(int time){
        long startTime = System.currentTimeMillis();
        while(System.currentTimeMillis() - startTime < time);
    }
    
    //quicksort moves for aiMakeMove. last move should be the best for white
    private void quickSort(double[] scores, Move[] moves, int left, int right)
    {
        double temp; Move mTemp;
        double pivot = scores[(left + right) / 2];
        int i = left, j = right;
        
        while (i <= j) {
            while (scores[i] < pivot)
                i++;
            while (scores[j] > pivot)
                j--;
            if (i <= j) {
                temp = scores[i];
                scores[i] = scores[j];
                scores[j] = temp;
                mTemp = moves[i];
                moves[i] = moves[j];
                moves[j] = mTemp;
                i++;
                j--;
            }
        }
        
        if (left < j)
            quickSort(scores, moves, left, j);
        if (i < right)
            quickSort(scores, moves, i, right);
    }
}
