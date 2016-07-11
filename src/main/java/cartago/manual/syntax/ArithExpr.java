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

import cartago.manual.syntax.NumberTerm;

public class ArithExpr extends BinaryStructure {
    private static final long serialVersionUID = 1L;
    
    public enum ArithmeticOp {
        none { public String toString() { return "";	}},
        plus { public String toString() { return "+"; 	}},
        minus { public String toString() { return "-";  }},
        times { public String toString() { return "*";  }},
        div { public String toString() { return "/"; }},
        mod { public String toString() { return " mod "; }},
        pow { public String toString() { return "**"; }},
        intdiv { public String toString() { return " div ";}};
    }

    private ArithmeticOp  op = ArithmeticOp.none;

    public ArithExpr(NumberTerm t1, ArithmeticOp oper, NumberTerm t2) {
        super(t1,oper.toString(),t2);
    }

    public ArithExpr(ArithmeticOp oper, NumberTerm t2) {
        super(oper.toString(),t2);
    }

    /** make a hard copy of the terms */
    public ArithExpr clone() {
        return new ArithExpr(getLHS(),this.getOp(),this.getRHS());
    }

    /** gets the Operation of this Expression */
    public ArithmeticOp getOp() {
        return op;
    }

    /** gets the LHS of this Expression */
    public NumberTerm getLHS() {
        return (NumberTerm)getTerm(1);
    }

    /** gets the RHS of this Expression */
    public NumberTerm getRHS() {
        return (NumberTerm)getTerm(0);
    }

    @Override
    public String toString() {
        	if (isUnary()) {
                return "(" + op + getTerm(0) + ")";
            } else {
                return "(" + getTerm(0) + op + getTerm(1) + ")";
            }
    }
}
