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
		Method init = getMethodInHierarchy(callee, "init", paramsTypes);
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

	/**
	 * Returns a {@link Method} object that reflects the specified public member
	 * method of the class or interface represented by the {@code clazz} object.
	 * The {@code name} parameter is a {@link String} specifying the simple name
	 * of the desired method. The {@code paramsTypes} parameter is an array of
	 * {@link Class} objects that identify the method's formal parameter types,
	 * in declared order.
	 * 
	 * @param clazz
	 *            class where the method must be looked up first
	 * @param name
	 *            the name of the method
	 * @param paramsTypes
	 *            the list of parameters
	 * @return the {@link Method} object that matches the specified {@code name}
	 *         and {@code paramsTypes}
	 * @throws NoSuchMethodException
	 *             if a matching method is not found or if the name is
	 *             {@literal "<init>"} or {@literal "<cinit>"}
	 */
	private Method getMethodInHierarchy(Class<?> clazz, String name, Class<?>[] paramsTypes)
			throws NoSuchMethodException {
		Class<?> cls = clazz;
		do {
			Method[] methods = cls.getDeclaredMethods();
			methodLoop: for (Method method : methods) {
				if (method.getName().equals(name) && method.getParameterCount() == paramsTypes.length) {
					Class<?> types[] = method.getParameterTypes();
					int i = 0;
					for (i = 0; i < method.getParameterCount(); i++) {
						if (!types[i].isAssignableFrom(paramsTypes[i])) {
							continue methodLoop;
						}
					}
					return method;
				}
			}
			cls = cls.getSuperclass();
		} while (cls != null);
		throw new NoSuchMethodException();
	}
}