package cellsociety.simulation;

import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import cellsociety.cell.Cell;
import cellsociety.cell.CellView;
import cellsociety.grid.GridModel;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class SimView {

    public static final Color BACKGROUND = Color.WHEAT;
    private SimModel model;
    private SimController controller;
    private BorderPane bPane;
    private int size; //of entire grid

    Button startBttn;
    Button pauseBttn;

    public SimView(){
        bPane = new BorderPane();
    }

    public Scene getSimScene(){
        return new Scene(bPane, size, size, BACKGROUND);
    }

    public void createSimScene(){
        Group root = new Group();
        startBttn = new Button("Start");
        pauseBttn = new Button("Stop");
        root.getChildren().addAll(startBttn, pauseBttn);
        bPane.setBottom(root);
    }

    private void handleButtonClick(ActionEvent event){
        if(event.getSource() == startBttn){
            controller.play();
        } else if (event.getSource() == pauseBttn){
            controller.togglePause();
        }
    }

    public void updateCellGrid(List<List<Cell>> cells) {
        Group root = new Group();
        for (List<Cell> row : cells) {
            for (Cell cell : row) {
                CellView cellView = new CellView(size/row.size(), cells.indexOf(row), row.indexOf(cell));
                cellView.updateCellColor(cell.getState());
                root.getChildren().add(cellView);
            }
        }
        bPane.setCenter(root);
    }

    public void setGridSize(int size){
        this.size = size;
    }





}
