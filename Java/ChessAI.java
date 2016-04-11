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
    public double evaluate(Board board) {
        double score = 0; //positive means advantage for white
        
        //compare piece values, considering mobility
        double[] pieceValues = new double[2]; //0 = white, 1 = black
        int legalMoves = 0, turnCount = board.turnCount;
        double bonus;
        Piece p;
        for(int x = 0; x < 8; x++) {
            for(int y = 0; y < 8; y++) {
                legalMoves = 0;
                //reference for better readability
                p = board.board[x][y];
                
                if(p.name != ' ') {
                	// should eventually optimize for individual pieces
                    for(int ex = 0; ex < 8; ex++) {
                        for(int ey = 0; ey < 8; ey++) {
                            if(board.turnCount % 2 != (p.color == Color.WHITE ? 0 : 1))
                                board.turnCount++; //change turns to check legality
                            if(board.isLegal(new Move(x, y, ex, ey, p, new Empty()), true))
                                legalMoves++;
                            board.turnCount = turnCount;
                        }
                    }
                    //calculate mobility bonuses
                    switch(p.name) {
                        case 'P': bonus = Math.pow(legalMoves / 3 - 1, 3) + 1.5; break;
                        case 'N': bonus = Math.pow(legalMoves / 4 - 1, 3) + 1.2; break;
                        case 'B': bonus = Math.pow(legalMoves / 8 - 0.8, 3) + 1; break;
                        case 'R': bonus = Math.pow(legalMoves / 12 - 1, 3) + 1.5; break;
                        case 'Q': bonus = Math.pow(legalMoves / 12 - 1, 3) + 1.1; break;
                        case 'K': bonus = Math.pow(legalMoves / 5 - 0.8, 3) + 1.1; break;
                        default: bonus = 0; break;
                    }
                    
                    pieceValues[(p.color == Color.WHITE ? 0 : 1)] += p.value * bonus;
                }
            }
        }
        score += pieceValues[0] - pieceValues[1];
        
        //check other stuff
        
        return score;
    }

    //choose a move by evaulating multiple positions
    public Move aiMakeMove(Board board) {
        evaluate(board);
        
        return new Move();
    }
    
}
