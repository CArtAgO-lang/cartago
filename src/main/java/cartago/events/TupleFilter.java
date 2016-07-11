/**
 * CArtAgO - DEIS, University of Bologna
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

import cartago.IEventFilter;
import cartago.Tuple;

/**
 * Filter with a tuple as template.
 *  
 * @author aricci
 *
 */
public class TupleFilter implements IEventFilter, java.io.Serializable {

	Tuple tuple;
	
	public TupleFilter(Tuple t){
		tuple = t;
	}
	
	public boolean select(ArtifactObsEvent ev){
		Tuple sig = ev.getSignal();
		if (sig != null){
			if (tuple.getLabel().equals(sig.getLabel()) && tuple.getNArgs() == sig.getNArgs()){
				for (int i = 0; i < tuple.getNArgs(); i++){
					Object arg = tuple.getContent(i);
					if (arg!=null && !arg.equals(sig.getContent(i))){
						return false;
					}
				}
				return true;
			} else {
				return false;
			}		
		} else {
			return false;
		}
    }
	
}
