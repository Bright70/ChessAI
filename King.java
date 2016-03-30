/*
    King.
 */

package chessai;

public class King extends Piece
{
    //default constructor
    King()
    {
        value = 3;
        x = -1;
        y = -1;
    }
    
    //coordinate constructor
    King(int x, int y)
    {
        value = 3;
        this.x = x;
        this.y = y;
    }
}
