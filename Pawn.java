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
        x = -1;
        y = -1;
        name = 'P';
        color = Color.NONE;
    }
    
    //coordinate and color constructor
    Pawn(int x, int y, Color color)
    {
        value = 1;
        this.x = x;
        this.y = y;
        name = 'P';
        this.color = color;
    }
}
