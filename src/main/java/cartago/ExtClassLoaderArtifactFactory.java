package cartago;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ExtClassLoaderArtifactFactory extends ArtifactFactory {

	private ClassLoader cloader;

	public ExtClassLoaderArtifactFactory(ClassLoader cl) {
		super("default");
		this.cloader = cl;
	}

	@Override
	public Artifact createArtifact(String templateName) throws CartagoException {
		try {
			Class cl = cloader.loadClass(templateName);
			return (Artifact) cl.newInstance();
		} catch (Exception ex) {
			throw new CartagoException("Template not found: " + templateName + " with " + cloader);
		}
	}

	@Override
	public Artifact createArtifact(Class<?> caller, String templateName, Class<?>[] paramsTypes)
			throws CartagoException {
		try {
			Class<?> callee = cloader.loadClass(templateName);

			if ((caller == WorkspaceKernel.class) || (caller == WorkspaceArtifact.class)
					|| (accessible(caller, callee, paramsTypes))) {
				Constructor<?> defaultConstructor = callee.getDeclaredConstructor();
				defaultConstructor.setAccessible(true);
				return (Artifact) defaultConstructor.newInstance();
			}

			String errorMsg = String.format("%s can not access %s's init method", caller.getName(), callee.getName());
			throw new IllegalAccessException(errorMsg);
		} catch (ClassNotFoundException ex) {
			throw new CartagoException("Template not found: " + templateName + " with " + cloader);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
			throw new CartagoException(e.getMessage());
		} catch (IllegalArgumentException | NoSuchMethodException e) {
			throw new CartagoException("No init method signature with the given parameters in " + templateName);
		} catch (SecurityException e) {
			throw new CartagoException("Security violation");
		}
	}
}
