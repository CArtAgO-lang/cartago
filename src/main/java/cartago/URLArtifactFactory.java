package cartago;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;

public class URLArtifactFactory extends ArtifactFactory {

	public String address;
	public URLClassLoader cloader;

	public URLArtifactFactory(String name, URL address) {
		super(name);
		cloader = new URLClassLoader(new java.net.URL[] { address }, this.getClass().getClassLoader());
	}

	@Override
	public Artifact createArtifact(String templateName) throws CartagoException {
		try {
			Class cl = cloader.loadClass(templateName);
			return (Artifact) cl.newInstance();
		} catch (Exception ex) {
			throw new CartagoException("Template not found: " + templateName + " at " + address);
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
			throw new CartagoException("Template not found: " + templateName + " at " + address);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
			throw new CartagoException(e.getMessage());
		} catch (IllegalArgumentException | NoSuchMethodException e) {
			throw new CartagoException("No init method signature with the given parameters in " + templateName);
		} catch (SecurityException e) {
			throw new CartagoException("Security violation");
		}
	}
}
