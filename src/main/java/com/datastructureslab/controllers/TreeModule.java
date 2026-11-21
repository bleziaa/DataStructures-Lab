package com.datastructureslab.controllers;

import com.datastructureslab.animations.Anim;
import com.datastructureslab.models.InputValidator;
import com.datastructureslab.structures.BinarySearchTree;
import com.datastructureslab.views.BallView;
import com.datastructureslab.views.EdgeView;
import javafx.animation.Animation;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Árbol binario de búsqueda con visualización jerárquica y recorridos animados. */
public class TreeModule extends AbstractModule {
    private final BinarySearchTree tree=new BinarySearchTree();
    private final TextField value=field("Valor",100);
    private final Map<Integer,BallView> nodes=new HashMap<>();
    private final List<EdgeView> edges=new ArrayList<>();
    public TreeModule(Runnable home){
        super("Árbol binario de búsqueda","Menores a la izquierda y mayores a la derecha.","Cada inserción compara desde la raíz. Los duplicados se rechazan. Los recorridos disponibles son inorden, preorden y postorden.",home);
        Button insert=action("Insertar","primary-button",this::insert);
        Button search=action("Buscar","secondary-button",this::search);
        Button delete=action("Eliminar","secondary-button",this::delete);
        Button in=action("Inorden","secondary-button",()->traverse("inorden"));
        Button pre=action("Preorden","secondary-button",()->traverse("preorden"));
        Button post=action("Postorden","secondary-button",()->traverse("postorden"));
        bindEnter(value,insert);addRow("Valor",value,insert,search,delete,in,pre,post);
        addRow("Prueba guiada",action("Cargar ejemplo","demo-button",this::loadExample));render(false);
    }
    private void loadExample(){tree.clear();for(int v:new int[]{50,30,70,20,40,60,80})tree.insert(v);render(true);info("Ejemplo listo: árbol de búsqueda con raíz 50. Prueba Inorden para ver los valores ordenados.");}
    private int read(){return InputValidator.parseValue(value.getText(),"Valor");}
    private void insert(){int v=read();if(!tree.insert(v))throw new IllegalArgumentException("El valor "+v+" ya existe. El árbol no admite duplicados.");value.clear();render(true);info("Insertado "+v+": se comparó desde la raíz hasta encontrar su posición.");}
    private void search() {
        int target = read();
        List<Integer> path = tree.searchPath(target);
        if (path.isEmpty()) { info("El árbol está vacío."); return; }
        List<Animation> steps = new ArrayList<>();
        for (int value : path) {
            BallView ball = nodes.get(value);
            if (ball == null) continue;
            steps.add(Anim.action(() -> note("Comparando con " + value + ": "
                + (target == value ? "encontrado" : target < value ? "ir a la izquierda" : "ir a la derecha"))));
            steps.add(target == value
                ? Anim.mark(ball, BallView.State.FOUND, 500)
                : Anim.flash(ball, BallView.State.ACTIVE, BallView.State.NORMAL, 400));
        }
        steps.add(Anim.action(() -> info(tree.contains(target)
            ? "Valor " + target + " encontrado. Camino: " + path
            : "Valor " + target + " no encontrado. Camino revisado: " + path)));
        play(Anim.seq(steps.toArray(new Animation[0])));
    }
    private void delete(){int v=read();if(!tree.delete(v)){info("El valor "+v+" no existe.");return;}render(true);info("Eliminado "+v+". Se reorganizaron las ramas si fue necesario.");}
    private void traverse(String type){List<Integer> vals=switch(type){case "inorden"->tree.inorder();case "preorden"->tree.preorder();default->tree.postorder();};List<Animation>a=new ArrayList<>();for(int v:vals){BallView b=nodes.get(v);a.add(Anim.seq(Anim.action(()->note(type.toUpperCase()+": visitando "+v)),Anim.flash(b,BallView.State.VISITED,BallView.State.NORMAL,420)));}a.add(Anim.action(()->info("Recorrido "+type+": "+vals)));if(a.size()==1)info("El árbol está vacío.");else play(Anim.seq(a.toArray(new Animation[0])));}
    private void render(boolean animate){for(EdgeView e:edges)e.dispose();edges.clear();nodes.clear();clearCanvas();setCanvasMin(Math.max(viewW(),tree.size()*70+80),Math.max(viewH(),260));if(tree.getRoot()!=null){layout(tree.getRoot(),0,0,Math.max(viewW(),tree.size()*70+80));drawEdges(tree.getRoot());}setStats(tree.size()+" nodos · altura "+tree.height());if(animate&&!nodes.isEmpty()){List<Animation>a=new ArrayList<>();for(BallView b:nodes.values())a.add(Anim.popInDelayed(b,a.size()*70));play(Anim.seq(a.toArray(new Animation[0])));}}
    private int layout(BinarySearchTree.Node n,int depth,int index,double width){if(n==null)return index;index=layout(n.getLeft(),depth+1,index,width);double gap=width/(tree.size()+1);BallView b=new BallView(String.valueOf(n.getValue()),25,false);b.setCenter(gap*(index+1),45+depth*82);nodes.put(n.getValue(),b);canvas.getChildren().add(b);index++;return layout(n.getRight(),depth+1,index,width);}
    private void drawEdges(BinarySearchTree.Node n){if(n==null)return;BallView p=nodes.get(n.getValue());if(n.getLeft()!=null){EdgeView e=new EdgeView(p,nodes.get(n.getLeft().getValue()),false,false);edges.add(e);canvas.getChildren().add(0,e);drawEdges(n.getLeft());}if(n.getRight()!=null){EdgeView e=new EdgeView(p,nodes.get(n.getRight().getValue()),false,false);edges.add(e);canvas.getChildren().add(0,e);drawEdges(n.getRight());}}
    @Override protected void relayout(boolean animate){if(tree.getRoot()!=null)render(false);}
    @Override protected void onReset(){tree.clear();render(false);}
}
