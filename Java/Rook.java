/*
    Rook.
 */

package chessai;

public class Rook extends Piece
{
    //default constructor
    Rook()
    {
        value = 4.4;
        name = 'R';
        color = Color.NONE;
    }
    
    //coordinate and color constructor
    Rook(Color color)
    {
        value = 4.4;
        name = 'R';
        this.color = color;
    }

    Rook(Color color, double value)
    {
        this.value = value;
        name = 'R';
        this.color = color;
    }
}
