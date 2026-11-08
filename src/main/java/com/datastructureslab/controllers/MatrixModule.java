package com.datastructureslab.controllers;

import com.datastructureslab.animations.Anim;
import com.datastructureslab.models.InputValidator;
import com.datastructureslab.structures.MatrixStructure;
import com.datastructureslab.views.BallView;
import com.datastructureslab.views.BallView.State;
import com.datastructureslab.views.TagView;
import javafx.animation.Animation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

/** Módulo 2: matrices. */
public class MatrixModule extends AbstractModule {
    private static final int MAX_ROWS = 8;
    private static final int MAX_COLS = 10;
    private static final double R = 23;
    private static final double S = 58;

    private MatrixStructure matrix;
    private final List<List<BallView>> cells = new ArrayList<>();
    private final List<Label> coordLabels = new ArrayList<>();
    private final List<Label> rowHeaders = new ArrayList<>();
    private final List<Label> colHeaders = new ArrayList<>();

    private final TextField rowsField = field("Filas (1-" + MAX_ROWS + ")", 110);
    private final TextField colsField = field("Columnas (1-" + MAX_COLS + ")", 130);
    private final TextField rowField = field("Fila", 70);
    private final TextField colField = field("Columna", 80);
    private final TextField valueField = field("Valor", 80);

    public MatrixModule(Runnable onHome) {
        super("Matrices",
                "Datos organizados en filas y columnas; cada celda tiene una coordenada (fila, columna).",
                "Una matriz es un arreglo de dos dimensiones. Se accede a un elemento con dos índices, "
                        + "(fila, columna), ambos empezando en 0. El total de elementos es filas × columnas.",
                onHome);
        Button create = action("Crear matriz", "primary-button", this::create);
        bindEnter(colsField, create);
        addRow("1. Define las dimensiones", rowsField, colsField, create);

        Button register = action("＋  Registrar", "primary-button", this::register);
        Button modify = action("✎  Modificar", "secondary-button", this::modify);
        Button query = action("⌕  Consultar", "secondary-button", this::query);
        Button traverse = action("▶  Recorrer", "secondary-button", this::traverse);
        bindEnter(valueField, register);
        addRow("2. Trabaja con una celda", rowField, colField, valueField, register, modify, query, traverse);
        setStats("Sin matriz");
        info("Crea una matriz indicando filas y columnas.");
    }

    private MatrixStructure requireMatrix() {
        if (matrix == null) throw new IllegalStateException("Primero crea la matriz indicando filas y columnas.");
        return matrix;
    }

    private void updateStats() {
        setStats(matrix == null ? "Sin matriz"
                : matrix.rows() + " × " + matrix.cols() + " = " + matrix.totalElements()
                + " elementos · " + matrix.filledCount() + " registrados");
    }

    private double originX() {
        int cols = cells.isEmpty() ? 0 : cells.get(0).size();
        return Math.max(52, (viewW() - cols * S) / 2);
    }

    private static final double ORIGIN_Y = 40;

    private double cx(int c) { return originX() + c * S + S / 2; }

    private double cy(int r) { return ORIGIN_Y + r * S + S / 2; }

    @Override
    protected void relayout(boolean animate) {
        if (cells.isEmpty()) return;
        int rows = cells.size();
        int cols = cells.get(0).size();
        setCanvasMin(originX() + cols * S + 30, ORIGIN_Y + rows * S + 20);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                BallView cell = cells.get(r).get(c);
                cell.setCenter(cx(c), cy(r));
                Label coord = coordLabels.get(r * cols + c);
                coord.setLayoutX(cell.getLayoutX() + 4);
                coord.setLayoutY(cell.getLayoutY() + 2);
            }
            rowHeaders.get(r).setLayoutX(originX() - 34);
            rowHeaders.get(r).setLayoutY(cy(r) - 9);
        }
        for (int c = 0; c < cols; c++) {
            TagView.placeCentered(colHeaders.get(c), cx(c), 10);
        }
    }

    private void create() {
        int rows = InputValidator.parseSize(rowsField.getText(), "Filas", MAX_ROWS);
        int cols = InputValidator.parseSize(colsField.getText(), "Columnas", MAX_COLS);
        matrix = new MatrixStructure(rows, cols);
        clearCanvas();
        cells.clear();
        coordLabels.clear();
        rowHeaders.clear();
        colHeaders.clear();
        List<Animation> pops = new ArrayList<>();
        for (int c = 0; c < cols; c++) {
            Label h = TagView.index("c" + c);
            colHeaders.add(h);
            canvas.getChildren().add(h);
        }
        for (int r = 0; r < rows; r++) {
            Label h = TagView.index("f" + r);
            rowHeaders.add(h);
            canvas.getChildren().add(h);
            List<BallView> line = new ArrayList<>();
            for (int c = 0; c < cols; c++) {
                BallView cell = new BallView("", R, true);
                cell.setState(State.EMPTY);
                Label coord = TagView.index(r + "," + c);
                coord.getStyleClass().add("coord-label");
                line.add(cell);
                coordLabels.add(coord);
                canvas.getChildren().addAll(cell, coord);
                pops.add(Anim.seq(Anim.pause((r + c) * 55), Anim.popIn(cell, 260), Anim.fadeIn(coord, 120)));
            }
            cells.add(line);
        }
        relayout(false);
        updateStats();
        info("Matriz de " + rows + " × " + cols + " creada: " + (rows * cols) + " elementos en total.");
        play(Anim.par(pops.toArray(new Animation[0])));
    }

    private BallView cell(int r, int c) {
        return cells.get(r).get(c);
    }

    private void register() {
        MatrixStructure m = requireMatrix();
        int r = InputValidator.parseIndex(rowField.getText(), "Fila");
        int c = InputValidator.parseIndex(colField.getText(), "Columna");
        int value = InputValidator.parseValue(valueField.getText(), "Valor");
        Integer existing = m.get(r, c);
        if (existing != null) {
            throw new IllegalArgumentException("La celda (" + r + ", " + c + ") ya contiene " + existing
                    + ". Usa «Modificar» para cambiarla.");
        }
        m.set(r, c, value);
        BallView target = cell(r, c);
        BallView flying = new BallView(String.valueOf(value), R - 4, false);
        flying.setCenter(cx(c), cy(r) - 110);
        flying.setOpacity(0);
        canvas.getChildren().add(flying);
        updateStats();
        info("Se registró " + value + " en la celda (" + r + ", " + c + ").");
        double tx = cx(c) - flying.radius();
        double ty = cy(r) - flying.radius();
        play(Anim.seq(
                Anim.fadeIn(flying, 120),
                Anim.dropTo(flying, tx, ty, 420),
                Anim.action(() -> {
                    target.setText(String.valueOf(value));
                    target.setState(State.NORMAL);
                    canvas.getChildren().remove(flying);
                }),
                Anim.flash(target, State.FOUND, State.NORMAL, 250)));
    }

    private void modify() {
        MatrixStructure m = requireMatrix();
        int r = InputValidator.parseIndex(rowField.getText(), "Fila");
        int c = InputValidator.parseIndex(colField.getText(), "Columna");
        int value = InputValidator.parseValue(valueField.getText(), "Valor");
        Integer old = m.get(r, c);
        if (old == null) {
            throw new IllegalArgumentException("La celda (" + r + ", " + c + ") todavía está vacía. Usa «Registrar».");
        }
        m.set(r, c, value);
        BallView target = cell(r, c);
        info("Se modificó la celda (" + r + ", " + c + "): " + old + " → " + value + ".");
        play(Anim.seq(
                Anim.mark(target, State.ACTIVE, 250),
                Anim.action(() -> target.setText(String.valueOf(value))),
                Anim.flash(target, State.FOUND, State.NORMAL, 350)));
    }

    private void query() {
        MatrixStructure m = requireMatrix();
        int r = InputValidator.parseIndex(rowField.getText(), "Fila");
        int c = InputValidator.parseIndex(colField.getText(), "Columna");
        Integer value = m.get(r, c);
        BallView target = cell(r, c);
        if (value == null) {
            info("La celda (" + r + ", " + c + ") existe pero todavía no tiene valor.");
            play(Anim.flash(target, State.ACTIVE, State.EMPTY, 450));
        } else {
            info("matriz[" + r + "][" + c + "] = " + value);
            play(Anim.flash(target, State.FOUND, State.NORMAL, 600));
        }
    }

    private void traverse() {
        MatrixStructure m = requireMatrix();
        List<Animation> steps = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < m.rows(); r++) {
            sb.append(r == 0 ? "" : "  |  ");
            for (int c = 0; c < m.cols(); c++) {
                final int fr = r, fc = c;
                Integer v = m.get(r, c);
                sb.append(c == 0 ? "" : ", ").append(v == null ? "—" : v);
                State restore = v == null ? State.EMPTY : State.NORMAL;
                steps.add(Anim.seq(
                        Anim.action(() -> note("Recorriendo (" + fr + ", " + fc + ")"
                                + (v == null ? ": vacía" : " → " + v))),
                        Anim.flash(cell(r, c), State.ACTIVE, restore, 150)));
            }
        }
        steps.add(Anim.action(() -> info("Recorrido por filas completo: " + sb)));
        play(Anim.seq(steps.toArray(new Animation[0])));
    }

    @Override
    protected void onReset() {
        matrix = null;
        cells.clear();
        coordLabels.clear();
        rowHeaders.clear();
        colHeaders.clear();
        clearCanvas();
        updateStats();
    }
}
