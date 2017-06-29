/*
 * The MIT License
 *
 * Copyright 2017 Moritz Rieger.
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
package gens.rft.standard;

import general.GenController;
import general.GenModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.util.converter.NumberStringConverter;

/**
 * FXML Controller class
 *
 * @author Moritz Rieger
 */
public class RandomFunctionTreeController extends GenController implements Initializable{
    
    protected RandomFunctionTreeModel model = new RandomFunctionTreeModel();

    @FXML
    private TextField textAreaWidth;
    @FXML
    private TextField textAreaHeight;
    @FXML
    private TextField textAreaSeed;
    @FXML
    private Button buttonGenerate;
    @FXML
    private Slider depthSlider;
    @FXML
    private Slider hueSlider;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize();
        Bindings.bindBidirectional(textAreaWidth.textProperty(), model.getWidthProperty(), new NumberStringConverter());
        Bindings.bindBidirectional(textAreaHeight.textProperty(), model.getHeightProperty(), new NumberStringConverter());
        Bindings.bindBidirectional(textAreaSeed.textProperty(), model.getSeedProperty(), new NumberStringConverter());
        Bindings.bindBidirectional(depthSlider.valueProperty(), model.getDepthProperty());
        Bindings.bindBidirectional(hueSlider.valueProperty(), model.getHueProperty());
    }    

    @FXML
    private void handleGenerate(ActionEvent event) {
         try {
            model.validate();
            super.handleGenerate();
        } catch (Exception exception) {
            showAlert("Error", exception.getMessage());
        }
    }

    @Override
    public GenModel getModel() {
        return model;
    }

    @FXML
    private void handleDepthSlider(MouseEvent event) {
    }
    
}
