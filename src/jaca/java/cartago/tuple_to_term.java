/**
 * CARTAGO-JASON-BRIDGE - Developed by aliCE team at deis.unibo.it
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package cartago;

import jason.JasonException;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSemantics.DefaultInternalAction;
import jason.asSyntax.*;
import cartago.*;
import jaca.*;

/**
 * <p>Internal action: <b><code>cartago.tupleToTerm</code></b>.</p>
 *
 * <p>Description: get a Jason term from a CArtAgO Tuple [temporary, still to be refined].</p>
 *
 * <p>Parameters:
 * <ul>
 * <li>+ Tuple: source
 * <li>- <i>Term</i> (var): result
 * </ul></p>
 */
public class tuple_to_term extends DefaultInternalAction {
	
	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		CAgentArch agent = ((CAgentArch) ts.getUserAgArch());

		if (args.length != 2) {
			throw new JasonException(
					"Invalid number of arguments:  tupleToTerm(+Tuple,-Term)");
		}

		JavaLibrary lib = agent.getJavaLib();
		if (!args[0].isAtom()){
			return un.unifies(args[1], args[0]);
		} else {
			try {
				Tuple t = (Tuple)lib.getObject((Atom)args[0]);
				if (t!=null){
					Term te = lib.tupleToTerm(t);
					return lib.bindObject(un, args[1], te);
				} else {
					return un.unifies(args[1], args[0]);
				}
			} catch (Exception ex){
				return false;
			}
		}
	}
}

