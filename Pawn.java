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
        color = Color.NONE;
    }
    
    //coordinate constructor
    Pawn(int x, int y, Color color)
    {
        value = 1;
        this.x = x;
        this.y = y;
    }
}
