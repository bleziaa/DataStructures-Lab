package com.datastructureslab.controllers;

import com.datastructureslab.models.ValidationException;
import com.datastructureslab.structures.EmptyStructureException;
import com.datastructureslab.views.BallView;
import javafx.animation.Animation;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Esqueleto común de los siete módulos: encabezado, área de visualización,
 * panel de operaciones, panel de información, estado y bloqueo durante animaciones.
 * Cada módulo solo aporta su estructura real, sus operaciones y sus animaciones.
 */
public abstract class AbstractModule extends BorderPane {

    @FunctionalInterface
    protected interface Body {
        void run() throws Exception;
    }

    protected final Pane canvas = new Pane();
    protected final VBox opsBox = new VBox(10);

    private final ScrollPane scroll = new ScrollPane(canvas);
    private final Label statusBadge = new Label();
    private final Label resultLabel = new Label("Listo para comenzar.");
    private final Label statsLabel = new Label();
    private final ListView<String> logView = new ListView<>();
    private final List<Control> lockable = new ArrayList<>();
    private boolean busy;
    private Animation current;

    protected AbstractModule(String title, String subtitle, String concept, Runnable onHome) {
        getStyleClass().add("module");
        setPadding(new Insets(24, 28, 22, 28));

        Label eyebrow = new Label("MÓDULO");
        eyebrow.getStyleClass().add("eyebrow");
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("page-title");
        Label sub = new Label(subtitle);
        sub.getStyleClass().add("muted");
        sub.setWrapText(true);
        VBox titles = new VBox(3, eyebrow, titleLabel, sub);
        HBox.setHgrow(titles, Priority.ALWAYS);

        setBadge("●  LISTO", "status-ok");
        Button home = new Button("←  Menú principal");
        home.getStyleClass().add("secondary-button");
        home.setOnAction(e -> onHome.run());
        Button restart = new Button("↺  Reiniciar");
        restart.getStyleClass().add("ghost-button");
        restart.setOnAction(e -> restart());

        HBox header = new HBox(12, titles, statusBadge, home, restart);
        header.setAlignment(Pos.CENTER_LEFT);

        // Área de visualización
        canvas.getStyleClass().add("node-canvas");
        canvas.setMinSize(0, 0);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setMinHeight(230);
        scroll.getStyleClass().add("canvas-scroll");
        scroll.viewportBoundsProperty().addListener((o, a, b) -> {
            if (!busy && b.getWidth() > 50 && b.getHeight() > 50) relayout(false);
        });
        Label vizTitle = new Label("Área de visualización");
        vizTitle.getStyleClass().add("section-title");
        statsLabel.getStyleClass().add("count-badge");
        Region push = new Region();
        HBox.setHgrow(push, Priority.ALWAYS);
        HBox vizHeader = new HBox(vizTitle, push, statsLabel);
        vizHeader.setAlignment(Pos.CENTER_LEFT);
        VBox viz = new VBox(12, vizHeader, scroll);
        viz.getStyleClass().add("panel");
        viz.setPadding(new Insets(16, 18, 18, 18));
        VBox.setVgrow(scroll, Priority.ALWAYS);
        VBox.setVgrow(viz, Priority.ALWAYS);

        // Panel de operaciones
        Label opsTitle = new Label("Operaciones");
        opsTitle.getStyleClass().add("section-title");
        ScrollPane opsScroll = new ScrollPane(opsBox);
        opsScroll.setFitToWidth(true);
        opsScroll.getStyleClass().add("ops-scroll");
        VBox ops = new VBox(10, opsTitle, opsScroll);
        ops.getStyleClass().add("panel");
        ops.setPadding(new Insets(16, 18, 16, 18));
        VBox.setVgrow(opsScroll, Priority.ALWAYS);
        HBox.setHgrow(ops, Priority.ALWAYS);
        ops.setPrefWidth(560);

        // Panel de información
        Label infoTitle = new Label("Información y resultados");
        infoTitle.getStyleClass().add("section-title");
        Label conceptLabel = new Label(concept);
        conceptLabel.getStyleClass().add("concept-text");
        conceptLabel.setWrapText(true);
        resultLabel.getStyleClass().add("result-text");
        resultLabel.setWrapText(true);
        logView.getStyleClass().add("log-view");
        logView.setPrefHeight(60);
        logView.setMouseTransparent(false);
        VBox info = new VBox(8, infoTitle, conceptLabel, resultLabel, logView);
        info.getStyleClass().add("panel");
        info.setPadding(new Insets(16, 18, 16, 18));
        VBox.setVgrow(logView, Priority.ALWAYS);
        HBox.setHgrow(info, Priority.ALWAYS);
        info.setPrefWidth(420);

        HBox bottom = new HBox(14, ops, info);
        bottom.setPrefHeight(235);
        bottom.setMinHeight(200);
        bottom.setMaxHeight(260);

        VBox center = new VBox(14, header, viz, bottom);
        setCenter(center);
    }

    // ---------- ganchos para los módulos ----------

    /** Recalcula posiciones de los nodos (sin animar) según el tamaño actual de la vista. */
    protected abstract void relayout(boolean animate);

    /** Vacía la estructura real y la visualización. */
    protected abstract void onReset();

    // ---------- utilidades de interfaz ----------

    protected double viewW() {
        return Math.max(scroll.getViewportBounds().getWidth(), 300);
    }

    protected double viewH() {
        return Math.max(scroll.getViewportBounds().getHeight(), 200);
    }

    protected void setCanvasMin(double w, double h) {
        canvas.setMinSize(w, h);
    }

    protected void clearCanvas() {
        canvas.getChildren().clear();
        canvas.setMinSize(0, 0);
    }

    protected void setStats(String text) {
        statsLabel.setText(text);
    }

    protected TextField field(String prompt, double width) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setPrefWidth(width);
        f.getStyleClass().add("value-input");
        lockable.add(f);
        return f;
    }

    protected Button action(String text, String styleClass, Body body) {
        Button b = new Button(text);
        b.getStyleClass().add(styleClass);
        b.setOnAction(e -> run(body));
        lockable.add(b);
        return b;
    }

    protected void bindEnter(TextField f, Button b) {
        f.setOnAction(e -> b.fire());
    }

    protected void addRow(String caption, Node... nodes) {
        Label c = new Label(caption);
        c.getStyleClass().add("op-caption");
        FlowPane row = new FlowPane(10, 8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getChildren().addAll(nodes);
        opsBox.getChildren().addAll(c, row);
    }

    protected void resetStates(Collection<BallView> balls) {
        for (BallView b : balls) b.setState(BallView.State.NORMAL);
    }

    // ---------- estado, mensajes y animación ----------

    protected void info(String message) {
        resultLabel.getStyleClass().setAll("result-text");
        resultLabel.setText(message);
        log(message);
        if (!busy) setBadge("●  LISTO", "status-ok");
    }

    /** Texto intermedio durante una animación (no se guarda en la bitácora). */
    protected void note(String message) {
        resultLabel.getStyleClass().setAll("result-text");
        resultLabel.setText(message);
    }

    protected void error(String message) {
        resultLabel.getStyleClass().setAll("result-text", "result-error");
        resultLabel.setText("⚠  " + message);
        log("⚠  " + message);
        setBadge("●  ERROR", "status-error");
    }

    private void log(String message) {
        logView.getItems().add(0, message);
        if (logView.getItems().size() > 40) logView.getItems().remove(40);
    }

    private void setBadge(String text, String styleClass) {
        statusBadge.setText(text);
        statusBadge.getStyleClass().setAll("status-badge", styleClass);
    }

    private void run(Body body) {
        if (busy) return;
        try {
            body.run();
        } catch (ValidationException | EmptyStructureException | IllegalArgumentException
                 | IndexOutOfBoundsException | IllegalStateException e) {
            error(e.getMessage());
        } catch (Exception e) {
            error("Ocurrió un problema inesperado y se evitó cerrar la aplicación: " + e.getMessage());
        }
    }

    protected void play(Animation animation) {
        busy = true;
        setLocked(true);
        setBadge("●  ANIMANDO", "status-busy");
        current = animation;
        animation.setOnFinished(e -> finishAnimation());
        animation.play();
    }

    private void finishAnimation() {
        busy = false;
        current = null;
        setLocked(false);
        setBadge("●  LISTO", "status-ok");
        relayout(false);
    }

    private void setLocked(boolean locked) {
        for (Control c : lockable) c.setDisable(locked);
    }

    protected boolean isBusy() {
        return busy;
    }

    /** Detiene cualquier animación, vacía la estructura y desbloquea la interfaz. */
    public void restart() {
        if (current != null) current.stop();
        current = null;
        busy = false;
        setLocked(false);
        onReset();
        setBadge("●  LISTO", "status-ok");
        info("Estructura reiniciada.");
    }

    /** Recuperación ante un error interno inesperado. */
    public void recover(Throwable t) {
        restart();
        error("Error interno recuperado (" + t.getClass().getSimpleName() + "). La estructura se reinició.");
    }
}
