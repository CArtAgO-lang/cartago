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

import java.util.regex.*;

import cartago.IEventFilter;

/**
 * A Filter based on regular-expression pattern matching.
 *  
 * @author aricci
 *
 */
public class RegExFilter implements IEventFilter, java.io.Serializable {
	
	private Pattern pattern;
	
	public RegExFilter(String pattern){
		this.pattern=Pattern.compile(pattern);
	}
	
	public boolean select(ArtifactObsEvent ev){
        Matcher matcher = pattern.matcher(ev.getSignal().getLabel().toString());
		return matcher.matches();
	}
	
}
