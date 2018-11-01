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
package cartago.tools.graph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * This class mainly generates a graphviz file with environment view, it shows all current created artifacts
 * its observable properties and operations lining with observing agents and linked artifacts
 * 
 * @author cleberjamaral
 */
public class GraphGenerator {

	private HashMap<String, Collection<GraphNode>> nodesByWorkspace = new HashMap<String, Collection<GraphNode>>();
	private List<String> observingAgents = new ArrayList<String>();
	private static PrintWriter out;

    public static void main(String[] args) throws Exception {
    	String filename = args[0];
        if (args.length != 1) {
            System.err.println("The asl code file must be informed");
            filename = "graph.gv";
        }
        String graph = new GraphGenerator().generateGraph();
        
		FileWriter fw = new FileWriter(filename, false);
		BufferedWriter bw = new BufferedWriter(fw);
		out = new PrintWriter(bw);
		out.println(graph);
    }
	
	/**
	 * Add a new agent that is observing some artifact (if it is not included yet)
	 * @param observingAgent name of the observing agent
	 */
	public void addNewObservingAgent(String observingAgent) {
		if (!this.observingAgents.contains(observingAgent))
			this.observingAgents.add(observingAgent);
	}

	/**
	 * Add a graph node grouped by workspaces
	 * @param workspace were the artifact is placed
	 * @param item the graphnode
	 */
	public void addNode(String workspace, GraphNode item) {
		Collection<GraphNode> values = nodesByWorkspace.get(workspace);
		if (values == null) {
			values = new ArrayList<GraphNode>();
			nodesByWorkspace.put(workspace, values);
		}
		values.add(item);
	}

	/**
	 * Generate a graphviz creating a file called "graph.gv" where the 
	 * application is running
	 * @throws IOException
	 */
	public String generateGraph() throws IOException {
        StringBuilder sb = new StringBuilder();

		sb.append("digraph G {\n");
		sb.append("graph [\n");
		sb.append("rankdir = \"LR\"\n");
		sb.append("]\n");

		sb.append("\tsubgraph cluster_0 {\n");
		sb.append("\t\tlabel=\"Agents\"\n");

		this.observingAgents.forEach(x -> {
			sb.append("\t\t\"" + x + "\" [ \n");
			sb.append("\t\t\tlabel = \"" + x);

			// close record
			sb.append("\"\n");
			sb.append("\t\t\tshape = \"ellipse\"\n");
			sb.append("\t\t\tcolor=\"blue\"\n");
			sb.append("\t\t];\n");
		});
		sb.append("\t}\n");

		int counter = 1;
		for (Entry<String, Collection<GraphNode>> entry : nodesByWorkspace.entrySet()) {
			sb.append("\tsubgraph cluster_" + counter + " {\n");
			sb.append("\t\tlabel=\"" + entry.getKey() + "\"\n");

			entry.getValue().forEach(x -> {
				if (x.getType().equals("cartago.WorkspaceArtifact")) {
					sb.append("\t\t\"" + x.getName() + "\" [ " + "\n\t\t\tlabel = \"" + x.getName() + "\"\n");
					sb.append("\t\t\tshape = \"box\" color = \"gray\"\n");
				} else if (x.getType().equals("cartago.tools.Console")) {
					sb.append("\t\t\"" + x.getName() + "\" [ " + "\n\t\t\tlabel = \"" + x.getName() + "\"\n");
					sb.append("\t\t\tshape = \"box\" color = \"purple\"\n");
				} else if (x.getType().equals("cartago.ManRepoArtifact")) {
					sb.append("\t\t\"" + x.getName() + "\" [ " + "\n\t\t\tlabel = \"" + x.getName() + "\"\n");
					sb.append("\t\t\tshape = \"box\" color = \"orange\"\n");
				} else if (x.getType().equals("cartago.tools.TupleSpace")) {
					sb.append("\t\t\"" + x.getName() + "\" [ " + "\n\t\t\tlabel = \"" + x.getName() + "\"\n");
					sb.append("\t\t\tshape = \"box\" color = \"green\"\n");
				} else if (x.getType().equals("cartago.NodeArtifact")) {
					sb.append("\t\t\"" + x.getName() + "\" [ " + "\n\t\t\tlabel = \"" + x.getName() + "\"\n");
					sb.append("\t\t\tshape = \"box\" color = \"red\"\n");
				} else if (x.getType().equals("cartago.AgentBodyArtifact")) {
					sb.append("\t\t\"" + x.getName() + "\" [ " + "\n\t\t\tlabel = \"" + x.getName() + "\"\n");
					sb.append("\t\t\tshape = \"box\" color=\"gray\" style=\"filled\"\n");
				} else {
					// artifact name on first list item <f0> then artifact type on list item <f1>
					sb.append("\t\t\"" + x.getName() + "\" [ " + "\n\t\t\tlabel = \"<f0> " + x.getName());
					sb.append("| <f1> " + x.getType());

					// print observable properties on third list item <f2>
					sb.append("| <f2> ");
					x.getObservableProperties().forEach(y -> sb.append(y + "\\n"));

					// print operations on forth list item <f3>
					sb.append("| <f3> ");
					x.getOperations().forEach(y -> sb.append(y + "\\n"));

					sb.append("\"\n");
					sb.append("\t\t\tshape = \"record\"\n");
				}
				sb.append("\t\t];\n");

				x.getObservingAgents().forEach(
						y -> sb.append("\t\t" + y + " -> " + x.getName() + "[label=\"observes\",color=\"blue\"];\n"));
				x.getLinkedArtifacts().forEach(
						y -> sb.append("\t\t" + x.getName() + " -> " + y + "[label=\"link\",color=\"purple\"];\n"));
			});

			sb.append("\t}\n");
			counter++;
		}

		sb.append("}\n");
		
		return sb.toString();
	}
}
