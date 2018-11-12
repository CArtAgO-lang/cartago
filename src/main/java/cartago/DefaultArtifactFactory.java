package cartago;

import java.io.File;
import java.io.FileInputStream;

import net.openhft.compiler.CachedCompiler;

public class DefaultArtifactFactory extends ArtifactFactory {
	
	public DefaultArtifactFactory(){
		super("default");
	}
	
	public Artifact createArtifact(String templateName) throws CartagoException {
		try {
			Class cl;
			if (templateName.substring(0, 8).equals("dynamic.")) {
				String filename = "src/env/" + templateName.replace(".", "/") + ".java";

				File file = new File(filename);
				FileInputStream fis = new FileInputStream(file);
				byte[] data = new byte[(int) file.length()];
				fis.read(data);
				fis.close();

				String javacode = new String(data, "UTF-8");

				ClassLoader cloader = new ClassLoader() {
				};
				CachedCompiler cc = new CachedCompiler(null, null);

				cl = cc.loadFromJava(cloader, templateName, javacode);
			} else {
				cl = Class.forName(templateName);
			}
			return (Artifact) cl.newInstance();
		} catch (Exception ex) {
			throw new CartagoException("Template not found: " + templateName);
		}
	}
}
