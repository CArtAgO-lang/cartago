package cartago;

public class ExtClassLoaderArtifactFactory extends ArtifactFactory {

	private static final long serialVersionUID = -7870993795734573572L;
	private ClassLoader cloader;

	public ExtClassLoaderArtifactFactory(ClassLoader cl) {
		super("default");
		this.cloader = cl;
	}

	@Override
	public Artifact createArtifact(String templateName) throws CartagoException {
		try {
			Class<?> cl = cloader.loadClass(templateName);
			return (Artifact) cl.newInstance();
		} catch (Exception ex) {
			throw new CartagoException("Template not found: " + templateName + " with " + cloader);
		}
	}

	@Override
	public Artifact createDynamicArtifact(String templateName, String sourceDir) throws CartagoException {
		return null;
	}
}
