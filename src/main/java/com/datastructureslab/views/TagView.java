package com.datastructureslab.views;

import javafx.scene.control.Label;

/** Etiqueta pequeña para marcar posiciones: HEAD, TOP, FRONT, REAR, índices... */
public final class TagView {
    private TagView() {}

    public static Label tag(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("tag");
        l.setMouseTransparent(true);
        return l;
    }

    public static Label index(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("index-label");
        l.setMouseTransparent(true);
        return l;
    }

    /** Coloca la etiqueta centrada horizontalmente sobre (cx) con su borde superior en y. */
    public static void placeCentered(Label l, double cx, double y) {
        double w = l.getText().length() * 7.4 + 18;
        l.setLayoutX(cx - w / 2);
        l.setLayoutY(y);
    }
}
