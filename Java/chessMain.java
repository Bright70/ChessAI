/*
    Temporary main for chessAI.
 */

package chessai;

import java.util.Scanner;

public class chessMain
{
	
	static Scanner in = new Scanner(System.in);
	
    public static void main(String[] args)
    {
        ChessAI ai = new ChessAI();
        Board board = new Board();
        int sx, ex, sy, ey;
        board.displayBoard(board.board);
        
        do{
        	sx = in.nextInt();
	        ex = in.nextInt();
	        sy = in.nextInt();
	        ey = in.nextInt();
        	
        	Move move = new Move(sx, ex, sy, ey, board.board[sx][sy]);
        	if(board.isLegal(move)){
        		board.makeMove(move);
        	}
        }while(true);
    }
}
