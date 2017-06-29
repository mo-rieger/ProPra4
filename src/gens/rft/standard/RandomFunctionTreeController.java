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
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
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
    private ToggleButton createSetToggleButton;
    @FXML
    private Button saveToButton;
    @FXML
    private Slider depthSlider;
    @FXML
    private Slider hueSlider;
    @FXML
    private ComboBox imagesComboBox;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize();
        imagesComboBox.getItems().addAll(1,2,3,4,5,6,7,8,9,10,11,12);
        saveToButton.setDisable(true);
        imagesComboBox.setDisable(true);
        //connect model and view via Bindings
        Bindings.bindBidirectional(textAreaWidth.textProperty(), model.getWidthProperty(), new NumberStringConverter());
        Bindings.bindBidirectional(textAreaHeight.textProperty(), model.getHeightProperty(), new NumberStringConverter());
        Bindings.bindBidirectional(textAreaSeed.textProperty(), model.getSeedProperty(), new NumberStringConverter());
        Bindings.bindBidirectional(depthSlider.valueProperty(), model.getDepthProperty());
        Bindings.bindBidirectional(hueSlider.valueProperty(), model.getHueProperty());
        Bindings.bindBidirectional(imagesComboBox.valueProperty(), model.getImagesCountProperty());
        
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
    @FXML
    private void handleCreateSet(ActionEvent event) {
        boolean isSelected = createSetToggleButton.selectedProperty().getValue();
        saveToButton.setDisable(!isSelected);
        imagesComboBox.setDisable(!isSelected);
        String text = isSelected ? "Create Set" : "Create Image";
        createSetToggleButton.setText(text);
        model.setCreateSet(isSelected);
        System.out.println("Set " + isSelected);
    }
    @FXML
    private void handleSaveToButton(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Path for RFT Images");
        chooser.setInitialDirectory(new File(model.getSavePath()));
        File selectedDirectory = chooser.showDialog(getStage());
        //System.out.println(selectedDirectory.getPath());
        if(selectedDirectory != null)
            model.setSavePath(selectedDirectory.getPath());
    }

    @Override
    public GenModel getModel() {
        return model;
    }    
}
