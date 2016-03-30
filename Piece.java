/*
    Superclass for Pieces.
 */

package chessai;

public class Piece
{
    int value, x, y;
    Color color;
    
    //default constructor
    Piece()
    {
        value = -1;
        x = -1;
        y = -1;
        color = Color.NONE;
    }
    
    //piece constructor
    Piece(Piece p)
    {
        value = p.value;
        x = p.x;
        y = p.y;
        color = p.color;
    }
    
    //coordinate constructor
    Piece(int x, int y)
    {
        value = -1;
        this.x = x;
        this.y = y;
        color = Color.NONE;
    }
}
