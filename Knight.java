/*
    Knight;
 */

package chessai;

public class Knight extends Piece
{
    //default constructor
    Knight()
    {
        value = 3;
        name = 'N';
        color = Color.NONE;
    }
    
    //coordinate and color constructor
    Knight(Color color)
    {
        value = 3;
        name = 'N';
        this.color = color;
    }
}
