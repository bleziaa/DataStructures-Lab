package com.datastructureslab.controllers;

import com.datastructureslab.animations.Anim;
import com.datastructureslab.models.InputValidator;
import com.datastructureslab.structures.LinkedQueue;
import com.datastructureslab.views.BallView;
import javafx.animation.Animation;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.util.ArrayList;
import java.util.List;

/** Módulo de cola FIFO. */
public class QueueModule extends AbstractModule {
    private final LinkedQueue queue = new LinkedQueue();
    private final TextField value = field("Valor", 100);
    private final List<BallView> nodes = new ArrayList<>();

    public QueueModule(Runnable home) {
        super("Colas", "Elementos en fila: entran por REAR y salen por FRONT.",
              "FIFO: el primero en entrar es el primero en salir. ENQUEUE agrega al final; DEQUEUE retira el frente.", home);
        Button enq = action("ENQUEUE", "primary-button", this::enqueue);
        Button deq = action("DEQUEUE", "secondary-button", this::dequeue);
        Button peek = action("PEEK FRONT", "secondary-button", this::peek);
        Button show = action("Recorrer", "secondary-button", this::show);
        bindEnter(value, enq);
        addRow("Valor", value, enq, deq, peek, show);
        render(false);
    }

    private int read() { return InputValidator.parseValue(value.getText(), "Valor"); }

    private void enqueue() {
        int v = read();
        queue.enqueue(v);
        value.clear();
        render(true);
        info("ENQUEUE " + v + ": agregado en REAR.");
    }

    private void dequeue() {
        int v = queue.dequeue();
        if (nodes.isEmpty()) { info("DEQUEUE: " + v); return; }
        BallView first = nodes.get(0);
        play(Anim.seq(Anim.flyOut(first, first.getLayoutX() - 110, first.getLayoutY()),
                Anim.action(() -> { render(false); info("DEQUEUE: salió " + v + " desde FRONT."); })));
    }

    private void peek() {
        info("FRONT = " + queue.peek());
        if (!nodes.isEmpty()) play(Anim.flash(nodes.get(0), BallView.State.FOUND, BallView.State.NORMAL, 500));
    }

    private void show() {
        List<Integer> vals = queue.toList();
        List<Animation> steps = new ArrayList<>();
        for (int i = 0; i < vals.size(); i++) {
            final int index = i;
            steps.add(Anim.seq(
                    Anim.action(() -> note((index == 0 ? "FRONT" : index == vals.size() - 1 ? "REAR" : "Nodo " + index)
                            + " → " + vals.get(index))),
                    Anim.flash(nodes.get(i), BallView.State.VISITED, BallView.State.NORMAL, 230)));
        }
        steps.add(Anim.action(() -> info("Recorrido FRONT → REAR: " + vals)));
        if (vals.isEmpty()) info("La cola está vacía.");
        else play(Anim.seq(steps.toArray(new Animation[0])));
    }

    private void render(boolean animate) {
        nodes.clear();
        clearCanvas();
        List<Integer> vals = queue.toList();
        double gap = 90;
        double start = Math.max(40, (viewW() - vals.size() * gap) / 2);
        setCanvasMin(Math.max(viewW(), vals.size() * gap + 80), 170);
        for (int i = 0; i < vals.size(); i++) {
            BallView ball = new BallView(String.valueOf(vals.get(i)));
            ball.setCenter(start + i * gap + 26, 85);
            nodes.add(ball);
            canvas.getChildren().add(ball);
        }
        setStats(queue.size() + " elementos · FIFO");
        if (animate && !nodes.isEmpty()) {
            BallView newest = nodes.get(nodes.size() - 1);
            double targetX = newest.getLayoutX();
            newest.setLayoutX(viewW() + 45);
            play(Anim.moveTo(newest, targetX, 59, 420));
        }
    }

    @Override protected void relayout(boolean animate) {
        double gap = 90;
        double start = Math.max(40, (viewW() - nodes.size() * gap) / 2);
        for (int i = 0; i < nodes.size(); i++) Anim.moveTo(nodes.get(i), start + i * gap, 59, 220).play();
    }

    @Override protected void onReset() { queue.clear(); render(false); }
}