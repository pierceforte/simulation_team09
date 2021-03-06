package cellsociety;
import cellsociety.backend.*;
import cellsociety.config.ConfigReader;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class provides the main method to launch the application, and it provides the functionality to
 * continually step, effectively "playing" the application.
 *
 * Note that if this project had not been ended early due to COVID-19, a high priority next step would have
 * been to refactor this class.
 *
 * @author Pierce Forte
 */
public class MainController extends Application {
    public static final String STYLESHEET = "style.css";
    public static final String TITLE = "Cell Society";
    public static final int WIDTH = 600;
    public static final int HEIGHT = 720;
    public static final Paint BACKGROUND = Color.BEIGE;
    public static final int FRAMES_PER_SECOND = 5;
    public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    public static final String INTRO_SCREEN_IMG_NAME = "StartScreen.jpg";
    public static final int DEFAULT_FONT_SIZE = 5;
    public static final String DEFAULT_FONT = "Verdana";
    public static final DecimalFormat df2 = new DecimalFormat("#.##");

    private Group myRoot = new Group();
    private Scene myScene;
    private Stage myStage;
    private Pane myIntroPane;
    private Timeline myAnimation;
    private SimController mySimController;
    private boolean isMySimulationActive = false;

    @Override
    public void start(Stage stage) {
        /*

        USE THE FOLLOWING LINE TO GENERATE A RANDOM CONFIG

         */
        //printRandomConfig(100,100,3);
        Image introScreenImage = new Image(getClass().getClassLoader().getResourceAsStream(INTRO_SCREEN_IMG_NAME));
        ImageView introScreenNode = new ImageView(introScreenImage);
        introScreenNode.setFitHeight(HEIGHT);
        introScreenNode.setFitWidth(WIDTH);
        myIntroPane = new Pane();
        myIntroPane.setId("introPane");
        Scene introScene = new Scene(myIntroPane, WIDTH, HEIGHT);
        introScene.getStylesheets().add(STYLESHEET);
        myIntroPane.getChildren().add(introScreenNode);

        SimSelector simSelector = new SimSelector(this);
        Button simSelectorButton = simSelector.createSelectorButton();
        myIntroPane.getChildren().add(simSelectorButton);

        myStage = stage;
        myStage.setScene(introScene);
        myStage.setTitle(TITLE);
        myStage.show();
        setMyAnimation();
    }

    /**
     * Set whether a simulation is active or not.
     * @param activeStatus Whether a simulation is active or not
     */
    public void setMySimulationActiveStatus(boolean activeStatus) {
        isMySimulationActive = activeStatus;
    }

    /**
     * Set the stage for the application.
     * @param stage The stage to be set
     */
    public void setMyStage(Stage stage) {
        myStage = stage;
    }

    /**
     * Get the stage for the application.
     * @return The stage for the application
     */
    public Stage getMyStage() {
        return myStage;
    }

    /**
     * Set the animation for the application.
     */
    public void setMyAnimation() {
        KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> step(SECOND_DELAY));
        myAnimation = new Timeline();
        myAnimation.setCycleCount(Timeline.INDEFINITE);
        myAnimation.getKeyFrames().add(frame);
        myAnimation.play();
    }

    /**
     * Change the animation speed.
     * @param change The factor to change the speed by
     */
    public void changeAnimationSpeed(double change){
        myAnimation.setRate(FRAMES_PER_SECOND * change);
    }

    /**
     * Start a simulation.
     * @param simTypeClassName The type of simulation
     * @param csvFilePath The path to the initial CSV config
     * @param <T> The type of simulation's class
     */
    public <T extends SimModel> void beginSimulation(Class<T> simTypeClassName, String csvFilePath) {
        Scene simulationScene = setupSimulation(simTypeClassName, csvFilePath);
        myStage.setScene(simulationScene);
        isMySimulationActive = true;
    }

    /**
     * Step through and update the application.
     * @param elapsedTime The time that each step takes
     */
    public void step(double elapsedTime) {
        try {
            if (isMySimulationActive) {
                if (mySimController.isEnded()) {
                    returnToIntroScreen();
                } else {
                    mySimController.update(false);
                }
            }
        } catch (OutOfMemoryError e) {
            //logError(e);
            System.out.println("Caught mem error");
            mySimController.setIsEnded(true);
        }
    }

    /**
     * Get the current SimController.
     * @return The current SimController
     */
    public SimController getCurSimController() {
        return mySimController;
    }

    private <T extends SimModel> Scene setupSimulation(Class<T> simTypeClassName, String csvFilePath) {
        myRoot = new Group();
        String [] csvFilePathFromResources = csvFilePath.split("/");
        String validCsvFilePath = String.join("/",
                Arrays.copyOfRange(csvFilePathFromResources, csvFilePathFromResources.length-4, csvFilePathFromResources.length));
        mySimController = new SimController(simTypeClassName, this, validCsvFilePath);
        myRoot.getChildren().add(mySimController.getViewRoot());
        myScene = new Scene(myRoot, WIDTH, HEIGHT, BACKGROUND);
        myScene.setOnKeyPressed(e -> handleKeyInput(e.getCode(), myRoot));
        myStage.setTitle(mySimController.getSimResources().getString("Title"));

        //testing adding of css styles
        myScene.getStylesheets().add(STYLESHEET);
        return myScene;
    }

    private void returnToIntroScreen() {
        isMySimulationActive = false;
        myRoot.getChildren().clear();
        myAnimation.stop();
        start(myStage);
    }

    private void handleKeyInput(KeyCode code, Group root) {

    }

    public static void main(String[] args)
    {
        launch(args);
    }

    private void printRandomConfig(int rows, int cols, double vals) {
        List<List<Integer>> list = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            list.add(new ArrayList());
            for (int j = 0; j < cols; j++) {
                int randomInteger = (int) (vals * Math.random());
                list.get(i).add(randomInteger);
            }
        }

        try {
            PrintWriter pw = new PrintWriter("randomConfig.csv");
            pw.println(rows + ConfigReader.SPLIT_REGEX + cols);

            for (int row = 0; row < rows; row++) {
                if (cols == 0) {
                    break;
                }
                String line = "" + list.get(row).get(0);
                for (int col = 1; col < cols; col++) {
                    line += ConfigReader.SPLIT_REGEX + list.get(row).get(col);
                }
                pw.println(line);
            }
            pw.close();
        } catch (FileNotFoundException e) {
            // TODO: handle exception properly
            e.printStackTrace();
            //logError(e);
            e.printStackTrace();
            System.exit(0);
        }
        System.exit(0);
    }
}
