/* 
    Pawn.
 */

package chessai;

public class Pawn extends Piece
{
    //default constructor
    Pawn()
    {
        value = 1;
        name = 'P';
        color = Color.NONE;
    }
    
    //coordinate and color constructor
    Pawn(Color color)
    {
        value = 1;
        name = 'P';
        this.color = color;
    }

    Pawn(Color color, double value)
    {
        this.value = value;
        name = 'P';
        this.color = color;
    }
}
