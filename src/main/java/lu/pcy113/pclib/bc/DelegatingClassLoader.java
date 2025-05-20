package lu.pcy113.pclib.bc;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

public class DelegatingClassLoader extends ClassLoader {
	
	private final ClassLoader[] delegates;

	public DelegatingClassLoader(ClassLoader... delegates) {
		super(null); // no parent
		this.delegates = delegates;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		for (ClassLoader cl : delegates) {
			try {
				return cl.loadClass(name);
			} catch (ClassNotFoundException ignored) {
			}
		}
		throw new ClassNotFoundException(name);
	}

	@Override
	protected URL findResource(String name) {
		for (ClassLoader cl : delegates) {
			URL resource = cl.getResource(name);
			if (resource != null)
				return resource;
		}
		return null;
	}

	@Override
	protected Enumeration<URL> findResources(String name) throws IOException {
		Vector<URL> all = new Vector<>();
		for (ClassLoader cl : delegates) {
			Enumeration<URL> urls = cl.getResources(name);
			while (urls.hasMoreElements()) {
				all.add(urls.nextElement());
			}
		}
		return all.elements();
	}
}
