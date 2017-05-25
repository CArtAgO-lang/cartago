package cartago;

/**
 * Basic abstract class representing factories to instantiate artifacts,
 * managing artifact types
 * 
 * @author aricci
 *
 */
public abstract class ArtifactFactory implements java.io.Serializable {
	private String name;

	public ArtifactFactory(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Deprecated
	abstract public Artifact createArtifact(String templateName) throws CartagoException;

	/**
	 * Create an artifact.
	 * 
	 * In order to create an artifact, the visibility modifier of
	 * {@link Artifact#init} is checked for access restrictions. If
	 * {@code caller} has access to it, then the artifact will be created.
	 * Otherwise, an exception should be thrown.
	 * 
	 * @param caller
	 *            class which is trying to create the artifact
	 * @param templateName
	 *            class to be loaded
	 * @param paramsTypes
	 *            parameters types of the init method
	 * @return new artifact
	 * @throws CartagoException
	 *             if any exception is thrown in the artifact creation process
	 */
	abstract public Artifact createArtifact(Class<?> caller, String templateName, Class<?>[] paramsTypes)
			throws CartagoException;
}
