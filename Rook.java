/*
    Rook.
 */

package chessai;

public class Rook extends Piece
{
    //default constructor
    Rook()
    {
        value = 5;
        x = -1;
        y = -1;
    }
    
    //coordinate constructor
    Rook(int x, int y)
    {
        value = 5;
        this.x = x;
        this.y = y;
    }
}
