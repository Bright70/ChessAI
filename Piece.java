/*
    Superclass for Pieces.
 */

package chessai;

public class Piece {
    double value;
    char name;
    Color color;
    
    //default constructor
    Piece() {
        value = -1;
        name = ' ';
        color = Color.NONE;
    }
    
    //piece constructor
    Piece(Piece p) {
        value = p.value;
        name = p.name;
        color = p.color;
    }
}
