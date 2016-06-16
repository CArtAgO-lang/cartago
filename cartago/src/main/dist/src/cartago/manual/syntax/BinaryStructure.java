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
   Represents a binary/unary logical/relational operator.
   
   @opt nodefillcolor lightgoldenrodyellow
 
   @navassoc - left  - Term
   @navassoc - right - Term
    
 */
public abstract class BinaryStructure extends Structure {

    /** Constructor for binary operator */
    public BinaryStructure(Term t1, String id, Term t2) {
        super(id,2);
        addTerm(t1);
        addTerm(t2);
        if (t1.getSrcInfo() != null)
            srcInfo = t1.getSrcInfo();
        else
            srcInfo = t2.getSrcInfo();
    }

    /** Constructor for unary operator */
    public BinaryStructure(String id, Term arg) {
        super(id,1);
        addTerm( arg );
        srcInfo = arg.getSrcInfo();
    }
    
    public boolean isUnary() {
        return getArity() == 1;
    }
    
    /** gets the LHS of this operation */
    public Term getLHS() {
        return getTerm(0);
    }
    
    /** gets the RHS of this operation */
    public Term getRHS() {
        return getTerm(1);
    }

    @Override
    public String toString() {
        if (isUnary()) {
            return getFunctor()+"("+getTerm(0)+")";
        } else {
            return "("+getTerm(0)+getFunctor()+getTerm(1)+")";
        }
    }

}
