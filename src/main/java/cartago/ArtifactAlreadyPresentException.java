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
package cartago;

/**
 * Exception thrown when an artifact with the same name
 * exists in the same workspace
 *  
 * @author aricci
 *
 */
public class ArtifactAlreadyPresentException extends CartagoException {
	private String artName;
	private String wspName;
	
	public ArtifactAlreadyPresentException(String artName, String wspName){
		this.wspName = wspName;
		this.artName = artName;
	}
	
	public String toString(){
		return "ArtifactAlreadyPresentException: "+artName+" in "+wspName;
	}
}
