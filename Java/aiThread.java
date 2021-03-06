/*
    Game tree branching using threads.
    Owns the branching and evaluation functions.
 */

package chessai;

//threading
public class aiThread extends Thread {
    //heuristic constants
    private static double[] SCOREWEIGHT = { //evaluate
        1.0,    //piece value + mobility
        0.1,    //space bonus
        0.3     //king safety
    };
    private static double LIFETIME = 300, DROPOFF = 30, THRESHOLD = 30; //branching
    private static int TIMEOUT = 120; // time limit for thread runtime in seconds

    //variables
    private Board game;
    private int depth, startTime, possibleMoves,  arrPos;
    private double score, temp, eval = 1.675e-27;
    
    //initializer given move index and board, copies data passed from ChessAI class to local vars
    aiThread(int arrPos, Board game, Move move) {
        initThread(arrPos, game, move);
    }

    aiThread(int arrPos, Board game, Move move, double[][] miscValues) {
        LIFETIME = miscValues[0][0];
        DROPOFF = miscValues[0][1];
        SCOREWEIGHT = miscValues[1];
        initThread(arrPos, game, move);
    }


    private void initThread(int arrPos, Board game, Move move){
        this.arrPos = arrPos;
        this.game = new Board();
        for(int x = 0; x < 8; x++){
            this.game.board[x] = game.board[x].clone();
        }
        this.game.turnCount = game.turnCount;
        for(int x = 0; x < 2; x++){
            this.game.hasMoved[x] = game.hasMoved[x].clone();
        }
        System.arraycopy(game.moves, 0, this.game.moves, 0, this.game.turnCount);
        this.game.makeMove(move);
        startTime = (int)(System.currentTimeMillis() /1000);
    }
    
    //threading
    @Override
    public void run() { // init thread
        ChessAI.scores[arrPos] = branch(1, game);
        ChessAI.threadDead[arrPos] = true; // once thread is done, notify that it's terminated
        if(ChessAI.debug)
            System.out.println("Thread " + arrPos + " ended @ depth " + depth + " with score " + ChessAI.scores[arrPos]);
    }
    
    //evaluate position, return score
    double evaluate(Board game) {
        double score = 0; //positive is advantage for white

        //piece value and mobility bonus
        double[] pieceBonus = new double[2]; //0 = white, 1 = black
        int turnCount = game.turnCount, legalMoves = 0; possibleMoves = 0;
        double bonus;
        Piece p;
        for(int x = 0; x < 8; x++) {
            for(int y = 0; y < 8; y++) {
                possibleMoves += legalMoves;
                legalMoves = 0;
                
                //for each piece
                if(game.board[x][y].name != ' ') {
                    //reference for better readability
                    p = game.board[x][y];
                    
                    //change turns to check legality
                    if(game.turnCount % 2 != (p.color == Color.WHITE ? 0 : 1))
                        game.turnCount++;
                    
                    //find all legal moves
                    switch(p.name) {
                        //pawn
                        case 'P': 
                            if(y < 7 && y > 1 && game.isLegal(new Move(x, x, y, y-(turnCount%2==0?1:-1), game.board[x][y], game.board[x][y-(turnCount%2==0?1:-1)]), true))
                                legalMoves++;
                            if(y == (turnCount%2==0?6:1) && game.isLegal(new Move(x, x, y, y-(turnCount%2==0?2:-2), game.board[x][y], game.board[x][y-(turnCount%2==0?2:-2)]), true))
                                legalMoves++;
                            if(x > 0 && x < 7 && y < 7 && game.isLegal(new Move(x, x-1, y, y-(turnCount%2==0?1:-1), game.board[x][y], game.board[x-1][y-(turnCount%2==0?1:-1)]), true))
                                legalMoves++;
                            if(x < 7 && y < 7 && game.isLegal(new Move(x, x+1, y, y-(turnCount%2==0?1:-1), game.board[x][y], game.board[x+1][y-(turnCount%2==0?1:-1)]), true))
                                legalMoves++;
                            break;
                        //knight
                        case 'N':
                            if(x > 1 && y > 0 && game.isLegal(new Move(x, x-2, y, y-1, game.board[x][y], game.board[x-2][y-1]), true))
                                legalMoves++;
                            if(x > 1 && y < 7 && game.isLegal(new Move(x, x-2, y, y+1, game.board[x][y], game.board[x-2][y+1]), true))
                                legalMoves++;
                            if(x < 6 && y > 0 && game.isLegal(new Move(x, x+2, y, y-1, game.board[x][y], game.board[x+2][y-1]), true))
                                legalMoves++;
                            if(x < 6 && y < 7 && game.isLegal(new Move(x, x+2, y, y+1, game.board[x][y], game.board[x+2][y+1]), true))
                                legalMoves++;
                            if(x > 0 && y > 1 && game.isLegal(new Move(x, x-1, y, y-2, game.board[x][y], game.board[x-1][y-2]), true))
                                legalMoves++;
                            if(x > 0 && y < 6 && game.isLegal(new Move(x, x-1, y, y+2, game.board[x][y], game.board[x-1][y+2]), true))
                                legalMoves++;
                            if(x < 7 && y > 1 && game.isLegal(new Move(x, x+1, y, y-2, game.board[x][y], game.board[x+1][y-2]), true))
                                legalMoves++;
                            if(x < 7 && y < 6 && game.isLegal(new Move(x, x+1, y, y+2, game.board[x][y], game.board[x+1][y+2]), true))
                                legalMoves++;
                            break;
                        //bishop
                        case 'B':
                            for(int ex = x-1, ey = y-1; ex >= 0 && ey >= 0; ex--, ey--)
                                if(game.isLegal(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]), true))
                                    legalMoves++;
                                else break;
                            for(int ex = x-1, ey = y+1; ex >= 0 && ey < 8; ex--, ey++)
                                if(game.isLegal(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]), true))
                                    legalMoves++;
                                else break;
                            for(int ex = x+1, ey = y-1; ex < 8 && ey >= 0; ex++, ey--)
                                if(game.isLegal(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]), true))
                                    legalMoves++;
                                else break;
                            for(int ex = x+1, ey = y+1; ex < 8 && ey < 8; ex++, ey++)
                                if(game.isLegal(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]), true))
                                    legalMoves++;
                                else break;
                            break;
                        //rook
                        case 'R':
                            for(int ex = x-1; ex >= 0; ex--)
                                if(game.isLegal(new Move(x, ex, y, y, game.board[x][y], game.board[ex][y]), true))
                                    legalMoves++;
                                else break;
                            for(int ex = x+1; ex < 8; ex++)
                                if(game.isLegal(new Move(x, ex, y, y, game.board[x][y], game.board[ex][y]), true))
                                    legalMoves++;
                                else break;
                            for(int ey = y-1; ey >= 0; ey--)
                                if(game.isLegal(new Move(x, x, y, ey, game.board[x][y], game.board[x][ey]), true))
                                    legalMoves++;
                                else break;
                            for(int ey = y+1; ey < 8; ey++)
                                if(game.isLegal(new Move(x, x, y, ey, game.board[x][y], game.board[x][ey]), true))
                                    legalMoves++;
                                else break;
                            break;
                        case 'Q':
                            //queen
                            for(int ex = x-1, ey = y-1; ex >= 0 && ey >= 0; ex--, ey--)
                                if(game.isLegal(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]), true))
                                    legalMoves++;
                                else break;
                            for(int ex = x-1, ey = y+1; ex >= 0 && ey < 8; ex--, ey++)
                                if(game.isLegal(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]), true))
                                    legalMoves++;
                                else break;
                            for(int ex = x+1, ey = y-1; ex < 8 && ey >= 0; ex++, ey--)
                                if(game.isLegal(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]), true))
                                    legalMoves++;
                                else break;
                            for(int ex = x+1, ey = y+1; ex < 8 && ey < 8; ex++, ey++)
                                if(game.isLegal(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]), true))
                                    legalMoves++;
                                else break;
                            for(int ex = x-1; ex >= 0; ex--)
                                if(game.isLegal(new Move(x, ex, y, y, game.board[x][y], game.board[ex][y]), true))
                                    legalMoves++;
                                else break;
                            for(int ex = x+1; ex < 8; ex++)
                                if(game.isLegal(new Move(x, ex, y, y, game.board[x][y], game.board[ex][y]), true))
                                    legalMoves++;
                                else break;
                            for(int ey = y-1; ey >= 0; ey--)
                                if(game.isLegal(new Move(x, x, y, ey, game.board[x][y], game.board[x][ey]), true))
                                    legalMoves++;
                                else break;
                            for(int ey = y+1; ey < 8; ey++)
                                if(game.isLegal(new Move(x, x, y, ey, game.board[x][y], game.board[x][ey]), true))
                                    legalMoves++;
                                else break;
                            break;
                        case 'K':
                            //king
                            if(x > 0 && y > 0 && game.isLegal(new Move(x, x-1, y, y-1, game.board[x][y], game.board[x-1][y-1]), true))
                                legalMoves++;
                            if(x > 0 && y > 0 && game.isLegal(new Move(x, x-1, y, y, game.board[x][y], game.board[x-1][y]), true))
                                legalMoves++;
                            if(x > 0 && y < 7 && game.isLegal(new Move(x, x-1, y, y+1, game.board[x][y], game.board[x-1][y+1]), true))
                                legalMoves++;
                            if(y > 0 && game.isLegal(new Move(x, x, y, y-1, game.board[x][y], game.board[x][y-1]), true))
                                legalMoves++;
                            if(y < 7 && game.isLegal(new Move(x, x, y, y+1, game.board[x][y], game.board[x][y+1]), true))
                                legalMoves++;
                            if(x < 7 && y > 0 && game.isLegal(new Move(x, x+1, y, y-1, game.board[x][y], game.board[x+1][y-1]), true))
                                legalMoves++;
                            if(x < 7 && game.isLegal(new Move(x, x+1, y, y, game.board[x][y], game.board[x+1][y]), true))
                                legalMoves++;
                            if(x < 7 && y < 7 && game.isLegal(new Move(x, x+1, y, y+1, game.board[x][y], game.board[x+1][y+1]), true))
                                legalMoves++;
                            if(x == 4 && game.isLegal(new Move(x, x+2, y, y, game.board[x][y], game.board[x+2][y]), true))
                                legalMoves++;
                            if(x == 4 && game.isLegal(new Move(x, x-2, y, y, game.board[x][y], game.board[x-2][y]), true))
                                legalMoves++;
                            break;
                        default: System.out.print("Error."); //should never happen
                    }
                    
                    //reset turnCount
                    game.turnCount = turnCount;
                    
                    //calculate mobility bonuses
                    switch(p.name) {
                        case 'P': bonus = Math.pow((double)legalMoves / 4.6 - 0.6, 3) + 1; break;
                        case 'N': bonus = Math.pow((double)legalMoves / 6.4 - 0.55, 3) + 1; break;
                        case 'B': bonus = Math.pow((double)legalMoves / 11.3 - 0.58, 3) + 1; break;
                        case 'R': bonus = Math.pow((double)legalMoves / 15.6 - 0.5, 3) + 1; break;
                        case 'Q': bonus = Math.pow((double)legalMoves / 32.3 - 0.36, 3) + 1; break;
                        case 'K': bonus = Math.pow((double)legalMoves / 8.2 - 0.4, 3) + 1; break;
                        default: bonus = 0; break;
                    }
                    pieceBonus[(p.color == Color.WHITE ? 0 : 1)] += p.value * bonus;
                }
            }
        }
        //add to score
        score += (pieceBonus[0] - pieceBonus[1]) * SCOREWEIGHT[0];
        
        //space bonus
        int[] spaceBonus = new int[2]; //0 = white, 1 = black
        //white bonus
        for(int x = 0; x < 8; x++)
            for(int y = 1; y < 7; y++)
                if(game.board[x][y].name == 'P' && game.board[x][y].color == Color.WHITE) {
                    for(int i = y + 1; i < 8; i++)
                        if(game.board[x][i].color != Color.BLACK)
                            spaceBonus[0]++;
                    break;
                }
        //black bonus
        for(int x = 0; x < 8; x++)
            for(int y = 6; y > 0; y--)
                if(game.board[x][y].name == 'P' && game.board[x][y].color == Color.BLACK) {
                    for(int i = y - 1; i >= 0; i--)
                        if(game.board[x][i].color != Color.WHITE)
                            spaceBonus[1]++;
                    break;
                }
        //add to score
        if(spaceBonus[0] != 0 && spaceBonus[1] != 0)
            score += ((double)(spaceBonus[0] - spaceBonus[1]) / 
                    (spaceBonus[0] > spaceBonus[1] ? spaceBonus[0] : spaceBonus[1]) * SCOREWEIGHT[1]);
        else score += (double)(spaceBonus[0] > spaceBonus[1] ? spaceBonus[0] : -spaceBonus[1]) * SCOREWEIGHT[1];
        
        //king safety bonus
        double[] kingSafetyBonus = new double[2]; //0 white, 1 black
        //white bonus
        for(int x = 0; x < 8; x++)
            for(int y = 0; y < 8; y++)
                //find white king
                if(game.board[x][y].name == 'K' && game.board[x][y].color == Color.WHITE) {
                    //check 5x5 square around king
                    for(int x2 = x - 2; x2 <= x + 2; x2++)
                        for(int y2 = y - 2; y2 <= y + 2; y2++)
                            if(x2 >= 0 && x2 < 8 && y2 >= 0 && y2 < 8) {
                                switch(game.board[x2][y2].color) {
                                    case WHITE:
                                        //individual piece bonuses
                                        switch(game.board[x2][y2].name) {
                                            case 'P': 
                                                kingSafetyBonus[0] += 1 * (Math.abs(x2-x) < 2 && Math.abs(y2-y) < 2 ? 0.7: 0.3); 
                                                break;
                                            case 'N':
                                                kingSafetyBonus[0] += 0.3 * (Math.abs(x2-x) < 2 && Math.abs(y2-y) < 2 ? 0.7: 0.3); 
                                                break;
                                            case 'B':
                                                kingSafetyBonus[0] += 0.25 * (Math.abs(x2-x) < 2 && Math.abs(y2-y) < 2 ? 0.7: 0.3); 
                                                break;
                                            case 'R':
                                                kingSafetyBonus[0] += 0.45 * (Math.abs(x2-x) < 2 && Math.abs(y2-y) < 2 ? 0.7: 0.3); 
                                                break;
                                            case 'Q':
                                                kingSafetyBonus[0] += 0.2 * (Math.abs(x2-x) < 2 && Math.abs(y2-y) < 2 ? 0.7: 0.3); 
                                                break;
                                            case 'K': break;
                                            default: break;
                                        }
                                        break;
                                    case BLACK:
                                        kingSafetyBonus[0] -= 1.5 * (Math.abs(x2-x) < 2 && Math.abs(y2-y) < 2 ? 0.7: 0.3);
                                        break;
                                    case NONE: break;
                                }
                            }
                    x = 8;
                    break;
                }
        //black bonus
        for(int x = 0; x < 8; x++)
            for(int y = 0; y < 8; y++)
                //find black king
                if(game.board[x][y].name == 'K' && game.board[x][y].color == Color.BLACK) {
                    //check 5x5 square around king
                    for(int x2 = x - 2; x2 <= x + 2; x2++)
                        for(int y2 = y - 2; y2 <= y + 2; y2++)
                            if(x2 >= 0 && x2 < 8 && y2 >= 0 && y2 < 8) {
                                switch(game.board[x2][y2].color) {
                                    case BLACK:
                                        //individual piece bonuses
                                        switch(game.board[x2][y2].name) {
                                            case 'P': 
                                                kingSafetyBonus[1] += 1 * (Math.abs(x2-x) < 2 && Math.abs(y2-y) < 2 ? 0.7: 0.3); 
                                                break;
                                            case 'N':
                                                kingSafetyBonus[1] += 0.3 * (Math.abs(x2-x) < 2 && Math.abs(y2-y) < 2 ? 0.7: 0.3); 
                                                break;
                                            case 'B':
                                                kingSafetyBonus[1] += 0.25 * (Math.abs(x2-x) < 2 && Math.abs(y2-y) < 2 ? 0.7: 0.3); 
                                                break;
                                            case 'R':
                                                kingSafetyBonus[1] += 0.45 * (Math.abs(x2-x) < 2 && Math.abs(y2-y) < 2 ? 0.7: 0.3); 
                                                break;
                                            case 'Q':
                                                kingSafetyBonus[1] += 0.2 * (Math.abs(x2-x) < 2 && Math.abs(y2-y) < 2 ? 0.7: 0.3); 
                                                break;
                                            case 'K': break;
                                            default: break;
                                        }
                                        break;
                                    case WHITE:
                                        kingSafetyBonus[1] -= 1.5 * (Math.abs(x2-x) < 2 && Math.abs(y2-y) < 2 ? 0.7: 0.3);
                                        break;
                                    case NONE: break;
                                }
                            }
                    x = 8;
                    break;
                }
        //add to score
        score += (kingSafetyBonus[0] - kingSafetyBonus[1]) * SCOREWEIGHT[2];
        
        //check other stuff
        
        return score;
    }
    
    //branching game tree
    double branch(int branches, Board game) {
        //vars
        score = evaluate(game);
        int color = game.turnCount % 2 == 0 ? 1 : -1;

        depth = Math.max(depth, branches);

        //timeout if running for too long
        if((System.currentTimeMillis() / 1000) - startTime > TIMEOUT){
            if(ChessAI.debug)
                System.out.println("Timeout"); // if timeout will return non ideal moves
            return score;
        }

        // setting thread priority relative to depth, the deeper, the higher priority it is assigned
        this.setPriority((branches < 15 ? (int)((MIN_PRIORITY + branches) / 1.5) : 10));

        //no possible moves means either checkmate or stalemate
        if(possibleMoves == 0){
            if(game.isInCheck(game.turnCount % 2 == 0 ? Color.BLACK : Color.WHITE)){
                System.out.println("Checkmate @ depth " + branches);
                return (game.turnCount % 2 == 0 ? 50 : -50);
            }
            else{
                System.out.println("Stalemate @ depth " + branches);
                return (game.turnCount % 2 == 0 ? ((score < 0) ? 10 : -10) : ((score < 0) ? -10 : 10));
            }
        }

        //lambda to make a move if legal, otherwise return false
        java.util.function.Function<Move, Boolean> operateMove = (m) -> {
            if(game.isLegal(m, true)) {
                game.makeMove(m);
                return true;
            }
            else return false;
        };

        //return if a branch will continue based on heuristics
        java.util.function.BooleanSupplier branchability = () -> {
            return (evaluate(game) - score) * ((LIFETIME / ((double) branches - 0.9)) - (LIFETIME / (DROPOFF - 0.9))) > THRESHOLD;
        };

        //play all legal moves
        for(int x = 0; x < 8; x++) {
            for(int y = 0; y < 8; y++) {
                if(game.board[x][y].name != ' ' && game.board[x][y].color ==
                        (game.turnCount % 2 == 0 ? Color.WHITE : Color.BLACK)) {
                    //find all legal moves
                    switch(game.board[x][y].name) {
                        case 'P':
                            if((color != -1 || y < 6) && operateMove.apply(new Move(x, x, y, y-color, game.board[x][y], game.board[x][y-color]))) {
                                //branch further
                                if(branchability.getAsBoolean()) { 
                                    temp = branch(branches + 1, game);
                                    game.undoMove();
                                    //choose best move dependent on score
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                                else
                                    game.undoMove();
                            }
                            if(y == (color > 0 ? 6 : 1) && operateMove.apply(new Move(x, x, y, y-2*color, game.board[x][y], game.board[x][y-2*color]))) {
                                if(branchability.getAsBoolean()) {  
                                    temp = branch(branches + 1, game);
                                    game.undoMove();
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                                else
                                    game.undoMove();
                            }
                            if(x > 0 && (color != -1 || y < 6) && operateMove.apply(new Move(x, x-1, y, y-color, game.board[x][y], game.board[x-1][y-color]))) {
                                if(branchability.getAsBoolean()) {   
                                    temp = branch(branches + 1, game);
                                    game.undoMove();
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                                else
                                    game.undoMove();
                            }
                            if(x < 7 && (color != -1 || y < 6) && operateMove.apply(new Move(x, x+1, y, y-color, game.board[x][y], game.board[x+1][y-color]))) {
                                if(branchability.getAsBoolean()) {  
                                    temp = branch(branches + 1, game);
                                    game.undoMove();
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                                else
                                    game.undoMove();
                            }
                            break;
                        case 'N':
                            if(x > 1 && y > 0 && operateMove.apply(new Move(x, x-2, y, y-1, game.board[x][y], game.board[x-2][y-1]))) {
                                if(branchability.getAsBoolean()) {  
                                    temp = branch(branches + 1, game);
                                    game.undoMove();
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                                else
                                    game.undoMove();
                            }
                            if(x > 1 && y < 7 && operateMove.apply(new Move(x, x-2, y, y+1, game.board[x][y], game.board[x-2][y+1]))) {
                                if(branchability.getAsBoolean()) {  
                                    temp = branch(branches + 1, game);
                                    game.undoMove();
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                                else
                                    game.undoMove();
                            }
                            if(x < 6 && y > 0 && operateMove.apply(new Move(x, x+2, y, y-1, game.board[x][y], game.board[x+2][y-1]))) {
                                if(branchability.getAsBoolean()) {  
                                    temp = branch(branches + 1, game);
                                    game.undoMove();
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                                else
                                    game.undoMove();
                            }
                            if(x < 6 && y < 7 && operateMove.apply(new Move(x, x+2, y, y+1, game.board[x][y], game.board[x+2][y+1]))) {
                                if(branchability.getAsBoolean()) {  
                                    temp = branch(branches + 1, game);
                                    game.undoMove();
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                                else
                                    game.undoMove();
                            }
                            if(x > 0 && y > 1 && operateMove.apply(new Move(x, x-1, y, y-2, game.board[x][y], game.board[x-1][y-2]))) {
                                if(branchability.getAsBoolean()) {  
                                    temp = branch(branches + 1, game);
                                    game.undoMove();
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                                else
                                    game.undoMove();
                            }
                            if(x > 0 && y < 6 && operateMove.apply(new Move(x, x-1, y, y+2, game.board[x][y], game.board[x-1][y+2]))) {
                                if(branchability.getAsBoolean()) {  
                                    temp = branch(branches + 1, game);
                                    game.undoMove();
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                                else
                                    game.undoMove();
                            }
                            if(x < 7 && y > 1 && operateMove.apply(new Move(x, x+1, y, y-2, game.board[x][y], game.board[x+1][y-2]))) {
                                if(branchability.getAsBoolean()) {  
                                    temp = branch(branches + 1, game);
                                    game.undoMove();
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                                else
                                    game.undoMove();
                            }
                            if(x < 7 && y < 6 && operateMove.apply(new Move(x, x+1, y, y+2, game.board[x][y], game.board[x+1][y+2]))) {
                                if(branchability.getAsBoolean()) {  
                                    temp = branch(branches + 1, game);
                                    game.undoMove();
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                                else
                                    game.undoMove();
                            }
                            break;
                        case 'B':
                            for(int ex = x-1, ey = y-1; ex >= 0 && ey >= 0; ex--, ey--)
                                if(operateMove.apply(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]))) {
                                    if(branchability.getAsBoolean()) {  
                                        temp = branch(branches + 1, game);
                                        game.undoMove();
                                        if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                            eval = temp;
                                    }
                                    else
                                        game.undoMove();
                                }
                                else break;
                            for(int ex = x-1, ey = y+1; ex >= 0 && ey < 8; ex--, ey++)
                                if(operateMove.apply(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]))) {
                                    if(branchability.getAsBoolean()) {  
                                        temp = branch(branches + 1, game);
                                        game.undoMove();
                                        if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                            eval = temp;
                                    }
                                    else
                                        game.undoMove();
                                }
                                else break;
                            for(int ex = x+1, ey = y-1; ex < 8 && ey >= 0; ex++, ey--)
                                if(operateMove.apply(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]))) {
                                    if(branchability.getAsBoolean()) {  
                                        temp = branch(branches + 1, game);
                                        game.undoMove();
                                        if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                            eval = temp;
                                    }
                                    else
                                        game.undoMove();
                                }
                                else break;
                            for(int ex = x+1, ey = y+1; ex < 8 && ey < 8; ex++, ey++)
                                if(operateMove.apply(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]))) {
                                    if(branchability.getAsBoolean()) {  
                                        temp = branch(branches + 1, game);
                                        game.undoMove();
                                        if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                            eval = temp;
                                    }
                                    else
                                        game.undoMove();
                                }
                                else break;
                            break;
                        case 'R':
                            for(int ex = x-1; ex >= 0; ex--)
                                if(operateMove.apply(new Move(x, ex, y, y, game.board[x][y], game.board[ex][y]))) {
                                    if(branchability.getAsBoolean()) {  
                                        temp = branch(branches + 1, game);
                                        game.undoMove();
                                        if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                            eval = temp;
                                    }
                                    else
                                        game.undoMove();
                                }
                                else break;
                            for(int ex = x+1; ex < 8; ex++)
                                if(operateMove.apply(new Move(x, ex, y, y, game.board[x][y], game.board[ex][y]))) {
                                    if(branchability.getAsBoolean()) {  
                                        temp = branch(branches + 1, game);
                                        game.undoMove();
                                        if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                            eval = temp;
                                    }
                                    else
                                        game.undoMove();
                                }
                                else break;
                            for(int ey = y-1; ey >= 0; ey--)
                                if(operateMove.apply(new Move(x, x, y, ey, game.board[x][y], game.board[x][ey]))) {
                                    if(branchability.getAsBoolean()) {  
                                        temp = branch(branches + 1, game);
                                        game.undoMove();
                                        if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                            eval = temp;
                                    }
                                    else
                                        game.undoMove();
                                }
                                else break;
                            for(int ey = y+1; ey < 8; ey++)
                                if(operateMove.apply(new Move(x, x, y, ey, game.board[x][y], game.board[x][ey]))) {
                                    if(branchability.getAsBoolean()) {  
                                        temp = branch(branches + 1, game);
                                        game.undoMove();
                                        if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                            eval = temp;
                                    }
                                    else
                                        game.undoMove();
                                }
                                else break;
                            break;
                        case 'Q':
                            for(int ex = x-1, ey = y-1; ex >= 0 && ey >= 0; ex--, ey--)
                                if(operateMove.apply(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]))) {
                                    if(branchability.getAsBoolean()) {  
                                        temp = branch(branches + 1, game);
                                        game.undoMove();
                                        if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                            eval = temp;
                                    }
                                    else
                                        game.undoMove();
                                }
                                else break;
                            for(int ex = x-1, ey = y+1; ex >= 0 && ey < 8; ex--, ey++)
                                if(operateMove.apply(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]))) {
                                    if(branchability.getAsBoolean()) {  
                                        temp = branch(branches + 1, game);
                                        game.undoMove();
                                        if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                            eval = temp;
                                    }
                                    else
                                        game.undoMove();
                                }
                                else break;
                            for(int ex = x+1, ey = y-1; ex < 8 && ey >= 0; ex++, ey--)
                                if(operateMove.apply(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]))) {
                                    if(branchability.getAsBoolean()) {  
                                        temp = branch(branches + 1, game);
                                        game.undoMove();
                                        if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                            eval = temp;
                                    }
                                    else
                                        game.undoMove();
                                }
                                else break;
                            for(int ex = x+1, ey = y+1; ex < 8 && ey < 8; ex++, ey++)
                                if(operateMove.apply(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]))) {
                                    if(branchability.getAsBoolean()) {  
                                        temp = branch(branches + 1, game);
                                        game.undoMove();
                                        if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                            eval = temp;
                                    }
                                    else
                                        game.undoMove();
                                }
                                else break;
                            for(int ex = x-1; ex >= 0; ex--)
                                if(operateMove.apply(new Move(x, ex, y, y, game.board[x][y], game.board[ex][y]))) {
                                    if(branchability.getAsBoolean()) {  
                                        temp = branch(branches + 1, game);
                                        game.undoMove();
                                        if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                            eval = temp;
                                    }
                                    else
                                        game.undoMove();
                                }
                                else break;
                            for(int ex = x+1; ex < 8; ex++)
                                if(operateMove.apply(new Move(x, ex, y, y, game.board[x][y], game.board[ex][y]))) {
                                    if(branchability.getAsBoolean()) {  
                                        temp = branch(branches + 1, game);
                                        game.undoMove();
                                        if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                            eval = temp;
                                    }
                                    else
                                        game.undoMove();
                                }
                                else break;
                            for(int ey = y-1; ey >= 0; ey--)
                                if(operateMove.apply(new Move(x, x, y, ey, game.board[x][y], game.board[x][ey]))) {
                                    if(branchability.getAsBoolean()) {  
                                        temp = branch(branches + 1, game);
                                        game.undoMove();
                                        if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                            eval = temp;
                                    }
                                    else
                                        game.undoMove();
                                }
                                else break;
                            for(int ey = y+1; ey < 8; ey++)
                                if(operateMove.apply(new Move(x, x, y, ey, game.board[x][y], game.board[x][ey]))) {
                                    if(branchability.getAsBoolean()) {  
                                        temp = branch(branches + 1, game);
                                        game.undoMove();
                                        if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                            eval = temp;
                                    }
                                    else
                                        game.undoMove();
                                }
                                else break;
                            break;
                        case 'K':
                            if(x > 0 && y > 0 && operateMove.apply(new Move(x, x-1, y, y-1, game.board[x][y], game.board[x-1][y-1]))) {
                                if(branchability.getAsBoolean()) {  
                                    temp = branch(branches + 1, game);
                                    game.undoMove();
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                                else
                                    game.undoMove();
                                }
                            if(x > 0 && y > 0 && operateMove.apply(new Move(x, x-1, y, y, game.board[x][y], game.board[x-1][y]))) {
                                if(branchability.getAsBoolean()) {  
                                    temp = branch(branches + 1, game);
                                    game.undoMove();
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                                else
                                    game.undoMove();
                                }
                            if(x > 0 && y < 7 && operateMove.apply(new Move(x, x-1, y, y+1, game.board[x][y], game.board[x-1][y+1]))) {
                                if(branchability.getAsBoolean()) {  
                                    temp = branch(branches + 1, game);
                                    game.undoMove();
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                                else
                                    game.undoMove();
                                }
                            if(y > 0 && operateMove.apply(new Move(x, x, y, y-1, game.board[x][y], game.board[x][y-1]))) {
                                if(branchability.getAsBoolean()) {  
                                    temp = branch(branches + 1, game);
                                    game.undoMove();
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                                else
                                    game.undoMove();
                                }
                            if(y < 7 && operateMove.apply(new Move(x, x, y, y+1, game.board[x][y], game.board[x][y+1]))) {
                                if(branchability.getAsBoolean()) {  
                                    temp = branch(branches + 1, game);
                                    game.undoMove();
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                                else
                                    game.undoMove();
                                }
                            if(x < 7 && y > 0 && operateMove.apply(new Move(x, x+1, y, y-1, game.board[x][y], game.board[x+1][y-1]))) {
                                if(branchability.getAsBoolean()) {  
                                    temp = branch(branches + 1, game);
                                    game.undoMove();
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                                else
                                    game.undoMove();
                                }
                            if(x < 7 && operateMove.apply(new Move(x, x+1, y, y, game.board[x][y], game.board[x+1][y]))) {
                                if(branchability.getAsBoolean()) {  
                                    temp = branch(branches + 1, game);
                                    game.undoMove();
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                                else
                                    game.undoMove();
                                }
                            if(x < 7 && y < 7 && operateMove.apply(new Move(x, x+1, y, y+1, game.board[x][y], game.board[x+1][y+1]))) {
                                if(branchability.getAsBoolean()) {  
                                    temp = branch(branches + 1, game);
                                    game.undoMove();
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                                else
                                    game.undoMove();
                                }
                            if(x == 4 && operateMove.apply(new Move(x, x+2, y, y, game.board[x][y], game.board[x+2][y]))) {
                                if(branchability.getAsBoolean()) {  
                                    temp = branch(branches + 1, game);
                                    game.undoMove();
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                                else
                                    game.undoMove();
                                }
                            if(x == 4 && operateMove.apply(new Move(x, x-2, y, y, game.board[x][y], game.board[x-2][y]))) {
                                if(branchability.getAsBoolean()) {  
                                    temp = branch(branches + 1, game);
                                    game.undoMove();
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                                else
                                    game.undoMove();
                                }
                            break;
                        default: System.out.print("Error."); //should never happen
                    }
                }
            }
        }
        return (eval != 1.675e-27 ? eval : score);
    }
}
