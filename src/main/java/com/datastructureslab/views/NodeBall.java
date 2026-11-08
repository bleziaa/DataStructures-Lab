package com.datastructureslab.views;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * Bola (o celda) que representa un elemento de una estructura de datos.
 * Solo se encarga de su aspecto; la lógica vive en el paquete structures.
 */
public class NodeBall extends StackPane {
    public enum State { EMPTY, NORMAL, ACTIVE, VISITED, FOUND, PENDING }

    public static final double BALL_SIZE = 52;
    public static final double CELL_SIZE = 60;

    private final Shape shape;
    private final Label valueLabel = new Label();
    private final Label subLabel = new Label();
    private State state = State.NORMAL;

    public NodeBall(String text, boolean square) {
        double size = square ? CELL_SIZE : BALL_SIZE;
        if (square) {
            Rectangle r = new Rectangle(size, size);
            r.setArcWidth(16);
            r.setArcHeight(16);
            shape = r;
        } else {
            shape = new Circle(size / 2);
        }
        valueLabel.getStyleClass().add("node-number");
        subLabel.getStyleClass().add("node-sub");
        VBox texts = new VBox(0, valueLabel, subLabel);
        texts.setAlignment(Pos.CENTER);
        texts.setMouseTransparent(true);
        getChildren().addAll(shape, texts);
        setPrefSize(size, size);
        setMinSize(size, size);
        setMaxSize(size, size);
        setValue(text);
        setState(text == null || text.isEmpty() ? State.EMPTY : State.NORMAL);
    }

    public static NodeBall circle(int value) {
        return new NodeBall(String.valueOf(value), false);
    }

    public void setValue(String text) {
        valueLabel.setText(text == null ? "" : text);
    }

    public String getValueText() {
        return valueLabel.getText();
    }

    public boolean isFilled() {
        return !valueLabel.getText().isEmpty();
    }

    public void setSub(String text) {
        subLabel.setText(text == null ? "" : text);
    }

    public State getState() {
        return state;
    }

    /** Estado de reposo de una bola: lleno = normal, vacío = hueco punteado. */
    public static State restoreOf(NodeBall b) {
        return b.isFilled() ? State.NORMAL : State.EMPTY;
    }

    public void setState(State s) {
        this.state = s;
        shape.getStrokeDashArray().clear();
        shape.setEffect(null);
        Color textColor = Color.WHITE;
        switch (s) {
            case EMPTY -> {
                shape.setFill(Color.web("#171320"));
                shape.setStroke(Color.web("#5b4a7a"));
                shape.setStrokeWidth(1.6);
                shape.getStrokeDashArray().addAll(6.0, 5.0);
            }
            case NORMAL -> {
                shape.setFill(Color.web("#7c3aed"));
                shape.setStroke(Color.web("#a78bfa"));
                shape.setStrokeWidth(2);
                shape.setEffect(new DropShadow(10, Color.web("#7c3aed88")));
            }
            case ACTIVE -> {
                shape.setFill(Color.web("#e9d5ff"));
                shape.setStroke(Color.web("#7c3aed"));
                shape.setStrokeWidth(3.5);
                shape.setEffect(new DropShadow(18, Color.web("#c084fc")));
                textColor = Color.web("#0d0b13");
            }
            case VISITED -> {
                shape.setFill(Color.web("#4c1d95"));
                shape.setStroke(Color.web("#c4b5fd"));
                shape.setStrokeWidth(2.5);
            }
            case FOUND -> {
                shape.setFill(Color.WHITE);
                shape.setStroke(Color.web("#7c3aed"));
                shape.setStrokeWidth(4);
                shape.setEffect(new DropShadow(22, Color.web("#a855f7")));
                textColor = Color.web("#0d0b13");
            }
            case PENDING -> {
                shape.setFill(Color.web("#2e1065"));
                shape.setStroke(Color.web("#a78bfa"));
                shape.setStrokeWidth(2);
                shape.getStrokeDashArray().addAll(5.0, 4.0);
            }
        }
        valueLabel.setTextFill(textColor);
        subLabel.setTextFill(s == State.ACTIVE || s == State.FOUND ? Color.web("#4c1d95") : Color.web("#d8ccff"));
    }
}
