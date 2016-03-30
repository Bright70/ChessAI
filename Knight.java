/*
    Knight;
 */

package chessai;

public class Knight extends Piece
{
    //default constructor
    Knight()
    {
        value = 3;
        x = -1;
        y = -1;
    }
    
    //coordinate constructor
    Knight(int x, int y)
    {
        value = 3;
        this.x = x;
        this.y = y;
    }
}
