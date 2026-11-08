package com.datastructureslab.views;

import javafx.beans.binding.DoubleBinding;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/** Nodo visual reutilizable: una bola (o celda cuadrada) con un texto y varios estados de color. */
public class BallView extends StackPane {

    public enum State { NORMAL, ACTIVE, FOUND, VISITED, PENDING, EMPTY }

    public static final double DEFAULT_RADIUS = 26;

    private final double radius;
    private final Shape shape;
    private final Label label = new Label();
    private State state = State.NORMAL;

    public BallView(String text, double radius, boolean square) {
        this.radius = radius;
        if (square) {
            Rectangle r = new Rectangle(radius * 2, radius * 2);
            r.setArcWidth(16);
            r.setArcHeight(16);
            shape = r;
        } else {
            shape = new Circle(radius);
        }
        shape.setStrokeWidth(2);
        setPrefSize(radius * 2, radius * 2);
        setMinSize(radius * 2, radius * 2);
        setMaxSize(radius * 2, radius * 2);
        getChildren().addAll(shape, label);
        setText(text);
        setState(State.NORMAL);
    }

    public BallView(String text) {
        this(text, DEFAULT_RADIUS, false);
    }

    public double radius() { return radius; }

    public DoubleBinding centerXBinding() { return layoutXProperty().add(radius); }

    public DoubleBinding centerYBinding() { return layoutYProperty().add(radius); }

    public double centerX() { return getLayoutX() + radius; }

    public double centerY() { return getLayoutY() + radius; }

    public void setCenter(double cx, double cy) {
        setLayoutX(cx - radius);
        setLayoutY(cy - radius);
    }

    public String getText() { return label.getText(); }

    public void setText(String text) {
        label.setText(text);
        applyLabelStyle();
    }

    public State getState() { return state; }

    public void setState(State s) {
        this.state = s;
        Color glow;
        shape.getStrokeDashArray().clear();
        switch (s) {
            case ACTIVE -> {
                shape.setStyle("-fx-fill: #ffffff; -fx-stroke: #e9d5ff;");
                glow = Color.web("#ffffff", 0.85);
            }
            case FOUND -> {
                shape.setStyle("-fx-fill: #d946ef; -fx-stroke: #fae8ff;");
                glow = Color.web("#d946ef", 0.9);
            }
            case VISITED -> {
                shape.setStyle("-fx-fill: #a78bfa; -fx-stroke: #ede9fe;");
                glow = Color.web("#a78bfa", 0.7);
            }
            case PENDING -> {
                shape.setStyle("-fx-fill: #2e1065; -fx-stroke: #e9d5ff;");
                shape.getStrokeDashArray().setAll(6.0, 4.0);
                glow = Color.web("#7c3aed", 0.5);
            }
            case EMPTY -> {
                shape.setStyle("-fx-fill: #14101d; -fx-stroke: #6d28d9;");
                shape.getStrokeDashArray().setAll(6.0, 5.0);
                glow = null;
            }
            default -> {
                shape.setStyle("-fx-fill: #7c3aed; -fx-stroke: #a78bfa;");
                glow = Color.web("#7c3aed", 0.55);
            }
        }
        shape.setEffect(glow == null ? null : new DropShadow(s == State.NORMAL ? 12 : 24, glow));
        applyLabelStyle();
    }

    private void applyLabelStyle() {
        String text = label.getText() == null ? "" : label.getText();
        int size = text.length() >= 5 ? 11 : text.length() == 4 ? 13 : 15;
        String color = switch (state) {
            case ACTIVE, VISITED -> "#1b0b3a";
            case EMPTY -> "#a78bfa";
            default -> "#ffffff";
        };
        label.setStyle("-fx-font-size: " + size + "px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
    }
}
