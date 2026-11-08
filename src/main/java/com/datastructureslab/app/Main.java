package com.datastructureslab.app;

import com.datastructureslab.controllers.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Entrada principal: navegación real hacia los siete laboratorios interactivos. */
public class Main extends Application {
    private BorderPane root;
    private VBox sidebar;
    private Stage stage;

    @Override public void start(Stage stage) {
        this.stage=stage;
        root=new BorderPane();
        root.getStyleClass().add("app-root");
        sidebar=buildSidebar();
        root.setLeft(sidebar);
        showHome();
        Scene scene=new Scene(root,1280,820);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        stage.setTitle("DataStructures Lab · Laboratorio visual");
        stage.setMinWidth(1024);
        stage.setMinHeight(700);
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildSidebar() {
        VBox side=new VBox(11);
        side.getStyleClass().add("sidebar");
        side.setPadding(new Insets(26,17,22,17));
        side.setPrefWidth(235);
        Label brand=new Label("◈  DATASTRUCTURES");
        brand.getStyleClass().add("brand");
        Label sub=new Label("LABORATORIO VISUAL");
        sub.getStyleClass().add("sidebar-caption");
        VBox logo=new VBox(5,brand,sub);
        logo.setPadding(new Insets(0,0,25,4));
        Label heading=new Label("ESTRUCTURAS");
        heading.getStyleClass().add("sidebar-caption");
        side.getChildren().addAll(logo,heading);
        String[][] items={
            {"Arreglos","array"},{"Matrices","matrix"},{"Listas enlazadas","list"},
            {"Pilas · LIFO","stack"},{"Colas · FIFO","queue"},{"Árbol binario","tree"},{"Grafos","graph"}
        };
        for(String[] item:items){
            Button b=new Button(item[0]);
            b.getStyleClass().add("nav-button");
            b.setMaxWidth(Double.MAX_VALUE);
            b.setAlignment(Pos.CENTER_LEFT);
            b.setOnAction(e->openModule(item[1]));
            side.getChildren().add(b);
        }
        Region spacer=new Region();VBox.setVgrow(spacer,Priority.ALWAYS);
        Label footer=new Label("JAVA 17  ·  JAVAFX 21");
        footer.getStyleClass().add("sidebar-caption");
        side.getChildren().addAll(spacer,footer);
        return side;
    }

    private void showHome() {
        VBox home=new VBox(20);
        home.setPadding(new Insets(42));
        Label eyebrow=new Label("UNIVERSIDAD DE CARTAGENA  ·  INGENIERÍA DE SOFTWARE");
        eyebrow.getStyleClass().add("eyebrow");
        Label title=new Label("DataStructures Lab");
        title.getStyleClass().add("page-title");
        Label subtitle=new Label("Un laboratorio para construir, manipular y observar estructuras de datos en movimiento.");
        subtitle.getStyleClass().add("muted");
        subtitle.setWrapText(true);
        VBox intro=new VBox(9,eyebrow,title,subtitle);
        intro.setPadding(new Insets(0,0,15,0));
        home.getChildren().add(intro);
        String[][] cards={
            {"01","Arreglos","Índices, posiciones y acceso directo.","array"},
            {"02","Matrices","Filas, columnas y coordenadas.","matrix"},
            {"03","Listas enlazadas","Nodos y referencias next.","list"},
            {"04","Pilas · LIFO","PUSH, POP y elemento TOP.","stack"},
            {"05","Colas · FIFO","ENQUEUE, DEQUEUE, FRONT y REAR.","queue"},
            {"06","Árboles binarios","Comparaciones, ramas y recorridos.","tree"},
            {"07","Grafos","Vértices, aristas, BFS y DFS.","graph"}
        };
        for(String[] c:cards){
            Button b=new Button(c[0]+"   "+c[1]+"     ·     "+c[2]);
            b.getStyleClass().add("nav-button");
            b.setMaxWidth(Double.MAX_VALUE);
            b.setAlignment(Pos.CENTER_LEFT);
            b.setOnAction(e->openModule(c[3]));
            home.getChildren().add(b);
        }
        home.getStyleClass().add("home-panel");
        root.setCenter(home);
    }

    private void openModule(String key) {
        Runnable home=this::showHome;
        switch(key) {
            case "array" -> root.setCenter(new ArrayModule(home));
            case "matrix" -> root.setCenter(new MatrixModule(home));
            case "list" -> root.setCenter(new LinkedListModule(home));
            case "stack" -> root.setCenter(new StackModule(home));
            case "queue" -> root.setCenter(new QueueModule(home));
            case "tree" -> root.setCenter(new TreeModule(home));
            case "graph" -> root.setCenter(new GraphModule(home));
            default -> showHome();
        }
    }

    public static void main(String[] args){launch(args);}
}