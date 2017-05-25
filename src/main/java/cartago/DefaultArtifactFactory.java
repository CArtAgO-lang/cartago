package cartago;

public class DefaultArtifactFactory extends ArtifactFactory {

	public DefaultArtifactFactory() {
		super("default");
	}

	@Override
	public Artifact createArtifact(String templateName) throws CartagoException {
		try {
			Class cl = Class.forName(templateName);
			return (Artifact) cl.newInstance();
		} catch (Exception ex) {
			throw new CartagoException("Template not found: " + templateName);
		}
	}

	@Override
	public Artifact createArtifact(Class<?> caller, String templateName, Class<?>[] paramsTypes)
			throws CartagoException {
		// TODO Auto-generated method stub
		return null;
	}
}
