package cartago;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

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

	/**
	 * Check if {@code callee}'s init method signed with {@code paramTypes} is
	 * accessible from {@code caller}.
	 * 
	 * @param caller
	 *            caller class
	 * @param callee
	 *            class where the init method which should be called is located
	 * @param paramsTypes
	 *            parameters types of the init method
	 * @return true if {@code callee}'s init method is accessible from
	 *         {@code caller}
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	protected boolean accessible(Class<?> caller, Class<?> callee, Class<?>[] paramsTypes)
			throws NoSuchMethodException, SecurityException {
		Method init = callee.getDeclaredMethod("init", paramsTypes);
		switch (init.getModifiers()) {
		case Modifier.PRIVATE:
			Class<?> clazz = caller;
			do {
				if (clazz == callee)
					return true;
				clazz = clazz.getDeclaringClass();
			} while (clazz != null);
			break;
		case Modifier.PROTECTED:
			if (caller.isAssignableFrom(callee))
				return true;
			break;
		case Modifier.PUBLIC:
			return true;
		default: // package-private
			if (caller.getPackage() == callee.getPackage())
				return true;
		}
		return false;
	}
}
