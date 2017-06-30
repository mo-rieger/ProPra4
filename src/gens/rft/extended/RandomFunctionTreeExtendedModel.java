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
package gens.rft.extended;

import gens.rft.Function;
import gens.rft.standard.RandomFunctionTreeModel;

/**
 *
 * @author Moritz Rieger
 */
public class RandomFunctionTreeExtendedModel extends RandomFunctionTreeModel{
    
    public RandomFunctionTreeExtendedModel(){
        depthProperty.set(5);
        seedProperty.set(65);
    }
        
    @Override
    public String getGenName() {
        return "Extended Random Function Tree Generator";
    }
     /**
     * creates recursively a directional Tree in Post-order within the given depth
     * @param depth
     * @return 
     */
    @Override
    protected Function createTree(int depth){
        Function node;
        //create one children for unary, two for binary ...
        if(depth > 0){
            node = funcFactory.getRandomFunction();
            Function[] children = new Function[node.getType()];
            for(int i=0; i < children.length; i++){
                children[i] = createTree(depth-1);
            }
            node.setChildren(children);
        }else{
            //make sure that leafes only get unary or binary functions
            node = funcFactory.getUnaryOrBinaryFunction();
        }
        return node;
    }
    @Override
    protected String getImageName(){
        return "extended-rft-"+super.getImageName();
    }
}
