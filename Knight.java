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
        x = -1;
        y = -1;
    }
    
    //coordinate and color constructor
    Knight(int x, int y, Color color)
    {
        value = 3;
        this.x = x;
        this.y = y;
        this.color = color;
    }
}
