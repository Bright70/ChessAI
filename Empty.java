/*
    Empty square.
 */

package chessai;

public class Empty extends Piece
{
    //default constructor
    Empty()
    {
        value = -1;
        x = -1;
        y = -1;
    }
    
    //coordinate constructor
    Empty(int x, int y)
    {
        value = -1;
        this.x = x;
        this.y = y;
        this.color = Color.NONE;
    }
}
