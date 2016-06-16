package cartago.tools.inspector;

/**
 *
 * 2-dimensional point
 * objects are completely state-less
 *
 */
public class P2d implements java.io.Serializable {

    public double x,y;

    public P2d(double x,double y){
        this.x=x;
        this.y=y;
    }

    public void sum(V2d v){
        x+=v.x;
        y+=v.y;
    }

    public void scale(double factor){
    	x*=factor;
    	y*=factor;
    }
    
    public V2d sub(P2d v){
        return new V2d(x-v.x,y-v.y);
    }

    public String toString(){
        return "P2d("+x+","+y+")";
    }
    
    public P2d copy(){
    	return new P2d(x,y);
    }

}
