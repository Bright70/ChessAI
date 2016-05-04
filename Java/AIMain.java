/*
    Pits two AI's against each other for self-improvement
 */

package chessai;

public class AIMain {

    public static void main(String[] args){

        Board board;
        ChessAI control;
        ChessAI test;

        while(true) {
            board = new Board();
            getValues(false);
            control = new ChessAI();
            // modify values
            getValues(true);
            test = new ChessAI();
            boolean checkmate = false;

            while (!checkmate) {
                Move controlMove = control.aiMakeMove(board);
                if (controlMove == null)
                    checkmate = true;
                else
                    board.makeMove(controlMove);
                board.displayBoard(board.board);
                Move testMove = test.aiMakeMove(board);
                if (testMove == null)
                    checkmate = true;
                else
                    board.makeMove(testMove);
                board.displayBoard(board.board);
            }

            if (board.turnCount % 2 == 0) { // black checkmated white | control AI won
                System.out.println("Control AI won");
                // nothing changes
            } else { // white checkmated black | test AI won
                System.out.println("Testing AI won");
                setValues();
                // modify static values to values used by testing AI
            }
        }
    }

    private static void getValues(boolean test){
        if(test){ // modify values from file without modifying file, as test

        }
        else{ // grab values from file

        }
    }

    private static void setValues(){

    }
}