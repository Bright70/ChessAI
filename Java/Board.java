package chessai;

public class Board {
	
	static Piece[][] board = new Piece[8][8];
	
	Board(){
		createBoard();
	}
	
	public void displayBoard(Piece[][] board)
    {
        for(int y = 0; y < 8; y++)
        {
            System.out.print("-----------------\n|"); //17
            
            for (int x = 0; x < 8; x++)
            {
                System.out.print(board[x][y].name + " ");
            }
            System.out.print("\n");
        }
        System.out.print("-----------------");
    }
	
	public static void createBoard(){
		
		Color color; color = Color.BLACK;
		
		for(int x = 0; x < 8; x++){
			for(int y = 2; y < 6; y++){
				board[x][y] = new Empty();
			}
		}
		
		int y = 0;
		
		for(int x = 0; x < 2; x++){
			if(x == 1){color = Color.WHITE;}
			
			board[0][y] = new Rook(0,y, color);
			board[7][y] = new Rook(7,y, color); 
	
			board[1][y] = new Knight(1,y, color);		
			board[6][y] = new Knight(6,y, color);
			
			board[2][y] = new Bishop(2,y, color);
			board[5][y] = new Bishop(5,y, color);
			
			board[3][y] = new Queen(3,y, color);
			board[4][y] = new King(4,y, color);
			if(y == 0){y++;}
			if(y == 7){y--;}
			for(int a = 0; a < 8; a++){
				board[a][y] = new Pawn(a, y, color);
			}
			y = 7;
		}
	}
	
	public static boolean isLegal(int sx, int sy, int ex, int ey, Piece[][] board){
		
		if(sx > ex){ // will witch variables if the endpoint is smaller than the startpoint on the coordinate grid
			int tmp = sx;
			sx = ex;
			ex = tmp;
		}
		
		if(sy > ey){ // will witch variables if the endpoint is smaller than the startpoint on the coordinate grid
			int tmp = sy;
			sy = ey;
			ey = tmp;
		}
		
		if(board[sx][sy].name == 'P'){
			if(ey - sy > 1){
				if(ey - sy > 2){ // under no circumstances can the pawn move more than two ahead
					return false;
				}
				if((board[sx][sy].color != Color.BLACK || sy != 1) || (board[sx][sy].color != Color.WHITE || sy != 6)){ // if pawn is on starting pos, allows two space moves
					return false;
				}
				if(Math.abs(ex - sx) > 0){
					// if pawn is trying to move on x axis, allow only if a piece other than the king is present, the distance on the x-axis is 1 and the position it's moving into has a piece
					if(board[ex][ey].name == ' ' || board[ex][ey].name == 'K' || ex-sx > 1){ 
						return false;
					}
				}
			}
		}
		
		if(board[sx][sy].name == 'R'){
			if(ey - sy > 0 && ex - sx > 0){ // if is trying to move in both dimensions, disallow
				return false;
			}
		}
		
		if(board[sx][sy].name == 'N'){
			if((ex - sx != 1 || ey - sy != 2) || (ex - sx != 2 || ey - sy != 1)){ // knight can only legally move two on x/y axis, and then 1 on the axis that was not chosen first
				return false;
			}
		}
		
		if(board[sx][sy].name == 'B'){
			if(ex - sx != ey - sy){ // moving horizontally means that your x/y position will change in a same amount (disregarding signs)
				return false;
			}
		}
		
		if(board[sx][sy].name == 'Q'){
			
		}
		
		if(board[sx][sy].name == 'K'){
			
		}
		
		// piece specific checks end here
		
		if(ey - sy > 0){ // only traveling the y-axis
			for(int y = sy; y < ey; y++){ // checks to see that it does not run through existing pieces.
				if(board[sx][y].name != ' ' || sy+1 != ey){
					return false;
				}
			}
		}
		else if(Math.abs(ey - sy) > 0 && ex - sx > 0 && ey-sy == ex-sx){ // moving diagonally
			
		}
		else{ // only traveling the x-axis
			for(int x = sx; x < ex; x++){ // checks to see that it does not run through existing pieces.
				if(board[x][sy].name != ' ' || sx+1 != ex){
					return false;
				}
			}
		}
		
		
		
		// if has not returned false before here, it's legal
		return true;
	}
}
