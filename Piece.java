/*
    Superclass for Pieces.
 */

package chessai;

public class Piece
{
    int value, x, y;
    
    //default constructor
    Piece()
    {
        value = -1;
        x = -1;
        y = -1;
    }
    
    //piece constructor
    Piece(Piece p)
    {
        value = p.value;
        x = p.x;
        y = p.y;
    }
    
    //coordinate constructor
    Piece(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
}
