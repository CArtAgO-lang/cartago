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
package cartago;

import cartago.security.SecurityException;

import cartago.security.*;
import java.util.*;
import java.net.*;
import java.io.*;

/**
 * Artifact providing basic functionalities to access and manage manuals.
 * 
 * @author aricci
 *
 */
public class ManRepoArtifact extends Artifact {

	private WorkspaceKernel wspKernel;

	@OPERATION void init(WorkspaceKernel env){
		this.wspKernel = env;
	}

	@OPERATION void storeManual(String artifactModelName, String uri){
		java.net.URI id = java.net.URI.create(uri);
	    if (id != null){
	    	StringBuffer src = new StringBuffer("");
	    	if (id.getScheme()!=null){
			    if (id.getScheme().equals("http")){
			    	try {
				    	URL source = new URL(uri);
				    	URLConnection conn = source.openConnection();
				    	BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				    	String inputLine;
				        while ((inputLine = in.readLine()) != null) {
				            src.append(inputLine);
				    	} 
				    	in.close();
			    	} catch (Exception ex){}
			    } else if (id.getScheme().equals("file")){
			    	try {
				    	BufferedReader in = new BufferedReader(new FileReader(id.getPath()));
				    	String inputLine;
				        while ((inputLine = in.readLine()) != null) {
				            src.append(inputLine);
				    	} 
				    	in.close();
			    	} catch (Exception ex){}
			    }
	    	}
			try {
				wspKernel.registerManual(artifactModelName, uri, src.toString());
			} catch (Exception ex){
				failed("Invalid artifact manual.");
			}
		} else {
			failed("URI syntax exception");
		}
	}

	@OPERATION void getManualContent(String artifactModelName, OpFeedbackParam<String> content){
		AgentId userId = this.getCurrentOpAgentId();
		try {
			Manual man = wspKernel.getManual(userId, artifactModelName);
			content.set(man.getSource());
		} catch(Exception ex){
			failed("Artifact manual not available.");
		}
	}
	
	@OPERATION void consultManual(String artifactModelName){
		AgentId userId = this.getCurrentOpAgentId();
		OpExecutionFrame opFrame = this.getOpFrame();
		try {
			Manual man = wspKernel.getManual(userId, artifactModelName);
			wspKernel.notifyConsultManualCompleted(opFrame.getAgentListener(), opFrame.getActionId(), opFrame.getSourceArtifactId(), opFrame.getOperation(), man);
			opFrame.setCompletionNotified();
		} catch(Exception ex){
			failed("Artifact manual not available.");
		}
	}


	
}
