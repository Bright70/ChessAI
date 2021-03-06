/*
    Console interface for chessAI.
 */

package chessai;

import java.util.Scanner;

public class chessMain {
    
    static Scanner in = new Scanner(System.in);
	
    public static void main(String[] args) {
        
        //create new game and vars
        ChessAI ai = new ChessAI(true);
        Board game = new Board();
        String input;
        long start;
        Move move;
        
        game.displayBoard(game.board);
        
        //game loop
        do {
            //get a move from user
            System.out.print((game.turnCount % 2 == 0 ? "White" : "Black") + "'s turn: ");
            input = in.nextLine();

            System.out.print("\n\n");
            
            move = convertMove(input, game);
            
            if(move != null && game.isLegal(move, true)) {
                game.makeMove(move);
                game.displayBoard(game.board);

                System.out.print("Computer is thinking...");
                start = System.currentTimeMillis();
                move = ai.aiMakeMove(game);
                System.out.println("\nProcessing time: " + (((double)System.currentTimeMillis() - start) / 1000) + "s");
                game.makeMove(move);
                game.displayBoard(game.board);
            }
            else if(move != null){
                System.out.print("Illegal move.");
                game.displayBoard(game.board);
            }
            
        } while(move != null); //win condition
        
        System.out.print((game.turnCount % 2 == 0 ? "Black" : "White") + " wins!");
        
    }
    
    //takes a string and converts it into a move: supports coordinate and notation
    public static Move convertMove(String in, Board game) {
        //coordinate variables
        int sx = -1, ex, sy = -1, ey;
        
        if(in.equals("undo")){
            game.undoMove();
        }
        
        //coordinate input
        if(in.length() == 7) {
            sx = Integer.parseInt(in.substring(0, 1));
            sy = Integer.parseInt(in.substring(2, 3));
            ex = Integer.parseInt(in.substring(4, 5));
            ey = Integer.parseInt(in.substring(6, 7));
        }
        //chess notational input. note: 'a' = 97, 'h' = 104, '1' = 49, '8' = 56
        else if(in.length() <= 5 && in.charAt(in.length() - 1) >= 49 && in.charAt(in.length() - 1) <= 56
                && in.charAt(in.length() - 2) >= 97 && in.charAt(in.length() - 2) <= 104) { 
            //remove 'take' notation: unneeded.
            in = in.replaceFirst("x", "");

            //get ex and ey coordinates from final 2 characters of input
            ex = in.charAt(in.length() - 2) - 97;
            ey = 8 - Integer.parseInt(in.substring(in.length() - 1, in.length()));

                //find piece
            //pawn move
            if(in.charAt(0) >= 97 && in.charAt(0) <= 104) {
                if(in.length() == 3) { //taking a piece
                    sx = in.charAt(0) - 97;
                    sy = ey + (game.turnCount % 2 == 0 ? 1 : -1);
                }
                else { //forwards movement
                    sx = ex;
                    sy = ey + (game.turnCount % 2 == 0 ? 1 : -1);
                    //double forwards
                    if(game.board[sx][sy].name != 'P' && ey == (game.turnCount % 2 == 0 ? 4 : 3) 
                            && game.board[sx][sy + (game.turnCount % 2 == 0 ? 1 : -1)].name == 'P')
                        sy += (game.turnCount % 2 == 0 ? 1 : -1);
                }
            }
            else { //other pieces
                if(in.length() == 4) { //specific piece
                    if(in.charAt(1) >= 97 && in.charAt(1) <= 104) {//known sx
                        sx = in.charAt(1) - 97;
                        for (int y = 0; y < 8; y++) {
                            if(game.board[sx][y].name == in.charAt(0) && 
                                    game.board[sx][y].color == (game.turnCount % 2 == 0 ? Color.WHITE : Color.BLACK) &&
                                    game.isLegal(new Move(sx, ex, y, ey, game.board[sx][y], game.board[ex][ey]), true)) {
                                sy = y;
                                break;
                            }
                        }
                    }
                    else { //known sy
                        sy = 8 - Integer.parseInt(in.substring(1, 2));
                        for (int x = 0; x < 8; x++) {
                            if(game.board[x][sy].name == in.charAt(0) && 
                                    game.board[x][sy].color == (game.turnCount % 2 == 0 ? Color.WHITE : Color.BLACK) &&
                                    game.isLegal(new Move(x, ex, sy, ey, game.board[x][sy], game.board[ex][ey]), true)) {
                                sx = x;
                                break;
                            }
                        }
                    }
                }
                else {
                    for(int x = 0; x < 8; x++) {
                        for(int y = 0; y < 8; y++) {
                            if(game.board[x][y].name == in.charAt(0) && 
                                    game.board[x][y].color == (game.turnCount % 2 == 0 ? Color.WHITE : Color.BLACK) &&
                                    game.isLegal(new Move(x, ex, y, ey, game.board[x][y], game.board[ex][ey]), true)) {
                                sx = x; sy = y;
                                x = 8; break;
                            }
                        }
                    }
                }
            }
        }
        //probably illegal move
        else return new Move();
        
//        System.out.print("\n" + sx + " " + sy + " to " + ex + " " + ey + "\n"); //debug
        
        return new Move(sx, ex, sy, ey, (sx >= 0 && sy >= 0 ? game.board[sx][sy] : new Empty()), (ex >= 0 && ey >= 0 ? game.board[ex][ey] : new Empty()));
    }
    
}
