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
 * <p>Internal action: <b><code>cartago.new_array</code></b>.</p>
 *
 * <p>Description: create a new Java array in the agent private memory [temporary, still to be refined].</p>
 *
 * <p>Parameters:
 * <ul>
 * <li>+ objClass (string): class name</li>
 * <li>+ params (list): constructor paramters</li>
 * <li>- ObjRef (var, expecting an atom): object reference</li>
 * </ul></p>
 */
public class new_array extends DefaultInternalAction {
	
	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {

		CAgentArch agent = CAgentArch.getCartagoAgArch(ts);

        if (agent == null)
            throw new JasonException("No Cartago AgArch available!");
        
		if (args.length != 3) {
			throw new JasonException(
					"Invalid number of arguments:  new_array(+ClassName,+Params,-ObjRef)");
		}

		StringTerm clName = null;
		if (!args[0].isString()){
			throw new JasonException("Invalid array class name: "+args[0]);
		} else {
			clName = (StringTerm)args[0];
		}
		
		ListTerm elems = null;
		if (!args[1].isList()){
			throw new JasonException("Invalid elems: "+args[1]);
		} else {
			elems = (ListTerm)args[1];
		}
		
		VarTerm objRef = null;
		if (!args[2].isVar()){
			throw new JasonException("Invalid objRef var: "+args[2]);
		} else {
			objRef = (VarTerm)args[2];
		}
		return agent.getJavaLib().java_new_array(un, clName, elems, objRef);
	}
}

