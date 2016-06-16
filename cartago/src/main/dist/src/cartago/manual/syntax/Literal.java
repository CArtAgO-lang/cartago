// ----------------------------------------------------------------------------
// Copyright (C) 2003 Rafael H. Bordini, Jomi F. Hubner, et al.
// 
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
// 
// To contact the authors:
// http://www.dur.ac.uk/r.bordini
// http://www.inf.furb.br/~jomi
//
//----------------------------------------------------------------------------

package cartago.manual.syntax;


import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 This class represents an abstract literal (an Atom, Structure, Predicate, etc), it is mainly
 the interface of a literal. 
 
 To create a new Literal, one of the following concrete classes may be used:
 <ul>
 <li> Atom -- the most simple literal, is composed by only a functor (no term, no annots);
 <li> Structure -- has functor and terms;
 <li> Pred -- has functor, terms, and annotations;
 <li> LiteralImpl -- Pred + negation. 
 </ul>
 The latter class supports all the operations of 
 the Literal interface.
 
 <p>There are useful static methods in class {@link ASSyntax} to create Literals.
 
 @navassoc - type - PredicateIndicator
 @opt nodefillcolor lightgoldenrodyellow

 @author jomi
 
 @see ASSyntax
 @see Atom
 @see Structure
 @see Pred
 @see LiteralImpl
 
 */
public abstract class Literal extends DefaultTerm {

    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(Literal.class.getName());
    
    public static final boolean LPos   = true;
    public static final boolean LNeg   = false;
    public static final Literal LTrue  = new TrueLiteral();
    public static final Literal LFalse = new FalseLiteral();

    protected PredicateIndicator predicateIndicatorCache = null; // to not compute it all the time (it is used many many times)
    
    public Literal copy() {
        return (Literal)clone(); // should call the clone, that is overridden in subclasses
    }

    /** returns the functor of this literal */
    public abstract String getFunctor();
    
    @Override
    public boolean isLiteral() {
        return true;
    }
    
    /** returns functor symbol "/" arity */
    public PredicateIndicator getPredicateIndicator() {
        if (predicateIndicatorCache == null) {
            predicateIndicatorCache = new PredicateIndicator(getFunctor(),getArity());
        }
        return predicateIndicatorCache;
    }

    /* default implementation of some methods */

    /** returns the number of terms of this literal */
    public int getArity()         { return 0;  }
    /** returns true if this literal has some term */
    public boolean hasTerm()      { return false; } 
    /** returns all terms of this literal */
    public List<Term> getTerms()  { return Structure.emptyTermList;   }
    /** returns all terms of this literal as an array */
    public Term[] getTermsArray() { return getTerms().toArray(Structure.emptyTermArray);  }
    
    private static final List<VarTerm> emptyListVar = new ArrayList<VarTerm>();
    /** returns all singleton vars (that appears once) in this literal */
    public List<VarTerm> getSingletonVars() { return emptyListVar; }

    /**
     * returns the sources of this literal as a new list. e.g.: from annots
     * [source(a), source(b)], it returns [a,b]
     */
    public ListTerm getSources()    { return new ListTerm(); }
    /** returns true if this literal has some source annotation */
    public boolean hasSource()      { return false; }
    /** returns true if this literal has a "source(<i>agName</i>)" */
    public boolean hasSource(Term agName) { return false; }

    /** returns this if this literal can be added in the belief base (Atoms, for instance, can not be) */
    public boolean canBeAddedInBB() { return false; }
    
    /** returns whether this literal is negated or not, use Literal.LNeg and Literal.LPos to compare the returned value */
    public boolean negated()        { return false; }

    public boolean equalsAsStructure(Object p) { return false;  }
    
	/* Not implemented methods */
	
    // structure
    public void addTerm(Term t)              { logger.log(Level.SEVERE, "addTerm is not implemented in the class "+this.getClass().getSimpleName(), new Exception());  }
    public void delTerm(int index)           { logger.log(Level.SEVERE, "delTerm is not implemented in the class "+this.getClass().getSimpleName(), new Exception());  }
    /** adds some terms and return this */
    public Literal addTerms(Term ... ts )    { logger.log(Level.SEVERE, "addTerms is not implemented in the class "+this.getClass().getSimpleName(), new Exception()); return null; }
    /** adds some terms and return this */
    public Literal addTerms(List<Term> l)    { logger.log(Level.SEVERE, "addTerms is not implemented in the class "+this.getClass().getSimpleName(), new Exception()); return null; }
    /** returns the i-th term (first term is 0) */
    public Term getTerm(int i)               { logger.log(Level.SEVERE, "getTerm(i) is not implemented in the class "+this.getClass().getSimpleName(), new Exception()); return null; }
    /** set all terms of the literal and return this */
    public Literal setTerms(List<Term> l)    { logger.log(Level.SEVERE, "setTerms is not implemented in the class "+this.getClass().getSimpleName(), new Exception()); return null; }
    public void setTerm(int i, Term t)       { logger.log(Level.SEVERE, "setTerm is not implemented in the class "+this.getClass().getSimpleName(), new Exception());  }
    

    /** adds the annotation source(<i>agName</i>) */
    public void addSource(Term agName)       { logger.log(Level.SEVERE, "addSource is not implemented in the class "+this.getClass().getSimpleName(), new Exception());  }
    /** deletes one source(<i>agName</i>) annotation, return true if deleted */
    public boolean delSource(Term agName)    { logger.log(Level.SEVERE, "delSource is not implemented in the class "+this.getClass().getSimpleName(), new Exception());  return false; }
    /** deletes all source annotations */
    public void delSources()                 { logger.log(Level.SEVERE, "delSources is not implemented in the class "+this.getClass().getSimpleName(), new Exception());  }

    // literal    
    /** changes the negation of the literal and return this */
    public Literal setNegated(boolean b)     { logger.log(Level.SEVERE, "setNegated is not implemented in the class "+this.getClass().getSimpleName(), new Exception()); return null; }
    
    
 
    
	/** returns this literal as a list with three elements: [functor, list of terms] */
	public ListTerm getAsListOfTerms() {
		ListTerm l = new ListTerm();
		l.add(new LiteralImpl(!negated(), getFunctor()));
		ListTerm lt = new ListTerm();
		lt.addAll(getTerms());
		l.add(lt);		
		return l;
	}

	/** creates a literal from a list with three elements: [functor, list of terms, list of annots] */
	public static Literal newFromListOfTerms(ListTerm lt) throws Exception {
		try {
			Iterator<Term> i = lt.iterator();
			
			Term tfunctor = i.next();

			boolean pos = Literal.LPos;
			if (tfunctor.isLiteral() && ((Literal)tfunctor).negated()) {
				pos = Literal.LNeg;
			}

			Literal l = new LiteralImpl(pos,((Atom)tfunctor).getFunctor());

			if (i.hasNext()) {
				l.setTerms(((ListTerm)i.next()).cloneLT());
			}
			return l;
		} catch (Exception e) {
			throw new Exception("Error creating literal from "+lt);
		}
	}
	
    /** 
     * Transforms this into a full literal (which implements all methods of Literal), if it is an Atom; 
     * otherwise returns 'this'
     */
    public Literal forceFullLiteralImpl() {
        if (this.isAtom() && !(this instanceof LiteralImpl)) 
            return new LiteralImpl(this);
        else 
            return this;
    }
	
	
    @SuppressWarnings("serial")
    static final class TrueLiteral extends LiteralImpl {
    	public TrueLiteral() {
    		super("true", 0);
		}

    	@Override
        public boolean canBeAddedInBB() {
    		return false;
    	}
        
    }
    
    @SuppressWarnings("serial")
	static final class FalseLiteral extends LiteralImpl {
    	public FalseLiteral() {
    		super("false", 0);
		}

    	@Override
        public boolean canBeAddedInBB() {
    		return false;
    	}    	
    }
}
