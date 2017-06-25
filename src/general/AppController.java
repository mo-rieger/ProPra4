/*
 * The MIT License
 *
 * Copyright 2017 Christoph Baumhardt.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package general;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.imageio.ImageIO;

/**
 * FXML Controller class for AppView.fxml
 *
 * @author Christoph Baumhardt
 */
public class AppController {
    
    @FXML
    private MenuItem menuItemSaveImage;     
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Label statusLabel;   
    
    private Canvas canvas; // the main canvas of the App    
    private Stage genStage; // a window for a selected generator
    private GenController genController; // the controller associated with the selected generator

    
    @FXML
    public void initialize() {
        statusLabel.textProperty().setValue("No generator selected.");
        menuItemSaveImage.setDisable(true); // cannot save if nothing generated
        menuItemSaveImage.setOnAction(e -> saveImage("generated_image.png"));
    }
    
    @FXML
    private void showSimpleGenView() {
        showSpecializedGenView("Simple Generator",
                "/gens/basicexample1/SimpleGenView.fxml");
    }
    
    @FXML
    private void showSimpleAnimationGenView() {
        showSpecializedGenView("Simple Animation Generator",
                "/gens/basicexample2/SimpleAnimationGenView.fxml");
    }

    @FXML
    private void showWolframGenView() {
        showSpecializedGenView("Wolfram Generator",
                "/gens/ca/wolfram/WolframGenView.fxml");
    }
    
    @FXML
    private void showGameOfLifeGenView() {
        showSpecializedGenView("Game of Life Generator",
                "/gens/ca/gameoflife1/GameOfLifeGenView.fxml");
    }
    
    @FXML
    private void showWolframEvolvedGenView() {
        showSpecializedGenView("Wolfram Evolved Generator",
                "/gens/ca/wolframevolved/WolframEvolvedGenView.fxml");
    }
    
    @FXML
    private void showCookieMonsterGenView() {
        showSpecializedGenView("Cookie Monster Generator",
                "/gens/ca/cookiemonster/CookieMonsterGenView.fxml");
    }
    
    @FXML
    private void showLindenmayerGenView() {
        showSpecializedGenView("Standard Lindenmayer System",
                "/gens/lsystems/standard/LindenmayerSystemView.fxml");
    }
    
    @FXML
    private void showProbabilisticLindenmayerGenView() {
        showSpecializedGenView("Probabilistic Lindenmayer System",
                "/gens/lsystems/probabilistic/ProbabilisticLindenmayerSystemView.fxml");
    }
    
    /**
     * Displays a new view of a specialized Generator.
     * 
     * @param genName  The name associated with the generator
     * @param pathToFXMLFile The path to the fxml file of the wanted view
     */    
    private void showSpecializedGenView(String genName, String pathToFXMLFile){
        if (genController != null && 
                genController.getModel().getGenName().equals(genName)){
            // window for specialized Generator exists already -> no creation
            genStage.requestFocus();
        } else {
            try {
                if (genController != null) {
                    // a window for a different specialized Generator exists
                    // already -> close it first
                    genController.getModel().interruptBackgroundThread();
                    genStage.close();
                }
                // create new view
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(App.class.getResource(pathToFXMLFile));
                Parent content = loader.load();
                
                // let the app view listen to the GenState of the newly
                // created model (to update statusbar and display generated 
                // image when it is finished)
                genController = loader.getController();
                genController.getModel().genStateProperty().
                        addListener(new ChangeListener<GenState>(){
                    @Override
                    public void changed(ObservableValue<? extends 
                            GenState> observable, GenState oldValue,
                            GenState newValue) {
                        // Make sure the following runs always inside JavaFX
                        // Application Thread (even if started from another
                        // Thread), as UI changes need to be done in there
                        Platform.runLater(() -> {
                            if (newValue != GenState.ITERATION_READY) {
                                statusLabel.textProperty().setValue(
                                        newValue.getDescription());
                            }                           
                            if ((newValue == GenState.FINISHED_READY ||
                                    newValue == GenState.ITERATION_READY) &&
                                    genController != null) {
                                canvas = genController.getModel().
                                        getCanvas();
                                scrollPane.setContent(canvas);
                                menuItemSaveImage.setDisable(false);
                            }
                        });                   
                    }

                });
                
                statusLabel.textProperty().setValue(genController.getModel().getStateDescription());
                genStage = new Stage();
                genStage.setTitle(genController.getModel().getGenName());
                genStage.setOnCloseRequest((WindowEvent e) -> {
                    genController.getModel().interruptBackgroundThread();
                    genController = null;
                    statusLabel.textProperty().setValue(
                            "No generator selected.");
                });                 
                genStage.setScene(new Scene(content));
                genStage.setResizable(false);
                genStage.show();                
                
            }  catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    private void handleExit() {
        Platform.exit(); // close all windows of application gracefully
    }

    /**
     * Creates a dialog to save the App canvas as png.
     * 
     * @param fileName  The initial filename displayed in the save image dialog
     */
    public void saveImage(String fileName){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.setInitialFileName(fileName);
        fileChooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        );  
        FileChooser.ExtensionFilter extFilter 
                = new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);

        // show Save Image dialog and process user input
        File file = fileChooser.showSaveDialog(canvas.getScene().getWindow());
        if (file != null) {
            try {
                WritableImage writableImage = canvas.snapshot(null, null);
                RenderedImage renderedImage = 
                        SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", file);
                
                statusLabel.textProperty().setValue("Saved!");
                
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }            
        } // else file save was cancelled by user
    }    
    
}
