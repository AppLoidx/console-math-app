package com.apploidxxx.app.graphics;

import util.DoubleUtil;
import util.function.ExtendedFunction;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Function;

//import java.awt.Point;


/**
 * @author Arthur Kupriyanov on 07.04.2020
 */
public class GraphPanel extends JPanel {

    private final static Color LINE_COLOR = new Color(255, 48, 62);
    private final static Color POINT_COLOR = new Color(255, 48, 62);
    private final static Color GRID_COLOR = new Color(74, 74, 74, 200);
    private final static Color BACKGROUND_COLOR = new Color(28, 28, 28);
    private final static Color LABEL_COLOR = Color.GRAY;
    private final static int LABEL_PADDING = 25;
    private final static int GLOBAL_PADDING = 25;
    private final static int POINT_WIDTH = 5;
    private final static double ACCURACY = 0.001d;
    private final static Stroke GRAPH_STROKE = new BasicStroke(2f);
    private final static Stroke GRID_STROKE = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2f, new float[]{2f}, 0.0f);
    private final static double GRID_WIDTH = 40;
    private final List<Score> scores;
    private final static Color[] LINE_COLORS = new Color[]{
            new Color(255, 48, 62),
            new Color(48, 172, 255),
            new Color(200, 48, 255)
    };

    public GraphPanel(List<Score> scores) {
        this.scores = scores;
    }

    private int graphSize() {
        return scores.stream().mapToInt(Score::graphSize).sum();
    }

    private double calculateXScale() {
        double currentScale = Double.MAX_VALUE;
        for (Score score : scores) {
            if (score.isNotInGraph()) continue;
            double xScale = ((double) getWidth() - (2 * GLOBAL_PADDING) - LABEL_PADDING) / (score.getList().size() - 1);
            currentScale = Math.min(xScale, currentScale);
        }

        return currentScale;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        double xScale = calculateXScale();
        double yScale = ((double) getHeight() - 2 * GLOBAL_PADDING - LABEL_PADDING) / (getMaxScore() - getMinScore());
//        List<Point> graphPoints = calculateGraphPoints(xScale, yScale, scores.get(0));
//        List<Point> graphPoints2 = calculateGraphPoints(xScale, yScale, scores.get(1));
//        List<Point> graphPoints3 = calculateGraphPoints(xScale, yScale, scores.get(2));
        List<List<Point>> graphPoints = new ArrayList<>();
        for (Score score : scores) {
            if (score.isNotInGraph()) continue;
            graphPoints.add(calculateGraphPoints(xScale, yScale, score));
        }
        //  background for only graph :
        //  g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding, getHeight() - 2 * padding - labelPadding);

        g2.setColor(BACKGROUND_COLOR);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(Color.LIGHT_GRAY);
        drawFrameName(g2);

        // creating labels for x and y
        drawYMarks(g2);
        drawXMarks(g2);

        // padding for zero point for each axis
        int paddingX = calculateXPaddingToZeroPoint(); // length to zero point in x axis
        int paddingY = -calculateYPaddingToZeroPoint(); // length to zero point in y axis

        drawAxis(g2, paddingX, paddingY);
        drawGrids(g2, paddingX, paddingY);
        drawGraph(g2, graphPoints);


        scores.forEach(score ->
                score.getIterator().forEachRemaining(p -> {
                    if (p.isNotInGraph()) {
                        drawOneDot(g2, p.getX(), p.getY());
                    }
                }));


    }

    private void drawFrameName(Graphics2D g2) {
        String frameName = "Куприянов Артур - Лабораторная работа №3";
        String smallFrameName = "Куприянов Артур";
        int frameNameSize = g2.getFontMetrics().stringWidth(frameName);
        if (frameNameSize < getWidth())
            g2.drawString(frameName, GLOBAL_PADDING + LABEL_PADDING + (getWidth() - (2 * GLOBAL_PADDING) - LABEL_PADDING - LABEL_PADDING) / 2 - frameNameSize / 2, 15);
        else
            g2.drawString(smallFrameName, GLOBAL_PADDING + LABEL_PADDING + (getWidth() - (2 * GLOBAL_PADDING) - LABEL_PADDING - LABEL_PADDING) / 2 - g2.getFontMetrics().stringWidth(smallFrameName) / 2, 15);

    }

    private List<com.apploidxxx.app.graphics.Point> calculateGraphPoints(double xScale, double yScale, Score score) {

        List<Point> graphPoints = new ArrayList<>();
        Iterator<com.apploidxxx.app.graphics.Point> iterator = score.getIterator();
        int index = 0;
        while (iterator.hasNext()) {
            com.apploidxxx.app.graphics.Point p = iterator.next();
            if (p.isNotInGraph()) continue;
            int x1 = (int) (index * xScale + GLOBAL_PADDING + LABEL_PADDING);
            int y1 = (int) ((getMaxScore() - p.getY()) * yScale + GLOBAL_PADDING);
            graphPoints.add(new com.apploidxxx.app.graphics.Point(x1, y1));
            System.out.println("Saving point " + x1 + " " + y1);
            index++;

        }

        return graphPoints;
    }

    private void drawYMarks(Graphics2D g2) {
        // create hatch marks and grid lines for y axis.

        int maxNumberYDivisions = 20;
        int calculatedNumberOfDivisions = (getHeight() - GLOBAL_PADDING * 2 - LABEL_PADDING) / 20;
        int numberYDivisions = Math.min(calculatedNumberOfDivisions, maxNumberYDivisions);

        for (int i = 0; i < numberYDivisions + 1; i++) {
            int x0 = GLOBAL_PADDING + LABEL_PADDING;
            int x1 = POINT_WIDTH + GLOBAL_PADDING + LABEL_PADDING;
            int y0 = getHeight() - ((i * (getHeight() - GLOBAL_PADDING * 2 - LABEL_PADDING)) / numberYDivisions + GLOBAL_PADDING + LABEL_PADDING);
            if (graphSize() > 0) {
                g2.setColor(LABEL_COLOR);
                String yLabel = DoubleUtil.round(getMinScore() + (getMaxScore() - getMinScore()) * ((i * 1.0) / numberYDivisions), ACCURACY) + "";
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            }

            g2.drawLine(x0, y0, x1, y0);
        }
    }

    private void drawXMarks(Graphics2D g2) {
        double xLabelSize = getWidth() / 40d;

        // and for x axis
        List<Point> sortedList = getAllPointsSorted();
        for (int i = 0; i < graphSize(); i++) {
            if (graphSize() > 1) {
                int x0 = i * (getWidth() - GLOBAL_PADDING * 2 - LABEL_PADDING) / (graphSize() - 1) + GLOBAL_PADDING + LABEL_PADDING;
                int y0 = getHeight() - GLOBAL_PADDING - LABEL_PADDING;
                int y1 = y0 - POINT_WIDTH;
                if ((i % ((int) ((graphSize() / xLabelSize)) + 1)) == 0) {

                    g2.setColor(LABEL_COLOR);
                    String xLabel = DoubleUtil.round(sortedList.get(i).getX(), ACCURACY) + "";
                    FontMetrics metrics = g2.getFontMetrics();
                    int labelWidth = metrics.stringWidth(xLabel);
                    g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
                }
                g2.drawLine(x0, y0, x0, y1);
            }
        }
    }

    private List<Point> getAllPointsSorted(){
        List<Point> sortedList = new ArrayList<>();
        scores.forEach(score -> sortedList.addAll(score.getList()));
        sortedList.sort(Comparator.comparingDouble(Point::getX));
        return sortedList;
    }


    private int calculateXPaddingToZeroPoint() {
        List<Point> score = getAllPointsSorted();
        double xMax = score.get(graphSize() - 1).getX();
        double xMin = score.get(0).getX();
//        if (xMax >= 0 && xMin < 0) {
        return (int) ((getWidth() - GLOBAL_PADDING * 2 - LABEL_PADDING) / (xMax - xMin) * (0 - xMin));
//        }
//        return 0;
    }

    private int calculateYPaddingToZeroPoint() {
        double yMax = getMaxScore();
        double yMin = getMinScore();

        if (yMax >= 0 && yMin < 0) {
            return (int) ((getHeight() - GLOBAL_PADDING * 2 - LABEL_PADDING) / (yMax - yMin) * (0 - yMin));
        }

        return 0;
    }

    private void drawAxis(Graphics2D g2, int paddingX, int paddingY) {
        g2.setColor(new Color(41, 169, 162));
        g2.setStroke(new BasicStroke(3f));
        g2.drawLine(GLOBAL_PADDING + LABEL_PADDING + paddingX, getHeight() - GLOBAL_PADDING - LABEL_PADDING, GLOBAL_PADDING + LABEL_PADDING + paddingX, GLOBAL_PADDING);
        g2.drawLine(GLOBAL_PADDING + LABEL_PADDING, getHeight() - GLOBAL_PADDING - LABEL_PADDING + paddingY, getWidth() - GLOBAL_PADDING, getHeight() - GLOBAL_PADDING - LABEL_PADDING + paddingY);

    }

    private void drawGraph(Graphics2D g2, List<List<Point>> graphPoints) {
        int index = 0;
        for (List<Point> graph : graphPoints) {

            g2.setColor(getLineColor(index));
            g2.setStroke(GRAPH_STROKE);
            for (int i = 0; i < graph.size() - 1; i++) {
                System.out.println("Point : " + graph.get(i).x + " " + graph.get(i).y);
                if (graph.get(i + 1).isBreakPoint() || graph.get(i).isBreakPoint()) {
                    continue;
                }
                int x1 = (int) graph.get(i).x;
                int y1 = (int) graph.get(i).y;
                int x2 = (int) graph.get(i + 1).x;
                int y2 = (int) graph.get(i + 1).y;
                g2.drawLine(x1, y1, x2, y2);
            }

            g2.setColor(getLineColor(index));
            for (Point graphPoint : graph) {
                int x = (int) (graphPoint.x - POINT_WIDTH / 2);
                int y = (int) (graphPoint.y - POINT_WIDTH / 2);
                g2.fillOval(x, y, POINT_WIDTH, POINT_WIDTH);
            }
            index++;
        }

    }

    private Color getLineColor(int index) {
        if (index >= LINE_COLORS.length) {
            index = index % LINE_COLORS.length;
        }
        return LINE_COLORS[index];
    }

    private void drawGrids(Graphics2D g2, int paddingX, int paddingY) {

        g2.setColor(GRID_COLOR);
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(GRID_STROKE);

        final int xZeroPointPos = GLOBAL_PADDING + LABEL_PADDING + paddingX;

        int currentPosX = xZeroPointPos;
        while (currentPosX > GLOBAL_PADDING + LABEL_PADDING) {
            g2.drawLine(currentPosX, getHeight() - GLOBAL_PADDING - LABEL_PADDING, currentPosX, GLOBAL_PADDING);
            currentPosX -= GRID_WIDTH;
        }
        currentPosX = xZeroPointPos;
        while (currentPosX < getWidth() - GLOBAL_PADDING) {
            g2.drawLine(currentPosX, getHeight() - GLOBAL_PADDING - LABEL_PADDING, currentPosX, GLOBAL_PADDING);
            currentPosX += GRID_WIDTH;
        }

        final int yZeroPointPos = getHeight() - GLOBAL_PADDING - LABEL_PADDING + paddingY;

        int currentPosY = yZeroPointPos;
        while (currentPosY > GLOBAL_PADDING) {
            g2.drawLine(GLOBAL_PADDING + LABEL_PADDING, currentPosY, getWidth() - GLOBAL_PADDING, currentPosY);
            currentPosY -= 40;
        }

        currentPosY = yZeroPointPos;
        while (currentPosY < getHeight() - GLOBAL_PADDING - LABEL_PADDING) {
            g2.drawLine(GLOBAL_PADDING + LABEL_PADDING, currentPosY, getWidth() - GLOBAL_PADDING, currentPosY);
            currentPosY += 40;
        }

        g2.setStroke(oldStroke);
    }

    private double getMinScore() {
        double minScore = Double.MAX_VALUE;
        for (Score score : scores) {
            for (com.apploidxxx.app.graphics.Point point : score) {
                if (point.isNotInGraph()) continue;
                minScore = Math.min(minScore, point.getY());
            }
        }

        return minScore;
    }

    private double getMaxScore() {
        double maxScore = Double.MIN_VALUE;
        for (Score score : scores) {
            for (com.apploidxxx.app.graphics.Point point : score) {
                if (point.isNotInGraph()) continue;
                maxScore = Math.max(maxScore, point.getY());
            }
        }
        return maxScore;
    }

//    public void setScores(Score score) {
//        this.score = score;
//        invalidate();
//        this.repaint();
//    }

    private static void createAndShowGui(List<Score> scores) {

        GraphPanel mainPanel = new GraphPanel(scores);
        mainPanel.setPreferredSize(new Dimension(800, 600));
        JFrame frame = new JFrame("Arthur Kupriyanov - Lab 3");

        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    public static void main(String[] args) {
        int maxDataPoints = 100;
//        Function<Double, Double> func = x -> Math.pow(x, 2) - x - 5;
        Function<Double, Double> func = Math::sin;
        double add = -6d;

        final Score score = new Score();

        for (int i = 0; i < maxDataPoints; i++) {
            add += 0.1d;
            score.addScore(Math.round(add * 100d) / 100d, func.apply(add));
            System.out.println("Adding score " + Math.round(add * 100d) / 100d + " " + func.apply(add));
        }
        Function<Double, Double> func2 = Math::cos;
        final Score score2 = new Score();
        add = -6d;
        for (int i = 0; i < maxDataPoints; i++) {
            add += 0.1d;
            score2.addScore(Math.round(add * 100d) / 100d, func2.apply(add));
            System.out.println("Adding score " + Math.round(add * 100d) / 100d + " " + func2.apply(add));
        }
        Function<Double, Double> func3 = x -> 5 * Math.pow(x, 2);

        Score score1 = new Score();
        score1.addScore(0d, 0d, true);
        score1.addScore(1d, 1d, true);
        score1.setNotInGraph(true);
        SwingUtilities.invokeLater(() -> createAndShowGui(List.of(score, score1, score2)));
    }

    public static void drawGraph(ExtendedFunction function, Map<Double, Double> answers, double accuracy) {
        drawGraph(List.of(function), answers, accuracy);
    }

    public static void drawGraph(List<ExtendedFunction> functions, Map<Double, Double> answers, double accuracy) {
        int maxDataPoints = 100;
        List<Score> scores = new ArrayList<>();
        for (ExtendedFunction function : functions) {
            double add = Math.min(function.getBoundaries()[0], function.getBoundaries()[1]);
            double step = 1d * Math.abs(function.getBoundaries()[0] - function.getBoundaries()[1]) / maxDataPoints;
            final Score score = new Score();
            for (int i = 0; i < maxDataPoints; i++) {
                add += step;
                score.addScore(Math.round(add * (1d / accuracy)) / (1d / accuracy), function.apply(add));
            }
            scores.add(score);
        }
        Score answersScore = new Score();
        for (Double key : answers.keySet()) {
            answersScore.addScore(key, answers.get(key), true);
        }

        SwingUtilities.invokeLater(() -> createAndShowGui(scores));
    }


    public void drawOneDot(Graphics2D g2, Double xV, Double yV) {
//        System.out.println("drawing one dot! [ " + xV + " " + yV + "]");
        List<Point> score = getAllPointsSorted();
        int x1 = calculateXPaddingToZeroPoint() + (int) (((getWidth() - LABEL_PADDING - 2 * GLOBAL_PADDING) / (score.get(graphSize() - 1).getX() - score.get(0).getX())) * xV + LABEL_PADDING + GLOBAL_PADDING);
        int y1 = getHeight() - calculateYPaddingToZeroPoint() - (int) ((getHeight() - LABEL_PADDING - 2 * GLOBAL_PADDING) / (getMaxScore() - getMinScore()) * yV + GLOBAL_PADDING + LABEL_PADDING);
        Point point = new Point(x1, y1);
//        System.out.println("calculated : " + x1 + " " + y1);
//        System.out.println((int) ((getHeight() - LABEL_PADDING - 2 * GLOBAL_PADDING) / (getMaxScore() - getMinScore()) * yV + GLOBAL_PADDING));
//        System.out.println( calculateYPaddingToZeroPoint());
        g2.setColor(Color.GREEN);
        int pointW = POINT_WIDTH + 4;
        int x = (int) (point.x - pointW / 2);
        int y = (int) (point.y - pointW / 2);
        g2.fillOval(x, y, pointW, pointW);
        g2.setColor(LABEL_COLOR);
        g2.drawString("x=" + DoubleUtil.round(xV, 0.0001d) + ", y=" + DoubleUtil.round(yV, 0.01d), x, y + g2.getFontMetrics().getHeight() + 5);

    }

}