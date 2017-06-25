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

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * This is an abstract GenController which all specialized
 GenController (for example SimpleGenController) extend. It 
 defines the things all subtypes of GenController have in common:
 a Generate-button, a method to handle that button and method to get the
 underlying specific GenModel that each GenController has to have.
 *
 * @author Christoph Baumhardt
 */
public abstract class GenController {
    
    /**
     * Each SpecializedGeneratorView.fxml file has to have a Button with fx:id
     * "buttonGenerate" and onAction "#handleGenerate".
     * 
     */     
    @FXML private Button buttonGenerate;

    /**
     * This automatically called method makes sure that when the generateButton
     * has the focus and Enter is pressed the same thing happens as if the user
     * clicked on the Button.
     * 
     */       
    public void initialize() {
        buttonGenerate.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent ke) -> {
            if (ke.getCode() == KeyCode.ENTER) {
                handleGenerate();
            }            
        });        
    }    
    
     /**
     * Returns the model each specialized GenController declares. Note 
 that the model is not defined in this abstract class as a GenModel,
 but rather in the subclasses of GenController as a specialized
 GenModel so that the specialized GenController can access
 its individual specialized functions.
     * 
     * @return The GenModel that is linked with the GenController
     * 
     */    
    public abstract GenModel getModel();
    
    // Closes windows that have been spawned by the controller
    /*public void closeSpawnedWindows() {
        // Close commands to be added by individual controller if necessary
    }*/
    
    
    /**
     * Handles a press on the buttonGenerate by initiating the
 generateInNewThread() method of the GenModel, which creates an
 a new Image.
     * 
     */     
    public void handleGenerate(){
        getModel().interruptBackgroundThread();
        getModel().generateInNewThread();
    };
    
    // Handles activation of "Stop" button, so that parameters can be modified
    // before the generator may be started again
    public void handleStop() {
        getModel().interruptBackgroundThread();
    }
  
    /**
     * Can be used in a subclass of GenController to show an Alert if 
 there was an invalid user input.
     * 
     * @param description The description of the invalid input
     */     
    protected void showInputAlert(String description) {
        showAlert("Invalid Input", description);
    }

    /**
     * Can be used in a subclass of GenController to show an Alert.
     * 
     * @param title The title of the alert
     * @param description The description of the alert
     */     
    protected void showAlert(String title, String description){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(description);
        alert.showAndWait();        
    }    
}
