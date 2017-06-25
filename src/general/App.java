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

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Entry point to the whole program (JavaFX Main class).
 *
 * @author Christoph Baumhardt
 */
public class App extends Application {
    
    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Algorithmic Art Generator");
        primaryStage.setOnCloseRequest((WindowEvent e) -> {
            Platform.exit(); // close all open windows
            System.exit(0);  // exit non-GUI threads which are perhaps running
        });

        // load app view from FXML file and set as scene on primary stage        
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/general/AppView.fxml"));
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.show();
            
        } catch (IOException e) {
            System.out.println("Loading AppView.fxml failed!");
        } 
    }
    
    /**
     * Launches the JavaFX Application Thread with command line arguments.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
