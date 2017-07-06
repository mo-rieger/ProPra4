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
package gens.rft;

/**
 * A Function can be a Node in a RFT.
 * @author Moritz Rieger
 */
public class Function implements TreeNode{
    
    private Function[] children;
    private Function parent;
    
    private int type;
    
    /**
     * 
     * @param type 1 for unary, 2 binary ...
     */
    public Function(int type) {
        this.type = type;
    }
    /**
     * This Method is overriden for any specific Function, for an Example
     * see line 64 createFunction method in Class FunctionFactory
     * @param param
     * @return 
     */
    public double getResult(double[] param){
        return param[0];
    }

    @Override
    public Function getParent() {
        return parent;
    }

    @Override
    public Function[] getChildren() {
        return children;
    }

    @Override
    public void setChildren(TreeNode[] nodes) {
        children = (Function[]) nodes;
    }

    @Override
    public void setParent(TreeNode node) {
        parent = (Function) node;
    }
    @Override
    public  int getChildrenCount(){
        if(children != null)
            return children.length;
        else
            return 0;
    }
    public int getType(){
        return type;
    }
    
}
