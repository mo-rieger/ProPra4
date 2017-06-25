/*
 * The MIT License
 *
 * Copyright 2017 sebastian.
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
package gens.ca.gameoflife1;

import general.GenController;
import java.io.IOException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import general.GenModel;
import general.GenState;

/**
 * FXML Controller class
 *
 * @author sebastian
 */
public class GameOfLifeGenController extends GenController implements HasMouseInputController {
    
    @FXML private Button buttonMouseInput;
    @FXML private Button buttonStop;
    @FXML private TextField textFieldCells;  
    @FXML private TextField textFieldGens;
    @FXML private TextField textFieldCellSize;
    @FXML private TextField textFieldStep;
    @FXML private RadioButton radioButtonFixed;
    @FXML private RadioButton radioButtonWrapped;
    @FXML private ToggleGroup toggleGrpEdges;
    @FXML private Slider sliderPopDens;
    @FXML private TextField textFieldPopDens;
    
    private Stage mouseInputStage;
    private MouseInputController mouseInputController;
    
    GameOfLifeGenModel model;
    
    
    public void closeMouseInputWindow() {
        mouseInputController = null;
        mouseInputStage.close();
        mouseInputStage = null;
        model.setRandomInit(true);
    }
    
    // Deliver initial generation to model, for use with mouse input
    public void setInitGen(boolean[][] initGen) {
        if(model!=null) {
            model.setInitGen(initGen);
        }
    }
    
    @Override
    public GenModel getModel() {
        return model;
    }
    
    @Override
    public void handleGenerate() {
        // Lock certain parameter input elements
        buttonMouseInput.setDisable(true);
        textFieldCells.setEditable(false);
        textFieldGens.setEditable(false);
        textFieldPopDens.setEditable(false);
        sliderPopDens.setDisable(true);
        radioButtonFixed.setDisable(true);
        radioButtonWrapped.setDisable(true);
        super.handleGenerate();
    }
    
    @Override
    public void initialize() {
        super.initialize();
        model = new GameOfLifeGenModel();
        
        model.genStateProperty().addListener(new ChangeListener<GenState>() {
            @Override
            public void changed(ObservableValue<? extends GenState> observable, GenState oldValue,
                    GenState newValue) {
                // As soon as calculations have terminated, parameters can
                // be modified again
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (newValue == GenState.FINISHED_READY) {
                            buttonMouseInput.setDisable(false);
                            textFieldCells.setEditable(true);
                            textFieldGens.setEditable(true);
                            textFieldPopDens.setEditable(true);
                            sliderPopDens.setDisable(false);
                            radioButtonFixed.setDisable(false);
                            radioButtonWrapped.setDisable(false);
                        }
                    }
                });
            }

        });
        
        // display values from model
        textFieldCells.textProperty().setValue(
                String.valueOf(model.getCells()));
        textFieldGens.textProperty().setValue(
                String.valueOf(model.getGens()));
        textFieldCellSize.textProperty().setValue(
                String.valueOf(model.getCellSize()));
        textFieldStep.textProperty().setValue(
                String.valueOf(model.getStep()));
        
        radioButtonFixed.setUserData("fixed");
        radioButtonWrapped.setUserData("wrapped");
        if(model.getEdgesWrapped()) {
            radioButtonWrapped.setSelected(true);
        } else {
            radioButtonFixed.setSelected(true);
        }
        
        sliderPopDens.setValue(model.getPopulationDensity());
        textFieldPopDens.setText(Double.toString(sliderPopDens.getValue()));
        
        // change model if user changes something on the view
        
        textFieldCells.focusedProperty().addListener((observableBoolean,
                oldValue, newValue) -> {
            if (!newValue){ // newValue=0 means no focus -> if no longer focused
                try {
                    String s = textFieldCells.textProperty().getValue();
                    int c = Integer.parseInt(s);
                    model.setCells(c);
                } catch (IllegalArgumentException ex) {
                    // catches both the possible NumberFormatException from
                    // parseInt() as well as the possible IllegalArgumentExcept.
                    // from GameOfLifeGenModel.setCells(..)
                    
                    // display last valid value for cells from model
                    textFieldCells.textProperty().setValue(
                            String.valueOf(model.getCells()));
                    showInputAlert(ex.getMessage());
                }
            }
        });

        textFieldGens.focusedProperty().addListener((observableBoolean,
                oldValue, newValue) -> {
            if (!newValue){
                try {
                    String s = textFieldGens.textProperty().getValue();
                    int g = Integer.parseInt(s);
                    model.setGens(g);
                } catch (IllegalArgumentException ex) {
                    textFieldGens.textProperty().setValue(
                            String.valueOf(model.getGens()));
                    showInputAlert(ex.getMessage());
                }
            }
        });
        
        textFieldCellSize.focusedProperty().addListener((observableBoolean,
                oldValue, newValue) -> {
            if (!newValue){
                try {
                    String s = textFieldCellSize.textProperty().getValue();
                    int cs = Integer.parseInt(s);
                    model.setCellSize(cs);
                } catch (IllegalArgumentException ex) {
                    textFieldCellSize.textProperty().setValue(
                            String.valueOf(model.getCellSize()));
                    showInputAlert(ex.getMessage());
                }
            }
        });
        
        textFieldStep.focusedProperty().addListener((observableBoolean,
                oldValue, newValue) -> {
            if (!newValue){
                try {
                    String s = textFieldStep.textProperty().getValue();
                    long st = Long.parseLong(s);
                    model.setStep(st);
                } catch (IllegalArgumentException ex) {
                    textFieldStep.textProperty().setValue(
                            String.valueOf(model.getStep()));
                    showInputAlert(ex.getMessage());
                }
            }
        });
        
        // Monitor choice regarding wrapping of edges
        toggleGrpEdges.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (toggleGrpEdges.getSelectedToggle() != null) {
                    String choice = toggleGrpEdges.getSelectedToggle().getUserData().toString();
                    model.setEdgesWrapped(choice.equals("wrapped"));
                }
            }
        });
        
        // Monitor slider for choice of population density
        sliderPopDens.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
                    model.setPopulationDensity(new_val.doubleValue());
                    // Update text field accordingly
                    textFieldPopDens.setText(String.format("%.2f", new_val));
            }
        });
        
        // Value for population density can also be modified via entry in text field
        textFieldPopDens.focusedProperty().addListener((observableBoolean,
                oldValue, newValue) -> {
            if (!newValue){
                try {
                    String s = textFieldPopDens.textProperty().getValue();
                    double d = Double.parseDouble(s);
                    model.setPopulationDensity(d);
                    // Update slider accordingly
                    sliderPopDens.setValue(d);
                } catch (IllegalArgumentException ex) {
                    textFieldPopDens.textProperty().setValue(
                            String.valueOf(model.getPopulationDensity()));
                    showInputAlert(ex.getMessage());
                }
            }
        });
        
        // Mouse input is chosen via respective button
        // Button can be activated by pressing enter key
        buttonMouseInput.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent ke) -> {
            if (ke.getCode() == KeyCode.ENTER) {
                handleMouseInput();
            }            
        });
        
        // Generator can be stopped to enable modification of parameters
        // Button can be activated by pressing enter key
        buttonStop.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent ke) -> {
            if (ke.getCode() == KeyCode.ENTER) {
                handleStop();
            }            
        }); 
    }
    
    public void handleMouseInput() {
        handleMouseInput("/gens/ca/gameoflife1/MouseInputView.fxml");
    }

    // Mouse input is retrieved from a seperate window
    private void handleMouseInput(String pathToFXMLFile){
        // Set parameters for use with mouse input
        textFieldCells.textProperty().setValue("60");
        textFieldCellSize.textProperty().setValue("10");
        model.setCells(60);
        model.setCellSize(10);
        model.setRandomInit(false);
        
        if (mouseInputController != null) {
            // window for mouse input exists already -> no creation
            mouseInputStage.requestFocus();
        } else {
            try {
                // create new view
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource(pathToFXMLFile));
                Parent content = loader.load();
                
                // Retrieve controller
                mouseInputController = loader.getController();
                mouseInputController.setGeneratorController(this);
                
                // Set up scene
                mouseInputStage = new Stage();
                mouseInputStage.initOwner(buttonMouseInput.getScene().getWindow());
                mouseInputStage.setTitle("Mouse Input");
                mouseInputStage.setOnCloseRequest((WindowEvent e) -> {
                    mouseInputController = null;
                });                 
                mouseInputStage.setScene(new Scene(content));
                mouseInputStage.setResizable(false);
                mouseInputStage.show();
                
            }  catch (IOException e) {
                System.out.println("Error initializing mouse input");
            }
        }
    }

    /*@Override
    // Mouse input window is to be closed
    public void closeSpawnedWindows() {
        if(mouseInputController!=null) 
            closeMouseInputWindow();
    }*/
            

    
}
