/**
 * CArtAgO - DISI, University of Bologna
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
package cartago.events;

import cartago.*;
import java.util.*;
/**
 * Class representing a consult manual succeeded event.
 * 
 * @author aricci
 *
 */
public class ConsultManualSucceededEvent extends ActionSucceededEvent {

	private Manual man;
	
	ConsultManualSucceededEvent(){}
	
	public ConsultManualSucceededEvent(long id, long actionId, Op op, ArtifactId aid, Manual man){
		super(id,actionId,op,aid);
		this.man = man;
	}
	
	public Manual getManual(){
		return man;
	}

}
