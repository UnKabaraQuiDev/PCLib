package lu.pcy113.pclib.annotations.processors;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.Type;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.annotations.Getter;
import lu.pcy113.pclib.annotations.Setter;

@SupportedAnnotationTypes({ "lu.pcy113.pclib.annotations.Getter", "lu.pcy113.pclib.annotations.Setter" })
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class GetterSetterAnnotationProcessor extends AbstractProcessor {

	private static Set<String> createdFiles = new HashSet<>();
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			return false;
		}

		print(Kind.NOTE, "Starting file processing (" + annotations + ")");

		for (Element element : roundEnv.getElementsAnnotatedWith(Getter.class)) {
			if (element.getKind() == ElementKind.FIELD) {
				print(Kind.NOTE, "Field getter: "+element.getSimpleName());
				generateGetter((VariableElement) element);
			}
		}

		for (Element element : roundEnv.getElementsAnnotatedWith(Setter.class)) {
			if (element.getKind() == ElementKind.FIELD) {
				print(Kind.NOTE, "Field setter: "+element.getSimpleName());
				generateSetter((VariableElement) element);
			}
		}

		return true;
	}

	private void generateGetter(VariableElement element) {
		String fieldName = element.getSimpleName().toString();
		String className = ((TypeElement) element.getEnclosingElement()).getQualifiedName().toString();
		Type fieldType = StaticJavaParser.parseType(element.asType().toString());

		String methodName = "get" + PCUtils.capitalize(fieldName);
		MethodDeclaration getterMethod = new MethodDeclaration().addModifier(Modifier.Keyword.PUBLIC).setType(fieldType).setName(methodName).setBody(StaticJavaParser.parseBlock(String.format("{ return this.%s; }", fieldName)));

		addMethodToClass(element, getterMethod);
	}

	private void generateSetter(VariableElement element) {
		String fieldName = element.getSimpleName().toString();
		String className = ((TypeElement) element.getEnclosingElement()).getQualifiedName().toString();
		Type fieldType = StaticJavaParser.parseType(element.asType().toString());

		String methodName = "set" + PCUtils.capitalize(fieldName);
		MethodDeclaration setterMethod = new MethodDeclaration().addModifier(Modifier.Keyword.PUBLIC).setType(StaticJavaParser.parseType("void")).setName(methodName).addParameter(fieldType, fieldName)
				.setBody(StaticJavaParser.parseBlock(String.format("{ this.%s = %s; }", fieldName, fieldName)));

		addMethodToClass(element, setterMethod);
	}

	private void addMethodToClass(VariableElement fieldElement, MethodDeclaration method) {
		TypeElement classElement = (TypeElement) fieldElement.getEnclosingElement();
		String packageName = processingEnv.getElementUtils().getPackageOf(classElement).getQualifiedName().toString();
		String className = classElement.getSimpleName().toString();
		String qualifiedClassName = packageName + "." + className;
		String fileName = classElement.getQualifiedName().toString().replace('.', '/') + ".class";
		
		print(Kind.NOTE, "now: "+qualifiedClassName+", then: "+createdFiles);
		
		if(createdFiles.contains(qualifiedClassName)) {
			return;
		}

		try {
			// Get the existing source file
			print(Kind.NOTE, "looking for file: " + packageName + " " + className + ".java");
			FileObject sourceFile = processingEnv.getFiler().getResource(StandardLocation.SOURCE_PATH, packageName, className + ".java");

			CompilationUnit compilationUnit = StaticJavaParser.parse(sourceFile.openInputStream());

			// Find the class declaration
			compilationUnit.findFirst(ClassOrInterfaceDeclaration.class, c -> c.getName().getIdentifier().equals(className)).ifPresent(clazz -> {

				// Add the method if it doesn't already exist
				if (!clazz.getMethodsByName(method.getNameAsString()).isEmpty()) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Method " + method.getNameAsString() + " already exists in " + qualifiedClassName);
				} else {
					clazz.addMember(method);
				}
				
				clazz.getFieldByName(fieldElement.getSimpleName().toString()).get().getAnnotationByClass(Getter.class).get().remove();

				if (!sourceFile.delete()) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Could not delete file (" + qualifiedClassName + ").");
				} else {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Deleted file (" + qualifiedClassName + ").");
				}

				// clazz.setName(clazz.getName() + "Generated");

				// Write the updated class back to the file
				try (Writer writer = processingEnv.getFiler().createSourceFile(qualifiedClassName, classElement).openWriter()) {
					createdFiles.add(qualifiedClassName);
					writer.write(compilationUnit.toString());
				} catch (IOException e) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
				}
			});

		} catch (IOException e) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
		}
	}

	private void print(Kind note, String string) {
		processingEnv.getMessager().printMessage(note, "[" + this.getClass().getSimpleName() + "] " + string);
	}

	private void print(Kind note, String string, Element el) {
		processingEnv.getMessager().printMessage(note, "[" + this.getClass().getSimpleName() + "] " + string + " (" + el + ")");
	}

}
