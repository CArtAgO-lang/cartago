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
   Represents a logical formula with some logical operator ("&amp;",  "|", "not").

   @navassoc - op - LogicalOp
   
 */
public class LogExpr extends BinaryStructure  {

    public enum LogicalOp { 
        none   { public String toString() { return ""; } }, 
        not    { public String toString() { return "not "; } }, 
        and    { public String toString() { return " & "; } },
        or     { public String toString() { return " | "; } };
    }

    private  LogicalOp op = LogicalOp.none;
    
    public LogExpr(Term f1, LogicalOp oper, Term f2) {
        super(f1, oper.toString(), f2);
        op = oper;
    }

    public LogExpr(LogicalOp oper, Term f) {
        super(oper.toString(),(Term)f);
        op = oper;
    }

    /** gets the LHS of this Expression */
    public Term getLHS() {
        return (Term)getTerm(0);
    }
    
    /** gets the RHS of this Expression */
    public Term getRHS() {
        return (Term)getTerm(1);
    }


    /** make a hard copy of the terms */
    public LogExpr clone() {
        // do not call constructor with term parameter!
        if (isUnary())
            return new LogExpr(op, getTerm(0).clone());
        else
            return new LogExpr(getTerm(0).clone(), op, (Term)getTerm(1).clone());
    }
    

    /** gets the Operation of this Expression */
    public LogicalOp getOp() {
        return op;
    }
        
}
