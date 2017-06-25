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
package gens.ca.wolfram;

import general.GenController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import general.GenModel;
import gens.ca.wolfram.WolframGenModel;

/**
 * FXML Controller class
 *
 * @author sebastian
 */
public class WolframGenController extends GenController {
    
    @FXML private TextField textFieldCells;  
    @FXML private TextField textFieldGens;
    @FXML private TextField textFieldCellSize;
    @FXML private TextField textFieldRule;
    @FXML private RadioButton radioButtonSingle1;
    @FXML private RadioButton radioButtonRandom;
    @FXML private ToggleGroup toggleGrpInitCond;
    @FXML private RadioButton radioButtonFixed;
    @FXML private RadioButton radioButtonWrapped;
    @FXML private ToggleGroup toggleGrpEdges;
    @FXML private Slider sliderPopDens;
    @FXML private TextField textFieldPopDens;
    

    WolframGenModel model;
    
    @Override
    public GenModel getModel() {
        return model;
    }
    
    @Override
    public void initialize() {
        super.initialize();
        model = new WolframGenModel();
        
        // display values from model
        textFieldCells.textProperty().setValue(
                String.valueOf(model.getCells()));
        textFieldGens.textProperty().setValue(
                String.valueOf(model.getGens()));
        textFieldCellSize.textProperty().setValue(
                String.valueOf(model.getCellSize()));
        textFieldRule.textProperty().setValue(
                String.valueOf(model.getRule()));
        
        radioButtonRandom.setUserData("random");
        radioButtonSingle1.setUserData("single");
        if(model.getRandomInit()) {
            radioButtonRandom.setSelected(true);
        } else {
            radioButtonSingle1.setSelected(true);
        }
        
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
                    // from WolframGenModel.setCells(..)
                    
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
        
        textFieldRule.focusedProperty().addListener((observableBoolean,
                oldValue, newValue) -> {
            if (!newValue){
                try {
                    String s = textFieldRule.textProperty().getValue();
                    int r = Integer.parseInt(s);
                    model.setRule(r);
                } catch (IllegalArgumentException ex) {
                    textFieldRule.textProperty().setValue(
                            String.valueOf(model.getRule()));
                    showInputAlert(ex.getMessage());
                }
            }
        });
        
        // Monitor choice regarding initial condition
        toggleGrpInitCond.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (toggleGrpInitCond.getSelectedToggle() != null) {
                    String choice = toggleGrpInitCond.getSelectedToggle().getUserData().toString();
                    model.setRandomInit(choice.equals("random"));
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

    }

}
