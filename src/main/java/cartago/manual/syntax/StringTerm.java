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
 * Immutable class for string terms.
 * 
 * @author Jomi
 */
public final class StringTerm extends DefaultTerm {

    private static final long serialVersionUID = 1L;
    
    private final String value;

    public StringTerm() {
        super();
        value = null;
    }
    
    public StringTerm(String fs) {
        value = fs;
    }
    
    public StringTerm(StringTerm t) {
        value   = t.getString();
        srcInfo = t.srcInfo;        
    }

    public String getString() {
        return value;
    }
    
    public StringTerm clone() {
        return this;
    }
    
    /*
    public static StringTerm parseString(String sTerm) {
        as2j parser = new as2j(new StringReader(sTerm));
        try {
            return (StringTerm)parser.term();
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Error parsing string term " + sTerm,e);
            return null;
        }
    }
    */
    
    @Override
    public boolean isString() {
        return true;
    }

    public int length() {
        if (value == null)
            return 0;
        else
            return value.length();
    }

    @Override
    public boolean equals(Object t) {
        if (t == this) return true;

        if (t != null && t instanceof StringTerm) {
            StringTerm st = (StringTerm)t;
            if (value == null)
                return st.getString() == null;
            else
                return value.equals(st.getString());
        }
        return false;
    }

    @Override
    protected int calcHashCode() {
        if (value == null)
            return 0;
        else
            return value.hashCode();
    }
    
    public String toString() {
        return "\""+value+"\"";
    }

}
