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

import java.lang.reflect.Array;
import java.util.List;

import jaca.*;

/**
 * <p>Internal action: <b><code>cartago.current_wsps</code></b>.</p>
 *
 */
public class current_wsps extends DefaultInternalAction {
	
	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		CAgentArch agent = CAgentArch.getCartagoAgArch(ts);

        if (agent == null)
            throw new JasonException("No Cartago AgArch available!");

		if (args.length != 1) {
			throw new JasonException(
					"Invalid number of arguments:  current_wsps(-WspId)");
		}

		try {
			List<WorkspaceId> wlist = agent.getAllJoinedWsps();
			ListTermImpl list = new ListTermImpl();
		    for (WorkspaceId id: wlist){
		    		 list.add(agent.getJavaLib().registerDynamic(id));
		    }			
			return agent.getJavaLib().bindObject(un, args[0], list);
		} catch (Exception ex){
			throw new JasonException("Invalid obj ref: "+args[0]);
		}
	}
}

