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

/**
 * Represents an atom (a positive literal with no argument and no annotation, e.g. "tell", "a").
 */
public class Atom extends Literal {

    private static final long serialVersionUID = 1L;

    private final String functor; // immutable field
    
    public Atom(String functor) {
        this.functor = functor;
    }
    
    public Atom(Literal l) {
        this.functor            = l.getFunctor();
        predicateIndicatorCache = l.predicateIndicatorCache;
        hashCodeCache           = l.hashCodeCache;
        srcInfo                 = l.srcInfo;        
    }
    
    public String getFunctor() {
        return functor;
    }

    public Term clone() {
        return this; // since this object is immutable
    }
    
    @Override
    public boolean isAtom() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (o instanceof Atom) {
            Atom a = (Atom)o;
            //System.out.println(getFunctor() +" ==== " + a.getFunctor() + " is "+ (a.isAtom())); // && getFunctor().equals(a.getFunctor())));
            return a.isAtom() && getFunctor().equals(a.getFunctor());
        }
        return false;
    }
    
    public int compareTo(Term t) {
        if (t.isNumeric()) return 1;
        
        // this is a list and the other not
        if (isList() && !t.isList()) return 1;

        // this is not a list and the other is
        if (!isList() && t.isList()) return -1;

        // both are lists, check the size
        if (isList() && t.isList()) {
            ListTerm l1 = (ListTerm)this;
            ListTerm l2 = (ListTerm)t;
            final int l1s = l1.size();
            final int l2s = l2.size();
            if (l1s > l2s) return 1;
            if (l2s > l1s) return -1;
            return 0; // need to check elements (in Structure class)
        }
        
        if (t instanceof Atom) { 
            Atom tAsAtom = (Atom)t;
            return getFunctor().compareTo(tAsAtom.getFunctor());
        } 

        return super.compareTo(t);
    }
    
    @Override
    protected int calcHashCode() {
        return getFunctor().hashCode();
    }
    
    @Override
    public String toString() {
        return functor;
    }

}
