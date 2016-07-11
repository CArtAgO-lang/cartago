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

import cartago.*;
import jason.JasonException;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSemantics.DefaultInternalAction;
import jason.asSyntax.*;
import c4jason.*;

/**
 * <p>Internal action: <b><code>cartago.termToTuple</code></b>.</p>
 *
 * <p>Description: get a CArtAgO tuple from a Jason term [temporary, still to be refined].</p>
 *
 * <p>Parameters:
 * <ul>
 * <li>+ term: source
 * <li>- <i>Tuple</i> (var): result
 * </ul></p>
 */
public class term_to_tuple extends DefaultInternalAction {
	
	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		CAgentArch agent = ((CAgentArch) ts.getUserAgArch());

		if (args.length != 2) {
			throw new JasonException(
					"Invalid number of arguments:  termToTuple(+Term,-Tuple)");
		}

		JavaLibrary lib = agent.getJavaLib();
		try {
			cartago.Tuple t = lib.termToTuple(args[0]);
			return lib.bindObject(un, args[1], t);
		} catch (Exception ex){
			return false;
		}
	}
}

