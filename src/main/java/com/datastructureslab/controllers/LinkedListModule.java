package com.datastructureslab.controllers;

import com.datastructureslab.animations.Anim;
import com.datastructureslab.models.InputValidator;
import com.datastructureslab.structures.SinglyLinkedList;
import com.datastructureslab.views.BallView;
import com.datastructureslab.views.EdgeView;
import javafx.animation.Animation;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.util.ArrayList;
import java.util.List;

/** Módulo visual de lista simplemente enlazada. */
public class LinkedListModule extends AbstractModule {
    private final SinglyLinkedList list = new SinglyLinkedList();
    private final TextField value = field("Valor", 100);
    private final List<BallView> nodes = new ArrayList<>();
    private final List<EdgeView> edges = new ArrayList<>();

    public LinkedListModule(Runnable home) {
        super("Listas enlazadas", "Nodos conectados mediante referencias al siguiente elemento.",
              "Cada nodo contiene un valor y una referencia next. La lista mantiene head y tail; admite valores repetidos.", home);
        Button first = action("Insertar al inicio", "primary-button", this::insertFirst);
        Button last = action("Insertar al final", "primary-button", this::insertLast);
        Button find = action("Buscar", "secondary-button", this::search);
        Button remove = action("Eliminar valor", "secondary-button", this::remove);
        Button walk = action("Recorrer", "secondary-button", this::traverse);
        bindEnter(value, last);
        addRow("Valor de operación", value, first, last, find, remove, walk);
        render(false);
    }
    private int read() { return InputValidator.parseValue(value.getText(), "Valor"); }
    private void insertFirst() { int v=read(); list.insertFirst(v); value.clear(); render(true); info("Nodo "+v+" insertado al inicio. HEAD apunta al nuevo nodo."); }
    private void insertLast() { int v=read(); list.insertLast(v); value.clear(); render(true); info("Nodo "+v+" agregado al final. TAIL actualizado."); }
    private void search() {
        int v=read(), i=list.indexOf(v);
        if(i<0){ info("El valor "+v+" no existe en la lista."); return; }
        List<Animation> a=new ArrayList<>();
        for(int k=0;k<=i;k++){ final int n=k; a.add(Anim.seq(Anim.action(()->note("Visitando nodo "+n)),Anim.flash(nodes.get(k),BallView.State.ACTIVE,BallView.State.NORMAL,260))); }
        a.add(Anim.action(()->info("Valor "+v+" encontrado en la posición "+i+"."))); play(Anim.seq(a.toArray(new Animation[0])));
    }
    private void remove() {
        int v=read(); int i=list.remove(v);
        if(i<0){ info("No se encontró el valor "+v+"."); return; }
        render(true); info("Se eliminó la primera aparición de "+v+" y se actualizaron los enlaces.");
    }
    private void traverse() {
        List<Animation> a=new ArrayList<>(); List<Integer> vals=list.toList();
        for(int i=0;i<vals.size();i++){ final int n=i; a.add(Anim.seq(Anim.action(()->note("Recorriendo nodo "+n+" → "+vals.get(n))),Anim.flash(nodes.get(i),BallView.State.VISITED,BallView.State.NORMAL,240))); }
        a.add(Anim.action(()->info("Recorrido completo: "+vals))); if(a.isEmpty()) info("La lista está vacía."); else play(Anim.seq(a.toArray(new Animation[0])));
    }
    private void render(boolean animate) {
        for(EdgeView e:edges)e.dispose(); edges.clear(); nodes.clear(); clearCanvas();
        List<Integer> vals=list.toList(); double gap=105, start=Math.max(45,(viewW()-vals.size()*gap)/2);
        setCanvasMin(Math.max(viewW(),vals.size()*gap+90),180);
        for(int i=0;i<vals.size();i++){ BallView b=new BallView(String.valueOf(vals.get(i))); b.setCenter(start+i*gap+30,90); nodes.add(b); canvas.getChildren().add(b); }
        for(int i=0;i<nodes.size()-1;i++){ EdgeView edge=new EdgeView(nodes.get(i),nodes.get(i+1),true,false); edges.add(edge); canvas.getChildren().add(0,edge); }
        setStats(list.size()+" nodos · HEAD → TAIL");
        if(animate){ List<Animation> a=new ArrayList<>(); for(int i=0;i<nodes.size();i++)a.add(Anim.popInDelayed(nodes.get(i),i*55)); if(!a.isEmpty())play(Anim.seq(a.toArray(new Animation[0]))); }
    }
    @Override protected void relayout(boolean animate){ if(!nodes.isEmpty()){ double gap=105,start=Math.max(45,(viewW()-nodes.size()*gap)/2); for(int i=0;i<nodes.size();i++) Anim.moveTo(nodes.get(i),start+i*gap+4,64,250).play(); } }
    @Override protected void onReset(){ list.clear(); render(false); }
}