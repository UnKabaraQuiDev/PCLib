package lu.kbra.pclib.db.utils;

import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.aot.generate.GeneratedClass;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.javapoet.AnnotationSpec;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.CodeBlock;
import org.springframework.javapoet.FieldSpec;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.ParameterSpec;
import org.springframework.javapoet.ParameterizedTypeName;
import org.springframework.javapoet.TypeName;
import org.springframework.javapoet.TypeSpec;
import org.springframework.javapoet.TypeVariableName;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.query.QueryFunction;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.impl.ProxySQLQueryable;
import lu.kbra.pclib.db.query.ParamFunction;
import lu.kbra.pclib.db.registrar.SQLQueryableRegistrar;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.pclib.db.view.AbstractDBView;
import lu.kbra.pclib.db.view.DeferredDatabaseView;

public final class SQLQueryableSubclassGenerator {

	public ClassName generate(final GenerationContext generationContext, final Class<?> repositoryClass) {
		final boolean isInterface = repositoryClass.isInterface();

		if (!isInterface && !Modifier.isAbstract(repositoryClass.getModifiers())) {
			throw new IllegalArgumentException("Expected an abstract repository class or interface: " + repositoryClass.getName());
		}

		final boolean isView = isInterface && AbstractDBView.class.isAssignableFrom(repositoryClass);
		final Class<?> entryType = isInterface ? SQLQueryableRegistrar.getEntryType(repositoryClass) : null;

		final Class<?> superclass = isInterface ? isView ? DeferredDatabaseView.class : DeferredDatabaseTable.class : repositoryClass;

		final List<QueryMethod> queryMethods = this.findQueryMethods(repositoryClass, isInterface);

		final GeneratedClass generatedClass = generationContext.getGeneratedClasses()
				.addForFeatureComponent("sql-queryable", repositoryClass, builder -> {
					final String beanName = Introspector.decapitalize(repositoryClass.getSimpleName());

					builder.addAnnotation(AnnotationSpec.builder(Component.class).addMember("value", "$S", beanName).build());

					builder.addModifiers(PUBLIC);

					if (isInterface) {
						builder.superclass(ParameterizedTypeName.get(ClassName.get(superclass), TypeName.get(entryType)));
						builder.addSuperinterface(TypeName.get(repositoryClass));
						this.addInterfaceConstructor(builder, repositoryClass, superclass);
					} else {
						builder.superclass(TypeName.get(repositoryClass));
						this.addConstructors(builder, repositoryClass);
					}

					for (final QueryMethod queryMethod : queryMethods) {
						this.addQueryField(builder, queryMethod);
						this.addQueryMethod(builder, queryMethod);
					}
				});

		return generatedClass.getName();
	}

	private List<QueryMethod> findQueryMethods(final Class<?> repositoryClass, final boolean isInterface) {

		final List<QueryMethod> queryMethods = new ArrayList<>();
		int index = 0;

		for (final Method method : PCUtils.getAllMethods(repositoryClass)) {
			if (!AnnotatedElementUtils.hasAnnotation(method, Query.class)) {
				continue;
			}

			final int modifiers = method.getModifiers();

			if (Modifier.isStatic(modifiers) || Modifier.isPrivate(modifiers)) {
				continue;
			}

			if (!isInterface && Modifier.isFinal(modifiers)) {
				throw new IllegalStateException("Cannot override final @Query method: " + method.toGenericString());
			}

			queryMethods.add(new QueryMethod(method, "query__" + method.getName() + "_" + (++index)));
		}

		return queryMethods;
	}

	private void addConstructors(final TypeSpec.Builder builder, final Class<?> repositoryClass) {
		boolean foundConstructor = false;

		for (final Constructor<?> constructor : repositoryClass.getDeclaredConstructors()) {
			final int modifiers = constructor.getModifiers();

			if (Modifier.isPrivate(modifiers)) {
				continue;
			}

			foundConstructor = true;

			final MethodSpec.Builder generatedConstructor = MethodSpec.constructorBuilder();

			this.addConstructorModifiers(generatedConstructor, modifiers);

			final Parameter[] parameters = constructor.getParameters();
			final Type[] genericTypes = constructor.getGenericParameterTypes();
			final List<String> arguments = new ArrayList<>();

			for (int i = 0; i < parameters.length; i++) {
				final Parameter parameter = parameters[i];
				final String parameterName = parameter.isNamePresent() ? parameter.getName() : "arg" + i;

				final ParameterSpec.Builder parameterBuilder = ParameterSpec.builder(TypeName.get(genericTypes[i]), parameterName);

				for (final Annotation annotation : parameter.getAnnotations()) {
					parameterBuilder.addAnnotation(AnnotationSpec.get(annotation));
				}

				generatedConstructor.addParameter(parameterBuilder.build());
				arguments.add(parameterName);
			}

			this.addConstructorExceptions(generatedConstructor, constructor);

			generatedConstructor.addStatement("super($L)", CodeBlock.join(arguments.stream().map(CodeBlock::of).toList(), ", "));
			generatedConstructor
					.addStatement("super.customHints.put($T.TARGET_CLASS, $T.class)", DefaultQueryableHints.class, repositoryClass);

			builder.addMethod(generatedConstructor.build());
		}

		if (!foundConstructor) {
			throw new IllegalStateException("No accessible constructor for " + repositoryClass.getName());
		}
	}

	private void addInterfaceConstructor(final TypeSpec.Builder builder, final Class<?> repositoryClass, final Class<?> superclass) {
		final Method factoryMethod = Arrays.stream(repositoryClass.getMethods())
				.filter(method -> ProxySQLQueryable.METHOD_NAME.equals(method.getName()))
				.findFirst()
				.orElse(null);

		final Constructor<?> constructor;

		if (factoryMethod == null) {
			constructor = Arrays.stream(superclass.getDeclaredConstructors())
					.filter(candidate -> !Modifier.isPrivate(candidate.getModifiers()))
					.findFirst()
					.orElseThrow(() -> new IllegalStateException("No accessible constructor in " + superclass.getName()));
		} else {
			constructor = Arrays.stream(superclass.getDeclaredConstructors())
					.filter(candidate -> !Modifier.isPrivate(candidate.getModifiers()))
					.filter(candidate -> candidate.getParameterCount() == factoryMethod.getParameterCount())
					.findFirst()
					.orElseThrow(() -> new IllegalStateException(
							"No constructor in " + superclass.getName() + " has " + factoryMethod.getParameterCount() + " parameters"));
		}

		final Parameter[] constructorParameters = constructor.getParameters();
		final Parameter[] sourceParameters = factoryMethod == null ? constructorParameters : factoryMethod.getParameters();

		final MethodSpec.Builder generatedConstructor = MethodSpec.constructorBuilder();

		this.addConstructorModifiers(generatedConstructor, constructor.getModifiers());

		final Type[] genericTypes = constructor.getGenericParameterTypes();
		final List<String> arguments = new ArrayList<>();

		for (int i = 0; i < constructorParameters.length; i++) {
			final Parameter constructorParameter = constructorParameters[i];
			final Parameter sourceParameter = sourceParameters[i];

			final String parameterName = constructorParameter.isNamePresent() ? constructorParameter.getName() : "arg" + i;

			final ParameterSpec.Builder parameterBuilder = ParameterSpec.builder(TypeName.get(genericTypes[i]), parameterName);

			for (final Annotation annotation : sourceParameter.getAnnotations()) {
				parameterBuilder.addAnnotation(AnnotationSpec.get(annotation));
			}

			generatedConstructor.addParameter(parameterBuilder.build());
			arguments.add(parameterName);
		}

		this.addConstructorExceptions(generatedConstructor, constructor);

		generatedConstructor.addStatement("super($L)", CodeBlock.join(arguments.stream().map(CodeBlock::of).toList(), ", "));
		generatedConstructor.addStatement("super.customHints.put($T.TARGET_CLASS, $T.class)", DefaultQueryableHints.class, repositoryClass);

		builder.addMethod(generatedConstructor.build());
	}

	private void addConstructorModifiers(final MethodSpec.Builder builder, final int modifiers) {
		if (Modifier.isPublic(modifiers)) {
			builder.addModifiers(PUBLIC);
		} else if (Modifier.isProtected(modifiers)) {
			builder.addModifiers(PROTECTED);
		}
	}

	private void addConstructorExceptions(final MethodSpec.Builder builder, final Constructor<?> constructor) {
		for (final Type exceptionType : constructor.getGenericExceptionTypes()) {
			builder.addException(TypeName.get(exceptionType));
		}
	}

	private void addQueryField(final TypeSpec.Builder builder, final QueryMethod queryMethod) {
		builder.addField(FieldSpec.builder(ParamFunction.class, queryMethod.fieldName(), PRIVATE).build());
	}

	private void addQueryMethod(final TypeSpec.Builder builder, final QueryMethod queryMethod) {
		final Method original = queryMethod.method();

		final MethodSpec.Builder generatedMethod = MethodSpec.methodBuilder(original.getName())
				.addAnnotation(Override.class)
				.addAnnotation(AnnotationSpec.builder(QueryFunction.class).addMember("fieldName", "$S", queryMethod.fieldName()).build())
				.returns(TypeName.get(original.getGenericReturnType()));

		final int modifiers = original.getModifiers();

		if (Modifier.isPublic(modifiers)) {
			generatedMethod.addModifiers(PUBLIC);
		} else if (Modifier.isProtected(modifiers)) {
			generatedMethod.addModifiers(PROTECTED);
		}

		if (original.isVarArgs()) {
			generatedMethod.varargs();
		}

		for (final java.lang.reflect.TypeVariable<Method> variable : original.getTypeParameters()) {
			generatedMethod.addTypeVariable(TypeVariableName.get(variable));
		}

		final Parameter[] parameters = original.getParameters();
		final Type[] genericTypes = original.getGenericParameterTypes();
		final List<String> arguments = new ArrayList<>();

		for (int i = 0; i < parameters.length; i++) {
			final String parameterName = parameters[i].isNamePresent() ? parameters[i].getName() : "arg" + i;

			generatedMethod.addParameter(TypeName.get(genericTypes[i]), parameterName);

			arguments.add(parameterName);
		}

		for (final Type exceptionType : original.getGenericExceptionTypes()) {
			generatedMethod.addException(TypeName.get(exceptionType));
		}

		final CodeBlock invocation = CodeBlock.builder()
				.add("this.$L.get($L)", queryMethod.fieldName(), CodeBlock.join(arguments.stream().map(CodeBlock::of).toList(), ", "))
				.build();

		if (original.getReturnType() == void.class) {
			generatedMethod.addStatement("$L", invocation);
		} else {
			generatedMethod.addStatement("return $L", invocation);
		}

		builder.addMethod(generatedMethod.build());
	}

	private record QueryMethod(Method method, String fieldName) {
	}

}
