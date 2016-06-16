/*
 *   V2d.java
 *
 * Copyright 2000-2001-2002  aliCE team at deis.unibo.it
 *
 * This software is the proprietary information of deis.unibo.it
 * Use is subject to license terms.
 *
 */
package cartago.tools.inspector;

/**
 *
 * 2-dimensional vector
 * objects are completely state-less
 *
 */
public class V2d implements java.io.Serializable {

    public double x,y;

    public V2d(double x,double y){
        this.x=x;
        this.y=y;
    }
    
    public V2d(P2d p1, P2d p0){
    	this.x = p1.x - p0.x;
    	this.y = p1.y - p0.y;
    }

    public void sum(V2d v){
        x += v.x;
        y += v.y;
    }

    public double abs(){
        return (double)Math.sqrt(x*x+y*y);
    }

    public void normalize(){
        double module=(double)Math.sqrt(x*x+y*y);
        x = x/module;
        y = y/module;
    }

    public void mul(double fact){
        x*=fact;
        y*=fact;
    }

    public String toString(){
        return "V2d("+x+","+y+")";
    }
}
