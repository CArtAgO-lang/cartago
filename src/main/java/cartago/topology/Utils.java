
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
package cartago.topology;

/**
   Class that represents a tree that maintains the workspace topology

*/

import java.util.HashMap;
import cartago.WorkspaceId;
import cartago.CartagoNode;
import cartago.NodeId;
import cartago.CartagoWorkspace;
import cartago.CartagoException;
import java.util.Collection;
import java.util.ArrayList;

public class Utils
{
    
    private static String separationToken = "."; //stablishes the token for separating paths

    public static String getSeparationToken()
    {
	return Utils.separationToken;
    }

        public static String getSeparationTokenEscaped()
    {
	if(separationToken.equals(".") || separationToken.equals("-") || separationToken.equals("+") || separationToken.equals("$"))
	    return "\\"+Utils.separationToken;
	else
	    return getSeparationToken();
    }

        //returns the parent's path
    public static String parentPath(String path)
    {
	if(!path.contains(separationToken)) //is root or invalid
	    {
	       return ""; //no parent
	    }
	return path.substring(0, path.lastIndexOf(separationToken));
    }

    //strips subpath part to retrieve just the name
    public static String createSimpleName(String path)
    {
	return path.substring(path.lastIndexOf(Utils.separationToken)+1);
    }

    
    
}
