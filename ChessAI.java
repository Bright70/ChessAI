/*
    Chess AI for Andres and Adrian's Computer Science 20 Final Project.
*/

package chessai;

public class ChessAI
{
    
    public static Piece[][] emptyBoard()
    {
        Piece[][] board = new Piece[8][8];
        
        for (int x = 0; x < 8; x++)
        {
            for (int y = 0; y < 8; y++)
            {
                board[x][y] = new Empty();
            }
        }
        
        return board;
    }

}
