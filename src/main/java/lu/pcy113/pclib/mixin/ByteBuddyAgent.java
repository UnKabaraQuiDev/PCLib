package lu.pcy113.pclib.mixin;

import java.lang.instrument.Instrumentation;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.mixin.annotations.MixinClass;
import lu.pcy113.pclib.mixin.annotations.MixinMethod;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.Identified.Narrowable;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

public class ByteBuddyAgent {

	public static void premain(String arguments, Instrumentation instrumentation) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		AgentBuilder.Default default_ = new AgentBuilder.Default();

		for (String line : PCUtils.toString(ClassLoader.getSystemClassLoader().getResourceAsStream("mixins.txt")).split("\n")) {
			final Class<?> clazz = Class.forName(line);
			final MixinClass mixinClass = clazz.getAnnotation(MixinClass.class);

			final Object instance = clazz.newInstance();
			
			final Narrowable type = default_.type(ElementMatchers.named(mixinClass.value()));

			for (Class<?> subClazz : clazz.getDeclaredClasses()) {
				if (subClazz.isAnnotationPresent(MixinMethod.class)) {
					final MixinMethod mixinMethod = subClazz.getAnnotation(MixinMethod.class);

					type.transform((builder, typeDescription, classLoader, module, protectionDomain) -> {
							return builder
									.method(ElementMatchers.named(mixinMethod.name()).and(ElementMatchers.returns(mixinMethod.returnType()).and(ElementMatchers.takesArguments(mixinMethod.parameterTypes()))))
									.intercept(Advice.to(subClazz));
					}).installOn(instrumentation);
				}

			}
		}
	}

	public static void agentmain(String arguments, Instrumentation instrumentation) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		premain(arguments, instrumentation); // Use the same logic for dynamic agents
	}
}
