/*
    Bishop.
 */

package chessai;

public class Bishop extends Piece
{
    //default constructor
    Bishop()
    {
        value = 3;
        x = -1;
        y = -1;
    }
    
    //coordinate constructor
    Bishop(int x, int y)
    {
        value = 3;
        this.x = x;
        this.y = y;
    }
}
