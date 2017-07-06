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
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.DragEvent;
import javafx.stage.DirectoryChooser;
import javafx.util.converter.NumberStringConverter;

/**
 * FXML Controller class
 *
 * @author Moritz Rieger
 */
public class RandomFunctionTreeController extends GenController {

    protected RandomFunctionTreeModel model;

    @FXML
    private TextField textFieldWidth;
    @FXML
    private TextField textFieldHeight;
    @FXML
    private TextField textFieldSeed;
    @FXML
    private Button buttonGenerate;
    @FXML
    private ToggleButton createSetToggleButton;
    @FXML
    private Button saveToButton;
    @FXML
    private Slider minDepthSlider;
    @FXML
    private Slider maxDepthSlider;
    @FXML
    private Slider hueSlider;
    @FXML
    private ComboBox imagesComboBox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize() {
        super.initialize();
        model = createModel();
        // set UI controls
        imagesComboBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        saveToButton.setDisable(true);
        imagesComboBox.setDisable(true);

        setValuesFromModel();
        addListeners();
        // connect model and view via Bindings where no validation is needed
        Bindings.bindBidirectional(minDepthSlider.valueProperty(), model.getMinDepthProperty());
        Bindings.bindBidirectional(maxDepthSlider.valueProperty(), model.getMaxDepthProperty());
        Bindings.bindBidirectional(hueSlider.valueProperty(), model.getHueProperty());
        Bindings.bindBidirectional(imagesComboBox.valueProperty(), model.getImagesCountProperty());
    }

    protected RandomFunctionTreeModel createModel() {
        return new RandomFunctionTreeModel();
    }

    private void validate() throws Exception {
        if (minDepthSlider.getValue() > maxDepthSlider.getValue()) {
            throw new IllegalArgumentException("min must be less than max depth!");
        }
    }

    @FXML
    private void handleGenerate(ActionEvent event) {
        try {
            validate();
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
    }

    @FXML
    private void handleSaveToButton(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Path for RFT Images");
        chooser.setInitialDirectory(new File(model.getSavePath()));
        File selectedDirectory = chooser.showDialog(getStage());
        if (selectedDirectory != null) {
            model.setSavePath(selectedDirectory.getPath());
        }
    }

    @Override
    public GenModel getModel() {
        return model;
    }

    private void setValuesFromModel() {
        textFieldWidth.textProperty().setValue(
                String.valueOf(model.getWidthProperty().intValue()));
        textFieldHeight.textProperty().setValue(
                String.valueOf(model.getHeightProperty().intValue()));
        textFieldSeed.textProperty().setValue(
                String.valueOf(model.getSeedProperty().intValue()));
    }
    /**
     * add Listeners to set model with new userinput and validate input
     */
    private void addListeners() {
        textFieldWidth.focusedProperty().addListener((observableBoolean,
                oldValue, newValue) -> {
            if (!newValue) { // newValue=0 means no focus -> if no longer focused
                try {
                    String s = textFieldWidth.textProperty().getValue();
                    int width = Integer.parseInt(s);
                    model.setWidth(width);
                } catch (IllegalArgumentException ex) {
                    // display last valid value for Width from model
                    textFieldWidth.textProperty().setValue(
                            String.valueOf(model.getWidthProperty().intValue()));
                    showInputAlert(ex.getMessage());
                }
            }
        });

        textFieldHeight.focusedProperty().addListener((observableBoolean,
                oldValue, newValue) -> {
            if (!newValue) {
                try {
                    String s = textFieldHeight.textProperty().getValue();
                    int height = Integer.parseInt(s);
                    model.setHeight(height);
                } catch (IllegalArgumentException ex) {
                    textFieldHeight.textProperty().setValue(
                            String.valueOf(model.getHeightProperty().intValue()));
                    showInputAlert(ex.getMessage());
                }
            }
        });
        textFieldSeed.focusedProperty().addListener((observableBoolean,
                oldValue, newValue) -> {
            if (!newValue) {
                try {
                    String s = textFieldSeed.textProperty().getValue();
                    int seed = Integer.parseInt(s);
                    model.setSeed(seed);
                } catch (IllegalArgumentException ex) {
                    textFieldSeed.textProperty().setValue(
                            String.valueOf(model.getSeedProperty().intValue()));
                    showInputAlert(ex.getMessage());
                }
            }
        });
    }
}
