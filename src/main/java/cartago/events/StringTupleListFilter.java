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

/**
 * Filter with list of string tuple templates
 *  
 * @author aricci
 *
 */
public class StringTupleListFilter implements IEventFilter, java.io.Serializable {
	
	private Tuple[] tlist;
	
	public StringTupleListFilter(Tuple[] list){
		tlist = list;
	}
	
	public boolean select(ArtifactObsEvent ev){
		//System.out.println("CHECK FOR "+ev);
		Tuple sig = ev.getSignal();
		if (sig != null){
			for (Tuple tuple: tlist){
				//System.out.println("-- "+tuple);
				if (tuple.getLabel().equals(sig.getLabel()) && tuple.getNArgs() == sig.getNArgs()){
					boolean match = true;
					for (int i = 0; i < tuple.getNArgs(); i++){
						Object arg = tuple.getContent(i);
						if (arg!=null && sig.getContent(i)!=null && !arg.toString().equals(sig.getContent(i).toString())){
							match = false;
							break;
						}
					}
					if (match){
						//System.out.println("FOUND "+tuple);
						return true;
					}
				} 		
			}
			return false;
		} else {
			return false;
		}
    }
	
}
