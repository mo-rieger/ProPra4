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
import gens.rft.TreeNode;
import java.util.Random;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

/**
 *
 * @author Moritz Rieger
 */
public class RandomFunctionTreeModel extends GenModel {
    
    private static final int UNARY = 1;
    private static final int BINARY = 2;
    private int hue;
    
    private final IntegerProperty widthProperty = new SimpleIntegerProperty(250);
    private final IntegerProperty heightProperty = new SimpleIntegerProperty(250);
    private final DoubleProperty depthProperty = new SimpleDoubleProperty(3);
    protected final IntegerProperty seedProperty = new SimpleIntegerProperty(105);
    private final DoubleProperty hueProperty = new SimpleDoubleProperty(50);
    private final IntegerProperty imagesCountProperty = new SimpleIntegerProperty(6);
    private boolean createSet = false;
    protected FunctionFactory funcFactory;
    
    public RandomFunctionTreeModel(){
        funcFactory = new FunctionFactory(seedProperty.getValue());
        setHue();
    }
    
    public void validate() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getGenName() {
        return "Standard Random Function Tree Generator";
    }

    @Override
    public void generate() {
        canvas = new Canvas(widthProperty.getValue(), heightProperty.getValue());
        if(createSet)
            generateSet();
        else
            generateImage();
    }
    public void generateImage(){
        // to generate the same image with the same seed we have to reset the pseudorandom int-stream
        funcFactory.setSeed(seedProperty.intValue());
        setHue();
        Function rootNode = createTree(depthProperty.intValue());  
        GraphicsContext gc = canvas.getGraphicsContext2D();
        PixelWriter pw = gc.getPixelWriter();
        //loop through every pixel
        int percentage;
        for(int x = 0; x < widthProperty.getValue(); x++){
            percentage =(int) ((double)(x*heightProperty.getValue())/(double)(widthProperty.getValue()*heightProperty.getValue())*100);
            for(int y = 0; y < heightProperty.getValue(); y++){
                double[] nCoords = normalize(x,y);
                double result = evalRFT(rootNode, nCoords[0], nCoords[1]);
                pw.setColor(x, y, getColor(result));
            }
            setGenState("Calculating Randomized Function Tree Image  " + percentage + " %");
        }
    }
    private void generateSet(){
        Random rnd  = new Random();
        for(int i = 0; i < imagesCountProperty.intValue(); i++){
            seedProperty.set(rnd.nextInt());
            depthProperty.set(rnd.nextInt(10));
            generateImage();
            this.saveImage("depth"+depthProperty.intValue()+"seed"+seedProperty.intValue()+"hue"+hue);
        }
    }
    private double[] normalize(int x,int y){
        double[] result = {x/widthProperty.doubleValue(), y/heightProperty.doubleValue()};
        return result;
    }
    /**
     * creates recursively a directional Tree in Post-order within the given depth
     * @param depth
     * @return 
     */
    private Function createTree(int depth){
        Function node;
        //take random function from pool
        node = funcFactory.getRandomFunction();
        //create one children for unary, two for binary ...
          if(depth > 0){
            Function[] children = new Function[node.getType()];
            for(int i=0; i < children.length; i++){
                children[i] = createTree(depth-1);
            }
            node.setChildren(children);
        }
        return node;
    }
    /**
     * recursively evaluate a random function tree
     * @param node
     * @param x
     * @param y
     * @return 
     */
    private double evalRFT(Function node, double x, double y){
        if(node.getChildrenCount() > 0){
            Function[] children = (Function[]) node.getChildren();
            //save results from each children
            double[] values = new double[children.length];
            for(int i=0; i<children.length; i++){
                values[i] = evalRFT(children[i], x, y);
            }
            //when all childrens are evaluated we can set the values of the children in this nodes function
            switch (node.getType()){
                case UNARY: return node.getResult(values[0], values[0]);
                case BINARY: return node.getResult(values[0], values[1]);
                default: return node.getResult(values[0], values[0]); //this should never be reached, just to satisfy javacompiler
            }
        }else{
            return node.getResult(x, y);
        }
    }
    
    private Color getColor(double val){
        return Color.hsb((val*360+hue)%360, val, val);     
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
    public DoubleProperty getDepthProperty() {
        return depthProperty;
    }
    public DoubleProperty getHueProperty() {
        return hueProperty;
    }
    public IntegerProperty getImagesCountProperty() {
        return imagesCountProperty;
    }
    public void setHue(){
        hue = hueProperty.intValue();
    }
    public void setCreateSet(boolean createSet){
        this.createSet = createSet;
    }
}
