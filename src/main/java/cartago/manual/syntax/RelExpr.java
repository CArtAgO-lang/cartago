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

import java.util.logging.Logger;


/** 
 * Represents a relational expression like 10 > 20.
 * 
 * When the operator is <b>=..</b>, the first argument is a literal and the 
 * second as list, e.g.:
 * <code>
 * Literal =.. [functor, list of terms, list of annots]
 * </code>
 * Examples:
 * <ul>
 * <li> X =.. [~p, [t1, t2], [a1,a2]]<br>
 *      X is ~p(t1,t2)[a1,a2]
 * <li> ~p(t1,t2)[a1,a2] =.. X<br>
 *      X is [~p, [t1, t2], [a1,a2]]
 * </ul>
 * 
 * @navassoc - op - RelationalOp
 * 
 * @author Jomi
 */
public class RelExpr extends BinaryStructure {

    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(RelExpr.class.getName());

    public enum RelationalOp { 
        none   { public String toString() { return ""; } }, 
        gt     { public String toString() { return " > "; } }, 
        gte    { public String toString() { return " >= "; } },
        lt     { public String toString() { return " < "; } }, 
        lte    { public String toString() { return " <= "; } },
        eq     { public String toString() { return " == "; } },
        dif    { public String toString() { return " \\== "; } },
        unify          { public String toString() { return " = "; } },
        literalBuilder { public String toString() { return " =.. "; } };
    }

    private RelationalOp op = RelationalOp.none;

    public RelExpr(Term t1, RelationalOp oper, Term t2) {
        super(t1,oper.toString(),t2);
        op = oper;
    }    
    
    /** make a hard copy of the terms */
    public RelExpr clone() {
        return  new RelExpr(getTerm(0).clone(), op, getTerm(1).clone());
    }
    
    /** gets the Operation of this Expression */
    public RelationalOp getOp() {
        return op;
    }
}
