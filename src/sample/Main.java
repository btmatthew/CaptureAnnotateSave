package sample;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;


/**
 * JavaFXDraw
 *
 * Simple drawing application written in JavaFX
 * @author Juneau
 */
public class Main extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }

    @Override
    public void start(Stage primaryStage) {

        StackPane root = new StackPane();
        Screen screen = Screen.getPrimary();

        Rectangle2D rect = screen.getVisualBounds();
        Canvas canvas = new Canvas(rect.getWidth()-10,
                rect.getHeight()-70 );

        final GraphicsContext graphicsContext =
                canvas.getGraphicsContext2D();
        graphicsContext.drawImage(copyBackground((int)rect.getMinX(),(int)rect.getMinY(), (int)rect.getWidth(),(int)rect.getHeight()),0,0);

        final Button saveCapture = new Button("Save Image");
        saveCapture.setOnAction(actionEvent-> {
            try {
                WritableImage wi = new WritableImage((int)graphicsContext.getCanvas().getWidth(), (int)graphicsContext.getCanvas().getHeight());
                graphicsContext.getCanvas().snapshot(null,wi);
                BufferedImage bi =SwingFXUtils.fromFXImage((Image)wi, null);
                File outputfile = new File("saved"+System.currentTimeMillis()+".png");
                ImageIO.write(bi, "png", outputfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        saveCapture.setTranslateX(10);
        final Button resetButton = new Button("Reset");

        resetButton.setOnAction(actionEvent-> {
            graphicsContext.clearRect(1, 1,
                    graphicsContext.getCanvas().getWidth()-2,
                    graphicsContext.getCanvas().getHeight()-2);

            primaryStage.setIconified(true);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(primaryStage.isMaximized());
            graphicsContext.drawImage(copyBackground((int)rect.getMinX(),(int)rect.getMinY(), (int)rect.getWidth(),(int)rect.getHeight()),0,0);

            primaryStage.setIconified(false);
        });

        resetButton.setTranslateX(10);

        // Set up the pen color chooser
        ChoiceBox colorChooser = new ChoiceBox(
                FXCollections.observableArrayList(
                        "Black", "Blue", "Red", "Green", "Brown", "Orange"
                ));
        // Select the first option by default
        colorChooser.getSelectionModel().selectFirst();

        colorChooser.getSelectionModel().
                selectedIndexProperty().addListener(
                (ChangeListener)(ov, old, newval) -> {
                    Number idx = (Number)newval;
                    Color newColor;
                    switch(idx.intValue()){
                        case 0: newColor = Color.BLACK;
                            break;
                        case 1: newColor = Color.BLUE;
                            break;
                        case 2: newColor = Color.RED;
                            break;
                        case 3: newColor = Color.GREEN;
                            break;
                        case 4: newColor = Color.BROWN;
                            break;
                        case 5: newColor = Color.ORANGE;
                            break;
                        default: newColor = Color.BLACK;
                            break;
                    }
                    graphicsContext.setStroke(newColor);

                });
        colorChooser.setTranslateX(5);

        ChoiceBox sizeChooser = new ChoiceBox(
                FXCollections.observableArrayList(
                        "1", "2", "3", "4", "5"
                ));
        // Select the first option by default
        sizeChooser.getSelectionModel().selectFirst();

        sizeChooser.getSelectionModel()
                .selectedIndexProperty().addListener(
                (ChangeListener)(ov, old, newval) -> {
                    Number idx = (Number)newval;

                    switch(idx.intValue()){
                        case 0: graphicsContext.setLineWidth(1);
                            break;
                        case 1: graphicsContext.setLineWidth(2);
                            break;
                        case 2: graphicsContext.setLineWidth(3);
                            break;
                        case 3: graphicsContext.setLineWidth(4);
                            break;
                        case 4: graphicsContext.setLineWidth(5);
                            break;
                        default: graphicsContext.setLineWidth(1);
                            break;
                    }
                });
        sizeChooser.setTranslateX(5);

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                (MouseEvent event) -> {
                    graphicsContext.beginPath();
                    graphicsContext.moveTo(
                            event.getX(), event.getY()
                    );
                    graphicsContext.stroke();
                });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                (MouseEvent event) -> {
                    graphicsContext.lineTo(
                            event.getX(), event.getY()
                    );
                    graphicsContext.stroke();
                });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
                (MouseEvent event) -> {
                    // DO NOTHING
                });

        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(colorChooser,
                sizeChooser,
                resetButton,saveCapture);

        initDraw(graphicsContext, canvas.getLayoutX(),
                canvas.getLayoutY());

        BorderPane container = new BorderPane();
        container.setTop(buttonBox);

        container.setCenter(canvas);

        root.getChildren().add(container);

        Scene scene = new Scene(root,
                rect.getHeight(), rect.getWidth());
        primaryStage.setTitle("CaptureTalk");
        primaryStage.setScene(scene);
        primaryStage.setX(rect.getMinX());
        primaryStage.setY(rect.getMinY());
        primaryStage.setWidth(rect.getWidth());
        primaryStage.setHeight(rect.getHeight());

        primaryStage.show();
    }


    private void initDraw(GraphicsContext gc,
                          double x, double y){
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();

        gc.fill();

        gc.strokeRect(
                x,             //x of the upper left corner
                y,             //y of the upper left corner
                canvasWidth,   //width of the rectangle
                canvasHeight); //height of the rectangle

    }
    private javafx.scene.image.Image copyBackground(int X,int Y,int W,int H) {

        try {
            java.awt.Robot robot = new java.awt.Robot();
            java.awt.image.BufferedImage image = robot.createScreenCapture(new java.awt.Rectangle(X, Y, W, H));

            return SwingFXUtils.toFXImage(image, null);
        } catch (java.awt.AWTException e) {
            System.out.println("The robot of doom strikes!");
            e.printStackTrace();

            return null;
        }
    }
}   