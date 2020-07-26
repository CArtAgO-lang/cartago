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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import cartago.manual.parser.ArtifactManualParser;
import java.net.*
;
/**
 * This class represents the artifact manual,
 * containing artifact operating instructions
 * and function description.
 * 
 * @author aricci
 * 
 */
public class Manual implements java.io.Serializable {

	private URI uri;
	private String artClass;
	private String name;
	private List<UsageProtocol> protocols;
	private String source;

	public static final Manual EMPTY_MANUAL = new Manual("",null);
		
	public Manual(String manualName, URI uri){
		this.name = manualName;
		this.uri = uri;
		protocols = new ArrayList<UsageProtocol>();
	}
	
	public void setSource(String src){
		this.source = src;
	}
	
	public void addUsageProtocol(UsageProtocol p){
		protocols.add(p);
	}
	
	public List<UsageProtocol> getUsageProtocols(){
		return protocols;
	}
	
	public String getName(){
		return name;
	}
	
	public String getSource(){
		return source;
	}
	
	public URI getURI(){
		return uri;
	}
	
	public static Manual parse(File file) throws Exception {
		ArtifactManualParser parser = new ArtifactManualParser(new FileInputStream(file));
		Manual man = parser.parse();
		return man;
	}
	
	public static Manual parse(String text) throws Exception {
		ArtifactManualParser parser = new ArtifactManualParser(new StringReader(text));
		Manual man = parser.parse();
		man.setSource(text);
		return man;
	}
	
}
