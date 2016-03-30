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
        name = 'B';
        color = Color.NONE;
    }
    
    //coordinate and color constructor
    Bishop(int x, int y, Color color)
    {
        value = 3;
        this.x = x;
        this.y = y;
        this.color = color;
    }
}
