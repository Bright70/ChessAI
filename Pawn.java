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
    }
    
    //coordinate constructor
    Pawn(int x, int y)
    {
        value = 1;
        this.x = x;
        this.y = y;
    }
}
