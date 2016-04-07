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
        name = 'R';
        color = Color.NONE;
    }
    
    //coordinate and color constructor
    Rook(Color color)
    {
        value = 5;
        name = 'R';
        this.color = color;
    }
}
