/*
    Temporary main for chessAI.
 */

package chessai;

public class chessMain
{
    public static void main(String[] args)
    {
        ChessAI ai = new ChessAI();
        ai.board = ai.emptyBoard();
        ai.displayBoard();
    }
}
