package com.datastructureslab.controllers;

import com.datastructureslab.animations.Anim;
import com.datastructureslab.models.InputValidator;
import com.datastructureslab.structures.ArrayStructure;
import com.datastructureslab.views.BallView;
import com.datastructureslab.views.BallView.State;
import com.datastructureslab.views.TagView;
import javafx.animation.Animation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

/** Módulo 1: arreglos. */
public class ArrayModule extends AbstractModule {
    private static final int MAX_SIZE = 12;
    private static final double R = 24;
    private static final double SPACING = 68;

    private ArrayStructure array;
    private final List<BallView> slots = new ArrayList<>();
    private final List<Label> indexLabels = new ArrayList<>();

    private final TextField sizeField = field("Cantidad (1-" + MAX_SIZE + ")", 150);
    private final TextField indexField = field("Índice", 90);
    private final TextField valueField = field("Valor", 90);

    public ArrayModule(Runnable onHome) {
        super("Arreglos",
                "Posiciones consecutivas de tamaño fijo, accesibles por índice.",
                "Un arreglo guarda elementos contiguos. Cada posición se identifica con un índice que empieza en 0, "
                        + "y consultar o modificar por índice es inmediato.",
                onHome);
        Button create = action("Crear arreglo", "primary-button", this::create);
        bindEnter(sizeField, create);
        addRow("1. Define cuántos elementos tendrá", sizeField, create);

        Button register = action("＋  Registrar", "primary-button", this::register);
        Button modify = action("✎  Modificar", "secondary-button", this::modify);
        Button query = action("⌕  Consultar", "secondary-button", this::query);
        Button traverse = action("▶  Recorrer", "secondary-button", this::traverse);
        bindEnter(valueField, register);
        addRow("2. Trabaja con una posición", indexField, valueField, register, modify, query, traverse);
        setStats("Sin arreglo");
        info("Crea un arreglo indicando cuántos elementos tendrá.");
    }

    private ArrayStructure requireArray() {
        if (array == null) throw new IllegalStateException("Primero crea el arreglo indicando su tamaño.");
        return array;
    }

    private void updateStats() {
        setStats(array == null ? "Sin arreglo"
                : array.length() + " posiciones · " + array.filledCount() + " registradas");
    }

    private double cx(int i) {
        double total = slots.size() * SPACING;
        double start = Math.max(24, (viewW() - total) / 2) + SPACING / 2;
        return start + i * SPACING;
    }

    private double cy() {
        return Math.max(90, viewH() / 2 - 8);
    }

    @Override
    protected void relayout(boolean animate) {
        if (slots.isEmpty()) return;
        setCanvasMin(slots.size() * SPACING + 48, 170);
        for (int i = 0; i < slots.size(); i++) {
            slots.get(i).setCenter(cx(i), cy());
            TagView.placeCentered(indexLabels.get(i), cx(i), cy() + R + 8);
        }
    }

    private void create() {
        int n = InputValidator.parseSize(sizeField.getText(), "Cantidad de elementos", MAX_SIZE);
        array = new ArrayStructure(n);
        clearCanvas();
        slots.clear();
        indexLabels.clear();
        List<Animation> pops = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            BallView slot = new BallView("", R, false);
            slot.setState(State.EMPTY);
            Label idx = TagView.index("[" + i + "]");
            slots.add(slot);
            indexLabels.add(idx);
            canvas.getChildren().addAll(slot, idx);
            pops.add(Anim.seq(Anim.pause(i * 70), Anim.popIn(slot, 300), Anim.fadeIn(idx, 150)));
        }
        relayout(false);
        updateStats();
        info("Arreglo de " + n + " posiciones creado (índices 0 a " + (n - 1) + "). Registra valores.");
        play(Anim.par(pops.toArray(new Animation[0])));
    }

    private void register() {
        ArrayStructure a = requireArray();
        int index = InputValidator.parseIndex(indexField.getText(), "Índice");
        int value = InputValidator.parseValue(valueField.getText(), "Valor");
        Integer existing = a.get(index);
        if (existing != null) {
            throw new IllegalArgumentException("La posición " + index + " ya contiene " + existing
                    + ". Usa «Modificar» para cambiarla.");
        }
        a.set(index, value);
        BallView slot = slots.get(index);
        BallView flying = new BallView(String.valueOf(value), R, false);
        flying.setCenter(cx(index), cy() - 120);
        flying.setOpacity(0);
        canvas.getChildren().add(flying);
        updateStats();
        info("Se registró " + value + " en la posición " + index + ".");
        play(Anim.seq(
                Anim.fadeIn(flying, 120),
                Anim.dropTo(flying, flying.getLayoutX(), slot.getLayoutY(), 420),
                Anim.action(() -> {
                    slot.setText(String.valueOf(value));
                    slot.setState(State.NORMAL);
                    canvas.getChildren().remove(flying);
                }),
                Anim.flash(slot, State.FOUND, State.NORMAL, 250)));
    }

    private void modify() {
        ArrayStructure a = requireArray();
        int index = InputValidator.parseIndex(indexField.getText(), "Índice");
        int value = InputValidator.parseValue(valueField.getText(), "Valor");
        Integer old = a.get(index);
        if (old == null) {
            throw new IllegalArgumentException("La posición " + index + " todavía está vacía. Usa «Registrar».");
        }
        a.set(index, value);
        BallView slot = slots.get(index);
        info("Se modificó la posición " + index + ": " + old + " → " + value + ".");
        play(Anim.seq(
                Anim.mark(slot, State.ACTIVE, 250),
                Anim.action(() -> slot.setText(String.valueOf(value))),
                Anim.flash(slot, State.FOUND, State.NORMAL, 350)));
    }

    private void query() {
        ArrayStructure a = requireArray();
        int index = InputValidator.parseIndex(indexField.getText(), "Índice");
        Integer value = a.get(index);
        BallView slot = slots.get(index);
        if (value == null) {
            info("La posición " + index + " existe pero todavía no tiene valor.");
            play(Anim.flash(slot, State.ACTIVE, State.EMPTY, 450));
        } else {
            info("arreglo[" + index + "] = " + value);
            play(Anim.flash(slot, State.FOUND, State.NORMAL, 600));
        }
    }

    private void traverse() {
        ArrayStructure a = requireArray();
        List<Animation> steps = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < slots.size(); i++) {
            final int idx = i;
            Integer v = a.get(i);
            sb.append(i == 0 ? "" : ", ").append(v == null ? "—" : v);
            BallView slot = slots.get(i);
            State restore = v == null ? State.EMPTY : State.NORMAL;
            steps.add(Anim.seq(
                    Anim.action(() -> note("Recorriendo posición " + idx + (v == null ? ": vacía" : " → " + v))),
                    Anim.flash(slot, State.ACTIVE, restore, 260)));
        }
        steps.add(Anim.action(() -> info("Recorrido completo: [" + sb + "]")));
        play(Anim.seq(steps.toArray(new Animation[0])));
    }

    @Override
    protected void onReset() {
        array = null;
        slots.clear();
        indexLabels.clear();
        clearCanvas();
        updateStats();
    }
}
