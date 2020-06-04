package com.apploidxxx.app.graphics;

import org.junit.jupiter.api.Test;
import util.function.ExtendedFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Arthur Kupriyanov on 22.04.2020
 */
class GraphPanelTest {
    @Test
    public void simpleTest() {
        List<Score> scores = new ArrayList<>();

        GraphPanel panel = new GraphPanel(scores, "Test");
        List<ExtendedFunction> functions = new ArrayList<>();
        functions.add(new ExtendedFunction(Math::sin));
        functions.add(new ExtendedFunction(Math::cos));
        Map<Double, Double> map = new HashMap<>();
        GraphPanel.drawGraph(functions, map, 0.001d);

    }

}