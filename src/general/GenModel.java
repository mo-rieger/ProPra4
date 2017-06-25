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

import javafx.scene.canvas.Canvas;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;

/**
 * This is an abstract GenModel which all specialized
 GenModel (for example SimpleGenModel) extend. It 
 defines the things all subtypes of GenModel have in common, for example
 a canvas to draw on, a GenState property, the ability to save an image
 and a generate() method to actually generate the desired image onto the
 canvas.
 *
 * @author Christoph Baumhardt
 */
public abstract class GenModel {

    protected String genName; 
    protected Canvas canvas; // canvas for the GenModel to draw on
    protected Thread backgroundThread; // to execute generate()
    // use a property instead of GenState object for easy change monitoring
    private final ObjectProperty<GenState> genState;
    volatile boolean canvasIterationDisplayedInApp;

    public GenModel() {// constructor will be automatically called from subclass
        genName = getGenName();
        genState = new SimpleObjectProperty<>(this, "generatorState",
                GenState.READY);
    }
    
    abstract public String getGenName();
       
    public abstract void generate(); // method to affect the GenModel canvas
    

    /**
     * Define and use a new Thread for the potentially time-consuming generate()
     * method, so that the Java FX Application Thread does not get blocked
     * (which would result in unresponsiveness in the GUI).
     */
    public void generateInNewThread() {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                generate();
                genStateProperty().setValue(GenState.FINISHED_READY);
                return null;
            }
        };
        // Task catches all Exceptions, this code makes sure to output them
        task.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent arg0) {
                Throwable throwable = task.getException();
                throwable.printStackTrace();
            }
        });        
        backgroundThread = new Thread(task);
        backgroundThread.start();       
    }

    /**
     * Checks whether a background thread is calculating the generate()-method
     * at the moment.
     *
     * @return True if generate() method is processed right now, otherwise false
     */
    public boolean isBackgroundThreadAlive() {
        return (backgroundThread != null && backgroundThread.isAlive());
    }
    
    /**
     * Interrupts the thread the generate() method uses for its computation.
     *
     */    
    public void interruptBackgroundThread(){
        if(isBackgroundThreadAlive()){
            backgroundThread.interrupt();
            // toggles only a status bit, which still needs to be checked in 
            // generate(): if(Thread.currentThread().isInterrupted()){return;}
        }
    }
    
    /**
     * This method gets called automatically from AppController depending on the
     * GenState of the GenModel - it is used inside AppController to get the 
     * GenModel canvas and displays it in the App canvas.
     */
    public Canvas getCanvas() {
        return canvas; // can be null
    }
    

    /**
     * Saves the GenModel canvas directly under a filename.
     *
     * @param filename The filename under which the canvas should be saved
     */
    public void saveImage(String filename){
        WritableImage image = canvas.snapshot(new SnapshotParameters(), null);
        File file = new File(filename);
        try {
            WritableImage writableImage = canvas.snapshot(null, null);
            RenderedImage renderedImage = 
                    SwingFXUtils.fromFXImage(writableImage, null);
            ImageIO.write(renderedImage, "png", file);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }                 
    }

    /**
     * Describes the current state of the GenModel in words.
     *
     * @return Description of the current GenState
     */
    public final String getStateDescription() {
        return genState.get().getDescription();
    } 
    
    // declare the typical functions associated with properties
    
    public final GenState getGenState() {
        return genState.get();
    }
    
    public final void setGenState(GenState newGeneratorState) {
        genState.set(newGeneratorState);    
    }
    
    public final ObjectProperty<GenState> genStateProperty() {
        return genState;
    }

    /**
     * Comfortable method to quickly update the status label of RootView with
     * a description.
     * 
     * @param description What shall be displayed in the status label.
     */
    public final void setGenState(String description) {
        genState.set(new GenState(description));
    }    

    /**
     * The purpose of this method is to display an iteration step of a canvas in
     * the App and waiting till the canvas actually is displayed (which 
     * happens not instantly due to event handling), so that after calling this
     * method a new canvas can be created for the next iteration step.
     *
     */
    public void waitForCanvasIterationDisplayedInApp() {
        // display canvas but do not update status label
        setGenState(GenState.ITERATION_READY);
        canvasIterationDisplayedInApp = true;
        Platform.runLater(() -> {
            // the following gets executed after the event handling finishes
            // (GenState changed -> display canvas in App canvas)
            canvasIterationDisplayedInApp = false;
        });
        while (canvasIterationDisplayedInApp){
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                // interrupt current Thread so that it can be stopped later. The
                // reason why this has to be called is because once an
                // InterruptedException is caught isInterrupted() is false again
                Thread.currentThread().interrupt(); // process somewhere later
                return; // does NOT stop thread, just exits method
            }
        }
        //setGenState("Calculate iteration x...");
        // now a new canvas can be safely be created
        // NOTE 1: You dont't need this method if you only use a single canvas.
        // NOTE 2: Remember that when you create a new canvas, you need to
        // create a new GraphicsContext and a new PixelWriter for that canvas!
    }
    
}
