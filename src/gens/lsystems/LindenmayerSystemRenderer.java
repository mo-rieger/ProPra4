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
package gens.lsystems;

import java.util.Stack;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

/**
 * Ein Zeichensystem, das die graphische Repräsentation eines L-Systems erzeugt.
 *
 * @author jzentner
 */
public class LindenmayerSystemRenderer {

    private static final double STEP_LEGNTH = 10;

    private final String word;
    private final double rotation;
    private final int width;
    private final int height;
    private final double startAngle;

    private RendererState state;
    private final Stack<RendererState> stateStack = new Stack<>();

    private double scalingFactor = 1.0;

    private double minX = 0;
    private double maxX = 0;
    private double minY;
    private double maxY;

    private Canvas canvas;

    public LindenmayerSystemRenderer(int width, int height, final String word, final double rotation, final double startAngle) {
        this.width = width;
        this.height = height;
        this.minY = height;
        this.maxY = height;
        this.word = word;
        this.rotation = rotation;
        this.startAngle = startAngle;
        this.state = new RendererState(0, height, startAngle);
    }

    // Rotiert den Zeichenkopf um eine bestimmte Gradzahl im Uhrzeigersinn
    private void rotate(double degrees) {
        state.alpha = (state.alpha + degrees) % 360;
    }

    // Kellert den aktuellen Zustand des Zeichensystems ein
    private void pushStack() {
        stateStack.push(new RendererState(state.x, state.y, state.alpha));
    }

    // Kellert das oberste Element auf dem Stack aus und stellt diesen Zustand des Zeichensystems wieder her.
    private void popStack() {
        final RendererState poppedState = stateStack.pop();
        if (poppedState != null) {
            state = poppedState;
        }
    }

    // Zeichnet eine Linie von n Pixeln in die aktuelle Richtung
    private void forward(double n) {
        double oldX = state.x;
        double oldY = state.y;
        state.x = (state.x + n * Math.cos(state.alpha * Math.PI / 180));
        state.y = (state.y - n * Math.sin(state.alpha * Math.PI / 180));
        updateExtrema();
        canvas.getGraphicsContext2D().strokeLine(oldX, oldY, state.x, state.y);
    }

    // Verfolgt die Extremkoordinaten des Zeichensystems (wird zur Berechnung des Skalierfaktors benötigt)
    private void updateExtrema() {
        minX = state.x < minX ? state.x : minX;
        maxX = state.x > maxX ? state.x : maxX;
        minY = state.y < minY ? state.y : minY;
        maxY = state.y > maxY ? state.y : maxY;
    }

    // Ermittelt den Skalierfaktor so, dass das gesamte Bild auf die Zeichenfläche passt.
    private void setScalingFactor() {
        final double scaleX = width / (double) Math.abs(maxX - minX);
        final double scaleY = height / (double) Math.abs(maxY - minY);
        this.scalingFactor = Math.min(scaleX, scaleY);
    }
    
    
    // Ermittelt die Startposition des Zeichenkopfes so, dass das gesamte Bild auf die Zeichenfläche passt.
    private void setStartPosition() {
        state.y = (int) (maxY > height ? state.y - (maxY - height) * scalingFactor : state.y);
        state.x = (int) (minX < 0 ? state.x + Math.abs(minX) * scalingFactor : state.x);
    }

    public Canvas render() {
        // Zuerst auf eine "Wegwerf-Canvas" zeichnen, um den richtigen Skalierungsfaktor zu ermitteln;
        newCanvas();
        drawCanvas();
        // Danach wird eine neue Canvas erzeugt, Skalierfaktor und Position des Zeichenkopfs berechnet.
        newCanvas();
        setScalingFactor();
        setStartPosition();
        drawCanvas();
        return canvas;
    }

    // Zeichnet das im Konstruktor übergebene Wort auf die Canvas.
    private void drawCanvas() {
        for (char character : word.toCharArray()) {
            switch (character) {
                case '+':
                    rotate(rotation);
                    break;
                case '-':
                    rotate(-rotation);
                    break;
                case '[':
                    pushStack();
                    break;
                case ']':
                    popStack();
                    break;
                case 'X':
                case 'Y':
                    break;
                default:
                    forward(STEP_LEGNTH * scalingFactor);
                    break;
            }
        }
    }

    // Erzeugt eine leere, weiße Canvas
    private void newCanvas() {
        canvas = new Canvas(width, height);
        canvas.getGraphicsContext2D().setFill(Color.WHITE);
        canvas.getGraphicsContext2D().fillRect(0, 0, width, height);
        state = new RendererState(0, height, startAngle);
    }

    // Hilfsklasse, um den Zustand des Zeichensystems zu speichern.
    private class RendererState {

        private double x;
        private double y;
        private double alpha;

        private RendererState(double x, double y, double alpha) {
            this.x = x;
            this.y = y;
            this.alpha = alpha;
        }
    }

}
