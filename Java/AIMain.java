/*
    Pits two AI's against each other for self-improvement
 */

package chessai;

public class AIMain {

    public static void main(String[] args){
        Board board = new Board();
        ChessAI control = new ChessAI();
        // modify values
        ChessAI test = new ChessAI();

        while(!board.checkmated()){
            board.makeMove(control.aiMakeMove(board));
            board.displayBoard(board.board);
            board.makeMove(test.aiMakeMove(board));
            board.displayBoard(board.board);
        }

        if(board.turnCount % 2 == 0){ // black checkmated white | control AI won
            System.out.println("Control AI won");
        }
        else{ // white checkmated black | test AI won
            System.out.println("Testing AI won");
            // modify static values to values used by testing AI
        }
    }
}
