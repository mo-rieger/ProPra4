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
package gens.lsystems.standard;

import gens.lsystems.LindenmayerSystemRenderer;
import general.GenModel;
import gens.lsystems.Rule;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author jzentner
 */
public class LindenmayerSystemModel extends GenModel {

    private final IntegerProperty widthProperty = new SimpleIntegerProperty(500);
    private final IntegerProperty heightProperty = new SimpleIntegerProperty(500);
    private final StringProperty alphabetProperty = new SimpleStringProperty();
    private final StringProperty axiomProperty = new SimpleStringProperty();
    private final DoubleProperty rotationProperty = new SimpleDoubleProperty(90.0);
    private final DoubleProperty startingAngleProperty = new SimpleDoubleProperty(0.0);
    private final IntegerProperty iterationsProperty = new SimpleIntegerProperty(5);
    private final StringProperty rulesProperty = new SimpleStringProperty();
    private final Map<String, Object[]> examples = createExamples();

    protected List<Rule> rules;

    public IntegerProperty getWidthProperty() {
        return widthProperty;
    }

    public IntegerProperty getHeightProperty() {
        return heightProperty;
    }

    public StringProperty getAlphabetProperty() {
        return alphabetProperty;
    }

    public StringProperty getAxiomProperty() {
        return axiomProperty;
    }

    public DoubleProperty getRotationProperty() {
        return rotationProperty;
    }

    public DoubleProperty getStartingAngleProperty() {
        return startingAngleProperty;
    }

    public IntegerProperty getIterationsProperty() {
        return iterationsProperty;
    }

    public StringProperty getRulesProperty() {
        return rulesProperty;
    }

    public void loadExample(final String key) {
        final Object[] example = examples.get(key);
        if (example != null) {
            alphabetProperty.set((String) example[0]);
            axiomProperty.set((String) example[1]);
            startingAngleProperty.set((double) example[2]);
            rotationProperty.set((double) example[3]);
            rulesProperty.set((String) example[4]);
        }
    }

    // Prüft, ob schon eine Regel zum jeweiligen Buchstaben in der Regelmenge vorhanden ist
    protected boolean checkRuleAgainstRuleset(Rule rule) {
        return !rules.stream()
                .filter(element -> element.getLeftSide() == rule.getLeftSide())
                .findAny().isPresent();
    }

    // Prüft, ob ein Wort dem eingegebenen Alphabet entspricht
    protected boolean checkWordAgainstAlphabet(final String word) {
        for (char character : word.toCharArray()) {
            if (!isCharacterInAlphabet(character)) {
                return false;
            }
        }
        return true;
    }

    private boolean isCharacterInAlphabet(final char character) {
        for (char c : alphabetProperty.get().toCharArray()) {
            if (c == character) {
                return true;
            }
        }
        return false;
    }

    // erzeugt die n-te Generation des eingegebenen L-Systems
    private String iterateSystem(int generations) {
        String word = axiomProperty.get();
        for (int i = 0; i < generations; i++) {
            word = createNextGeneration(word);
        }
        return word;
    }

    // Ersetzt gemäß den eingegebenen Regeln alle Zeichen, die 
    private String createNextGeneration(final String word) {
        final StringBuilder builder = new StringBuilder();
        for (char character : word.toCharArray()) {
            final Rule rule = getRuleForCharacter(character);
            if (rule != null) {
                builder.append(rule.getRightSide());
            } else {
                builder.append(character);
            }
        }
        return builder.toString();
    }

    @Override
    public String getGenName() {
        return "Standard Lindenmayer System";
    }

    public void validate() {
        createRuleset();
        // Prüft, ob das Axiom nur Buchstaben aus dem Alphabet enthält
        if (!checkWordAgainstAlphabet(axiomProperty.getValueSafe())) {
            throw new IllegalStateException("Axiom contains characters that are not in the alphabet");
        }
        // Prüft, ob Start- und Rotationswinkel zwischen 0° und 360° sind
        if (rotationProperty.getValue() < 0 || rotationProperty.getValue() > 360 || startingAngleProperty.getValue() < 0 || startingAngleProperty.getValue() > 360) {
            throw new IllegalStateException("Angles have to be between 0 and 360 degrees.");
        }
        // Prüft, ob eine positive Zahl von Iterationen durchgeführt werden soll
        if (iterationsProperty.getValue() < 0) {
            throw new IllegalStateException("Number of iterations has to be greater than zero.");

        }
    }

    @Override
    public void generate() {
        final String word = iterateSystem(iterationsProperty.get());
        this.canvas = new LindenmayerSystemRenderer(widthProperty.get(), heightProperty.get(), word, rotationProperty.get(), startingAngleProperty.get()).render();
        waitForCanvasIterationDisplayedInApp();
    }

    // Ermittelt die Ersetzungsregel für einen gegebenen Buchstaben
    protected Rule getRuleForCharacter(char character) {
        final Optional<Rule> optional = rules.stream()
                .filter(rule -> rule.getLeftSide() == character)
                .findFirst();
        return optional.isPresent() ? optional.get() : null;
    }

    public void resetValues() {
        alphabetProperty.setValue("");
        axiomProperty.setValue("");
        rotationProperty.setValue(90.0);
        iterationsProperty.setValue(5);
        rules.clear();
    }

   
    // Erzeugt und validiert die Regelmenge
    protected void createRuleset() {
        final String[] rulesArray = rulesProperty.getValueSafe().split("\n");
        this.rules = new ArrayList<>();
        for (String ruleString : rulesArray) {
            final String[] rawRule = ruleString.split("=");
            // Links des =-Zeichens darf nur ein Symbol stehen
            if (rawRule[0].length() > 1) {
                throw new IllegalStateException("Only single characters are allowed on the left side of a rule.");
            }
            // Die Regel darf nur Symbole aus dem Alphabet enthalten
            if (!checkWordAgainstAlphabet(rawRule[0]) || !checkWordAgainstAlphabet(rawRule[1])) {
                throw new IllegalStateException("Rule contains illegal characters.");
            }
            final Rule rule = new Rule(rawRule[0].charAt(0), rawRule[1]);
            if (!checkRuleAgainstRuleset(rule)) {
                throw new IllegalStateException("The ruleset has to be deterministic");
            }
            rules.add(rule);
        }

    }

    protected Map<String, Object[]> createExamples() {
        final Map<String, Object[]> examples = new HashMap<>();
        examples.put("Koch Curve", new Object[]{"F+-", "F", 0.0, 60.0, "F=F+F--F+F"});
        examples.put("Koch Snowflake", new Object[]{"F+-", "F--F--F", 0.0, 60.0, "F=F+F--F+F"});
        examples.put("Sierpinski Triangle", new Object[]{"FG+-", "F-G-G", 0.0, 120.0, "F=F-G+F+G-F\nG=GG"});
        examples.put("Dragon Curve", new Object[]{"XYF+-", "FX", 0.0, 90.0, "X=X+YF+\nY=-FX-Y"});
        examples.put("Fractal Plants", new Object[]{"FX+-[]", "X", 45.0, 25.0, "X=F[-X][X]F[-X]+FX\nF=FF"});
        return examples;
    }

    public ObservableList getExampleKeys() {
        return FXCollections.observableList(new ArrayList<>(examples.keySet()));
    }

}
