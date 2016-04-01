/*
    Temporary interface for chessAI.
 */

package chessai;

import java.util.Scanner;

public class chessMain
{
    static Scanner in = new Scanner(System.in);
	
    public static void main(String[] args)
    {
        //create new game and vars
        ChessAI ai = new ChessAI();
        Board board = new Board();
        int sx, ex, sy, ey;
        board.displayBoard(board.board);
        
        //loop for input
        do{
            System.out.print((board.turnCount % 2 == 0 ? "White" : "Black") + "'s turn: ");
            sx = in.nextInt();
            sy = in.nextInt();
            ex = in.nextInt();
            ey = in.nextInt();

            Move move = new Move(sx, ex, sy, ey, board.board[sx][sy]);
            
            System.out.print("\n\n\n\n\n\n\n\n");
            
            if(board.isLegal(move))
                board.makeMove(move);
            else
                System.out.print("Illegal move.");
            
            board.displayBoard(board.board);
        } while(true); //win condition
    }
}
