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
package gens.lsystems.probabilistic;

import gens.lsystems.Rule;
import gens.lsystems.standard.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author jzentner
 */
public class ProbabilisticLindenmayerSystemModel extends LindenmayerSystemModel {

    @Override
    public String getGenName() {
        return "Probabilistic Lindenmayer System";
    }

    @Override
    // Erzeugt und validiert die Regelmenge für das stochastische L-System
    protected void createRuleset() {
        final String[] rulesArray = getRulesProperty().getValueSafe().split("\n");
        this.rules = new ArrayList<>();
        for (String ruleString : rulesArray) {
            final String[] rawRule = ruleString.split("=");
            if (rawRule[0].length() > 1) {
                throw new IllegalStateException("Only single characters are allowed on the left side of a rule.");
            }
            if (rawRule[1].contains("(")) {
                rules.add(createProbabilisticRule(rawRule));
            } else {
                rules.add(createDeterministicRule(rawRule));
            }
        }
    }

    // Erzeugt eine stochastische Regel
    private Rule createProbabilisticRule(String[] rawRule) {
        final char rightSide = rawRule[0].charAt(0);
        final String leftSide = rawRule[1].split("[(]")[0];
        // Extrahiert die Wahrscheinlichkeit
        final double probability = Double.parseDouble(rawRule[1].split("[(]")[1].replace(")", ""));
        if (!checkWordAgainstAlphabet(leftSide)) {
            throw new IllegalStateException("Rule contains illegal characters.");
        }
        return new ProbabilisticRule(rightSide, leftSide, probability);
    }

    // Erzeugt eine deterministische Regel
    private Rule createDeterministicRule(String[] rawRule) {
        if (!checkWordAgainstAlphabet(rawRule[0]) || !checkWordAgainstAlphabet(rawRule[1])) {
            throw new IllegalStateException("Rule contains illegal characters.");
        }
        return new Rule(rawRule[0].charAt(0), rawRule[1]);

    }

    // Liefert alle Regeln zurück, die einem Symbol zugewiesen sind.
    private List<Rule> getRulesForCharacter(char character) {
        return rules.stream()
                .filter((rule) -> rule.getLeftSide() == character)
                .collect(toList());
    }

    @Override
    public void validate() {
        super.validate();
        checkProbabilitiesInRuleset();
    }

    @Override
    // Wählt eine Produktionsregel für ein bestimmtes Symbol aus
    protected Rule getRuleForCharacter(char character) {
        final List<Rule> rules = getRulesForCharacter(character);
        // Wenn es nur eine deterministische Regel gibt, ist dies klar
        if (rules.size() == 1) {
            return rules.get(0);
        // Wenn es mehrere stochastische Regeln gibt, muss eine davon ausgewählt werden.
        } else if (isProbabilistic(rules)) {
            return getRandomRule(rules);
        }
        return null;
    }

    // Wählt zufällig anhand der eingegebenen Wahrscheinlichkeitsverteilung eine Regel aus
    private Rule getRandomRule(List<Rule> rules) {
        double state = 0.0;
        final double random = Math.random();
        for (Rule rule : rules) {
            state += ((ProbabilisticRule) rule).getProbability();
            if (state >= random) {
                return rule;
            }
        }
        return null;
    }

    // Überprüft, ob alle Regeln mit den notwendigen Wahrscheinlichkeiten versehen sind, und ob diese pro Symbol zusammen 100% ergeben
    private void checkProbabilitiesInRuleset() {
        for (char character : getAlphabetProperty().getValueSafe().toCharArray()) {
            final List<Rule> rules = getRulesForCharacter(character);
            if (rules.size() > 0 && !isProbabilistic(rules) && rules.size() > 1) {
                throw new IllegalStateException("Please add probabilities to all rules for charachter " + character);
            }
            if (rules.size() > 0 && isProbabilistic(rules) && !probabilitiesOK(rules)) {
                throw new IllegalStateException("Probabilities for character " + character + " have to be 100% in sum.");
            }
        }
    }

    
    private boolean isProbabilistic(List<Rule> rules) {
        return rules.stream()
                .filter((rule) -> rule instanceof ProbabilisticRule)
                .count() == rules.size();
    }

    // Überprüft, ob die Wahrscheinlichkeiten einer Menge von stocahstischen Regeln zusammen 100% ergeben.
    private boolean probabilitiesOK(List<Rule> rules) {
        final double probabilitySum = rules.stream()
                .map((rule) -> ((ProbabilisticRule) rule).getProbability())
                .reduce(0.0, (sum, probability) -> sum + probability);
        return Math.abs(1.0 - probabilitySum) < 1E-7;
    }

    @Override
    protected Map<String, Object[]> createExamples() {
        final Map<String, Object[]> examples = new HashMap<>();
        examples.put("Probabilistic Plants", new Object[]{"F+-[]", "F", 45.0, 25.0, "F=F[+F]F[-F]F(0.33)\nF=F[+F]F(0.33)\nF=F[-F]F(0.34)"});
        return examples;
    }

}
