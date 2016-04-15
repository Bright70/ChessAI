/*
    Actual AI for ChessAI.
    Chess is a zero-sum game, so only one evaluation is required.
    Factors to consider:
        Piece values
        Piece mobility
        Space advantage
        King safety
        Whose turn it is
*/

package chessai;

public class ChessAI {
    //variables

    //initialize vars
    public ChessAI() {
        
    }
    
    //evaluate position, return score
    public double evaluate(Board game) {
        double score = 0; //positive means advantage for white
        
        //compare piece values, considering mobility
        double[] pieceValues = new double[2]; //0 = white, 1 = black
        int turnCount = game.turnCount;
        double bonus, legalMoves;
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
                    
                	//should eventually optimize for individual pieces
                    //find all legal moves
                    for(int ex = 0; ex < 8; ex++) {
                        for(int ey = 0; ey < 8; ey++) {
                            
                            if(game.isLegal(new Move(x, ex, y, ey, p, game.board[ex][ey]), false))
                                legalMoves++;
                        }
                    }
                    
                    //reset turnCount
                    game.turnCount = turnCount;
                    
                    //calculate mobility bonuses
                    switch(p.name) {
                        case 'P': bonus = Math.pow(legalMoves / 4.6 - 0.6, 3) + 1; break;
                        case 'N': bonus = Math.pow(legalMoves / 6.4 - 0.55, 3) + 1; break;
                        case 'B': bonus = Math.pow(legalMoves / 11.3 - 0.58, 3) + 1; break;
                        case 'R': bonus = Math.pow(legalMoves / 12.6 - 0.6, 3) + 1; break;
                        case 'Q': bonus = Math.pow(legalMoves / 22.3 - 0.56, 3) + 1; break;
                        case 'K': bonus = Math.pow(legalMoves / 8.2 - 0.4, 3) + 1; break;
                        default: bonus = 0; break;
                    }
                    pieceValues[(p.color == Color.WHITE ? 0 : 1)] += p.value * bonus;
                }
            }
        }
        //add to weighted score
        score += (pieceValues[0] - pieceValues[1]) * 0.2;
        
        //check other stuff
        
        return score;
    }
    
    //branching game tree, currently static: pass number of branches
    public double branch(int branches, Board game) {
        double score = evaluate(game);
        
        //lambda for next block, similar to aiMakeMove
//        java.util.function.BiFunction<Move, Integer, Boolean> operateMove = (m, i) -> {
//            if(game.isLegal(m, true)) {
//                game.makeMove(m);
//                double temp = branch(i - 1, game);
//                game.undoMove();
//                return true;
//            }
//            else return false;
//        };
        
        //if not last branch
        if(branches > 1) {
            Move m;
            double temp, eval = 1.675e-27;
            //find all legal moves
            for(int x = 0; x < 8; x++) {
                for(int y = 0; y < 8; y++) {
                    if(game.board[x][y].name != ' ' && game.board[x][y].color == 
                            (game.turnCount % 2 == 0 ? Color.WHITE : Color.BLACK)) {
                        for(int ex = 0; ex < 8; ex++) {
                            for(int ey = 0; ey < 8; ey++) {
                                if(game.isLegal(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]), true)) {
                                    m = new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]);
                                    game.makeMove(m);
                                    //branch further
                                    temp = branch(branches - 1, game);
                                    game.undoMove();
                                    //choose best move dependent on score
                                    if(temp > (game.turnCount % 2 == 0 ? eval : -eval) || eval == 1.675e-27)
                                        eval = temp;
                                }
                            }
                        }
                    }
                }
            }
            
            score = eval;
        }
        
        return score;
    }

    //choose a move by evaulating multiple positions
    public Move aiMakeMove(Board game) {
        
        //vars
        double eval = evaluate(game); 
        int possibleMoves = 0, color = game.turnCount % 2 == 0 ? 1 : -1;
        Move[] moves = new Move[64];
        double[] scores = new double[64];
        long start = System.currentTimeMillis();
        
        //lambda for next block
        java.util.function.BiFunction<Move, Integer, Boolean> operateMove = (m, i) -> {
            if(game.isLegal(m, true)) {
                moves[i] = m;
                game.makeMove(m);
                scores[i] = branch(3, game);
                game.undoMove();
                return true;
            }
            else return false;
        };
        
        //get and evaluate all possible moves
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
        
        Move[] nMoves = new Move[possibleMoves];
        System.arraycopy(moves, 0, nMoves, 0, possibleMoves);
        double[] nScores = new double[possibleMoves];
        System.arraycopy(scores, 0, nScores, 0, possibleMoves);
        
        //quicksort moves based on score
        quickSort(nScores, nMoves, 0, possibleMoves - 1);
        
        System.out.print("Processing time: " + (System.currentTimeMillis() - start));
        
        return nMoves[(game.turnCount % 2 == 0 ? possibleMoves : 0)];
    }
    
    //quicksort moves for aiMakeMove. last move should be the best for White
    public void quickSort(double[] scores, Move[] moves, int left, int right)
    {
        double temp; Move mTemp;
        double pivot = scores[(int)((left + right) / 2)];
        int i = left, j = right;
        
        while (i <= j)
        {
            while (scores[i] < pivot)
                i++;
            while (scores[j] > pivot)
                j--;
            if (i <= j)
            {
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
