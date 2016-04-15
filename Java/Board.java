/*
    Board class for ChessAI.
    Contains all elements of a chess game.
*/

package chessai;

public final class Board {
    //vars
    Piece[][] board = new Piece[8][8];
    //rook, king, rook (0 is white, 1 is black on 2nd dimension)    
    boolean[][] hasMoved = new boolean[3][2]; 
    //used for undoing moves and importing/exporting
    public Move[] moves = new Move[128];
    int turnCount = 0;

    //default constructor
    Board() {
        createBoard();
        for(int x = 0; x < 3; x++)
            for(int y = 0; y < 2; y++)
                hasMoved[x][y] = false;
        for(int i = 0; i < 128; i++) moves[i] = new Move();
    }
    
    //show a console version of the board
    public void displayBoard(Piece[][] board) {
        
//        for(int x = 0; x < 2; x++) //debug
//            for (int y = 0; y < 3; y++)
//                System.out.println((hasMoved[y][x] ? "T" : "F"));
        
        System.out.print("\n    a   b   c   d   e   f   g   h\n");
        
        for(int y = 0; y < 8; y++) {
            System.out.print("  ---------------------------------\n" + (8 - y) + " |"); //17
            
            for (int x = 0; x < 8; x++) {
                switch (board[x][y].color) {
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

            board[0][y] = new Rook(color);
            board[7][y] = new Rook(color);

            board[1][y] = new Knight(color);		
            board[6][y] = new Knight(color);

            board[2][y] = new Bishop(color);
            board[5][y] = new Bishop(color);

            board[3][y] = new Queen(color);
            board[4][y] = new King(color);
            if(y == 0) y++;
            if(y == 7) y--;
            for(int a = 0; a < 8; a++){
                board[a][y] = new Pawn(color);
            }
            
            y = 7;
        }
    }

    //check for legality
    public boolean isLegal(Move m, boolean checkingCheck) {

            //initial universal checks
    	
//        System.out.println("Legality check starting."); //debug
        
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
                        //taking pieces
                        if(Math.abs(m.sx - m.ex) == 1) {
                            if(turnCount > 0) {
                                Move lm = moves[turnCount-1]; //reference for readability
                                if(board[m.ex][m.ey].color == 
                                        (turnCount % 2 == 0 ? Color.BLACK : Color.WHITE))
                                    break;
                                //en passant
                                else if(lm.piece.name == 'P' && Math.abs(lm.ey - lm.sy) == 2 
                                        && (lm.piece.color == (turnCount % 2 == 0 ? Color.BLACK : Color.WHITE))
                                        && lm.ex == m.ex && lm.ey == m.sy)
                                    break;
                                else return false;
                            }
                            else return false;
                        }
                        //single move up
                        else if (m.sx == m.ex && board[m.ex][m.ey].name == ' ')
                            break;
                        else return false;
                    case 2: //double move forwards
                        //check for in initial position
                        if(m.sy == (turnCount % 2 == 0 ? 6 : 1) && m.sx == m.ex
                                && board[m.ex][m.ey].name == ' ')
                            break;
                        else return false;
                    default: return false; //any other magnitude of dy is illegal
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
                //castling. if legal, only check moving through other pieces later
            	if(m.ex - m.sx == 2 && m.sy == m.ey) { //castling kingside
                    if(m.piece.color == Color.WHITE && !hasMoved[2][0] && 
                            !hasMoved[1][0] && board[m.ex][m.ey].name == ' ')
                        break;
                    else if(m.piece.color == Color.BLACK && !hasMoved[2][1] && 
                            !hasMoved[1][1] && board[m.ex][m.ey].name == ' ')
                        break;
                    else return false;
            	}
                else if(m.sx - m.ex == 2 && m.sy == m.ey) { //castling queenside, extra check
                    if(m.piece.color == Color.WHITE && !hasMoved[1][0] && !hasMoved[0][0]
                            && board[1][m.sy].name == ' ' && board[m.ex][m.ey].name == ' ')
                        break;
                    else if(m.piece.color == Color.BLACK && !hasMoved[1][1] && !hasMoved[0][1]
                            && board[1][m.sy].name == ' ' && board[m.ex][m.ey].name == ' ')
                        break;
                    else return false;
            	}
            	//if not castling, directional movement <= 1
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
        
        //player's king in check, bool to prevent infinite recursion
        if(checkingCheck) {
            makeMove(m);
            if(isInCheck(turnCount % 2 == 0 ? Color.BLACK : Color.WHITE)) {
                undoMove();
                return false;
            }
            else undoMove();
        }

        // if has not returned false before here, must be legal
        return true;
    }
    
    //check for checkmate
    public boolean checkmated() {
        //find the king
    	for(int x = 0; x < 8; x++) {
            for(int y = 0; y < 8; y++) {
                if(board[x][y].name == 'K' && 
                        isInCheck(turnCount % 2 == 0 ? Color.WHITE : Color.BLACK) &&
                        board[x][y].color == (turnCount % 2 == 0 ? Color.WHITE : Color.BLACK)){
                    // check 3x3 square surrounding king to see if any legal moves remain
                    for(int ex = x - 1; ex < x + 2; ex++) {
                        for(int ey = y - 1; ey < y + 2; ey++) {
                            if(ex >= 0 && ex < 8 && ey >= 0 && ey < 8) { // prevents arrayOutOfBounds
                                if(isLegal(new Move(x, ex, y, ey, board[x][y], board[ex][ey]), true)) {
                                    return false; // if any legal move is found, not checkmate
                                }
                            }
                        }
                    }
                    x = 8;
                    break;
                }
            }
    	}
    	return true;
    }
    
    //check for king in check
    public boolean isInCheck(Color c) {
        //find the king
    	for(int x = 0; x < 8; x++) {
            for(int y = 0; y < 8; y++) {
                if(board[x][y].name == 'K' && board[x][y].color == c){
                        //check all possible angles of attack
                    //left
                    for(int i = x - 1; i >= 0; i--)
                        if(board[i][y].name != ' ')
                            if(isLegal(new Move(i, x, y, y, board[i][y], board[x][y]), false))
                                return true;
                            else break;
                    //right
                    for (int i = x + 1; i < 8; i++)
                        if(board[i][y].name != ' ')
                            if(isLegal(new Move(i, x, y, y, board[i][y], board[x][y]), false))
                                return true;
                            else break;
                    //up
                    for(int i = y - 1; i >= 0; i--)
                        if(board[x][i].name != ' ')
                            if(isLegal(new Move(x, x, i, y, board[x][i], board[x][y]), false))
                                return true;
                            else break;
                    //down
                    for(int i = y + 1; i < 8; i++)
                        if(board[x][i].name != ' ')
                            if(isLegal(new Move(x, x, i, y, board[x][i], board[x][y]), false))
                                return true;
                            else break;
                    //northwest
                    for(int i = x - 1, j = y - 1; i >= 0 && j >= 0; i--, j--)
                        if(board[i][j].name != ' ')
                            if(isLegal(new Move(i, x, j, y, board[i][j], board[x][y]), false))
                                return true;
                            else break;
                    //northeast
                    for(int i = x + 1, j = y - 1; i < 8 && j >= 0; i++, j--)
                        if(board[i][j].name != ' ')
                            if(isLegal(new Move(i, x, j, y, board[i][j], board[x][y]), false))
                                return true;
                            else break;
                    //southwest
                    for(int i = x - 1, j = y + 1; i >= 0 && j < 8; i--, j++)
                        if(board[i][j].name != ' ')
                            if(isLegal(new Move(i, x, j, y, board[i][j], board[x][y]), false))
                                return true;
                            else break;
                    //southeast
                    for(int i = x + 1, j = y + 1; i < 8 && j < 8; i++, j++)
                        if(board[i][j].name != ' ')
                            if(isLegal(new Move(i, x, j, y, board[i][j], board[x][y]), false))
                                return true;
                            else break;

                    ///knights
                    if(x-2 >= 0 && y-1 >= 0 && board[x-2][y-1].name == 'N' && 
                            isLegal(new Move(x-2, x, y-1, y, board[x-2][y-1], board[x][y]), false))
                        return true;
                    else if(x+2 < 8 && y-1 >= 0 && board[x+2][y-1].name == 'N' && 
                            isLegal(new Move(x+2, x, y-1, y, board[x+2][y-1], board[x][y]), false))
                        return true;
                    else if(x-2 >= 0 && y+1 < 8 && board[x-2][y+1].name == 'N' && 
                            isLegal(new Move(x-2, x, y+1, y, board[x-2][y+1], board[x][y]), false))
                        return true;
                    else if(x+2 < 8 && y+1 < 8 && board[x+2][y+1].name == 'N' && 
                            isLegal(new Move(x+2, x, y+1, y, board[x+2][y+1], board[x][y]), false))
                        return true;
                    else if(x-1 >= 0 && y-2 >= 0 && board[x-1][y-2].name == 'N' && 
                            isLegal(new Move(x-1, x, y-2, y, board[x-1][y-2], board[x][y]), false))
                        return true;
                    else if(x+1 < 8 && y-2 >= 0 && board[x+1][y-2].name == 'N' && 
                            isLegal(new Move(x+1, x, y-2, y, board[x+1][y-2], board[x][y]), false))
                        return true;
                    else if(x-1 >= 0 && y+2 < 8 && board[x-1][y+2].name == 'N' && 
                            isLegal(new Move(x-1, x, y+2, y, board[x-1][y+2], board[x][y]), false))
                        return true;
                    else if(x+1 < 8 && y+2 < 8 && board[x+1][y+2].name == 'N' && 
                            isLegal(new Move(x+1, x, y+2, y, board[x+1][y+2], board[x][y]), false))
                        return true;

                    x = 8; break;
                }
            }
    	}
        
    	return false;
    }
    
    //make a move, hopefully after checking legality
    public void makeMove(Move m) {
            //special moves
        //castling
        switch (m.piece.name) {
            case 'K':
                hasMoved[1][(turnCount % 2 == 0 ? 0 : 1)] = true;
                //castling
                if(m.ex - m.sx == 2) { //kingside
                    // place rook to left of king
                    swap(m.ex-1, 7, m.sy, m.sy);
                }
                else if(m.sx - m.ex == 2) { //queenside
                    // place rook to right of king
                    swap(m.ex+1, 0, m.sy, m.sy);
                }   break;
            case 'R':
                // if a rook is moved, set hasMoved to true to prevent castling
                hasMoved[(m.sx == 7 ? 2 : 0)][(turnCount % 2 == 0 ? 0 : 1)] = true;
                break;
            case 'P':
                //en passant
                if(Math.abs(m.ex - m.sx) == 1 && Math.abs(m.ey - m.sy) == 1
                        && board[m.ex][m.ey].name == ' ') {
                    board[m.ex][m.sy] = new Empty();
                }
                //promotion
                else if((m.ey == 0 && turnCount % 2 == 0) || 
                        (m.ey == 8 && turnCount % 2 == 1)) {
                    // shouldnt interact with user, temporary
                    System.out.println("Enter piece you want to promote to: ");
                    String piece = chessMain.in.next();
                    Color color = (turnCount % 2 == 0 ? Color.WHITE : Color.BLACK);
                    switch(piece.charAt(0)){
                        case 'Q':
                            board[m.sx][m.sy] = new Queen(color);
                            break;
                        case 'R':
                            board[m.sx][m.sy] = new Rook(color);
                            break;
                        case 'N':
                            board[m.sx][m.sy] = new Knight(color);
                            break;
                        case 'B':
                            board[m.sx][m.sy] = new Bishop(color);
                            break;
                        default:
                            board[m.sx][m.sy] = new Queen(color);
                            System.out.println("No piece was created, input invalid");
                            break;
                    }
                    chessMain.in.nextLine();
                }   break;
            default:
                break;
        }
        
        //stop castling if any piece moves to or from the corner
        if((m.sx == 0 || m.sx == 7) && (m.sy == 0 || m.sy == 7))
            hasMoved[(m.sx == 0 ? 0 : 2)][(m.sy == 0 ? 1 : 0)] = true;
        if((m.ex == 0 || m.ex == 7) && (m.ey == 0 || m.ey == 7))
            hasMoved[(m.ex == 0 ? 0 : 2)][(m.ey == 0 ? 1 : 0)] = true;
    	
    	//move pieces
        board[m.ex][m.ey] = board[m.sx][m.sy];
        board[m.sx][m.sy] = new Empty();
    	
        moves[turnCount] = m; //does not store castling
        turnCount++;
    }
    
    //undo a move by accessing moves stack, decrease turn count
    public void undoMove() {
        //if a move has been made
        if(turnCount > 0) {
            turnCount--;
            Move m = moves[turnCount];
            board[m.sx][m.sy] = board[m.ex][m.ey];
            board[m.ex][m.ey] = m.pieceCaptured;
            
            //special moves
            if(m.piece.name == 'P' && m.pieceCaptured.name == ' ' && (m.ex != m.sx)) { //en passant
            	board[m.ex][m.sy] = new Pawn((turnCount % 2 == 1 ? Color.WHITE : Color.BLACK));
            }
            else if(m.piece.name == 'K' && m.ex - m.sx == 2) { //kingside castling
            	System.out.println("Undo castling");
            	//reset bools
            	hasMoved[1][(turnCount % 2 == 0 ? 0 : 1)] = false;
            	swap(5, 7, m.sy, m.ey);
            	hasMoved[2][(turnCount % 2 == 0 ? 0 : 1)] = false;
            }
            else if(m.piece.name == 'K' && m.ex - m.sx == -2) { //queenside
                //reset bools
            	hasMoved[1][(turnCount % 2 == 0 ? 0 : 1)] = false;
            	swap(0, 3, m.sy, m.ey);
            	hasMoved[0][(turnCount % 2 == 0 ? 0 : 1)] = false;
            }
            else if(m.piece.name == 'P' && (m.ey == 7 || m.ey == 0)) { //promotion
	    	board[m.ex][m.ey] = m.pieceCaptured;
		board[m.sx][m.sy] = new Pawn(turnCount % 2 == 0 ? Color.WHITE : Color.BLACK);
	    }
            
            //reset bools for castling
            if((m.ex == 0 || m.ex == 7) && (m.ey == 0 || m.ey == 7))
                for(int i = 0; i <= turnCount; i++)
                    if(i == turnCount)
                        hasMoved[(m.ex == 0 ? 0 : 2)][(m.ey == 0 ? 1 : 0)] = false;
                    else if((moves[i].ex == m.ex && moves[i].ey == m.ey) || 
                            (moves[i].sx == m.ex && moves[i].sy == m.ey))
                        break;
            if((m.sx == 0 || m.sx == 7) && (m.sy == 0 || m.sy == 7))
                for(int i = 0; i <= turnCount; i++)
                    if(i == turnCount)
                        hasMoved[(m.sx == 0 ? 0 : 2)][(m.sy == 0 ? 1 : 0)] = false;
                    else if((moves[i].ex == m.sx && moves[i].ey == m.sy) || 
                            (moves[i].sx == m.sx && moves[i].sy == m.sy))
                        break;
        }
    }
    
    public void swap(int sx, int ex, int sy, int ey){
    	Piece tmp = board[sx][sy];
    	board[sx][sy] = board[ex][ey];
    	board[ex][ey] = tmp;
    }
}
