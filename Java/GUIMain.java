/*
    Graphical User Interface for game
    TODO: EXPORT/IMPORT | DISPLAY CHECK WHEN IN CHECK
*/

package chessai;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

class BoardUI{ // only for visual representation of Board class

    ImageView square, pieceView;
    Image piece; // for drag/drop
    private int x, y;

    BoardUI(int x, int y){
        this.x = x;
        this.y = y;
        if((x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 &&  y % 2 == 1)){
            square = new ImageView(new Image("/media/squareWhite.png"));
        }
        else{
            square = new ImageView(new Image("/media/squareBlack.png"));
        }
    }
    void updateBoard(Piece[][] board){ // calls for pieces to be set according to board in Board class
        if(board[x][y].name == 'P'){
            if(board[x][y].color == Color.WHITE){
                piece = new Image("/media/pawnWhite.png");
                pieceView = new ImageView(piece);
            }
            else{
                piece = new Image("/media/pawnBlack.png");
                pieceView = new ImageView(new Image("/media/pawnBlack.png"));
            }
        }
        else if(board[x][y].name == 'R'){
            if(board[x][y].color == Color.WHITE){
                piece = new Image("/media/rookWhite.png");
                pieceView = new ImageView(piece);
            }
            else{
                piece = new Image("/media/rookBlack.png");
                pieceView = new ImageView(piece);
            }
        }
        else if(board[x][y].name == 'N'){
            if(board[x][y].color == Color.WHITE){
                piece = new Image("/media/knightWhite.png");
                pieceView = new ImageView(piece);
            }
            else{
                piece = new Image("/media/knightBlack.png");
                pieceView = new ImageView(piece);
            }
        }
        else if(board[x][y].name == 'B'){
            if(board[x][y].color == Color.WHITE){
                piece = new Image("/media/bishopWhite.png");
                pieceView = new ImageView(piece);
            }
            else{
                piece = new Image("/media/bishopBlack.png");
                pieceView = new ImageView(piece);
            }
        }
        else if(board[x][y].name == 'Q'){
            if(board[x][y].color == Color.WHITE){
                piece = new Image("/media/queenWhite.png");
                pieceView = new ImageView(piece);
            }
            else{
                piece = new Image("/media/queenBlack.png");
                pieceView = new ImageView(piece);
            }
        }
        else if(board[x][y].name == 'K'){
            if(board[x][y].color == Color.WHITE){
                piece = new Image("/media/kingWhite.png");
                pieceView = new ImageView(piece);
            }
            else{
                piece = new Image("/media/kingBlack.png");
                pieceView = new ImageView(piece);
            }
        }
    }
}

public class GUIMain extends Application{

    private Board board = new Board();
    private ChessAI ai = new ChessAI();
    private int width = 800, height = 800, offset = 75;
    private BorderPane layout = new BorderPane();
    private Scene scene = new Scene(layout, width, height);
    private int sx, sy, ex, ey;

    private BoardUI[][] boardUI = new BoardUI[8][8];

    public static void main(String args[]){launch(args);}

    private void setDragEvents(int x, int y, boolean square){
        final int X = x, Y = y;
        boardUI[x][y].square.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if(db.hasImage()) {
                success = true;
                ex = (int) ((event.getSceneX() - 75) / 75);
                ey = (int) ((event.getSceneY() - 75) / 75);
                System.out.println("ex: " + ex);
                System.out.println("ey: " + ey);
                Move move = new Move(sx, ex, sy, ey, board.board[sx][sy], board.board[sx][ey]);
                if (board.isLegal(move, true)){
                    board.makeMove(move);
                    System.out.println("Move is legal");
                    board.makeMove(ai.aiMakeMove(board));
                }
                else
                    System.out.println("Move is illegal");
                // have to actually move the pieces, without static references
            }

            event.setDropCompleted(success);
            event.consume();
        });
        boardUI[x][y].square.setOnDragOver(event -> {
            if (event.getGestureSource() != boardUI[X][Y].square &&
                    event.getDragboard().hasImage()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }

            event.consume();
        });

        if(!square){
            boardUI[x][y].pieceView.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if(db.hasImage()) {
                    success = true;
                    ex = (int) ((event.getSceneX() - 75) / 75);
                    ey = (int) ((event.getSceneY() - 75) / 75);
                    System.out.println("ex: " + ex);
                    System.out.println("ey: " + ey);
                    Move move = new Move(sx, ex, sy, ey, board.board[sx][sy], board.board[sx][ey]);
                    if (board.isLegal(move, true)){
                        board.makeMove(move);
                        System.out.println("Move is legal");
                        board.makeMove(ai.aiMakeMove(board));
                    }
                    else
                        System.out.println("Move is illegal");
                    // have to actually move the pieces, without static references
                }

                event.setDropCompleted(success);
                event.consume();
            });
            boardUI[x][y].pieceView.setOnDragOver(event -> {
                if (event.getGestureSource() != boardUI[X][Y].pieceView &&
                        event.getDragboard().hasImage()) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }

                event.consume();
            });
            boardUI[x][y].pieceView.setOnDragDetected(event -> { // lambda :D
                sx = (int)((event.getSceneX() - 75) /75);
                sy = (int)((event.getSceneY() - 75) /75);
                System.out.println("sx: " + sx + " sy: " + sy);

                Dragboard db = boardUI[X][Y].pieceView.startDragAndDrop(TransferMode.ANY);

                ClipboardContent content = new ClipboardContent();
                content.putImage(boardUI[X][Y].piece);
                db.setContent(content);
                updateBoard();

                event.consume();
            });
            boardUI[x][y].pieceView.setOnDragDone(event -> {
                updateBoard();
                event.consume();
            });

        }
    }

    private void updateBoard(){
        for(int x = 0; x < 8; x++){
            for(int y = 0; y < 8; y++){
                if(boardUI[x][y].piece != null){
                    layout.getChildren().remove(boardUI[x][y].pieceView);
                    boardUI[x][y].piece = null;
                }
                boardUI[x][y].updateBoard(board.board);
                if(boardUI[x][y].piece != null){ // could probably be simplified
                    boardUI[x][y].pieceView.relocate(offset + x*75, offset + y*75);
                    layout.getChildren().add(boardUI[x][y].pieceView);
                    setDragEvents(x, y, false);
                }
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("ChessAI");

        for(int x = 0; x < 8; x++){
            for(int y = 0; y < 8; y++){
                boardUI[x][y] = new BoardUI(x, y);
                boardUI[x][y].square.relocate(offset + x*75, offset + y*75);
                layout.getChildren().add(boardUI[x][y].square);
                setDragEvents(x, y, true);
            }
        }

        updateBoard();

        MenuBar menuBar = new MenuBar();
        Menu gameMenu = new Menu("_Game");
        Menu boardMenu = new Menu("_Board");
        MenuItem newGameMenu = new MenuItem("New Game");
        newGameMenu.setOnAction(event -> {board = new Board(); updateBoard();});
        MenuItem undoMenu = new MenuItem("Undo");
        undoMenu.setOnAction(event -> {board.undoMove(); updateBoard();});
        MenuItem exportMenu = new MenuItem("Export");
        exportMenu.setOnAction(event -> exportBoard());
        MenuItem importMenu = new MenuItem("Import");
        importMenu.setOnAction(event -> {importBoard(); updateBoard();});

        gameMenu.getItems().addAll(newGameMenu, new SeparatorMenuItem(), undoMenu);
        boardMenu.getItems().addAll(exportMenu, importMenu);

        menuBar.getMenus().addAll(gameMenu, boardMenu);

        layout.setTop(menuBar);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void exportBoard(){

        PrintWriter writer = null;
        String input = JOptionPane.showInputDialog(this, "Enter name to store board as:");

        // writing board
        try {
            writer = new PrintWriter("src/exports/" + input, "UTF-8"); // add file extension?
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){
                if(board.board[x][y].color == Color.BLACK)
                    writer.print("b");
                else if(board.board[x][y].color == Color.NONE)
                    writer.print(" ");
                else
                    writer.print("w");
                writer.print(board.board[x][y].name);

            }
            writer.println();
        }

        writer.close();

        // writing moves from moves array, not working
        try {
            writer = new PrintWriter("src/exports/" + input + "Moves", "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        writer.println(board.turnCount); // print turn count to beginning of moves file

        for(int x = 0; x < 128; x++){ // same as moves[] may change

            if(board.moves[x] == null)
                break; // whenever this is null, end of moves recorded reached
            else{
                writer.println(
                        board.moves[x].sx + board.moves[x].ex + board.moves[x].sy + board.moves[x].ey
                        + board.moves[x].piece.name + board.moves[x].pieceCaptured.name);
            }
        }

        writer.close();

        System.out.println("Board exported to file");
    }

    private void importBoard(){

        String input = JOptionPane.showInputDialog(this, "Enter board to import:");

        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get("src/exports/" + input), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("No such file exists");
            // error trap
        }

        for(int x = 1; x < lines.size(); x++){ // start at one since 0 is turncount
            for(int y = 1; y < 15; y+=2){
                switch(lines.get(x).charAt(y)) {
                    default:
                        board.board[y / 2][x] = new Empty();
                        break;
                    case 'P':
                        board.board[y / 2][x] = new Pawn((lines.get(x).charAt(y - 1) == 'b' ? Color.BLACK : Color.WHITE));
                        break;
                    case 'R':
                        board.board[y / 2][x] = new Rook((lines.get(x).charAt(y - 1) == 'b' ? Color.BLACK : Color.WHITE));
                        break;
                    case 'N':
                        board.board[y / 2][x] = new Knight((lines.get(x).charAt(y - 1) == 'b' ? Color.BLACK : Color.WHITE));
                        break;
                    case 'B':
                        board.board[y / 2][x] = new Bishop((lines.get(x).charAt(y - 1) == 'b' ? Color.BLACK : Color.WHITE));
                        break;
                    case 'Q':
                        board.board[y / 2][x] = new Queen((lines.get(x).charAt(y - 1) == 'b' ? Color.BLACK : Color.WHITE));
                        break;
                    case 'K':
                        board.board[y / 2][x] = new King((lines.get(x).charAt(y - 1) == 'b' ? Color.BLACK : Color.WHITE));
                        break;
                }
            }
        }

        // moves array must be imported
        // turnCount must also be imported

        try {
            lines = Files.readAllLines(Paths.get("src/exports/" + input + "Moves"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("No such file exists");
            // error trap
        }

        board.turnCount = lines.get(0).charAt(0);

        System.out.println("Board imported");
    }
}
