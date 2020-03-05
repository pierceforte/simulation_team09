package cellsociety.simulation;

import cellsociety.InputStage;
import cellsociety.MainController;
import cellsociety.cell.FileNameVerifier;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.stage.Stage;

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
        Button restartBttn = createButton(myResources.getString("RestartBttn"),"restartBttn", 0, 0, 100, 30);
        Button continueBttn = createButton(myResources.getString("ContinueBttn"), "restartBttn", 100, 0, 100, 30);

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
            handleExitRequest();
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

    private Button createButton(String text, String id, double xPos, double yPos, double width, double height) {
        Button button = new Button(text);
        button.setTranslateX(xPos);
        button.setTranslateY(yPos);
        button.setPrefWidth(width);
        button.setPrefHeight(height);
        button.setId(id);
        return button;
    }

    // TODO: refactor everything below

    private void handleExitRequest() {
        controller.pause();

        InputStage stage = new InputStage("Exit", InputStage.DEFAULT_WIDTH, InputStage.DEFAULT_HEIGHT);

        Button beginSaveBttn = createButton("Save", "beginSaveBttn", 300/2 - 100/2, 100, 100, 30);
        Button noSaveBttn = createButton("Quit", "noSaveBttn", 300/2 - 100/2, 140, 100, 30);

        beginSaveBttn.setOnAction(new EventHandler<ActionEvent>() {
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

        stage.addNodeToPane(beginSaveBttn);
        stage.addNodeToPane(noSaveBttn);
        stage.showAndWait();
    }

    private void letUserSaveConfig() {
        InputStage stage = new InputStage("Save Current Configuration", InputStage.DEFAULT_WIDTH, InputStage.DEFAULT_HEIGHT);

        stage.addTextToCenterX("Configuration File Name", 50);
        TextField fileNameField = stage.addTextFieldToCenterX(75);

        stage.addTextToCenterX("Author", 150);
        TextField authorField = stage.addTextFieldToCenterX(175);

        stage.addTextToCenterX("Description", 250);
        TextArea descriptionField  = stage.addTextAreaToCenterX(275);

        Button saveBttn = createButton("Save Configuration", "saveBttn", 300/2 - 100/2, 500, 100, 30);
        Button cancelSaveBttn = createButton("Cancel", "cancelSaveBttn", 300/2 - 100/2, 540, 100, 30);

        saveBttn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent t) {
                FileNameVerifier fileNameVerifier = new FileNameVerifier(fileNameField.getText(), controller.getModel().getClass());
                stage.removeErrorMessage();

                String errorMessage = fileNameVerifier.verify();
                if (errorMessage.equals(FileNameVerifier.NAME_IS_VALID)) {
                    stage.close();
                    controller.saveConfig(fileNameField.getText(), authorField.getText(), descriptionField.getText());
                    ensureUserWantsToQuit();
                }
                else {
                    stage.addErrorMessageToCenterX(errorMessage, 590);
                }
            }
        });
        cancelSaveBttn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent t) {
                stage.close();
                controller.start();
            }
        });

        stage.addNodeToPane(saveBttn);
        stage.addNodeToPane(cancelSaveBttn);

        stage.showAndWait();
    }

    private void ensureUserWantsToQuit() {
        InputStage stage = new InputStage("Resume or Quit", InputStage.DEFAULT_WIDTH, InputStage.DEFAULT_HEIGHT);

        stage.addTextToCenterX("Are you sure you want to Quit the simulation?", 150);

        Button resumeBttn = createButton("Resume", "resumeBttn", 300/2 - 100/2, 400, 100, 30);
        Button quitBttn = createButton("Quit", "quitBttn", 300/2 - 100/2, 440, 100, 30);

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

        stage.addNodeToPane(resumeBttn);
        stage.addNodeToPane(quitBttn);

        stage.showAndWait();
    }


}
