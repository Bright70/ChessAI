package chessai;

public class Board {
	
	static Piece[][] board = new Piece[8][8];
	
	public static void main(String[] args){
		createBoard();
	}
	
	public static void createBoard(){
		Color color; color = Color.BLACK;
		
		
		for(int x = 0; x < 7; x++){
			for(int y = 2; y < 6; y++){
				board[x][y] = new Empty();
			}
		}
		
		int y = 0;
		
		for(int x = 0; x < 1; x++){
			if(x == 1){color = Color.WHITE;}
			
			board[0][y] = new Rook(0,y); board[0][y].color = color;
			board[7][y] = new Rook(7,y); board[7][y].color = color;
	
			board[1][y] = new Knight(1,y); board[1][y].color = color;		
			board[6][y] = new Knight(6,y); board[6][y].color = color;
			
			board[2][y] = new Bishop(2,y); board[2][y].color = color;
			board[5][y] = new Bishop(5,y); board[5][y].color = color;
			
			board[3][y] = new Queen(3,y); board[3][y].color = color;
			board[4][y] = new King(4,y); board[4][y].color = color;
			if(y == 0){y++;}
			if(y == 7){y--;}
			for(int a = 0; a < 7; a++){
				board[a][y] = new Pawn(a, y);
				board[a][y].color = color;
			}
			y = 7;
		}	
	}
}
