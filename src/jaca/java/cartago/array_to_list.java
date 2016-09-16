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

import jaca.*;
import jason.JasonException;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSemantics.DefaultInternalAction;
import jason.asSyntax.*;

/**
 * <p>Internal action: <b><code>cartago.arrayToList</code></b>.</p>
 *
 * <p>Description: get a Jason list from an Java array or Java list [temporary, still to be refined].</p>
 *
 * <p>Parameters:
 * <ul>
 * <li>+ arrayOrList (atom): object reference
 * <li>- <i>List</i> (var): result
 * </ul></p>
 */
public class array_to_list extends DefaultInternalAction {
	
	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		CAgentArch agent = CAgentArch.getCartagoAgArch(ts);

        if (agent == null)
            throw new JasonException("No Cartago AgArch available!");

		if (args.length != 2 && args.length != 3) {
			throw new JasonException(
					"Invalid number of arguments:  callMethod(+ObjRef,+Signature{,-Result})");
		}

		Atom objId = null;
		/*if (args[0] instanceof VarTerm) {
			VarTerm objIdvar = (VarTerm)args[0];
			if (objIdvar.isGround() && objIdvar.isAtom()){
				objId = (Atom)objIdvar.getValue();
			} else {
				throw new JasonException("Invalid obj ref: "+args[0]);
			}		
		} else*/ if (args[0].isAtom()){
			objId = (Atom)args[0];
		} else {
			throw new JasonException("Invalid obj ref: "+args[0]);
		}
		
		VarTerm idResult = null;
		if (!args[1].isVar()){
			throw new JasonException("Invalid result variable: "+args[2]);
		}
		idResult = (VarTerm)args[1];
		
		return agent.getJavaLib().javaArrayToList(un, objId, idResult);
	}
}

