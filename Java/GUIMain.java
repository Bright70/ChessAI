package chessai;

/*
 Graphical User Interface for game
 */

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

class BoardUI{ // only for visual representation of Board class

    ImageView square;
    ImageView pieceView;
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

        square.setOnDragOver(event -> {
            if (event.getGestureSource() != square &&
                    event.getDragboard().hasImage()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }

            event.consume();
        });
    }
    public void updateBoard(Piece[][] board){ // calls for pieces to be set according to board in Board class
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

public class GUIMain extends Application implements EventHandler<ActionEvent>{

    private Board board = new Board();
    private ChessAI ai = new ChessAI();
    private int width = 800, height = 800, offset = 75;
    private Pane layout = new Pane();
    private Scene scene = new Scene(layout, width, height);
    private Button undo = new Button("Undo");
    private int sx, sy, ex, ey;

    private BoardUI[][] boardUI = new BoardUI[8][8];

    public static void main(String args[]){
        launch(args);
    }

    private void setCoords(double x, double y, Button button){
        button.setLayoutX(x);
        button.setLayoutY(y);
    }

    private void setDragEvents(int x, int y){
        final int X = x, Y = y;
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
                }
                else
                    System.out.println("Move is illegal");
                // have to actually move the pieces, without static references
            }

            event.setDropCompleted(success);
            event.consume();
        });
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
                    setDragEvents(x, y);
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
                setDragEvents(x, y);
            }
        }

        updateBoard();

        undo.setOnAction(this);
        setCoords(width/2, 0, undo);

        layout.getChildren().add(undo);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void handle(ActionEvent event) {
        if(event.getSource() == undo){
            board.undoMove();
            updateBoard();
        }
    }
}