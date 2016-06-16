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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Represents a structure: a functor with <i>n</i> arguments, 
 * e.g.: val(10,x(3)).
 *
 * @composed - terms 0..* Term
 */
public class Structure extends Atom {

    private static final long serialVersionUID = 1L;

    protected static final List<Term> emptyTermList  = new ArrayList<Term>(0);
    protected static final Term[]     emptyTermArray = new Term[0]; // just to have a type for toArray in the getTermsArray method  

    private List<Term> terms;

    public Structure(String functor) {
        //this.functor = (functor == null ? null : functor.intern()); // it does not improve performance in test i did!
        super(functor);
        this.terms = new ArrayList<Term>(5);
    }
    
    public Structure(Literal l) {
        super(l);
        final int tss = l.getArity();
        terms = new ArrayList<Term>(tss);
        for (int i = 0; i < tss; i++)
            terms.add(l.getTerm(i).clone());
    }

    /** 
     * Create a structure with a defined number of terms.
     * 
     * It is used by list term, plan body, ... to not create the array list for terms. 
     */
    public Structure(String functor, int termsSize) {
        super(functor);
        if (termsSize > 0)
            terms = new ArrayList<Term>(termsSize);
    }

    
    @Override
    protected int calcHashCode() {
        int result = super.calcHashCode();
        final int ts = getArity();
        for (int i=0; i<ts; i++)
            result = 7 * result + getTerm(i).hashCode();
        return result;
    }

    public boolean equals(Object t) {
        if (t == null) return false;
        if (t == this) return true;

        if (t instanceof Structure) {
            Structure tAsStruct = (Structure)t;

            // if t is a VarTerm, uses var's equals
            if (tAsStruct.isVar()) 
                return ((VarTerm)t).equals(this);

            final int ts = getArity();
            if (ts != tAsStruct.getArity()) 
                return false;

            if (!getFunctor().equals(tAsStruct.getFunctor())) 
                return false;

            for (int i=0; i<ts; i++)
                if (!getTerm(i).equals(tAsStruct.getTerm(i))) 
                    return false;

            return true;
        }
        if (t instanceof Atom && this.isAtom()) {
            // consider atom equals only when this is an atom
            return super.equals(t); 
        }
        return false;
    }
    
    public int compareTo(Term t) {
        int c = super.compareTo(t);
        if (c != 0)
            return c;

        if (t.isStructure()) { 
            Structure tAsStruct = (Structure)t;

            final int ma = getArity();
            final int oa = tAsStruct.getArity();
            if (ma < oa) return -1;
            if (ma > oa) return 1;

            for (int i=0; i<ma && i<oa; i++) {
                c = getTerm(i).compareTo(tAsStruct.getTerm(i));
                if (c != 0) 
                    return c;
            }
        }
        return 0;
    }  
    

    /** make a deep copy of the terms */
    public Term clone() {
        return new Structure(this);
    }

    @Override
    public void addTerm(Term t) {
        if (t == null) return;
        terms.add(t);
        predicateIndicatorCache = null;
        resetHashCodeCache();
    }
    
    @Override
    public void delTerm(int index) {
        terms.remove(index);
        predicateIndicatorCache = null;
        resetHashCodeCache();
    }
    
    @Override
    public Literal addTerms(Term ... ts ) {
        for (Term t: ts)
            terms.add(t);
        predicateIndicatorCache = null;
        resetHashCodeCache();
        return this;
    }

    @Override
    public Literal addTerms(List<Term> l) {
        for (Term t: l)
            terms.add(t);
        predicateIndicatorCache = null;
        resetHashCodeCache();
        return this;
    }
 
    @Override
    public Literal setTerms(List<Term> l) {
        terms = l;
        predicateIndicatorCache = null;
        resetHashCodeCache();
        return this;
    }
    
    @Override
    public void setTerm(int i, Term t) {
        terms.set(i,t);
        resetHashCodeCache();
    }
     
    public Term getTerm(int i) {
        return terms.get(i);
    }

    @Override
    public int getArity() {
        if (terms == null)
            return 0;
        else
            return terms.size();
    }
    
    /** @deprecated use getArity */
    public int getTermsSize() {
        return getArity();
    }

    @Override
    public List<Term> getTerms() {
        return terms;
    }
    
    @Override
    public boolean hasTerm() {
        return getArity() > 0; // should use getArity to work for list
    }
    
    @Override
    public boolean isStructure() {
        return true;
    }
    
    @Override
    public boolean isAtom() {
        return !hasTerm();
    }

    @Override
    public boolean isGround() {
        final int size = getArity();
        for (int i=0; i<size; i++) {
            if (!getTerm(i).isGround()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean hasVar(VarTerm t) {
        final int size = getArity();
        for (int i=0; i<size; i++)
            if (getTerm(i).hasVar(t))
                return true;
        return false;
    }

    public List<VarTerm> getSingletonVars() {
        Map<VarTerm, Integer> all  = new HashMap<VarTerm, Integer>();
        countVars(all);
        List<VarTerm> r = new ArrayList<VarTerm>();
        for (VarTerm k: all.keySet()) {
            if (all.get(k) == 1 && !k.isUnnamedVar())
                r.add(k);
        }
        return r;
    }

    public void countVars(Map<VarTerm, Integer> c) {
        final int tss = getArity();
        for (int i = 0; i < tss; i++)
            getTerm(i).countVars(c);
    }

    
    public String toString() {
        StringBuilder s = new StringBuilder();
        if (getFunctor() != null) 
            s.append(getFunctor());
        if (getArity() > 0) {
            s.append('(');
            Iterator<Term> i = terms.iterator();
            while (i.hasNext()) {
                s.append(i.next());
                if (i.hasNext()) s.append(',');
            }
            s.append(')');
        }
        return s.toString();
    }
   
}
