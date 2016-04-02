/*
    Board class for ChessAI.
    Contains all elements of a chess game.
*/

package chessai;

public final class Board
{
    //vars
    Piece[][] board = new Piece[8][8];
    boolean[][] hasMoved = new boolean[3][2]; // rook, king, rook (0 is black, 1 is white on 2nd dimension)
    private Move[] moves = new Move[128]; //will be used for undoing moves, needs to account for taking pieces
    int turnCount = 0;

    //default constructor creates the initial position
    Board() {
        createBoard();
    }
    
    //show a console version of the board
    public void displayBoard(Piece[][] board) {
        System.out.print("\n    a   b   c   d   e   f   g   h\n");
        
        for(int y = 0; y < 8; y++)
        {
            System.out.print("  ---------------------------------\n" + (8 - y) + " |"); //17
            
            for (int x = 0; x < 8; x++)
            {
                switch (board[x][y].color)
                {
                    case WHITE: System.out.print("w"); break;
                    case BLACK: System.out.print("b"); break;
                    case NONE: System.out.print(" "); break;
                }
                System.out.print(board[x][y].name + " |");
            }
            System.out.print("\n");
        }
        System.out.print("  ---------------------------------\n");
    }
    
    //create the initial position
    public void createBoard() {
		
        Color color; color = Color.BLACK;
		
        for(int x = 0; x < 8; x++){
            for(int y = 2; y < 6; y++){
                board[x][y] = new Empty();
            }
        }

        int y = 0;

        for(int x = 0; x < 2; x++) {
            if(x == 1) color = Color.WHITE;

            board[0][y] = new Rook(0, y, color);
            board[7][y] = new Rook(7, y, color);

            board[1][y] = new Knight(1, y, color);		
            board[6][y] = new Knight(6, y, color);

            board[2][y] = new Bishop(2, y, color);
            board[5][y] = new Bishop(5, y, color);

            board[3][y] = new Queen(3, y, color);
            board[4][y] = new King(4, y, color);
            if(y == 0) y++;
            if(y == 7) y--;
            for(int a = 0; a < 8; a++){
                board[a][y] = new Pawn(a, y, color);
            }
            
            y = 7;
        }
    }

    //check for legality
    public boolean isLegal(Move m) {

            //initial universal checks
        
        //type check
        if(m.piece.name == ' ') return false;
        if(board[m.sx][m.sy] != m.piece) return false;
        
        //bounds check
        if(m.sx > 7 || m.sx < 0 || m.sy > 7 || m.sy < 0 || m.ex > 7 || m.ex < 0 || m.ey > 7 || m.ey < 0)
            return false;
        
        //color check
        if(board[m.sx][m.sy].color != (turnCount % 2 == 0 ? Color.WHITE : Color.BLACK)) 
            return false;
        
        //destination is own color piece, also prevents same square movement
        if(board[m.ex][m.ey].color == (turnCount % 2 == 0 ? Color.WHITE : Color.BLACK))
            return false;
        
        //piece specific checks
        switch (board[m.sx][m.sy].name) {
            //pawn
            case 'P':
                //find change in y, inverse if black's turn. should always be positive
                int dy = (m.sy - m.ey) * (turnCount % 2 == 0 ? 1 : -1);
                
                //moving up
                switch (dy) { 
                    case 1:
                        ///needs en passant
                        //taking pieces
                        if(Math.abs(m.sx - m.ex) == 1) {
                            if(board[m.ex][m.ey].name == ' ')
                                return false;
                        }
                        //single move up
                        else if (m.sx == m.ex && board[m.ex][m.ey].name == ' ')
                            return true;
                        else return false;
                        break;
                    case 2: //double move forwards
                        //check for in initial position
                        if(!((turnCount % 2 == 0 && m.sy == 6) || (turnCount % 2 == 1 && m.sy == 1)) || board[m.ex][m.ey].name != ' ')
                            return false;
                        break;
                    default: return false; //any greater magnitude of dy is illegal
                }
                break;
            //rook
            case 'R':
                //disallow trying to move in both dimensions
                if(m.ey - m.sy != 0 && m.ex - m.sx != 0)
                    return false;
                break;
            //knight
            case 'N':
                //knight moves 2 spaces in one direction, and 1 square in the other
                if(!((Math.abs(m.ex - m.sx) == 1 && Math.abs(m.ey - m.sy) == 2) || 
                        (Math.abs(m.ex - m.sx) == 2 && Math.abs(m.ey - m.sy) == 1)))
                    return false;
                break;
            //bishop
            case 'B':
                //change in x and y are the same
                if(Math.abs(m.ex - m.sx) != Math.abs(m.ey - m.sy))
                    return false;
                break;
            //queen
            case 'Q':
                //checks of both bishop and rook
                if((m.ey - m.sy != 0 && m.ex - m.sx != 0) && 
                        (Math.abs(m.ex - m.sx) != Math.abs(m.ey - m.sy)))
                    return false;
                break;
            //king
            case 'K':
                //magnitude of any movement should be no greater than one unless castling
            	if(Math.abs(m.ex - m.sx) == 2 && Math.abs(m.ey - m.sy) == 0){ // castling right
            		if(m.piece.color == Color.WHITE && !hasMoved[1][1] && !hasMoved[2][1]){
            			break; // don't check elsewhere, this is legal. If it's trying to move through pieces, it will get caught later
            		}
            		if(m.piece.color == Color.BLACK && !hasMoved[1][0] && !hasMoved[2][0]){
            			break; // don't check elsewhere, this is legal. If it's trying to move through pieces, it will get caught later
            		}
            	}
            	if(Math.abs(m.ex - m.sx) == 3 && Math.abs(m.ey - m.sy) == 0){ // castling left
            		if(m.piece.color == Color.WHITE && !hasMoved[1][1] && !hasMoved[0][1]){
            			break; // don't check elsewhere, this is legal. If it's trying to move through pieces, it will get caught later
            		}
            		if(m.piece.color == Color.BLACK && !hasMoved[1][0] && !hasMoved[0][0]){
            			break; // don't check elsewhere, this is legal. If it's trying to move through pieces, it will get caught later
            		}
            	}
            	// if not castling use regular check
                if(Math.abs(m.ex - m.sx) > 1 || Math.abs(m.ey - m.sy) > 1)
                    return false;
                break;
            default: return false;
        }
        
        //moving through pieces
        if(board[m.sx][m.sy].name != 'N') { //not for knights
           for(int x = m.sx, y = m.sy; x != m.ex || y != m.ey;) {
                if(x != m.ex) x += (m.sx < m.ex ? 1 : -1);
                if(y != m.ey) y += (m.sy < m.ey ? 1 : -1);

                if(board[x][y].name != ' ' && !(x == m.ex && y == m.ey))
                    return false;
            } 
        }
        
        ///king in check

        // if has not returned false before here, must be legal
        return true;
    }
    
    //check for checkmate
    public boolean checkmated() {
        //find the king
    	for(int x = 0; x < 8; x++) {
            for(int y = 0; y < 8; y++) {
                if(board[x][y].name == 'K' && board[x][y].color == (turnCount % 2 == 0 ? Color.WHITE : Color.BLACK)){
                    // check 3x3 square surrounding king to see if any legal moves remain
                    for(int ex = x-1; ex < x+2; ex++){
                        for(int ey = y-1; ey < y+2; ey++){
                            if(ex >= 0 && ey >= 0) // prevents arrayOutOfBounds
                                if(isLegal(new Move(x, y, ex, ey, board[x][y])))
                                    return false; // if any legal move is found, not checkmate
                        }
                    }
                    break;
                }
            }
    	}
    	return true;
    }
    
    //make a move, hopefully after checking legality
    public void makeMove(Move m) {
        //currently cannot en passant
    	
    	if(m.piece.name == 'K'){ // check if castling
    		board[m.ex][m.sy] = board[m.sx][m.sy];
    		if(Math.abs(m.ex - m.sx) == 2){ // if moving left
    			board[m.ex-1][m.sy] = board[7][m.sy]; // place rook to left of king
    		}
    		if(Math.abs(m.ex - m.sx) == 3){ // if moving right
    			board[m.ex+1][m.sy] = board[0][m.sy]; // place rook to right of king
    		}
    	}
    	else{ // if not castling use standard move creation, might slow things down a bit...
    		board[m.ex][m.ey] = board[m.sx][m.sy];
    		board[m.sx][m.sy] = new Empty();
    	}
    	
        moves[turnCount] = m; // may not store castling properly
        turnCount++;
    }
    
    //undo a move by accessing moves stack, decrease turn count
    public void undoMove() {
        //if a move has been made
        if (turnCount > 0) {
            //currently does not account for taking pieces
            board[moves[turnCount-1].sx][moves[turnCount-1].sy] = 
                    board[moves[turnCount-1].ex][moves[turnCount-1].ey];
            board[moves[turnCount-1].ex][moves[turnCount-1].ey] = new Empty();
            turnCount--;
        }
    }

}
