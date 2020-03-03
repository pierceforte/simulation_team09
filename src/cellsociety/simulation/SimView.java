package cellsociety.simulation;

import cellsociety.MainController;
import cellsociety.cell.config.ConfigSaver;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import cellsociety.cell.Cell;
import cellsociety.cell.CellView;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class SimView {
    public ResourceBundle myResources;
    public static final int GRID_SIZE = 400;
    public static final Color BACKGROUND = Color.WHEAT;
    private SimController controller;
    private BorderPane bPane;
    private GridPane gridPane;
    private Button playBttn;
    private Button pauseBttn;
    private Button stepBttn;
    private Button exitBttn;

    public SimView(SimController controller){
        Locale locale = new Locale("en", "US");
        myResources = ResourceBundle.getBundle("default", locale);
        this.controller = controller;
        bPane = new BorderPane();
        createControls();
    }

    public Node getRoot(){
        return bPane;
    }

    private void createControls(){
        playBttn = new Button(myResources.getString("PlayBttn"));
        playBttn.setId("playBttn");
        pauseBttn = new Button(myResources.getString("PauseBttn"));
        pauseBttn.setId("pauseBttn");
        stepBttn = new Button(myResources.getString("StepBttn"));
        stepBttn.setId("stepBttn");
        exitBttn = new Button(myResources.getString("ExitBttn"));
        exitBttn.setId("exitBttn");
        gridPane = new GridPane();
        gridPane.add(playBttn, 1, 0);
        gridPane.add(pauseBttn, 2, 0);
        gridPane.add(stepBttn, 3, 0);
        gridPane.add(exitBttn, 4, 0);
        bPane.setBottom(gridPane);

        playBttn.setOnAction(event -> handleButtonClick(event));
        pauseBttn.setOnAction(event -> handleButtonClick(event));
        stepBttn.setOnAction(event -> handleButtonClick(event));
        exitBttn.setOnAction(event -> handleButtonClick(event));
    }
    //TODO: cleanup this code
    public boolean userRestartedSimulation() {
        Stage input = new Stage();
        input.setTitle(myResources.getString("StartSim"));
        final boolean[] ret = {false};
        Button restartBttn = createButton(myResources.getString("RestartBttn"), 0, 0, 100, 30);
        restartBttn.setId("restartBttn");
        Button continueBttn = createButton(myResources.getString("ContinueBttn"), 100, 0, 100, 30);
        continueBttn.setId("continueBttn");

        restartBttn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent t) {
                input.close();
                ret[0] = true;
            }
        });
        continueBttn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent t) {
                input.close();
                ret[0] = false;
            }
        });

        Pane pane = new Pane();
        pane.getChildren().addAll(restartBttn, continueBttn);

        input.setScene(new Scene(pane, 200, 30));
        input.showAndWait();

        return ret[0];
    }


    private void handleButtonClick(ActionEvent event){
        if(event.getSource() == playBttn){
            controller.start();
        } else if (event.getSource() == pauseBttn){
            controller.pause();
        }
        else if (event.getSource() == stepBttn) {
            controller.update(true);
            controller.pause();
        }
        else if (event.getSource() == exitBttn) {
            controller.pause();

            Stage stage = new Stage();
            stage.setTitle("Save Current Configuration");

            Pane pane = new Pane();
            pane.setBackground(new Background(new BackgroundFill(Color.MAROON, CornerRadii.EMPTY, Insets.EMPTY)));

            Button saveBttn = createButton("Save Configuration:", 300/2 - 100/2, 400, 100, 30);
            saveBttn.setId("saveBttn");
            Button noSaveBttn = createButton("Quit without Saving", 300/2 - 100/2, 440, 100, 30);
            noSaveBttn.setId("noSaveBttn");

            saveBttn.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent t) {
                    letUserSaveConfig();
                    stage.close();
                }
            });
            noSaveBttn.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent t) {
                    stage.close();
                    ensureUserWantsToQuit();
                }
            });

            pane.getChildren().addAll(saveBttn, noSaveBttn);
            Scene scene = new Scene(pane, 300, 600);
            stage.setScene(scene);
            stage.showAndWait();
        }
    }

    public <T extends Cell> void update(List<List<T>> cells) {
        Group root = new Group();

        // divide by the large dimension so everything fits on screen
        double size = ((double) MainController.WIDTH) / Math.max(cells.get(0).size(), cells.size());
        // TODO: get these to work (the calculations are correct, but changing xPos and yPos in cellView
        //  doesn't do work
        double xOffset = size * Math.max(0, cells.size() - cells.get(0).size())/2;
        double yOffset = size * Math.max(0, cells.get(0).size() - cells.size())/2;

        int cellViewIdNum = 0;
        for (List<T> row : cells) {
            for (T cell : row) {
                CellView cellView = new CellView(cell,size, xOffset, yOffset, cellViewIdNum);
                cellViewIdNum++;
                root.getChildren().add(cellView);
            }
        }
        bPane.setCenter(root);
    }

    private Button createButton(String text, double xPos, double yPos, double width, double height) {
        Button button = new Button(text);
        button.setTranslateX(xPos);
        button.setTranslateY(yPos);
        button.setPrefWidth(width);
        button.setPrefHeight(height);
        return button;
    }

    private void letUserSaveConfig() {
        Stage stage = new Stage();
        stage.setTitle("Save Current Configuration");

        Text nameHeader = new Text("Configuration File Name");
        nameHeader.setX(300/2 - nameHeader.getLayoutBounds().getWidth()/2);
        nameHeader.setY(50);

        TextField nameField = new TextField();
        nameField.setPrefWidth(100);
        nameField.setLayoutX(300/2 - nameField.getPrefWidth()/2);
        nameField.setLayoutY(75);

        Text authorHeader = new Text("Author");
        authorHeader.setX(300/2 - authorHeader.getLayoutBounds().getWidth()/2);
        authorHeader.setY(150);

        TextField authorField = new TextField();
        authorField.setPrefWidth(100);
        authorField.setLayoutX(300/2 - authorField.getPrefWidth()/2);
        authorField.setLayoutY(175);

        Text descriptionHeader = new Text("Description");
        descriptionHeader.setX(300/2 - descriptionHeader.getLayoutBounds().getWidth()/2);
        descriptionHeader.setY(250);

        TextArea descriptionField = new TextArea();
        descriptionField.setWrapText(true);
        descriptionField.setPrefWidth(200);
        descriptionField.setLayoutX(300/2 - descriptionField.getPrefWidth()/2);
        descriptionField.setLayoutY(275);

        Button saveBttn = createButton("Save Configuration", 300/2 - 100/2, 500, 100, 30);
        saveBttn.setId("saveBttn");
        Button cancelBttn = createButton("Cancel", 300/2 - 100/2, 540, 100, 30);
        cancelBttn.setId("cancelBttn");

        saveBttn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent t) {
                stage.close();
                controller.saveConfig(nameField.getText(), authorField.getText(), descriptionField.getText());
                ensureUserWantsToQuit();
            }
        });
        cancelBttn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent t) {
                stage.close();
                controller.start();
            }
        });

        Pane pane = new Pane();
        pane.setBackground(new Background(new BackgroundFill(Color.MAROON, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.getChildren().addAll(nameHeader, nameField, authorHeader, authorField, descriptionHeader, descriptionField,
                saveBttn, cancelBttn);


        Scene scene = new Scene(pane, 300, 600);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void ensureUserWantsToQuit() {
        Stage stage = new Stage();
        stage.setTitle("Resume or Quit");

        Pane pane = new Pane();
        pane.setBackground(new Background(new BackgroundFill(Color.MAROON, CornerRadii.EMPTY, Insets.EMPTY)));

        Text areYouSureHeader = new Text("Are you sure you want to Quit the simulation?");
        areYouSureHeader.setX(300/2 - areYouSureHeader.getLayoutBounds().getWidth()/2);
        areYouSureHeader.setY(150);

        Button resumeBttn = createButton("Resume", 300/2 - 100/2, 400, 100, 30);
        resumeBttn.setId("resumeBttn");
        Button quitBttn = createButton("Quit", 300/2 - 100/2, 440, 100, 30);
        quitBttn.setId("quitBttn");

        resumeBttn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent t) {
                stage.close();
                controller.start();
            }
        });
        quitBttn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent t) {
                stage.close();
                controller.setIsEnded(true);
            }
        });

        pane.getChildren().addAll(areYouSureHeader, resumeBttn, quitBttn);
        Scene scene = new Scene(pane, 300, 600);
        stage.setScene(scene);
        stage.showAndWait();
    }


}
