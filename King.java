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
        name = 'K';
        color = Color.NONE;
    }
    
    //coordinate and color constructor
    King(int x, int y, Color color)
    {
        value = 3;
        this.x = x;
        this.y = y;
        this.color = color;
    }
}
