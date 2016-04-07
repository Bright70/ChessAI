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
        name = ' ';
        color = Color.NONE;
    }
    
    //coordinate constructor
    Empty(int x, int y)
    {
        value = -1;
        this.x = x;
        this.y = y;
        name = ' ';
        this.color = Color.NONE;
    }
}
