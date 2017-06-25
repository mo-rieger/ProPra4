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
package gens.basicexample1;

import general.GenController;
import general.GenModel;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Christoph Baumhardt
 */
public class SimpleGenController extends GenController {

    @FXML private TextField textFieldWidth;  
    @FXML private TextField textFieldHeight;
    
    SimpleGenModel model;

    
    @Override
    public GenModel getModel() {
        return model;
    }
    
    /**
     * This automatically called method creates a new SimpleGenModel and 
     * links it with its view, so that changes on the view get reflected in the
     * model (if they are allowed in the model). 
     * 
     * Note that the view does not get updated if the model is changed from
     * anywhere else besides the very view.
     * 
     */      
    @Override
    public void initialize() {
        super.initialize(); // activate buttonGenerate on Enter
        
        model = new SimpleGenModel();
        
        // display values from model
        textFieldWidth.textProperty().setValue(
                String.valueOf(model.getWidth()));
        textFieldHeight.textProperty().setValue(
                String.valueOf(model.getHeight()));
        
        // change model if user changes something on the view
        
        textFieldWidth.focusedProperty().addListener((observableBoolean,
                oldValue, newValue) -> {
            if (!newValue){ // newValue=0 means no focus -> if no longer focused
                try {
                    String s = textFieldWidth.textProperty().getValue();
                    int w = Integer.parseInt(s);
                    model.setWidth(w);
                } catch (IllegalArgumentException ex) {
                    // catches both the possible NumberFormatException from
                    // parseInt() as well as the possible IllegalArgumentExcept.
                    // from SimpleGenModel.setWidth(..)
                    
                    // display last valid value for width from model
                    textFieldWidth.textProperty().setValue(
                            String.valueOf(model.getWidth()));
                    showInputAlert("Width requires an integer value between 1" +
                            " and 3000.");
                }
            }
        });

        textFieldHeight.focusedProperty().addListener((observableBoolean,
                oldValue, newValue) -> {
            if (!newValue){ // newValue=0 means no focus -> if no longer focused
                try {
                    String s = textFieldHeight.textProperty().getValue();
                    int h = Integer.parseInt(s);
                    model.setHeight(h);
                } catch (IllegalArgumentException ex) {
                    // catches both the possible NumberFormatException from
                    // parseInt() as well as the possible IllegalArgumentExcept.
                    // from SimpleGenModel.setHeight(..)
                    
                    // display last valid value for width from model
                    textFieldHeight.textProperty().setValue(
                            String.valueOf(model.getHeight()));
                    showInputAlert("Heigth requires an integer value between 1"+
                            " and 3000.");
                }
            }
        });
        
        // NOTE: The view does not reflect changes to the model that are done
        //       outside the given view.
    }

}
