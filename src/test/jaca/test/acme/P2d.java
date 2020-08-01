package acme;

import cartago.*;

/**
 *
 * 2-dimensional point
 * objects are completely state-less
 *
 */
public class P2d  extends AbstractWorkspacePoint implements java.io.Serializable  {

    public double x,y;

    public P2d(double x,double y){
        this.x=x;
        this.y=y;
    }

    public double getDistanceFrom(P2d p){
    	double dx = p.x - x;
    	double dy = p.y - y;
        return (double)Math.sqrt(dx*dx+dy*dy);
    }
    
    public P2d sum(V2d v){
        return new P2d(x+v.x,y+v.y);
    }

    public V2d sub(P2d v){
        return new V2d(x-v.x,y-v.y);
    }

    public String toString(){
        return "P2d("+x+","+y+")";
    }

}
