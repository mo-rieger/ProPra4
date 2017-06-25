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
package gens.ca.cookiemonster;

import general.GenController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import general.GenModel;
import general.GenState;

/**
 * FXML Controller class
 *
 * @author sebastian
 */
public class CookieMonsterGenController extends GenController {

    @FXML
    private Button buttonStop;
    @FXML
    private TextField textFieldCells;
    @FXML
    private TextField textFieldGens;
    @FXML
    private TextField textFieldCellSize;
    @FXML
    private TextField textFieldStep;
    @FXML
    private Slider sliderStates;
    @FXML
    private TextField textFieldStates;

    CookieMonsterGenModel model;

    @Override
    public GenModel getModel() {
        return model;
    }

    @Override
    public void handleGenerate() {
        // Lock certain parameter input elements
        textFieldCells.setEditable(false);
        textFieldGens.setEditable(false);
        textFieldStates.setEditable(false);
        sliderStates.setDisable(true);
        super.handleGenerate();
    }
    
    @Override
    public void initialize() {
        super.initialize();
        model = new CookieMonsterGenModel();
        
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
                            textFieldCells.setEditable(true);
                            textFieldStates.setEditable(true);
                            sliderStates.setDisable(false);
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

        sliderStates.setValue(model.getStates());
        textFieldStates.setText(Integer.toString((int) sliderStates.getValue()));

        // change model if user changes something on the view
        textFieldCells.focusedProperty().addListener((observableBoolean,
                oldValue, newValue) -> {
            if (!newValue) { // newValue=0 means no focus -> if no longer focused
                try {
                    String s = textFieldCells.textProperty().getValue();
                    int c = Integer.parseInt(s);
                    model.setCells(c);
                } catch (IllegalArgumentException ex) {
                    // catches both the possible NumberFormatException from
                    // parseInt() as well as the possible IllegalArgumentExcept.
                    // from GameOfLifeGeneratorModel.setCells(..)

                    // display last valid value for cells from model
                    textFieldCells.textProperty().setValue(
                            String.valueOf(model.getCells()));
                    showInputAlert(ex.getMessage());
                }
            }
        });

        textFieldGens.focusedProperty().addListener((observableBoolean,
                oldValue, newValue) -> {
            if (!newValue) {
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
            if (!newValue) {
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
            if (!newValue) {
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

        // Monitor slider for choice of impact event interval
        sliderStates.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                model.setStates(new_val.intValue());
                // Update text field accordingly
                textFieldStates.textProperty().setValue(
                        String.valueOf(new_val.intValue()));
            }
        });

        // Value for impact event interval can also be modified via entry in text field
        textFieldStates.focusedProperty().addListener((observableBoolean,
                oldValue, newValue) -> {
            if (!newValue) {
                try {
                    String s = textFieldStates.textProperty().getValue();
                    int st = Integer.parseInt(s);
                    model.setStates(st);
                    // Update slider accordingly
                    sliderStates.setValue(st);
                } catch (IllegalArgumentException ex) {
                    textFieldStates.textProperty().setValue(
                            String.valueOf(model.getStates()));
                    showInputAlert(ex.getMessage());
                }
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

}
