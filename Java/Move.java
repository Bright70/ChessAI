/*
    Move class for ChessAI.
*/

package chessai;

public class Move{

    int sx, ex, sy, ey;
    Piece piece;
    
    //empty constructor
    Move(){
        sx = -1;
        ex = -1;
        sy = -1;
        ey = -1;
        piece = new Empty();
    }

    //generate a Move given its information
    Move(int sx, int ex, int sy, int ey, Piece piece){
        this.sx = sx;
        this.ex = ex;
        this.sy = sy;
        this.ey = ey;
        this.piece = piece;
    }
}
