package com.datastructureslab.controllers;

import com.datastructureslab.animations.Anim;
import com.datastructureslab.models.InputValidator;
import com.datastructureslab.models.TraversalStep;
import com.datastructureslab.structures.Graph;
import com.datastructureslab.views.BallView;
import com.datastructureslab.views.EdgeView;
import javafx.animation.Animation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Grafo con listas de adyacencia, vértices, aristas y BFS/DFS animados. */
public class GraphModule extends AbstractModule {
    private Graph graph=new Graph(false);
    private final TextField vertex=field("Vértice",85);
    private final TextField from=field("Desde",85);
    private final TextField to=field("Hasta",85);
    private final TextField start=field("Inicio BFS/DFS",125);
    private final CheckBox directed=new CheckBox("Dirigido");
    private final Map<Integer,BallView> nodes=new HashMap<>();
    private final List<EdgeView> edges=new ArrayList<>();
    public GraphModule(Runnable home){
        super("Grafos","Vértices conectados por aristas; representación mediante listas de adyacencia.","Un grafo puede ser dirigido o no dirigido. BFS usa una cola; DFS usa una pila. No se permiten vértices ni aristas duplicadas ni lazos.",home);
        Button addV=action("Crear vértice","primary-button",this::addVertex);
        Button addE=action("Crear arista","primary-button",this::addEdge);
        Button neighbors=action("Consultar vecinos","secondary-button",this::neighbors);
        Button bfs=action("BFS","secondary-button",()->walk(true));
        Button dfs=action("DFS","secondary-button",()->walk(false));
        directed.setSelected(false);
        directed.setOnAction(e->{if(graph.vertexCount()>0){directed.setSelected(graph.isDirected());error("Reinicia el grafo para cambiar su tipo.");}else{graph=new Graph(directed.isSelected());info("Tipo de grafo: "+(directed.isSelected()?"dirigido":"no dirigido")+".");}});
        addRow("Tipo y vértices",directed,vertex,addV);
        addRow("Conexión",from,to,addE,neighbors);
        addRow("Recorridos desde",start,bfs,dfs);render(false);
    }
    private int parse(TextField f,String name){return InputValidator.parseValue(f.getText(),name);}
    private void addVertex(){int v=parse(vertex,"Vértice");graph.addVertex(v);vertex.clear();render(true);info("Vértice "+v+" creado.");}
    private void addEdge(){int a=parse(from,"Desde"),b=parse(to,"Hasta");graph.addEdge(a,b);render(true);info("Arista "+a+(graph.isDirected()?" → ":" — ")+b+" creada.");}
    private void neighbors(){int v=parse(vertex,"Vértice");info("Vecinos de "+v+": "+graph.neighbors(v));if(nodes.containsKey(v))play(Anim.flash(nodes.get(v),BallView.State.FOUND,BallView.State.NORMAL,500));}
    private void walk(boolean breadth){int s=parse(start,"Vértice inicial");List<TraversalStep> steps=breadth?graph.bfsSteps(s):graph.dfsSteps(s);List<Animation>a=new ArrayList<>();for(TraversalStep step:steps){int v=step.current();a.add(Anim.seq(Anim.action(()->{note((breadth?"BFS":"DFS")+" visitando "+v+" · visitados "+step.visited()+" · pendientes "+step.pending());for(int p:step.pending())if(nodes.containsKey(p))nodes.get(p).setState(BallView.State.PENDING);}),Anim.flash(nodes.get(v),BallView.State.VISITED,BallView.State.VISITED,500)));}a.add(Anim.action(()->info((breadth?"BFS":"DFS")+" completo: "+(breadth?graph.bfs(s):graph.dfs(s)))));play(Anim.seq(a.toArray(new Animation[0])));}
    private void render(boolean animate){for(EdgeView e:edges)e.dispose();edges.clear();nodes.clear();clearCanvas();List<Integer> vs=graph.vertices();int n=vs.size();double cx=Math.max(viewW()/2,180),cy=Math.max(viewH()/2,130),radius=Math.min(145,Math.max(75,n*15));setCanvasMin(Math.max(viewW(),360),Math.max(viewH(),300));for(int i=0;i<n;i++){double angle=-Math.PI/2+2*Math.PI*i/Math.max(1,n);BallView b=new BallView(String.valueOf(vs.get(i)));b.setCenter(cx+radius*Math.cos(angle),cy+radius*Math.sin(angle));nodes.put(vs.get(i),b);}for(int[] edge:graph.edges()){EdgeView e=new EdgeView(nodes.get(edge[0]),nodes.get(edge[1]),graph.isDirected(),false);edges.add(e);canvas.getChildren().add(e);}canvas.getChildren().addAll(nodes.values());setStats(n+" vértices · "+graph.edges().size()+" aristas · "+(graph.isDirected()?"dirigido":"no dirigido"));if(animate&&!nodes.isEmpty()){List<Animation>a=new ArrayList<>();for(BallView b:nodes.values())a.add(Anim.popInDelayed(b,a.size()*65));play(Anim.seq(a.toArray(new Animation[0])));}}
    @Override protected void relayout(boolean animate){if(!nodes.isEmpty())render(false);}
    @Override protected void onReset(){graph=new Graph(directed.isSelected());render(false);}
}