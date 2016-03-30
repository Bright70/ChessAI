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
        x = -1;
        y = -1;
    }
    
    //coordinate and color constructor
    Queen(int x, int y, Color color)
    {
        value = 9;
        this.x = x;
        this.y = y;
        this.color = color;
    }
}
