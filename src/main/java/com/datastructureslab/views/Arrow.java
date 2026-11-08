package com.datastructureslab.views;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

/** Línea con punta de flecha opcional, usada para conexiones entre nodos. */
public class Arrow extends Group {
    private final Line line = new Line();
    private final Polygon head = new Polygon();
    private final double x1;
    private final double y1;
    private final double x2;
    private final double y2;

    /**
     * Crea una conexión entre dos bolas de radio r1 y r2, recortada para que
     * empiece y termine en el borde de cada una.
     */
    public static Arrow between(double cx1, double cy1, double cx2, double cy2,
                                double r1, double r2, boolean withHead, double sideOffset) {
        double dx = cx2 - cx1;
        double dy = cy2 - cy1;
        double len = Math.max(1, Math.hypot(dx, dy));
        double ux = dx / len;
        double uy = dy / len;
        double ox = -uy * sideOffset;
        double oy = ux * sideOffset;
        return new Arrow(cx1 + ux * r1 + ox, cy1 + uy * r1 + oy,
                cx2 - ux * (r2 + 2) + ox, cy2 - uy * (r2 + 2) + oy, withHead);
    }

    public Arrow(double x1, double y1, double x2, double y2, boolean withHead) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        Color color = Color.web("#a78bfa");
        line.setStartX(x1);
        line.setStartY(y1);
        line.setEndX(x2);
        line.setEndY(y2);
        line.setStroke(color);
        line.setStrokeWidth(2.4);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        getChildren().add(line);
        if (withHead) {
            double angle = Math.atan2(y2 - y1, x2 - x1);
            double size = 11;
            head.getPoints().addAll(
                    x2, y2,
                    x2 - size * Math.cos(angle - Math.PI / 7), y2 - size * Math.sin(angle - Math.PI / 7),
                    x2 - size * Math.cos(angle + Math.PI / 7), y2 - size * Math.sin(angle + Math.PI / 7));
            head.setFill(color);
            getChildren().add(head);
        }
        setMouseTransparent(true);
    }

    /** Aparece dibujándose desde el origen hasta el destino. */
    public Animation drawIn(double ms) {
        line.setEndX(x1);
        line.setEndY(y1);
        head.setOpacity(0);
        Timeline grow = new Timeline(new KeyFrame(Duration.millis(ms),
                new KeyValue(line.endXProperty(), x2, Interpolator.EASE_OUT),
                new KeyValue(line.endYProperty(), y2, Interpolator.EASE_OUT)));
        FadeTransition headFade = new FadeTransition(Duration.millis(200), head);
        headFade.setToValue(1);
        headFade.setDelay(Duration.millis(ms * 0.8));
        return new ParallelTransition(grow, headFade);
    }
}
