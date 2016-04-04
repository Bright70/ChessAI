/*
    Queen.
 */

package chessai;

public class Queen extends Piece
{
    //default constructor
    Queen()
    {
        value = 9;
        name = 'Q';
        color = Color.NONE;
    }
    
    //coordinate and color constructor
    Queen(Color color)
    {
        value = 9;
        name = 'Q';
        this.color = color;
    }
}
