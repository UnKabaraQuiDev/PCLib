package lu.pcy113.pclib.annotations.processors;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.annotations.Getter;

import io.determann.shadow.api.ShadowApi;
import io.determann.shadow.api.ShadowProcessor;
import io.determann.shadow.api.TypeKind;
import io.determann.shadow.api.shadow.Field;
import io.determann.shadow.api.shadow.Shadow;

// @SupportedAnnotationTypes({ "lu.pcy113.pclib.annotations.Getter", "lu.pcy113.pclib.annotations.Setter" })
// @SupportedSourceVersion(SourceVersion.RELEASE_8)
public class GetterSetterAnnotationProcessor extends ShadowProcessor {

	@Override
	public void process(ShadowApi context) throws Exception {
		for (Shadow element : context.getAnnotatedWith(Getter.class.getName()).all()) {
			if (element.getTypeKind().equals(TypeKind.FIELD)) {
				print(Kind.NOTE, "Field getter: " + ((Field) element).getSimpleName());
				generateGetter((Field) element);
			}
		}
	}

	private void generateGetter(Field element) {
		String fieldName = element.getSimpleName().toString();
		String className = ((io.determann.shadow.api.shadow.Class) element.getSurrounding()).getQualifiedName().toString();
		
		String methodName = "get" + PCUtils.capitalize(fieldName);
		FileObject fo = element.getApi().readResource(StandardLocation.SOURCE_PATH, "", ((io.determann.shadow.api.shadow.Class) element.getSurrounding()).getQualifiedName());
		print(Kind.NOTE, new StringBuffer(fo.getCharContent(true)).toString());
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

		print(Kind.NOTE, "now: " + qualifiedClassName + ", then: " + createdFiles);

		if (createdFiles.contains(qualifiedClassName)) {
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
