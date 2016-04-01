/*
    Chess AI for Andres and Adrian's Computer Science 20 Final Project.
*/

package chessai;

public class ChessAI
{
    Piece[][] board;
    
    //returns an empty board
    public Piece[][] emptyBoard()
    {
        Piece[][] newBoard = new Piece[8][8];
        
        for (int x = 0; x < 8; x++)
        {
            for (int y = 0; y < 8; y++)
            {
                newBoard[x][y] = new Empty();
            }
        }
        
        return board;
    }
    
    //takes a board and converts it to the board
    public void importBoard()
    {
        
    }
}
