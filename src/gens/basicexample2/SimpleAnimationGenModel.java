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
package gens.basicexample2;

import general.GenModel;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author Christoph Baumhardt
 */
public class SimpleAnimationGenModel extends GenModel {

    @Override
    public String getGenName() {
        return "Simple Animation Generator";
    }

    @Override
    public void generate() {
        for (int iteration = 1; iteration <= 500; iteration++) {
            setGenState("Calculating iteration " + iteration + "...");
            canvas = new Canvas(500, 500);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, 500, 500);
            gc.setFill(Color.BLUE);
            gc.fillRect(0, 0, iteration, iteration);
            waitForCanvasIterationDisplayedInApp();
            try { // just needed for simulating long lasting task
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                return;
            }
        }
    }
    
}
