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
        name = 'B';
        color = Color.NONE;
    }
    
    //coordinate and color constructor
    Bishop(Color color)
    {
        value = 3;
        name = 'B';
        this.color = color;
    }
}
