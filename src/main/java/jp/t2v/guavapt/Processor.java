package jp.t2v.guavapt;

import static javax.lang.model.util.ElementFilter.fieldsIn;
import static javax.lang.model.util.ElementFilter.typesIn;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("jp.t2v.guavapt.Guavapt")
@SupportedOptions({Options.FUNCTIONS_SUFFIX, Options.PREDICATES_SUFFIX})
public class Processor extends AbstractProcessor {

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        for (final TypeElement annotation : annotations) {
            for (final TypeElement el : typesIn(roundEnv.getElementsAnnotatedWith(annotation))) {
                final Guavapt an = el.getAnnotation(Guavapt.class);
                if (an.functions()) generateFunctionsSource(el, roundEnv);
                if (an.predicates()) generatePredicatesSource(el, roundEnv);
            }
        }
        return true;
    }

    private void generateFunctionsSource(final TypeElement element, final RoundEnvironment roundEnv) {
        try {
            final String suffix = option(Options.FUNCTIONS_SUFFIX, "Functions");
            final JavaFileObject file = createSourceFile(element, suffix);
            try (
                final Writer r = file.openWriter();
                final PrintWriter writer = new PrintWriter(r);
            ) {
                final String className = fmt("%s%s", element.getSimpleName(), suffix);
                writer.println(fmt("package %s;", packageName(element)));
                writer.println();
                writer.println("import com.google.common.base.Function;");
                writer.println();
                writer.println(fmt("public class %s {", className));
                writer.println(fmt("    private %s() {}", className));
                for (final VariableElement e : fieldsIn(element.getEnclosedElements())) {
                    if (e.getModifiers().contains(Modifier.STATIC)) continue;
                    final String fieldType = getWrappedTypeName(e);
                    final String getterName = getterName(e, fieldType);
                    writer.println(fmt("    public static final Function<%s, %s> %s = new Function<%s, %s>() {", element.getSimpleName(), fieldType, getterName, element.getSimpleName(), fieldType));
                    writer.println(fmt("        @Override"));
                    writer.println(fmt("        public %s apply(final %s input) {", fieldType, element.getSimpleName()));
                    writer.println(fmt("            return input.%s();", getterName));
                    writer.println(fmt("        }"));
                    writer.println(fmt("    };"));
                }
                ;
                writer.println("}");
                writer.flush();
            }
        } catch (final IOException e) {
            final Messager messager = processingEnv.getMessager();
            messager.printMessage(Kind.ERROR, e.toString());
        }
    }

    private void generatePredicatesSource(final TypeElement element, final RoundEnvironment roundEnv) {
        try {
            final String suffix = option(Options.PREDICATES_SUFFIX, "Predicates");
            final JavaFileObject file = createSourceFile(element, suffix);
            try (
                final Writer r = file.openWriter();
                final PrintWriter writer = new PrintWriter(r);
            ) {
                final String className = fmt("%s%s", element.getSimpleName(), suffix);
                writer.println(fmt("package %s;", packageName(element)));
                writer.println();
                writer.println("import com.google.common.base.Predicate;");
                writer.println();
                writer.println(fmt("public class %s {", className));
                writer.println(fmt("    private %s() {}", className));
                for (final VariableElement e : fieldsIn(element.getEnclosedElements())) {
                    if (e.getModifiers().contains(Modifier.STATIC)) continue;
                    final String fieldType = getWrappedTypeName(e);
                    if (!fieldType.equals(Boolean.class.getCanonicalName())) continue;
                    final String getterName = getterName(e, fieldType);
                    writer.println(fmt("    public static final Predicate<%s> %s = new Predicate<%s>() {", element.getSimpleName(), getterName, element.getSimpleName()));
                    writer.println(fmt("        @Override"));
                    writer.println(fmt("        public boolean apply(final %s input) {", element.getSimpleName()));
                    writer.println(fmt("            return input.%s();", getterName));
                    writer.println(fmt("        }"));
                    writer.println(fmt("    };"));
                }
                ;
                writer.println("}");
                writer.flush();
            }
        } catch (final IOException e) {
            final Messager messager = processingEnv.getMessager();
            messager.printMessage(Kind.ERROR, e.toString());
        }
    }
    
    private String option(final String key, final String defaultValue) {
        final String value = processingEnv.getOptions().get(key);
        return value == null ? defaultValue : value;
    }
    
    private JavaFileObject createSourceFile(final TypeElement baseElement, final String suffix) throws IOException {
        return processingEnv.getFiler().createSourceFile(baseElement.getQualifiedName() + suffix, baseElement);
    }
    
    private Name packageName(final TypeElement element) {
        return processingEnv.getElementUtils().getPackageOf(element).getQualifiedName();
    }
    
    private String getterName(final VariableElement e, final String fieldType) {
        final char[] field = e.getSimpleName().toString().toCharArray();
        field[0] = Character.toUpperCase(field[0]);
        return (fieldType.equals(Boolean.class.getCanonicalName()) ? "is" : "get") + new String(field);
    }
    
    private static String fmt(final String format, Object... params) {
        return String.format(format, params);
    }
    
    private String getWrappedTypeName(final Element e) {
        final Types util = processingEnv.getTypeUtils();
        final TypeMirror mirror = e.asType();
        return mirror.getKind().isPrimitive() 
            ? util.boxedClass((PrimitiveType) mirror).getQualifiedName().toString()
            : mirror.toString();
    }

}
