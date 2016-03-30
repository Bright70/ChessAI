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
        name = 'R';
        color = Color.NONE;
    }
    
    //coordinate and color constructor
    Rook(int x, int y, Color color)
    {
        value = 5;
        this.x = x;
        this.y = y;
        this.color = color;
    }
}
