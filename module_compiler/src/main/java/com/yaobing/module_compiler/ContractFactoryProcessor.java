package com.yaobing.module_compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import com.yaobing.module_apt.ContractFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

/**
 * @author : yaobing
 * @date : 2020/10/26 14:35
 * @desc :
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({ "com.yaobing.module_apt.ContractFactory"})
@AutoService(Processor.class)
public class ContractFactoryProcessor extends AbstractProcessor {
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return super.getSupportedAnnotationTypes();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        try {
            Iterator var3 = ElementFilter.typesIn(roundEnvironment.getElementsAnnotatedWith(ContractFactory.class)).iterator();

            while (var3.hasNext()) {
                TypeElement element = (TypeElement) var3.next();
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "正在处理: " + element.toString());
                String CLASS_NAME = element.getSimpleName().toString().replace("API", "Contract");
                String API_PATH = element.getEnclosingElement().toString();
                String PACKAGE_NAME = API_PATH.substring(0, API_PATH.lastIndexOf("."));
                String CONTRACT_PATH = PACKAGE_NAME + ".contract";
                String BEAN_PATH = PACKAGE_NAME + ".bean";
                String[] entityClass = null;
                Iterator var11 = element.getAnnotationMirrors().iterator();

                label65:
                while (var11.hasNext()) {
                    AnnotationMirror am = (AnnotationMirror) var11.next();
                    if (ContractFactory.class.getName().equals(am.getAnnotationType().toString())) {
                        Iterator var25 = am.getElementValues().entrySet().iterator();

                        Map.Entry entry;
                        do {
                            if (!var25.hasNext()) {
                                continue label65;
                            }

                            entry = (Map.Entry) var25.next();
                        } while (!"entites".equals(((ExecutableElement) entry.getKey()).getSimpleName().toString()));

                        AnnotationValue annotationValue = (AnnotationValue) entry.getValue();
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "entities: " + annotationValue.toString());
                        entityClass = annotationValue.toString().replace(".class", "").replace("{", "").replace("}", "").split(",");
                    }
                }

                Builder tb = TypeSpec.interfaceBuilder(CLASS_NAME).addModifiers(new Modifier[]{Modifier.PUBLIC, Modifier.STATIC}).addJavadoc("@Contract created by apt\nzxcv\nadd by yaobing\n", new Object[0]);
                Builder viewTb = TypeSpec.interfaceBuilder("View").addModifiers(new Modifier[]{Modifier.PUBLIC, Modifier.STATIC}).addSuperinterface(ClassName.get("com.yaobing.module_common_view.contract", "IBaseView", new String[0])).addJavadoc("@View created by apt\n", new Object[0]);
                List<Element> enclosedMethods = new ArrayList();
                enclosedMethods.addAll(element.getEnclosedElements());
                int index = 0;

                for (Iterator var15 = enclosedMethods.iterator(); var15.hasNext(); ++index) {
                    Element e = (Element) var15.next();
                    ExecutableElement executableElement = (ExecutableElement) e;
                    MethodSpec.Builder successMethodBuilder = null;
                    if (entityClass != null && entityClass.length > index && entityClass[index] != null && !entityClass[index].contains("NullEntity")) {
                        String currentClass = entityClass[index];
                        successMethodBuilder = MethodSpec.methodBuilder(e.getSimpleName().toString() + "Success").addParameter(ClassName.get(((String) currentClass).substring(0, ((String) currentClass).lastIndexOf(".")), ((String) currentClass).substring(((String) currentClass).lastIndexOf(".") + 1), new String[0]), "entity", new Modifier[0]).addJavadoc("@method create by apt\n", new Object[0]).addModifiers(new Modifier[]{Modifier.PUBLIC, Modifier.ABSTRACT});
                    } else {
                        successMethodBuilder = MethodSpec.methodBuilder(e.getSimpleName().toString() + "Success").addJavadoc("@method create by apt\n", new Object[0]).addModifiers(new Modifier[]{Modifier.PUBLIC, Modifier.ABSTRACT});
                    }

                    successMethodBuilder.returns(TypeName.get(executableElement.getReturnType()));
                    MethodSpec.Builder failedMethodBuilder = MethodSpec.methodBuilder(e.getSimpleName().toString() + "Failed").addParameter(ClassName.get(String.class), "errorMsg", new Modifier[0]).addJavadoc("@method create by apt\n", new Object[0]).addModifiers(new Modifier[]{Modifier.PUBLIC, Modifier.ABSTRACT});
                    failedMethodBuilder.returns(TypeName.get(executableElement.getReturnType()));
                    viewTb.addMethod(successMethodBuilder.build());
                    viewTb.addMethod(failedMethodBuilder.build());
                }

                tb.addType(viewTb.build());
                Builder presenterTb = TypeSpec.classBuilder("Presenter").addModifiers(new Modifier[]{Modifier.PUBLIC, Modifier.STATIC, Modifier.ABSTRACT}).superclass(ParameterizedTypeName.get(ClassName.get("com.yaobing.module_common_view.base.presenter", "BasePresenter", new String[0]), new TypeName[]{ClassName.get(CONTRACT_PATH, CLASS_NAME + ".View", new String[0])})).addSuperinterface(ClassName.get(API_PATH, element.getSimpleName().toString(), new String[0])).addJavadoc("@Presenter created by apt\n", new Object[0]);
                tb.addType(presenterTb.build());
                if (CONTRACT_PATH == null) {
                    return true;
                }

                JavaFile javaFile = JavaFile.builder(CONTRACT_PATH, tb.build()).build();
                javaFile.writeTo(processingEnv.getFiler());
            }
        } catch (FilerException var20) {
        } catch (IOException var21) {
            var21.printStackTrace();
        } catch (Exception var22) {
            var22.printStackTrace();
        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return super.getSupportedSourceVersion();
    }
}
