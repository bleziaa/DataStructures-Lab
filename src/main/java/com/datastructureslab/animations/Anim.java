package com.datastructureslab.animations;

import com.datastructureslab.views.BallView;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.List;

/** Animaciones reutilizables de JavaFX; no contienen lógica de estructuras. */
public final class Anim {
    private Anim() {}

    public static Animation popIn(Node n) {
        n.setScaleX(0.1); n.setScaleY(0.1); n.setOpacity(0);
        ScaleTransition scale=new ScaleTransition(Duration.millis(450),n);
        scale.setToX(1); scale.setToY(1); scale.setInterpolator(Interpolator.EASE_OUT);
        FadeTransition fade=new FadeTransition(Duration.millis(320),n); fade.setToValue(1);
        return new ParallelTransition(scale,fade);
    }
    public static Animation popInDelayed(Node n,double delayMs) {
        return new SequentialTransition(new PauseTransition(Duration.millis(delayMs)),popIn(n));
    }
    public static Animation pulse(Node n) {
        ScaleTransition st=new ScaleTransition(Duration.millis(220),n);
        st.setFromX(1);st.setFromY(1);st.setToX(1.28);st.setToY(1.28);
        st.setCycleCount(2);st.setAutoReverse(true);return st;
    }
    public static Timeline moveTo(Node n,double x,double y,double ms) {
        return new Timeline(new KeyFrame(Duration.millis(ms),
            new KeyValue(n.layoutXProperty(),x,Interpolator.EASE_BOTH),
            new KeyValue(n.layoutYProperty(),y,Interpolator.EASE_BOTH)));
    }
    public static Timeline dropTo(Node n,double x,double y,double ms) {
        return new Timeline(new KeyFrame(Duration.millis(ms),
            new KeyValue(n.layoutXProperty(),x,Interpolator.EASE_IN),
            new KeyValue(n.layoutYProperty(),y,Interpolator.EASE_IN)));
    }
    public static Animation vanish(Node n) {
        ScaleTransition scale=new ScaleTransition(Duration.millis(420),n);
        scale.setToX(0.1);scale.setToY(0.1);
        FadeTransition fade=new FadeTransition(Duration.millis(420),n);fade.setToValue(0);
        return new ParallelTransition(scale,fade);
    }
    public static Animation flyOut(Node n,double x,double y) {
        FadeTransition fade=new FadeTransition(Duration.millis(520),n);fade.setToValue(0);
        return new ParallelTransition(moveTo(n,x,y,520),fade);
    }
    public static Animation fadeIn(Node n,double delayMs) {
        n.setOpacity(0);FadeTransition fade=new FadeTransition(Duration.millis(300),n);
        fade.setToValue(1);fade.setDelay(Duration.millis(delayMs));return fade;
    }
    public static Timeline steps(List<Runnable> actions,double stepMs) {
        Timeline t=new Timeline();
        for(int i=0;i<actions.size();i++){Runnable action=actions.get(i);
            t.getKeyFrames().add(new KeyFrame(Duration.millis(i*stepMs),e->action.run()));}
        t.getKeyFrames().add(new KeyFrame(Duration.millis(actions.size()*stepMs)));return t;
    }
    public static Animation seq(Animation... animations){return new SequentialTransition(animations);}
    public static Animation par(Animation... animations){return new ParallelTransition(animations);}
    public static Animation pause(double millis){return new PauseTransition(Duration.millis(millis));}
    public static Animation action(Runnable action){return new Timeline(new KeyFrame(Duration.ZERO,e->action.run()));}
    public static Animation mark(BallView ball,BallView.State state,double millis){
        return seq(action(()->ball.setState(state)),pause(millis));
    }
    public static Animation flash(BallView ball,BallView.State active,BallView.State restore,double millis){
        return seq(action(()->ball.setState(active)),pause(millis),action(()->ball.setState(restore)));
    }
    public static Animation popIn(Node n, double durationMs) {
        n.setScaleX(0.1); n.setScaleY(0.1); n.setOpacity(0);
        ScaleTransition scale=new ScaleTransition(Duration.millis(durationMs),n);
        scale.setToX(1); scale.setToY(1); scale.setInterpolator(Interpolator.EASE_OUT);
        FadeTransition fade=new FadeTransition(Duration.millis(durationMs),n); fade.setToValue(1);
        return new ParallelTransition(scale,fade);
    }
}