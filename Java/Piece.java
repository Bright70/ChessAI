/*
    Superclass for Pieces.
 */

package chessai;

public class Piece {
    int x, y;
    double value;
    char name;
    Color color;
    
    //default constructor
    Piece() {
        value = -1;
        x = -1;
        y = -1;
        name = ' ';
        color = Color.NONE;
    }
    
    //piece constructor
    Piece(Piece p) {
        value = p.value;
        x = p.x;
        y = p.y;
        name = p.name;
        color = p.color;
    }
    
    //coordinate constructor
    Piece(int x, int y) {
        value = -1;
        this.x = x;
        this.y = y;
        name = ' ';
        color = Color.NONE;
    }
}
