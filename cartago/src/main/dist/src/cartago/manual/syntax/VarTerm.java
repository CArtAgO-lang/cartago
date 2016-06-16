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



public class VarTerm extends LiteralImpl  {

    private static final long serialVersionUID = 1L;

    public VarTerm(String s) {
        super(s);
        if (s != null && Character.isLowerCase(s.charAt(0))) {
            throw new IllegalArgumentException("Not a variable");
        }
    }


    public Term clone() {
            // do not call constructor with term parameter!
            VarTerm t = new VarTerm(super.getFunctor());
            t.srcInfo = this.srcInfo;        
            return t;
    }
    
    public boolean isUnnamedVar() {
        return false;
    }
    

}
