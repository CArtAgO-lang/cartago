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

import java.lang.reflect.*;

/**
 * Descriptor for an operation
 *  
 * @author aricci
 *
 */
public class OpDescriptor {

	private IArtifactOp op;
	private IArtifactGuard guard;
    private String id;	
	public static enum OpType { LINK, UI, INTERNAL};
	private OpType type;
	private boolean isDynamic;
	
	public OpDescriptor(String id, IArtifactOp op, IArtifactGuard guard, OpType t){
		this(id, op, t);
		this.guard = guard;
	}

	public OpDescriptor(String id, IArtifactOp op, OpType t){
		this.op = op;
		this.guard = null;
		this.type = t;
		this.id = id;
		if (op instanceof ArtifactDynOp) {
			isDynamic = true;
		} else {
			isDynamic = false;
		}
	}

	public IArtifactOp getOp(){
		return op;
	}
	
	public IArtifactGuard getGuard(){
		return guard;
	}
	
	public String getKeyId(){
		return id;
	}
	
	public boolean isDynamic() {
		return isDynamic;
	}
	
	public boolean isLinkOperation(){
		return type == OpType.LINK;
	}

	public boolean isUI(){
		return type == OpType.UI;
	}
	
	public boolean isInternalOp(){
		return type == OpType.INTERNAL;
	}
}
