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

        GraphPanel panel = new GraphPanel(scores);
        List<ExtendedFunction> functions = new ArrayList<>();
        functions.add(new ExtendedFunction(Math::sin));
        functions.add(new ExtendedFunction(Math::cos));
        Map<Double, Double> map = new HashMap<>();
        GraphPanel.drawGraph(functions, map, 0.001d);

    }

    public static void main(String[] args) {
        List<Score> scores = new ArrayList<>();

        GraphPanel panel = new GraphPanel(scores);
        List<ExtendedFunction> functions = new ArrayList<>();
        functions.add(new ExtendedFunction(Math::sin));
        functions.get(0).setBoundaries(0.3d, 8d);
        functions.add(new ExtendedFunction(Math::cos));
        functions.get(1).setBoundaries(0.3d, 8d);
        functions.add(new ExtendedFunction(x -> Math.sin(1/x)));
        functions.get(2).setBoundaries(0.3d, 8d);

        Map<Double, Double> map = new HashMap<>();
        GraphPanel.drawGraph(functions, map, 0.001d);

    }
}