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

import java.util.*;

public class ChessAI {
    //variables
    private final Map<Character, int[]> mobilityBonus;

    //initialize vars
    public ChessAI() {
        //create mobility bonuses for each piece
        mobilityBonus = new HashMap<>();
        mobilityBonus.put('P', new int[] {0,1,2,3,4}); //max 5
        mobilityBonus.put('N', new int[] {0,1,2,3,4,5,6,7,8}); //max 9
        mobilityBonus.put('B', new int[] 
            {0,1,2,3,4,5,6,7,8,9,10,11,12,13}); //max 14
        mobilityBonus.put('R', new int[] 
            {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14}); //max 15
        mobilityBonus.put('Q', new int[] 
            {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,
            16,17,18,19,20,21,22,23,24,25,26,27}); //max 28
        mobilityBonus.put('K', new int[] 
            {0,1,2,3,4,5,6,7,8}); //max 9
    }
    
    //evaluate position, return score
    public double evaluate(Board board) {
        double score = 0; //positive means advantage for white
        
        //compare piece values, considering mobility
        double[] pieceValues = new double[2]; //0 = white, 1 = black
        int legalMoves = 0, turnCount = board.turnCount;
        int[] b;
        Piece p;
        for(int x = 0; x < 8; x++) {
            for(int y = 0; y < 8; y++) {
                legalMoves = 0;
                //reference for better readability
                p = board.board[x][y];
                b = mobilityBonus.get(p.name);
                
                if(p.name != ' ') {
                    for(int ex = 0; ex < 8; ex++) {
                        for(int ey = 0; ey < 8; ey++) {
                            if(board.turnCount % 2 != (p.color == Color.WHITE ? 0 : 1))
                                board.turnCount++; //change turns to check legality
                            if(board.isLegal(new Move(x, y, ex, ey, p)))
                                legalMoves++;
                            board.turnCount = turnCount;
                        }
                    }
                    pieceValues[(p.color == Color.WHITE ? 0 : 1)] += p.value * b[legalMoves] / b[b.length / 2];
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
