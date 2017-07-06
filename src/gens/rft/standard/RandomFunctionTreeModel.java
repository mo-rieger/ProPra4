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

import general.GenModel;
import gens.rft.Function;
import gens.rft.FunctionFactory;
import java.util.Random;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

/**
 *
 * @author Moritz Rieger
 */
public class RandomFunctionTreeModel extends GenModel {

    private final IntegerProperty widthProperty = new SimpleIntegerProperty(500);
    private final IntegerProperty heightProperty = new SimpleIntegerProperty(500);
    protected final DoubleProperty minDepthProperty = new SimpleDoubleProperty(3);
    protected final DoubleProperty maxDepthProperty = new SimpleDoubleProperty(6);
    protected final IntegerProperty seedProperty = new SimpleIntegerProperty(4845212);
    private final DoubleProperty hueProperty = new SimpleDoubleProperty(50);
    private final IntegerProperty imagesCountProperty = new SimpleIntegerProperty(6);

    private int hue;
    private int depth;
    private boolean createSet = false;
    protected FunctionFactory funcFactory;
    private Random random;

    /**
     * Constructor set up new factory for functions
     */
    public RandomFunctionTreeModel() {
        random = new Random();
        funcFactory = new FunctionFactory(seedProperty.getValue());
        setHue();
    }

    @Override
    public String getGenName() {
        return "Standard Random Function Tree Generator";
    }

    @Override
    public void generate() {
        canvas = new Canvas(widthProperty.getValue(), heightProperty.getValue());
        if (createSet) {
            generateSet();
        } else {
            generateImage();
        }
    }

    /**
     * generate a single image and display it in the general app view
     */
    public void generateImage() {
        // to generate the same image with the same seed we have to reset the pseudorandom int-stream
        funcFactory.setSeed(seedProperty.intValue());
        setHue();
        Function rootNode = createTree(getDepth());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        PixelWriter pw = gc.getPixelWriter();
        //loop through every pixel
        int percentage;
        for (int x = 0; x < widthProperty.getValue(); x++) {
            percentage = (int) ((double) (x * heightProperty.getValue()) / (double) (widthProperty.getValue() * heightProperty.getValue()) * 100);
            for (int y = 0; y < heightProperty.getValue(); y++) {
                double result = evalRFT(rootNode, normalize(x, y));
                pw.setColor(x, y, getColor(result));
            }
            setGenState("Calculating Randomized Function Tree Image  " + percentage + " %");
        }
    }

    /**
     * generate a set of images with different seeds
     * the images are also saved at a specified path
     * (default: home)
     */
    private void generateSet() {
        for (int i = 0; i < imagesCountProperty.intValue(); i++) {
            seedProperty.set(random.nextInt());
            generateImage();
            this.saveImage(getImageName());
        }
    }

    /**
     *
     * @return name of an image with all the properties to recreate this image,
     * this is just possible if the functions in the FunctionFactory dont get
     * touched
     */
    protected String getImageName() {
        return "depth" + depth + "seed" + seedProperty.intValue() + "hue" + hue;
    }

    /**
     * normalize the input to values between [0,1]
     *
     * @param x
     * @param y
     * @return
     */
    private double[] normalize(int x, int y) {
        return new double[]{x / widthProperty.doubleValue(), y / heightProperty.doubleValue()};
    }

    /**
     * creates recursively a directional Tree in Post-order within the given
     * depth
     *
     * @param depth
     * @return
     */
    protected Function createTree(int depth) {
        Function node = funcFactory.getUnaryOrBinaryFunction();
        //create one children for unary, two for binary ...
        if (depth > 0) {
            Function[] children = new Function[node.getType()];
            for (int i = 0; i < children.length; i++) {
                children[i] = createTree(depth - 1);
            }
            node.setChildren(children);
        }
        return node;
    }

    /**
     * recursively evaluate a random function tree in post-order
     *
     * @param node
     * @param x
     * @param y
     * @return
     */
    private double evalRFT(Function node, double[] parameter) {
        if (node.getChildrenCount() > 0) {
            Function[] children = (Function[]) node.getChildren();
            //save results from each children
            double[] values = new double[children.length];
            for (int i = 0; i < children.length; i++) {
                values[i] = evalRFT(children[i], parameter);
            }
            //when all children are evaluated we can get the Result of this function
            return node.getResult(values);
        } else {
            //here we reach the leafes
            return node.getResult(parameter);
        }
    }

    /**
     * represent the calculated value of the rft as a color the hue property
     * enables the user to adjust the calculated color
     *
     * @param val
     * @return Color
     */
    private Color getColor(double val) {
        return Color.hsb((val * 360 + hue) % 360, val, val);
    }
    
    /**
     * get random depth within the given bounds
     * @return 
     */
    private int getDepth() {
        int bound = maxDepthProperty.intValue() - minDepthProperty.intValue();
        // to recreate an image with a specified depth you can set min = max
        // but we have handle this seperate, because bound must be positive in Random
        if (bound == 0 )
            depth = maxDepthProperty.intValue();
        else
            depth = minDepthProperty.intValue() + random.nextInt(bound);
        return depth;
    }

    public IntegerProperty getWidthProperty() {
        return widthProperty;
    }

    public IntegerProperty getHeightProperty() {
        return heightProperty;
    }

    public IntegerProperty getSeedProperty() {
        return seedProperty;
    }
    
    public DoubleProperty getMinDepthProperty() {
        return minDepthProperty;
    }
    
    public DoubleProperty getMaxDepthProperty() {
        return maxDepthProperty;
    }

    public DoubleProperty getHueProperty() {
        return hueProperty;
    }

    public IntegerProperty getImagesCountProperty() {
        return imagesCountProperty;
    }
    
    public void setWidth(int width) {
        widthProperty.set(width);
    }
    
    public void setHeight(int height) {
        heightProperty.set(height);
    }
    
    public void setSeed(int seed) {
        seedProperty.set(seed);
    }
    
    public void setHue() {
        hue = hueProperty.intValue();
    }

    public void setCreateSet(boolean createSet) {
        this.createSet = createSet;
    }
}
