/*
    Board class for ChessAI.
    Contains all elements of a chess game.
*/

package chessai;

public final class Board
{
    //vars
    Piece[][] board = new Piece[8][8];
    private Move[] moves = new Move[128]; //will be used for undoing moves, needs to account for taking pieces
    int turnCount = 0;

    //default constructor creats the initial position
    Board(){
        createBoard();
    }
    
    //show a console version of the board
    public void displayBoard(Piece[][] board)
    {
        System.out.print("\n");
        
        for(int y = 0; y < 8; y++)
        {
            System.out.print("---------------------------------\n|"); //17
            
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
        System.out.print("---------------------------------\n");
    }
    
    //create the initial position
    public void createBoard(){
		
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
    public boolean isLegal(Move m){

            //initial universal checks
            
        //color check
        if(board[m.sx][m.sy].color != (turnCount % 2 == 0 ? Color.WHITE : Color.BLACK))
            return false;
        
        //destination is own color piece
        if(board[m.ex][m.ey].color == (turnCount % 2 == 0 ? Color.WHITE : Color.BLACK))
            return false;
        
            //piece specific checks
        
        //pawn moving
        if(board[m.sx][m.sy].name == 'P') {
            //find delta y, inverse if black's turn. should always be positive
            int dy = (m.sy - m.ey) * (turnCount % 2 == 0 ? 1 : -1);
                    
            //moving up
            switch (dy) {
                case 1: 
                        ///needs en passantm
                    //taking pieces
                    if(Math.abs(m.sx - m.ex) == 1) {
                        if(board[m.ex][m.ey].name == ' ')
                            return false;
                    }
                    //single move up
                    else if (m.sx == m.ex && board[m.ex][m.ey].name == ' ') return true;
                    else return false;
                    break;
                case 2:
                    //check for in initial position
                    if(!((turnCount % 2 == 0 && m.sy == 6) || (turnCount % 2 == 1 && m.sy == 1)))
                        return false;
                    break;
                default:
                    return false;
            }
            
        }

        //rook moving
        if(board[m.sx][m.sy].name == 'R'){
            // if is trying to move in both dimensions, disallow
            if(Math.abs(m.ey - m.sy) > 0 && Math.abs(m.ex - m.sx) > 0) 
                return false;
        }

        //knight moving
        if(board[m.sx][m.sy].name == 'N'){
            // knight can only legally move two on x/y axis, and then 1 on the axis that was not chosen first
            if(!((Math.abs(m.ex - m.sx) == 1 && Math.abs(m.ey - m.sy) == 2) || (Math.abs(m.ex - m.sx) == 2 && Math.abs(m.ey - m.sy) == 1))) 
                return false;
        }

        //bishop moving
        if(board[m.sx][m.sy].name == 'B'){
            // moving horizontally means that your x/y position will change in a same amount (disregarding signs)
            if(Math.abs(m.ex - m.sx) != Math.abs(m.ey - m.sy))
                return false;
        }

        //queen moving
        if(board[m.sx][m.sy].name == 'Q'){
            // if moving diagonally, same check as bishop
            if((Math.abs(m.ey - m.sy) > 0 && Math.abs(m.ex - m.sx) > 0) && (Math.abs(m.ex - m.sx) != Math.abs(m.ey - m.sy)))
                return false;
        }

        //king moving
        if(board[m.sx][m.sy].name == 'K'){
            // magnitude of all directional movement should be no greater than one
            if(Math.abs(m.ex - m.sx) > 1 && Math.abs(m.ey - m.sy) > 1)
                return false;
            ///castling
        }

            //piece specific checks end, check moving through other pieces
        
        //moving through pieces
        if(board[m.sx][m.sy].name != 'N') //not for knights
        {
           for(int x = m.sx, y = m.sy; x != m.ex && y != m.ey;) {
                if(y != m.ey) y += (m.sy < m.ey ? 1 : -1);
                if(x != m.ex) x += (m.sx < m.ex ? 1 : -1);

                if(board[x][y].name != ' ' && (x != m.ex || y != m.ey))
                    return false;
            } 
        }
        
        ///king in check


        // if has not returned false before here, it's legal
        return true;
    }
    
    //make a move, hopefully after checking legality
    public void makeMove(Move m)
    {
        //currently cannot en passant or castle
        board[m.ex][m.ey] = board[m.sx][m.sy];
        board[m.sx][m.sy] = new Empty();
        moves[turnCount] = m;
        turnCount++;
    }
    
    //undo a move by accessing moves stack, decrease turn count
    public void undoMove()
    {
        //if a move has been made
        if (turnCount > 0)
        {
            //currently does not account for taking pieces
            board[moves[turnCount-1].sx][moves[turnCount].sy] = board[moves[turnCount-1].ex][moves[turnCount].ey];
            turnCount--;
        }
    }

}
