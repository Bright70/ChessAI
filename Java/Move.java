package chessai;

public class Move{
	
	int sx, ex, sy, ey;
	Piece piece;
	
	Move(int sx, int ex, int sy, int ey, Piece piece){
		this.sx = sx;
		this.ex = ex;
		this.sy = sy;
		this.ey = ey;
		this.piece = piece;
	}
}
