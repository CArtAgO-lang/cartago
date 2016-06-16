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
 A particular type of literal used to represent internal actions (has a "." in the functor).

 @navassoc - ia - InternalAction

 */
public class InternalActionLiteral extends Structure {

    private static final long serialVersionUID = 1L;
        
    public InternalActionLiteral(String functor) {
        super(functor);
    }

    // used by clone
    public InternalActionLiteral(InternalActionLiteral l) {
        super((Structure) l);
    }

    // used by the parser
    public InternalActionLiteral(Structure p) throws Exception {
        super(p);
    }
    
    @Override
    public boolean isInternalAction() {
        return true;
    }

    @Override
    public boolean isAtom() {
        return false;
    }
        
    @Override
    public String getErrorMsg() {
        String src = getSrcInfo() == null ? "" : " ("+ getSrcInfo() + ")"; 
        return "Error in internal action '"+this+"'"+ src;      
    }
    
    public InternalActionLiteral clone() {
        return new InternalActionLiteral(this);
    }

}
