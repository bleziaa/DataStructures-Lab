package com.datastructureslab.controllers;

import com.datastructureslab.animations.Anim;
import com.datastructureslab.models.InputValidator;
import com.datastructureslab.structures.LinkedStack;
import com.datastructureslab.views.BallView;
import javafx.animation.Animation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.util.ArrayList;
import java.util.List;

/** Módulo de pila LIFO con nodos enlazados. */
public class StackModule extends AbstractModule {
    private final LinkedStack stack=new LinkedStack();
    private final TextField value=field("Valor",100);
    private final List<BallView> nodes=new ArrayList<>();
    private final Label topTag=new Label("TOP");
    public StackModule(Runnable home){
        super("Pilas", "Inserción y extracción por el extremo superior.", "LIFO: el último elemento que entra es el primero que sale. PUSH agrega en TOP; POP extrae TOP.",home);
        Button push=action("PUSH", "primary-button",this::push);
        Button pop=action("POP","secondary-button",this::pop);
        Button peek=action("PEEK","secondary-button",this::peek);
        Button walk=action("Mostrar pila","secondary-button",this::show);
        bindEnter(value,push); addRow("Valor",value,push,pop,peek,walk);
        topTag.getStyleClass().add("op-caption"); render(false);
    }
    private int read(){return InputValidator.parseValue(value.getText(),"Valor");}
    private void push(){int v=read(); stack.push(v); value.clear(); render(true); info("PUSH "+v+": el nuevo nodo queda en TOP.");}
    private void pop(){int v=stack.pop(); if(!nodes.isEmpty()){BallView b=nodes.get(0); play(Anim.seq(Anim.flyOut(b,b.getLayoutX()+130,b.getLayoutY()-70),Anim.action(()->{render(false);info("POP: salió "+v+".");})));}else info("POP: "+v);}
    private void peek(){info("TOP = "+stack.peek()); if(!nodes.isEmpty())play(Anim.flash(nodes.get(0),BallView.State.FOUND,BallView.State.NORMAL,500));}
    private void show(){List<Integer> vals=stack.toList(); List<Animation>a=new ArrayList<>(); for(int i=0;i<vals.size();i++){final int n=i;a.add(Anim.seq(Anim.action(()->note((n==0?"TOP":"Nivel "+n)+" → "+vals.get(n))),Anim.flash(nodes.get(i),BallView.State.VISITED,BallView.State.NORMAL,250)));} a.add(Anim.action(()->info("Desde TOP hacia el fondo: "+vals))); if(vals.isEmpty())info("La pila está vacía.");else play(Anim.seq(a.toArray(new Animation[0])));}
    private void render(boolean animate){nodes.clear();clearCanvas();List<Integer> vals=stack.toList();double x=Math.max(viewW()/2-28,120), y=35;setCanvasMin(Math.max(viewW(),300),Math.max(210,vals.size()*66+80)); for(int i=0;i<vals.size();i++){BallView b=new BallView(String.valueOf(vals.get(i)));b.setCenter(x,y+i*62+28);nodes.add(b);canvas.getChildren().add(b);}setStats(stack.size()+" elementos · LIFO"); if(animate&&!nodes.isEmpty()){BallView b=nodes.get(0);b.setCenter(x,0);play(Anim.dropTo(b,x-26,35,380));}}
    @Override protected void relayout(boolean animate){double x=Math.max(viewW()/2-28,120);for(int i=0;i<nodes.size();i++)Anim.moveTo(nodes.get(i),x-26,35+i*62,220).play();}
    @Override protected void onReset(){stack.clear();render(false);}
}