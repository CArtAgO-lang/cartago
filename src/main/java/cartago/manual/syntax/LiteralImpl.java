package cartago.manual.syntax;

/**
* A Literal extends a Pred with strong negation (~).
*/
public class LiteralImpl extends Structure {

 private static final long serialVersionUID = 1L;
 //private static Logger logger = Logger.getLogger(LiteralImpl.class.getName());
 
 private boolean type = LPos;

 /** creates a positive literal */
 public LiteralImpl(String functor) {
     super(functor);
 }

 /** if pos == true, the literal is positive, otherwise it is negative */
 public LiteralImpl(boolean pos, String functor) {
     super(functor);
     type = pos;
 }

 public LiteralImpl(Literal l) {
     super(l);
     type = !l.negated();
 }
 
 /** if pos == true, the literal is positive, otherwise it is negative */
 public LiteralImpl(boolean pos, Literal l) {
     super(l);
     type = pos;
 }

 protected LiteralImpl(String functor, int terms) {
     super(functor, terms);
 }

 @Override
 public boolean isAtom() {
     return super.isAtom() && !negated();
 }
 
 /** to be overridden by subclasses (as internal action) */
 @Override
 public boolean canBeAddedInBB() {
     return true;
 }
 
 @Override
 public boolean negated() {
     return type == LNeg;
 }
 
 public Literal setNegated(boolean b) {
     type = b;
     resetHashCodeCache();
     return this;
 }

 @Override
 public boolean equals(Object o) {
     if (o == null) return false;
     if (o == this) return true;

     if (o instanceof LiteralImpl) {
         final LiteralImpl l = (LiteralImpl) o;
         return type == l.type && hashCode() == l.hashCode() && super.equals(l);
     } else if (o instanceof Atom && !negated()) {
         return super.equals(o);
     }
     return false;
 }

 @Override    
 public String getErrorMsg() {
     String src = getSrcInfo() == null ? "" : " ("+ getSrcInfo() + ")"; 
     return "Error in '"+this+"'"+src;
 }
 
 @Override
 public int compareTo(Term t) {
     if (t.isLiteral()) {
         Literal tl = (Literal)t;
         if (!negated() && tl.negated()) {
             return -1;
         } if (negated() && !tl.negated()) {
             return 1;
         }
     }
     return super.compareTo(t);
 }        

 public Term clone() {
     return new LiteralImpl(this);
 }
 
 @Override
 protected int calcHashCode() {
     int result = super.calcHashCode();
     if (negated()) result += 3271;
     return result;
 }

 /** returns [~] super.getPredicateIndicator */
 @Override 
 public PredicateIndicator getPredicateIndicator() {
     if (predicateIndicatorCache == null)
         predicateIndicatorCache = new PredicateIndicator(((type == LPos) ? "" : "~")+getFunctor(),getArity());
     return predicateIndicatorCache;
 }
 
 public String toString() {
     if (type == LPos)
         return super.toString();
     else
         return "~" + super.toString();
 }

}
