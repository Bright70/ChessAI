/*
    Queen.
 */

package chessai;

public class Queen extends Piece
{
    // default constructor
    Queen()
    {
        value = 8.2;
        name = 'Q';
        this.color = Color.NONE;
    }

    //coordinate and color constructor
    Queen(Color color)
    {
        value = 8.2;
        name = 'Q';
        this.color = color;
    }

    Queen(Color color, double value)
    {
        this.value = value;
        name = 'Q';
        this.color = color;
    }
}
