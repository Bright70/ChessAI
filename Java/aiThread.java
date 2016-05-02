/*
    Game tree branching using threads.
    Owns the branching and evaluation functions.
 */

package chessai;

//threading
public class aiThread extends Thread {
    //heuristic constants
    private static final double[] SCOREWEIGHT = { //evaluate
        0.8,    //piece value + mobility
        0.125,  //space bonus
        0.4     //king safety
    };
    private static final double LIFETIME = 300, DROPOFF = 25, THRESHOLD = 30; //branching

    //variables
    private int arrPos;
    private Board game;
    
    //initializer given move index and board
    aiThread(int arrPos, Board game, Move move) {
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
    }
    
    //threading
    @Override
    public void run() {
//        System.out.println("Thread:" + arrPos);
        ChessAI.scores[arrPos] = branch(1, game);
        ChessAI.threadDead[arrPos] = true;
//        System.out.println("Thread " + arrPos + " finished with score " + ChessAI.scores[arrPos]);
    }
    
    //evaluate position, return score
    double evaluate(Board game) {
        double score = 0; //positive is advantage for white

        //piece value and mobility bonus
        double[] pieceBonus = new double[2]; //0 = white, 1 = black
        int turnCount = game.turnCount, legalMoves;
        double bonus;
        Piece p;
        for(int x = 0; x < 8; x++) {
            for(int y = 0; y < 8; y++) {
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
                        case 'P': 
                            if(x > 1 && y > 1 && game.isLegal(new Move(x, x, y, y-(turnCount%2==0?1:-1), game.board[x][y], game.board[x][y-(turnCount%2==0?1:-1)]), true))
                                legalMoves++;
                            if(y != (turnCount%2==0?1:6) && game.isLegal(new Move(x, x, y, y-(turnCount%2==0?2:-2), game.board[x][y], game.board[x][y-2*(turnCount%2==0?1:-1)]), true))
                                legalMoves++;
                            if(x > 0 && game.isLegal(new Move(x, x-1, y, y-(turnCount%2==0?1:-1), game.board[x][y], game.board[x-1][y-(turnCount%2==0?1:-1)]), true))
                                legalMoves++;
                            if(x < 7 && game.isLegal(new Move(x, x+1, y, y-(turnCount%2==0?1:-1), game.board[x][y], game.board[x+1][y-(turnCount%2==0?1:-1)]), true))
                                legalMoves++;
                            break;
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
                        case 'R': bonus = Math.pow((double)legalMoves / 12.6 - 0.6, 3) + 1; break;
                        case 'Q': bonus = Math.pow((double)legalMoves / 22.3 - 0.56, 3) + 1; break;
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
    
    //branching game tree, currently static: pass number of branches
    double branch(int branches, Board game) {
        //vars
        double score = evaluate(game);
        int color = game.turnCount % 2 == 0 ? 1 : -1;

        //when user is able to input depth, will use number so that it always works without having to change the code
        this.setPriority((branches < 20 ? (MIN_PRIORITY + branches) / 2 : 10)); // setting thread priority relative to depth

        if(game.checkmated()){
            System.out.println("Checkmate @ depth " + branches);
            return (game.turnCount % 2 == 0 ? 50 : -50);
        }
        
        //lambda for next block
        java.util.function.Function<Move, Boolean> operateMove = (m) -> {
            if(game.isLegal(m, true)) {
                game.makeMove(m);
                return true;
            }
            else return false;
        };

        java.util.function.BooleanSupplier branchability = () -> 
                 (evaluate(game) - score) * (LIFETIME / ((double)branches-0.9) - DROPOFF) * (game.turnCount % 2 == 1 ? 1 : -1) > THRESHOLD;

        double temp, eval = 1.675e-27;
        //find all legal moves
        for(int x = 0; x < 8; x++) {
            for(int y = 0; y < 8; y++) {
                if(game.board[x][y].name != ' ' && game.board[x][y].color ==
                        (game.turnCount % 2 == 0 ? Color.WHITE : Color.BLACK)) {
                    //find all legal moves
                    switch(game.board[x][y].name) {
                        case 'P':
                            if(operateMove.apply(new Move(x, x, y, y-color, game.board[x][y], game.board[x][y-color]))) {
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
                            if(y != (color > 0 ? 1 : 6) && operateMove.apply(new Move(x, x, y, y-2*color, game.board[x][y], game.board[x][y-2*color]))) {
                                if(branchability.getAsBoolean()) {  
                                    temp = branch(branches + 1, game);
                                    game.undoMove();
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                                else
                                    game.undoMove();
                            }
                            if(x > 0 && operateMove.apply(new Move(x, x-1, y, y-color, game.board[x][y], game.board[x-1][y-color]))) {
                                if(branchability.getAsBoolean()) {   
                                    temp = branch(branches + 1, game);
                                    game.undoMove();
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                                else
                                    game.undoMove();
                            }
                            if(x < 7 && operateMove.apply(new Move(x, x+1, y, y-color, game.board[x][y], game.board[x+1][y-color]))) {
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
