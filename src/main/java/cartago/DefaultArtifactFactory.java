package cartago;

import java.io.File;
import java.io.FileInputStream;
import net.openhft.compiler.CachedCompiler;

public class DefaultArtifactFactory extends ArtifactFactory {

	private static final long serialVersionUID = -7797272818138479745L;
	private CachedCompiler cc;

	public DefaultArtifactFactory(){
		super("default");
	}
	
	public Artifact createArtifact(String templateName) throws CartagoException {
		try {
			Class<?> cl = null;
			if (templateName.startsWith("cartago.")) {
				cl = Class.forName(templateName);
			} else {
				String filename = "src/env/" + templateName.split("\\.")[0] + "/" + templateName.split("\\.")[1] + ".java";
				File file = new File(filename);
				FileInputStream fis = new FileInputStream(file);
				byte[] data = new byte[(int) file.length()];
				fis.read(data);
				fis.close();
				
				String javacode = new String(data, "UTF-8");
				
				ClassLoader cloader = new ClassLoader() {};
				cc = new CachedCompiler(null, null);
				
				cl = cc.loadFromJava(cloader, templateName, javacode);
			}
			return (Artifact)cl.newInstance();
		} catch(Exception ex){
			throw new CartagoException("Template not found: "+templateName);
		}
	}
}
