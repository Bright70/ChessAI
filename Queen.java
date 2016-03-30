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
    
    //coordinate constructor
    Queen(int x, int y)
    {
        value = 9;
        this.x = x;
        this.y = y;
    }
}
