package com.datastructureslab.views;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

/** Línea (o flecha) entre dos nodos. Sigue a los nodos cuando estos se mueven. */
public class EdgeView extends Group {
    private static final Color NORMAL = Color.web("#a78bfa");
    private static final Color HIGHLIGHT = Color.web("#f0abfa");

    private final Line line = new Line();
    private final Polygon head = new Polygon();
    private final BallView from;
    private final BallView to;
    private final boolean arrow;
    private final double offset;
    private final DoubleProperty progress = new SimpleDoubleProperty(1);
    private final InvalidationListener listener = o -> update();

    public EdgeView(BallView from, BallView to, boolean arrow, boolean parallelOffset) {
        this.from = from;
        this.to = to;
        this.arrow = arrow;
        this.offset = parallelOffset ? 7 : 0;
        line.setStrokeWidth(2.4);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        head.setStrokeWidth(0);
        setMouseTransparent(true);
        getChildren().addAll(line, head);
        head.setVisible(arrow);
        paint(false);
        from.layoutXProperty().addListener(listener);
        from.layoutYProperty().addListener(listener);
        to.layoutXProperty().addListener(listener);
        to.layoutYProperty().addListener(listener);
        progress.addListener(listener);
        update();
    }

    public DoubleProperty progressProperty() { return progress; }

    public void setHighlight(boolean on) { paint(on); }

    private void paint(boolean on) {
        Color c = on ? HIGHLIGHT : NORMAL;
        line.setStroke(c);
        line.setStrokeWidth(on ? 3.6 : 2.4);
        head.setFill(c);
    }

    /** Dibuja la línea creciendo desde el origen hasta el destino. */
    public Timeline drawAnimation(double millis) {
        progress.set(0);
        Timeline t = new Timeline(new KeyFrame(Duration.millis(millis),
                new KeyValue(progress, 1.0, Interpolator.EASE_BOTH)));
        return t;
    }

    public void dispose() {
        from.layoutXProperty().removeListener(listener);
        from.layoutYProperty().removeListener(listener);
        to.layoutXProperty().removeListener(listener);
        to.layoutYProperty().removeListener(listener);
        progress.removeListener(listener);
    }

    private void update() {
        double x1 = from.centerX(), y1 = from.centerY();
        double x2 = to.centerX(), y2 = to.centerY();
        double dx = x2 - x1, dy = y2 - y1;
        double len = Math.hypot(dx, dy);
        if (len < 1) {
            line.setVisible(false);
            head.setVisible(false);
            return;
        }
        line.setVisible(true);
        double ux = dx / len, uy = dy / len;
        double nx = -uy, ny = ux;
        double sx = x1 + ux * from.radius() + nx * offset;
        double sy = y1 + uy * from.radius() + ny * offset;
        double ex = x2 - ux * (to.radius() + 2) + nx * offset;
        double ey = y2 - uy * (to.radius() + 2) + ny * offset;
        double p = progress.get();
        double px = sx + (ex - sx) * p;
        double py = sy + (ey - sy) * p;
        line.setStartX(sx);
        line.setStartY(sy);
        line.setEndX(px);
        line.setEndY(py);
        if (arrow) {
            head.setVisible(p > 0.97);
            double size = 11, half = 5.5;
            head.getPoints().setAll(
                    px, py,
                    px - ux * size + nx * half, py - uy * size + ny * half,
                    px - ux * size - nx * half, py - uy * size - ny * half);
        }
    }
}
