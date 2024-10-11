package lu.pcy113.pclib.mixin;

import lu.pcy113.pclib.datastructure.pair.Pair;

public class MixinLoader {

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		System.out.println(new Pair<>().toString());
		
		/*ClassLoader injectionClassLoader = new MultipleParentClassLoader(MixinLoader.class.getClassLoader(), Arrays.asList(), false);
		
		// Redefine the behavior of the toString() method in the Object class
		new ByteBuddy()
				.redefine(Class.forName("lu.pcy113.pclib.datastructure.pair.Pair", false, MixinLoader.class.getClassLoader()))
				.method(ElementMatchers.named("toString"))
				.intercept(FixedValue.value("Modified by ByteBuddy"))
				.make()
				.load(MixinLoader.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION);

		Thread.currentThread().setContextClassLoader(injectionClassLoader);
		
		System.out.println(new Pair<>().toString());
		
		// Now, whenever we call toString(), we get the modified value
		System.out.println(Class.forName(Pair.class.getName(), false, injectionClassLoader).newInstance().toString()); // Outputs: "Modified by ByteBuddy"*/
	}

}
