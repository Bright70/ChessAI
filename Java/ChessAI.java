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

    //choose a move by evaulating multiple positions
    public Move aiMakeMove(Board game) {
        //vars
        double eval; int possibleMoves = 0;
        Move[] moves = new Move[64];
        double[] scores = new double[64];
        
        //get current score
        eval = evaluate(game);
        
        //get and evaluate all possible moves
        for(int x = 0; x < 8; x++) {
            for(int y = 0; y < 8; y++) {
                if(game.board[x][y].name != ' ')
                {
                    for(int ex = 0; ex < 8; ex++) {
                        for(int ey = 0; ey < 8; ey++) {
                            if(game.isLegal(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]), true)) {
                                moves[possibleMoves] = new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]);
                                game.makeMove(moves[possibleMoves]);
                                scores[possibleMoves] = evaluate(game);
                                game.undoMove();
                                possibleMoves++;
                            }
                        }
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
