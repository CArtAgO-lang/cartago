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

/** Immutable class that implements a term that represents a number */
public final class NumberTerm extends DefaultTerm {

    private static final long serialVersionUID = 1L;

    private final double value;
    
    public NumberTerm() {
        super();
        value = 0;
    }
    
    public NumberTerm(String sn) throws Exception {
        value = Double.parseDouble(sn);
    }
    
    public NumberTerm(double vl) {
        value = vl;
    }
    
    public NumberTerm(NumberTerm t) {
        value   = t.solve();
        srcInfo = t.srcInfo;        
    }

    public double solve() {
        return value;
    }

    public NumberTerm clone() {
        return this;
    }
    
    @Override
    public boolean isNumeric() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;

        if (o != null && o instanceof Term && ((Term)o).isNumeric() && !((Term)o).isArithExpr()) {
            NumberTerm st = (NumberTerm)o;
            try {
                return solve() == st.solve();
            } catch (Exception e) { }
        } 
        return false;
    }

    @Override
    protected int calcHashCode() {
        return 37 * (int)solve();
    }
    
    @Override
    public int compareTo(Term o) {
        if (o instanceof NumberTerm) {
            NumberTerm st = (NumberTerm)o;
            if (solve() > st.solve()) return 1;
            if (solve() < st.solve()) return -1;
            return 0;
        }
        if (o instanceof Atom)
            return -1;
        return super.compareTo(o);    
    }

    public String toString() {
        long r = Math.round(value);
        if (value == (double)r) {
            return String.valueOf(r);
        } else {
            return String.valueOf(value);
        }
    }
}
