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

import general.GenModel;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author Christoph Baumhardt
 */
public class SimpleGenModel extends GenModel {

    @Override
    public String getGenName() {
        return "Simple Generator";
    }
   
    private int width; 
    private int height;
    
    
    public SimpleGenModel() {
        width = 600;
        height = 400;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }

    public void setWidth(int value) {
        if (value >  0 && value <= 3000) {
            width = value;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void setHeight(int value) {
        if (value >  0 && value <= 3000) {
            height = value;
        } else {
            throw new IllegalArgumentException();
        }
    }    
 
    @Override
    public void generate() {     

        setGenState("Creating new canvas...");
        canvas = new Canvas(width, height);

        setGenState("Filling image background...");
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);

        setGenState("Drawing blue circle...");
        double diameter = Math.min(width, height);
        gc.setFill(Color.BLUE);
        // draw a circle in the middle of the canvas
        gc.fillOval((width-diameter)/2., (height-diameter)/2.,
                diameter, diameter);
        
        // NOTE1: To show the different middle states (they are usually too fast
        // for the human eye) put the following code snippet before each call
        // of setGenState(...) inside this method. This simulates a
        // time-consuming generate process.
        /*try {
            Thread.sleep(1000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }*/
        
        // NOTE2: If the generate method runs very long, the user may choose to
        // stop the Thread e.g. by closing the window. If that happens, the
        // interrupt()-method is called on that Thread. However that does not 
        // mean that the Thread actually is stopped, only a status bit is set.
        // To stop the thread, it must check this status bit regularly and then
        // act on it. This can be done like this:
        // if(Thread.currentThread().isInterrupted()){return;}
     
    }
}
