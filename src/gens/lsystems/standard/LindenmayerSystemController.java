/*
 * The MIT License
 *
 * Copyright 2017 jzentner.
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
package gens.lsystems.standard;

import general.GenController;
import general.GenModel;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;

/**
 *
 * @author jzentner
 */
public class LindenmayerSystemController extends GenController {

    protected LindenmayerSystemModel model = new LindenmayerSystemModel();

    @FXML
    private ComboBox exampleDropdown;

    @FXML
    private TextField widthField;

    @FXML
    private TextField heightField;

    @FXML
    private TextField alphabetField;

    @FXML
    private TextField axiomField;

    @FXML
    private TextField rotationAngleField;

    @FXML
    private TextField startingAngleField;

    @FXML
    private TextField iterationsField;

    @FXML
    private TextArea rulesArea;

    @Override
    public GenModel getModel() {
        return model;
    }

    @Override
    public void initialize() {
        super.initialize();
        exampleDropdown.setItems(model.getExampleKeys());
        addChangeListenerToDropdown();
        alphabetField.textProperty().bindBidirectional(model.getAlphabetProperty());
        axiomField.textProperty().bindBidirectional(model.getAxiomProperty());
        rulesArea.textProperty().bindBidirectional(model.getRulesProperty());
        Bindings.bindBidirectional(rotationAngleField.textProperty(), model.getRotationProperty(), new NumberStringConverter());
        Bindings.bindBidirectional(startingAngleField.textProperty(), model.getStartingAngleProperty(), new NumberStringConverter());
        Bindings.bindBidirectional(iterationsField.textProperty(), model.getIterationsProperty(), new NumberStringConverter());
        Bindings.bindBidirectional(widthField.textProperty(), model.getWidthProperty(), new NumberStringConverter());
        Bindings.bindBidirectional(heightField.textProperty(), model.getHeightProperty(), new NumberStringConverter());
    }

    // Lädt das entsprechende Beispiel, wenn ein Eintrag im Dropdown-Menü angewählt wurde
    private void addChangeListenerToDropdown() {
        exampleDropdown.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                model.loadExample(newValue);
            }
        }
        );
    }

    public void handleReset() {
        model.resetValues();
    }

    @Override
    public void handleGenerate() {
        try {
            model.validate();
            super.handleGenerate();
        } catch (Exception exception) {
            showAlert("Error", exception.getMessage());
        }
    }

}
