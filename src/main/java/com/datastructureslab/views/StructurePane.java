package com.datastructureslab.views;

import com.datastructureslab.animations.Anim;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntConsumer;

/**
 * Pantalla base de cada módulo: encabezado, lienzo de animación, panel de
 * operaciones y panel de información. Los controladores concretos solo
 * añaden sus operaciones y su forma de dibujar la estructura.
 */
public abstract class StructurePane extends BorderPane {
    public static final int MAX_ABS_VALUE = 9999;

    protected final Pane canvas = new Pane();
    protected final VBox ops = new VBox(9);
    protected final Label info = new Label();
    protected final Label status = new Label();
    private final Label badge = new Label("● LISTO");
    private final Button resetButton = new Button("Limpiar / reiniciar");
    private final List<Animation> moves = new ArrayList<>();
    private int busy = 0;

    protected StructurePane(String eyebrow, String title, String rule, Runnable onHome) {
        getStyleClass().add("workspace");
        setPadding(new Insets(24, 26, 22, 26));

        Label eyebrowLabel = new Label(eyebrow);
        eyebrowLabel.getStyleClass().add("eyebrow");
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("page-title");
        Label ruleLabel = new Label(rule);
        ruleLabel.getStyleClass().add("muted");
        ruleLabel.setWrapText(true);
        VBox titles = new VBox(3, eyebrowLabel, titleLabel, ruleLabel);
        HBox.setHgrow(titles, Priority.ALWAYS);

        badge.getStyleClass().add("status-badge");
        resetButton.getStyleClass().add("secondary-button");
        resetButton.setOnAction(e -> guard(() -> {
            reset();
            relayout(false);
        }));
        Button home = new Button("← Menú principal");
        home.getStyleClass().add("ghost-button");
        home.setOnAction(e -> onHome.run());
        HBox actions = new HBox(10, badge, resetButton, home);
        actions.setAlignment(Pos.CENTER_RIGHT);
        HBox header = new HBox(16, titles, actions);
        header.setAlignment(Pos.TOP_LEFT);
        header.setPadding(new Insets(0, 0, 16, 0));
        setTop(header);

        canvas.setMinSize(0, 0);
        StackPane wrap = new StackPane(canvas);
        wrap.getStyleClass().add("canvas-wrap");
        wrap.setPadding(Insets.EMPTY);
        wrap.setMinSize(300, 300);
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(wrap.widthProperty());
        clip.heightProperty().bind(wrap.heightProperty());
        wrap.setClip(clip);
        canvas.prefWidthProperty().bind(wrap.widthProperty());
        canvas.prefHeightProperty().bind(wrap.heightProperty());
        canvas.widthProperty().addListener((o, a, b) -> refreshLayoutIfIdle());
        canvas.heightProperty().addListener((o, a, b) -> refreshLayoutIfIdle());
        setCenter(wrap);

        Label opsTitle = new Label("Operaciones");
        opsTitle.getStyleClass().add("section-title");
        ops.getChildren().add(opsTitle);
        ops.getStyleClass().add("panel");
        ops.setPadding(new Insets(16));

        Label infoTitle = new Label("Información y resultados");
        infoTitle.getStyleClass().add("section-title");
        info.getStyleClass().add("info-text");
        info.setWrapText(true);
        status.setWrapText(true);
        status.getStyleClass().add("status-text");
        VBox infoBox = new VBox(8, infoTitle, info, status);
        infoBox.getStyleClass().add("panel");
        infoBox.setPadding(new Insets(16));

        VBox side = new VBox(14, ops, infoBox);
        side.setPadding(new Insets(0, 4, 0, 0));
        ScrollPane scroll = new ScrollPane(side);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("side-scroll");
        scroll.setPrefWidth(318);
        scroll.setMinWidth(318);
        BorderPane.setMargin(scroll, new Insets(0, 0, 0, 18));
        setRight(scroll);
    }

    /** Elimina todos los datos y nodos de la estructura. */
    protected abstract void reset();

    /** Coloca los nodos según el tamaño del lienzo; con animate=true los desplaza suavemente. */
    protected abstract void relayout(boolean animate);

    private void refreshLayoutIfIdle() {
        if (busy == 0) relayout(false);
    }

    // ---------- Construcción de controles ----------

    protected TextField input(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.getStyleClass().add("value-input");
        return f;
    }

    protected Label section(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("sidebar-caption");
        l.setPadding(new Insets(6, 0, 0, 0));
        return l;
    }

    protected Button action(String text, String styleClass, Runnable r) {
        Button b = new Button(text);
        b.getStyleClass().add(styleClass);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setOnAction(e -> guard(r));
        return b;
    }

    protected Button primary(String text, Runnable r) {
        return action(text, "primary-button", r);
    }

    protected Button secondary(String text, Runnable r) {
        return action(text, "secondary-button", r);
    }

    protected Label tag(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("tag");
        l.setAlignment(Pos.CENTER);
        l.setPrefSize(64, 22);
        return l;
    }

    // ---------- Validación y mensajes ----------

    /** Ejecuta una operación sin dejar que una entrada incorrecta cierre la aplicación. */
    protected void guard(Runnable r) {
        try {
            r.run();
        } catch (Exception ex) {
            String msg = ex.getMessage();
            error(msg != null && !msg.isBlank() ? msg : "No se pudo completar la operación.");
        }
    }

    protected int readInt(TextField f, String name) {
        String t = f.getText() == null ? "" : f.getText().trim();
        if (t.isEmpty()) throw new IllegalArgumentException("El campo «" + name + "» está vacío.");
        try {
            return Integer.parseInt(t);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("«" + t + "» no es un número entero válido para «" + name + "».");
        }
    }

    /** Número entero positivo con un máximo para que la visualización siga siendo legible. */
    protected int readPositive(TextField f, String name, int max) {
        int v = readInt(f, name);
        if (v < 0) throw new IllegalArgumentException("«" + name + "» no puede ser negativo.");
        if (v == 0) throw new IllegalArgumentException("«" + name + "» debe ser mayor que cero.");
        if (v > max) throw new IllegalArgumentException("Para poder visualizarlo, «" + name + "» debe ser como máximo " + max + ".");
        return v;
    }

    protected int readValue(TextField f, String name) {
        int v = readInt(f, name);
        if (Math.abs(v) > MAX_ABS_VALUE) {
            throw new IllegalArgumentException("«" + name + "» debe estar entre -" + MAX_ABS_VALUE + " y " + MAX_ABS_VALUE + ".");
        }
        return v;
    }

    protected void ok(String msg) {
        status.setText("✔ " + msg);
        status.setStyle("-fx-text-fill: #c4b5fd;");
    }

    protected void warn(String msg) {
        status.setText("⚠ " + msg);
        status.setStyle("-fx-text-fill: #fbbf24;");
    }

    protected void error(String msg) {
        status.setText("✖ " + msg);
        status.setStyle("-fx-text-fill: #f87171;");
    }

    protected void setInfo(String text) {
        info.setText(text);
    }

    // ---------- Animaciones ----------

    /** Reproduce una animación bloqueando las operaciones hasta que termine (también las anidadas). */
    protected void play(Animation a) {
        busy++;
        updateBusy();
        EventHandler<ActionEvent> previous = a.getOnFinished();
        a.setOnFinished(e -> {
            try {
                if (previous != null) previous.handle(e);
            } finally {
                busy--;
                updateBusy();
            }
        });
        a.play();
    }

    private void updateBusy() {
        boolean working = busy > 0;
        ops.setDisable(working);
        resetButton.setDisable(working);
        badge.setText(working ? "● ANIMANDO" : "● LISTO");
    }

    /** Mueve (o coloca) un nodo para que su centro quede en (cx, cy). */
    protected void place(Region n, double cx, double cy, boolean animate) {
        double x = cx - n.getPrefWidth() / 2;
        double y = cy - n.getPrefHeight() / 2;
        if (animate) {
            moves.add(Anim.moveTo(n, x, y, 520));
        } else {
            n.setLayoutX(x);
            n.setLayoutY(y);
        }
    }

    /** Reproduce en paralelo los movimientos acumulados por place(...). */
    protected void flushMoves() {
        if (moves.isEmpty()) return;
        ParallelTransition p = new ParallelTransition(moves.toArray(new Animation[0]));
        moves.clear();
        play(p);
    }

    protected Animation flash(NodeBall b, NodeBall.State end) {
        b.setState(NodeBall.State.ACTIVE);
        PauseTransition hold = new PauseTransition(Duration.millis(500));
        hold.setOnFinished(e -> b.setState(end));
        return new SequentialTransition(Anim.pulse(b), hold);
    }

    /** Ilumina las bolas una tras otra; la última queda como encontrada si foundAtEnd. */
    protected Animation walk(List<NodeBall> order, boolean foundAtEnd,
                             Function<NodeBall, NodeBall.State> restore, IntConsumer onVisit) {
        List<Runnable> a = new ArrayList<>();
        for (int k = 0; k < order.size(); k++) {
            final int i = k;
            a.add(() -> {
                if (i > 0) order.get(i - 1).setState(NodeBall.State.VISITED);
                order.get(i).setState(NodeBall.State.ACTIVE);
                Anim.pulse(order.get(i)).play();
                if (onVisit != null) onVisit.accept(i);
            });
        }
        a.add(() -> {
            if (!order.isEmpty()) {
                order.get(order.size() - 1).setState(foundAtEnd ? NodeBall.State.FOUND : NodeBall.State.VISITED);
            }
        });
        a.add(() -> { });
        a.add(() -> {
            for (NodeBall b : order) b.setState(restore.apply(b));
        });
        return Anim.steps(a, 520);
    }

    protected void addToCanvas(Node... nodes) {
        canvas.getChildren().addAll(nodes);
    }
}
