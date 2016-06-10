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
        name = 'K';
        color = Color.NONE;
    }
    
    //coordinate and color constructor
    King(Color color)
    {
        value = 3;
        name = 'K';
        this.color = color;
    }

    King(Color color, double value)
    {
        this.value = value;
        name = 'K';
        this.color = color;
    }
}
