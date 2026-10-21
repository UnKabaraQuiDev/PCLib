package lu.kbra.pclib.db.factory;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.javapoet.ClassName;

import lu.kbra.pclib.db.impl.DeferredSQLQueryable;
import lu.kbra.pclib.db.impl.ProxySQLQueryable;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.pclib.db.table.ProxyDatabaseTable;
import lu.kbra.pclib.db.utils.SQLQueryableSubclassGenerator;
import lu.kbra.pclib.db.view.AbstractDBView;
import lu.kbra.pclib.db.view.DeferredDatabaseView;
import lu.kbra.pclib.db.view.ProxyDatabaseView;

import lombok.Setter;

public final class SQLQueryableAotProcessor implements BeanFactoryInitializationAotProcessor, ResourceLoaderAware {

	private static final String GENERATED_SUFFIX = "__SqlQueryable";

	@Setter
	private ResourceLoader resourceLoader;

	private final SQLQueryableSubclassGenerator subclassGenerator = new SQLQueryableSubclassGenerator();

	@Override
	public BeanFactoryInitializationAotContribution processAheadOfTime(final ConfigurableListableBeanFactory beanFactory) {
		final List<Class<?>> repositories = new ArrayList<>();

		this.forAll(bd -> {
			try {
				final Class<?> repoClass = Class.forName(bd.getBeanClassName());

				if (!repoClass.isInterface() && !Modifier.isAbstract(repoClass.getModifiers())) {
					return;
				}

				if (repoClass.equals(DeferredSQLQueryable.class) || repoClass.equals(DeferredDatabaseView.class)
						|| repoClass.equals(DeferredDatabaseTable.class) || repoClass.equals(AbstractDBView.class)
						|| repoClass.equals(AbstractDBTable.class) || repoClass.equals(ProxyDatabaseView.class)
						|| repoClass.equals(ProxyDatabaseTable.class) || repoClass.equals(SQLQueryable.class)
						|| repoClass.equals(ProxySQLQueryable.class)) {
					return;
				}

				repositories.add(repoClass);
			} catch (final ClassNotFoundException e) {
				throw new IllegalStateException(e);
			}
		});

		System.out.println(repositories);
		if (repositories.isEmpty()) {
			return null;
		}

		return (generationContext, initializationCode) -> {
			final List<ClassName> generatedClasses = new ArrayList<>();

			for (final Class<?> repository : repositories) {
				final ClassName generatedClass = this.subclassGenerator.generate(generationContext, repository);

				generatedClasses.add(generatedClass);
			}

//			final GeneratedMethod method = initializationCode.getMethods().add("registerGeneratedSQLQueryables", builder -> {
//				builder.addModifiers(javax.lang.model.element.Modifier.PRIVATE);
//				builder.addParameter(DefaultListableBeanFactory.class, "beanFactory");
//
//				for (int i = 0; i < repositories.size(); i++) {
//					final Queryable repository = repositories.get(i);
//					final ClassName generatedClass = generatedClasses.get(i);
//
//					// 	@formatter:off
//					builder
//						.beginControlFlow("")
//							.beginControlFlow("if (beanFactory.containsBeanDefinition($S))", repository.beanName())
//								.addStatement("beanFactory.removeBeanDefinition($S)", repository.beanName())
//							.endControlFlow()
//							.addStatement("final $T definition = new $T($T.class)",
//									RootBeanDefinition.class,
//									RootBeanDefinition.class,
//									generatedClass)
//							.addStatement("definition.setTargetType($T.forClass($T.class))", ResolvableType.class, generatedClass)
//							.addStatement("definition.setAutowireMode($T.AUTOWIRE_CONSTRUCTOR)", AbstractBeanDefinition.class)
//							.addStatement("beanFactory.registerBeanDefinition($S, definition)", repository.beanName())
//						.endControlFlow();
//					// 	@formatter:on
//				}
//			});
//
//			initializationCode.addInitializer(method.toMethodReference());
		};
	}

	protected void forAll(final Consumer<BeanDefinition> consumer) {
		final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false,
				new StandardEnvironment()) {

			@Override
			protected boolean isCandidateComponent(final AnnotatedBeanDefinition beanDefinition) {
				if (super.isCandidateComponent(beanDefinition)) {
					return true;
				}

				final AnnotationMetadata metadata = beanDefinition.getMetadata();

				final boolean isSpringComponent = metadata.hasAnnotation("org.springframework.stereotype.Component")
						|| metadata.hasMetaAnnotation("org.springframework.stereotype.Component");

				if (!isSpringComponent) {
					return false;
				}

				try {
					final Class<?> clazz = Class.forName(metadata.getClassName());
					return SQLQueryable.class.isAssignableFrom(clazz) && (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()));
				} catch (final ClassNotFoundException e) {
					return false;
				}
			}
		};

		scanner.setResourceLoader(this.resourceLoader);

		scanner.addIncludeFilter(new AssignableTypeFilter(SQLQueryable.class));

		for (final BeanDefinition bd : scanner.findCandidateComponents("**")) {
			consumer.accept(bd);
		}
	}

	private boolean isQueryableFactory(final String className) {
		return className.equals(DeferredSQLQueryableFactoryBean.class.getName())
				|| className.equals(AbstractSQLQueryableFactoryBean.class.getName())
				|| className.equals(SQLQueryableFactoryBean.class.getName());
	}

	private record Queryable(String beanName, Class<?> repositoryClass) {
	}

}
