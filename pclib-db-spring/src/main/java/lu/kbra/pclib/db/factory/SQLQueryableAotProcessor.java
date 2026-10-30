package lu.kbra.pclib.db.factory;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.aot.generate.GeneratedMethod;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.TypeReference;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.ResolvableType;
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

import lombok.Data;
import lombok.Setter;

public final class SQLQueryableAotProcessor implements BeanFactoryInitializationAotProcessor, ResourceLoaderAware {

	private static final String GENERATED_SUFFIX = "__SqlQueryable";

	@Setter
	private ResourceLoader resourceLoader;

	private final SQLQueryableSubclassGenerator subclassGenerator = new SQLQueryableSubclassGenerator();

	@Override
	public BeanFactoryInitializationAotContribution processAheadOfTime(final ConfigurableListableBeanFactory beanFactory) {
		@Data
		class Repo {
			final Class<?> clazz;
			final String[] beanNames;
		}

		final List<Repo> repositories = new ArrayList<>();

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

				final String[] beanNames = beanFactory.getBeanNamesForType(repoClass);
				repositories.add(new Repo(repoClass, beanNames));
			} catch (final ClassNotFoundException e) {
				throw new IllegalStateException(e);
			}
		});

		if (repositories.isEmpty()) {
			return null;
		}

		return (generationContext, initializationCode) -> {
			final List<ClassName> generatedClasses = new ArrayList<>();

			for (final Repo repository : repositories) {
				final ClassName generatedClass = this.subclassGenerator.generate(generationContext, repository.getClazz());

				generatedClasses.add(generatedClass);

				generationContext.getRuntimeHints()
						.reflection()
						.registerType(TypeReference.of(generatedClass.canonicalName()), MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
			}

			final GeneratedMethod method = initializationCode.getMethods().add("registerGeneratedSQLQueryables", builder -> {
				builder.addModifiers(javax.lang.model.element.Modifier.PRIVATE);
				builder.addParameter(DefaultListableBeanFactory.class, "beanFactory");

				for (int i = 0; i < repositories.size(); i++) {
					final Repo repository = repositories.get(i);
					final ClassName generatedClass = generatedClasses.get(i);

					for (String beanName : repository.getBeanNames()) {
						// 	@formatter:off
						builder
							.beginControlFlow("")
								.addStatement("final $T definition = new $T($T.class)",
										RootBeanDefinition.class,
										RootBeanDefinition.class,
										generatedClass)
								.addStatement("definition.setTargetType($T.forClass($T.class))", ResolvableType.class, generatedClass)
								.addStatement("definition.setAutowireMode($T.AUTOWIRE_CONSTRUCTOR)", AbstractBeanDefinition.class)
								.addStatement("beanFactory.registerBeanDefinition($S, definition)", beanName)
							.endControlFlow();
						// 	@formatter:on
					}
				}
			});

			initializationCode.addInitializer(method.toMethodReference());
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

}
