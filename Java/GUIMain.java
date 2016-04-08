package chessai;

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
    private int x, y, sx, sy; // sx and sy not being set below

    BoardUI(int x, int y, Board boardClass, Piece[][] board){
        this.x = x;
        this.y = y;
        if((x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 &&  y % 2 == 1)){
            square = new ImageView(new Image("/media/squareWhite.png"));
        }
        else{
            square = new ImageView(new Image("/media/squareBlack.png"));
        }

        square.setOnDragDropped(event -> {

            Move move = new Move(sx, (int)((event.getSceneX() - 150) /75), sy, (int)((event.getSceneY() - 150) /75),
                    board[sx][sy], board[(int)((event.getSceneX() - 150) /75)][(int)((event.getSceneY() - 150) /75)]);
            Dragboard db = event.getDragboard();
            System.out.println("Released");
            System.out.println("x: " + event.getSceneX());
            System.out.println("y: " + event.getSceneY());
            System.out.println("sx: " + sx);
            System.out.println("ex: " + (int)((event.getSceneX() - 150) /75));
            System.out.println("sy: " + sy);
            System.out.println("ey: " + (int)((event.getSceneY() - 150) /75));
            boolean success = false;
            if(db.hasImage()){
                success = true;
                if(boardClass.isLegal(move))
                    boardClass.makeMove(move);
                else
                    System.out.println("Move is illegal");
                // have to actually move the pieces, without static references
            }

            event.setDropCompleted(success);
            event.consume();
        });

        square.setOnDragOver(event -> {
            if (event.getGestureSource() != square &&
                    event.getDragboard().hasImage()) {
        /* allow for both copying and moving, whatever user chooses */
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
        if(pieceView != null){
            pieceView.setOnDragDetected(event -> { // lambda :D
                sx = (int)((event.getSceneX() - 150) /75);
                sy = (int)((event.getSceneY() - 150) /75);

                Dragboard db = pieceView.startDragAndDrop(TransferMode.ANY);

                ClipboardContent content = new ClipboardContent();
                content.putImage(piece);
                db.setContent(content);

                event.consume();
            });
        }
    }
}

public class GUIMain extends Application implements EventHandler<ActionEvent>{

    private Board board = new Board();
    private ChessAI ai = new ChessAI();
    private int width = 900, height = 900;
    private Pane layout = new Pane();
    private Scene scene = new Scene(layout, 900, 900);
    private Button undo = new Button("Undo");

    private BoardUI[][] boardUI = new BoardUI[8][8];

    public static void main(String args[]){
        launch(args);
    }

    private void setCoords(double x, double y, Button button){
        button.setLayoutX(x);
        button.setLayoutY(y);
    }

    private void updateBoard(){
        for(int x = 0; x < 8; x++){
            for(int y = 0; y < 8; y++){
                boardUI[x][y].updateBoard(board.board);
                if(boardUI[x][y].piece != null){ // could probably be simplified
                    boardUI[x][y].pieceView.relocate(150 + x*75, 150 + y*75);
                    layout.getChildren().add(boardUI[x][y].pieceView);
                }
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("ChessAI");

        for(int x = 0; x < 8; x++){
            for(int y = 0; y < 8; y++){
                boardUI[x][y] = new BoardUI(x, y, board, board.board);
                boardUI[x][y].square.relocate(150 + x*75, 150 + y*75);
                layout.getChildren().add(boardUI[x][y].square);
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
        }
    }
}
