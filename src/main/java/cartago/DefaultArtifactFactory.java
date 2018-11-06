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
	
	@Override
	public Artifact createArtifact(String templateName) throws CartagoException {
		try {
			Class<?> cl = Class.forName(templateName);
			return (Artifact)cl.newInstance();
		} catch(Exception ex){
			throw new CartagoException("Template not found: "+templateName);
		}
	}
	
	/**
	 * Create artifact dynamically allowing on-the-fly programming 	
	 * @param templateName artifact code template
	 * @param sourceDir source directory, if null "src/env/" will be used 
	 * @return Artifact instance
	 * @throws CartagoException 
	 */
	@Override
	public Artifact createDynamicArtifact(String templateName, String sourceDir) throws CartagoException {
		try {
			String filename = null;
			if (sourceDir.equals(""))
				filename = "src/env/" + templateName.replace(".", "/") + ".java";
			else
				filename = sourceDir + templateName.replace(".", "/") + ".java";

			File file = new File(filename);
			FileInputStream fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();

			String javacode = new String(data, "UTF-8");

			ClassLoader cloader = new ClassLoader() {
			};
			cc = new CachedCompiler(null, null);

			Class<?> cl = cc.loadFromJava(cloader, templateName, javacode);
			return (Artifact) cl.newInstance();
		} catch(Exception ex){
			throw new CartagoException("Template not found: "+templateName);
		}
	}
}
