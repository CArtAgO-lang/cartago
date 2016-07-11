package cartago.manual.syntax;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;


/** 
 *  Represents a plan body item (achieve, test, action, ...) and its successors.
 * 
 *  A plan body like <code>a1; ?t; !g</code> is represented by the following structure
 *  <code>(a1, (?t, (!g)))</code>.
 *  
 *  
 *  @navassoc - next - PlanBody
 *  @navassoc - type - PlanBody.BodyType
 *  
 *  @author Jomi  
 */
public class UsageProtBody extends Structure implements Iterable<UsageProtBody> {

    public enum BodyType {
        none {            public String toString() { return ""; }},
        action {          public String toString() { return ""; }},
        internalAction {  public String toString() { return ""; }},
        achieve {         public String toString() { return "!"; }},
        test {            public String toString() { return "?"; }},
        addBel {          public String toString() { return "+"; }},
        delBel {          public String toString() { return "-"; }},
        delAddBel {       public String toString() { return "-+"; }},
        achieveNF {       public String toString() { return "!!"; }},
        constraint {      public String toString() { return ""; }}
    }
	
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(UsageProtBody.class.getName());

    public static final String BODY_PLAN_FUNCTOR = ";";

    private Term        term     = null; 
    private UsageProtBody    next     = null;
    private BodyType    formType = BodyType.none;
    
    private boolean     isTerm = false; // it is true when the plan body is used as a term instead of an element of a plan
    
    /** constructor for empty plan body */
    public UsageProtBody() {
        super(BODY_PLAN_FUNCTOR, 0);
    }
    
    public UsageProtBody(BodyType t, Term b) {
        super(BODY_PLAN_FUNCTOR, 0);
        formType = t;
        if (b != null) { 
            srcInfo = b.getSrcInfo();
            // the atom issue is solved in TS
            //if (b.isAtom())
            //    b = ((Atom)b).forceFullLiteralImpl();
        }
        term = b;
    }

    public void setBodyNext(UsageProtBody next) {
        this.next = next;
    }
    public UsageProtBody getBodyNext() {
        return next;
    }

    public boolean isEmptyBody() {
        return term == null;
    }
    
    public BodyType getBodyType() {
        return formType;
    }
    public void setBodyType(BodyType bt) {
        formType = bt;
    }
    
    public Term getBodyTerm() {
        return term;
    }
    
    public void setBodyTerm(Term t) {
        term = t;
    }
    
    public boolean isBodyTerm() {
        return isTerm;
    }
    
    @Override
    public boolean isAtom() {
        return false;
    }
    
    public void setAsBodyTerm(boolean b) {
        isTerm = b;
        if (getBodyNext() != null)
            getBodyNext().setAsBodyTerm(b);
    }
    
    @Override
    public boolean isPlanBody() {
        return true;
    }
    
    public Iterator<UsageProtBody> iterator() {
        return new Iterator<UsageProtBody>() {
            UsageProtBody current = UsageProtBody.this;
            public boolean hasNext() {
                return current != null && current.getBodyTerm() != null; 
            }
            public UsageProtBody next() {
                UsageProtBody r = current;
                if (current != null)
                    current = current.getBodyNext();
                return r;
            }
            public void remove() { }
        };
    }

    // Override some structure methods to work with unification/equals
    @Override
    public int getArity() {
        if (term == null)
            return 0;
        else if (next == null)
            return 1;
        else
            return 2;
    }

    @Override
    public Term getTerm(int i) {
        if (i == 0) 
            return term;
        if (i == 1) {
            if (next != null && next.getBodyTerm().isVar() && next.getBodyNext() == null) 
                // if next is the last VAR, return that var
                return next.getBodyTerm();
            else
                return next;
        }
        return null;
    }

    @Override
    public void setTerm(int i, Term t) {
        if (i == 0) term = t;
        if (i == 1) System.out.println("Should not setTerm(1) of body literal!");
    }
        
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;

        if (o instanceof UsageProtBody) {
            UsageProtBody b = (UsageProtBody)o;
            return formType == b.getBodyType() && super.equals(o);
        }
        return false;
    }

    @Override
    public int calcHashCode() {
        return formType.hashCode() + super.calcHashCode();
    }

    public boolean add(UsageProtBody bl) {
        if (term == null) {
            bl = bl.clonePB();
            swap(bl);
            this.next = bl.getBodyNext();
        } else if (next == null)
            next = bl;
        else 
            next.add(bl);
        return true;
    }

    public UsageProtBody getLastBody() {
        if (next == null)
            return this;
        else
            return next.getLastBody();
    }
    
    public boolean add(int index, UsageProtBody bl) {
        if (index == 0) {
            UsageProtBody newpb = new UsageProtBody(this.formType, this.term);
            newpb.setBodyNext(next);
            swap(bl);
            this.next = bl.getBodyNext();
            this.getLastBody().setBodyNext(newpb);
        } else if (next != null) { 
            next.add(index - 1, bl);
        } else {
            next = bl;
        }
        return true;
    }

    public Term removeBody(int index) {
        if (index == 0) {
            Term oldvalue = term;
            if (next == null) {
                term = null; // becomes an empty
            } else {
                swap(next); // get values of text
                next = next.getBodyNext();
            }
            return oldvalue;
        } else { 
            return next.removeBody(index - 1);
        }
    }

    public int getPlanSize() {
        if (term == null) 
            return 0;
        else if (next == null)
            return 1;
        else
            return next.getPlanSize() + 1;
    }

    private void swap(UsageProtBody bl) {
        BodyType bt = this.formType;
        this.formType = bl.getBodyType();
        bl.setBodyType(bt);

        Term l = this.term;
        this.term = bl.getBodyTerm();
        bl.setBodyTerm(l);
    }

    public UsageProtBody clone() {
        if (term == null) // empty
            return new UsageProtBody();

        UsageProtBody c = new UsageProtBody(formType, term.clone());
        c.isTerm = isTerm;
        if (next != null)
            c.setBodyNext(getBodyNext().clonePB());
        return c;
    }
    
    public UsageProtBody clonePB() {
        return clone();
    }
    
    public String toString() {
        if (term == null) {
            return "";
        } else {
            StringBuilder out = new StringBuilder();
            if (isTerm)
                out.append("{ ");
            UsageProtBody pb = this;
            while (pb != null) {
                if (pb.getBodyTerm() != null) {
                    out.append(pb.getBodyType());
                    out.append(pb.getBodyTerm());
                }
                pb = pb.getBodyNext();
                if (pb != null)
                    out.append("; ");
            }
            if (isTerm) 
                out.append(" }"); 
            return out.toString();
        }
    }

}
