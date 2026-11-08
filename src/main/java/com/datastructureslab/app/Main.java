package com.datastructureslab.app;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Punto de entrada de DataStructures Lab.
 * Esta primera versión levanta la interfaz y una demostración animada
 * de nodos. Las implementaciones definitivas de las siete estructuras
 * se incorporarán en los siguientes módulos.
 */
public class Main extends Application {
    private final List<Integer> values = new ArrayList<>();
    private final FlowPane nodeCanvas = new FlowPane(14, 18);
    private final Label status = new Label("Listo para comenzar.");
    private final Label structureDescription = new Label();
    private final TextField valueField = new TextField();
    private final Label countLabel = new Label("0 elementos");
    private String selectedStructure = "Arreglo";

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setLeft(buildSidebar());
        root.setCenter(buildWorkspace());

        Scene scene = new Scene(root, 1180, 760);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        stage.setTitle("DataStructures Lab");
        stage.setMinWidth(980);
        stage.setMinHeight(650);
        stage.setScene(scene);
        stage.show();
        selectStructure("Arreglo");
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(12);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(26, 18, 22, 18));
        sidebar.setPrefWidth(245);

        Label brand = new Label("◈  DATASTRUCTURES");
        brand.getStyleClass().add("brand");
        Label subBrand = new Label("LABORATORIO VISUAL");
        subBrand.getStyleClass().add("sidebar-caption");

        VBox brandBox = new VBox(5, brand, subBrand);
        brandBox.setPadding(new Insets(0, 0, 24, 4));

        Label menuTitle = new Label("ESTRUCTURAS");
        menuTitle.getStyleClass().add("sidebar-caption");

        VBox menu = new VBox(7);
        String[] structures = {"Arreglo", "Matriz", "Lista enlazada", "Pila", "Cola", "Árbol binario", "Grafo"};
        for (String name : structures) {
            Button button = new Button(name);
            button.getStyleClass().add("nav-button");
            button.setMaxWidth(Double.MAX_VALUE);
            button.setAlignment(Pos.CENTER_LEFT);
            button.setOnAction(e -> selectStructure(name));
            menu.getChildren().add(button);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Label version = new Label("JAVA 17  •  JAVAFX");
        version.getStyleClass().add("sidebar-caption");
        sidebar.getChildren().addAll(brandBox, menuTitle, menu, spacer, version);
        return sidebar;
    }

    private VBox buildWorkspace() {
        VBox workspace = new VBox(22);
        workspace.setPadding(new Insets(30, 34, 28, 34));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        VBox titles = new VBox(5);
        Label eyebrow = new Label("ENTORNO DE APRENDIZAJE");
        eyebrow.getStyleClass().add("eyebrow");
        Label title = new Label("Visualiza cómo funcionan");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Manipula datos y observa cada movimiento en tiempo real.");
        subtitle.getStyleClass().add("muted");
        titles.getChildren().addAll(eyebrow, title, subtitle);
        Region push = new Region();
        HBox.setHgrow(push, Priority.ALWAYS);
        Label badge = new Label("●  SISTEMA LISTO");
        badge.getStyleClass().add("status-badge");
        header.getChildren().addAll(titles, push, badge);

        VBox visualization = new VBox(18);
        visualization.getStyleClass().add("panel");
        visualization.setPadding(new Insets(24));
        HBox panelHeader = new HBox();
        panelHeader.setAlignment(Pos.CENTER_LEFT);
        VBox panelTitles = new VBox(5);
        Label panelTitle = new Label("Área de visualización");
        panelTitle.getStyleClass().add("section-title");
        structureDescription.getStyleClass().add("muted");
        panelTitles.getChildren().addAll(panelTitle, structureDescription);
        Region push2 = new Region();
        HBox.setHgrow(push2, Priority.ALWAYS);
        countLabel.getStyleClass().add("count-badge");
        panelHeader.getChildren().addAll(panelTitles, push2, countLabel);

        nodeCanvas.setAlignment(Pos.CENTER_LEFT);
        nodeCanvas.setPrefWrapLength(760);
        nodeCanvas.setMinHeight(250);
        nodeCanvas.getStyleClass().add("node-canvas");
        Label hint = new Label("Los elementos aparecerán aquí como nodos animados.");
        hint.getStyleClass().add("muted");
        StackPane canvasWrap = new StackPane(nodeCanvas);
        canvasWrap.setMinHeight(260);
        canvasWrap.getStyleClass().add("canvas-wrap");
        visualization.getChildren().addAll(panelHeader, new Separator(), canvasWrap, hint);
        VBox.setVgrow(visualization, Priority.ALWAYS);

        VBox controls = new VBox(14);
        controls.getStyleClass().add("panel");
        controls.setPadding(new Insets(22));
        Label controlsTitle = new Label("Operaciones");
        controlsTitle.getStyleClass().add("section-title");
        HBox actionRow = new HBox(12);
        actionRow.setAlignment(Pos.CENTER_LEFT);
        valueField.setPromptText("Escribe un valor");
        valueField.getStyleClass().add("value-input");
        valueField.setPrefWidth(220);
        Button insert = new Button("＋  Insertar");
        insert.getStyleClass().add("primary-button");
        insert.setOnAction(e -> insertValue());
        Button remove = new Button("−  Eliminar último");
        remove.getStyleClass().add("secondary-button");
        remove.setOnAction(e -> removeValue());
        Button search = new Button("⌕  Buscar");
        search.getStyleClass().add("secondary-button");
        search.setOnAction(e -> searchValue());
        Button clear = new Button("Limpiar");
        clear.getStyleClass().add("ghost-button");
        clear.setOnAction(e -> clearValues());
        actionRow.getChildren().addAll(valueField, insert, remove, search, clear);
        status.getStyleClass().add("status-text");
        controls.getChildren().addAll(controlsTitle, actionRow, status);

        workspace.getChildren().addAll(header, visualization, controls);
        return workspace;
    }

    private void selectStructure(String name) {
        selectedStructure = name;
        values.clear();
        nodeCanvas.getChildren().clear();
        countLabel.setText("0 elementos");
        structureDescription.setText(descriptionFor(name));
        status.setText("Módulo seleccionado: " + name + ". Inserta un valor para comenzar.");
    }

    private String descriptionFor(String name) {
        return switch (name) {
            case "Arreglo" -> "Elementos almacenados en posiciones consecutivas.";
            case "Matriz" -> "Datos organizados en filas y columnas.";
            case "Lista enlazada" -> "Nodos conectados mediante enlaces.";
            case "Pila" -> "Último en entrar, primero en salir (LIFO).";
            case "Cola" -> "Primero en entrar, primero en salir (FIFO).";
            case "Árbol binario" -> "Organización jerárquica mediante ramas.";
            case "Grafo" -> "Vértices conectados por aristas.";
            default -> "";
        };
    }

    private void insertValue() {
        Integer value = readValue();
        if (value == null) return;
        values.add(value);
        Circle circle = new Circle(28, Color.web("#8B5CF6"));
        Label number = new Label(String.valueOf(value));
        number.getStyleClass().add("node-number");
        StackPane node = new StackPane(circle, number);
        node.getStyleClass().add("node");
        nodeCanvas.getChildren().add(node);

        ScaleTransition pop = new ScaleTransition(Duration.millis(260), node);
        pop.setFromX(0.15); pop.setFromY(0.15);
        pop.setToX(1); pop.setToY(1);
        FadeTransition fade = new FadeTransition(Duration.millis(260), node);
        fade.setFromValue(0); fade.setToValue(1);
        pop.play(); fade.play();

        updateCount();
        status.setText("Se insertó el valor " + value + " en " + selectedStructure + ".");
        valueField.clear();
    }

    private void removeValue() {
        if (values.isEmpty()) {
            status.setText("No se puede eliminar: la estructura está vacía.");
            return;
        }
        int removed = values.remove(values.size() - 1);
        if (!nodeCanvas.getChildren().isEmpty()) {
            var node = nodeCanvas.getChildren().get(nodeCanvas.getChildren().size() - 1);
            ScaleTransition shrink = new ScaleTransition(Duration.millis(220), node);
            shrink.setToX(0.1); shrink.setToY(0.1);
            FadeTransition fade = new FadeTransition(Duration.millis(220), node);
            fade.setToValue(0);
            fade.setOnFinished(e -> nodeCanvas.getChildren().remove(node));
            shrink.play(); fade.play();
        }
        updateCount();
        status.setText("Se eliminó el último valor: " + removed + ".");
    }

    private void searchValue() {
        Integer value = readValue();
        if (value == null) return;
        int index = values.indexOf(value);
        if (index < 0) {
            status.setText("El valor " + value + " no se encuentra.");
            return;
        }
        if (index < nodeCanvas.getChildren().size()) {
            var node = nodeCanvas.getChildren().get(index);
            ScaleTransition pulse = new ScaleTransition(Duration.millis(350), node);
            pulse.setFromX(1); pulse.setFromY(1);
            pulse.setToX(1.22); pulse.setToY(1.22);
            pulse.setCycleCount(2); pulse.setAutoReverse(true);
            pulse.play();
        }
        status.setText("Valor " + value + " encontrado en la posición " + index + ".");
    }

    private void clearValues() {
        values.clear();
        nodeCanvas.getChildren().clear();
        updateCount();
        status.setText("Visualización limpiada.");
    }

    private Integer readValue() {
        try {
            String raw = valueField.getText().trim();
            if (raw.isEmpty()) throw new NumberFormatException();
            return Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            status.setText("Escribe un número entero válido.");
            valueField.requestFocus();
            return null;
        }
    }

    private void updateCount() {
        countLabel.setText(values.size() + (values.size() == 1 ? " elemento" : " elementos"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
