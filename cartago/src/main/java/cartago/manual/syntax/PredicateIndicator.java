package cartago.manual.syntax;

/**
 * Represents the "type" of a predicate based on the functor and the arity, e.g.: ask/4
 * 
 * @author jomi
 */
public final class PredicateIndicator {

    private final String functor;
    private final int    arity;
    private final int    hash;
    
    public PredicateIndicator(String functor, int arity) {
        this.functor = functor;
        this.arity   = arity;
        hash         = calcHash();
    }
    public PredicateIndicator(String prefix, PredicateIndicator pi) {
        this.functor = prefix + pi.functor;
        this.arity   = pi.arity;
        hash         = calcHash();
    }

    public String getFunctor() {
        return functor;
    }
    
    public int getArity() {
        return arity;
    }
        
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o != null && o instanceof PredicateIndicator && o.hashCode() == this.hashCode()) {
            final PredicateIndicator pi = (PredicateIndicator)o;
            return arity == pi.arity && functor.equals(pi.functor);
        } 
        return false;
    }

    @Override
    public int hashCode() {
        return hash;
    }
    
    private int calcHash() {
        final int PRIME = 31;
        int t  = PRIME * arity;
        if (functor != null) t = PRIME * t + functor.hashCode();
        return t;
    }
      
    public String toString() {
        return functor + "/" + arity;
    }
}
