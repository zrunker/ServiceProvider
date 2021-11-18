package cc.ibooker.sprovider_processor;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import cc.ibooker.sprovider_annotation.SProvider;

public class SProviderProcessor extends AbstractProcessor {
    private Messager messager;
    private Filer filer;
    private Set<String> clazzSet;
    private Types types;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        clazzSet = new HashSet<>();
        types = processingEnv.getTypeUtils();
        messager = processingEnv.getMessager();
        messager.printMessage(Diagnostic.Kind.NOTE, "SProviderProcessor = init");
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        messager.printMessage(Diagnostic.Kind.NOTE, "SProviderProcessor = getSupportedAnnotationTypes " + SProvider.class.getCanonicalName());
        return Collections.singleton(SProvider.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE, "SProviderProcessor = process");
        if (roundEnv.processingOver()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "SProviderProcessor = processingOver");

            String resPath = "META-INF/services/cc.ibooker.sprovider_api.ISProvider";
            try {
                FileObject fo = filer.getResource(StandardLocation.CLASS_OUTPUT, "", resPath);
                if (fo != null) {
                    Reader resourceReader = fo.openReader(true);
                    BufferedReader reader = new BufferedReader(resourceReader);
                    String line = reader.readLine();
                    while (line != null) {
                        clazzSet.add(line);
                        line = reader.readLine();
                    }
                    messager.printMessage(Diagnostic.Kind.NOTE, "clazzSet1 = " + clazzSet.toString());
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }

            if (clazzSet != null && clazzSet.size() > 0) {
                try {
                    messager.printMessage(Diagnostic.Kind.NOTE, "clazzSet2 = " + clazzSet.toString());
                    FileObject fo = filer.createResource(StandardLocation.CLASS_OUTPUT, "", resPath);
                    String res = generateRes(clazzSet);
                    Writer writer = fo.openWriter();
                    writer.write(res);
                    writer.flush();
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

//            String code = generateCode(clazzSet);
//            try {
//                JavaFileObject jfo = filer.createSourceFile("cc.ibooker.sprovider_api.Loader", element);
//                Writer writer = jfo.openWriter();
//                writer.write(code);
//                writer.flush();
//                writer.close();
//            } catch (Exception e) {
//                messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
//            }
        } else {
            for (TypeElement typeElement : annotations) {
                Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(typeElement);
                if (elements == null) {
                    continue;
                }
                for (Element element : elements) {
                    if (element == null) {
                        continue;
                    }
                    messager.printMessage(Diagnostic.Kind.NOTE, element.toString());
                    if (element.getKind() == ElementKind.CLASS) {
                        TypeElement tElem = (TypeElement) element;
                        if (validInterface(tElem)) {
                            clazzSet.add(tElem.getQualifiedName().toString());
                        }
                        messager.printMessage(Diagnostic.Kind.NOTE, "clazzSet3 = " + clazzSet.toString());
                    }
                }
            }
        }
        return true;
    }

    private boolean validInterface(TypeElement typeElement) {
        if (typeElement == null) {
            return false;
        }
        List<? extends TypeMirror> typeMirrors = typeElement.getInterfaces();
        if (typeMirrors == null) {
            return false;
        }
        for (TypeMirror typeMirror : typeMirrors) {
            if (typeMirror == null) {
                continue;
            }
            TypeElement asElement = (TypeElement) types.asElement(typeMirror);
            messager.printMessage(Diagnostic.Kind.NOTE, "validInterface = " + asElement.getQualifiedName());
            if ("cc.ibooker.sprovider_api.ISProvider".equals(asElement.getQualifiedName().toString())) {
                return true;
            } else {
                if (validInterface(asElement)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String generateRes(Set<String> set) {
        StringBuilder sBuilder = new StringBuilder();
        for (String item : set) {
            sBuilder.append(item).append("\n");
        }
        return sBuilder.toString().trim();
    }

    private String generateCode(Set<String> set) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("package cc.ibooker.sprovider_api;\n")
                .append("import java.util.ArrayList;\n")
                .append("import cc.ibooker.sprovider_annotation.SProvider;\n")
                .append("public final class Loader implements ILoader {\n")
                .append("private final ArrayList<String> list = new ArrayList<>();\n")
                .append("public Loader() {\n");

        for (String className : set) {
            String str = "list.add(\"" + className + "\");\n";
            sBuilder.append(str);
        }

        sBuilder.append("}\n")
                .append("@Override\n")
                .append("public Object load(String alias) {\n")
                .append("for (String className : list) {\n")
                .append("try {\n")
                .append("Class<?> clazz = Class.forName(className);\n")
                .append("SProvider sProvider = clazz.getAnnotation(SProvider.class);\n")
                .append("if (sProvider != null && alias.equals(sProvider.alias())) {\n")
                .append("return clazz.newInstance();\n")
                .append("}\n")
                .append("} catch (Exception e) {\n")
                .append("e.printStackTrace();\n")
                .append("}\n")
                .append("}\n")
                .append("return null;\n")
                .append("}\n")
                .append("}");

        return sBuilder.toString();
    }
}