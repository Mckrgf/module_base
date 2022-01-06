package com.yaobing.module_compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import com.yaobing.module_apt.ApiFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

/**
 * Create by 姚冰
 * on 2020/10/29
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({ "com.yaobing.module_apt.ApiFactory"})
@AutoService(Processor.class)
public class ApiFactoryProcessor  extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        String CLASS_NAME = null;
        Builder tb = null;
        String CLIENT_PATH = null;

        try {
            Iterator var6 = ElementFilter.typesIn(roundEnvironment.getElementsAnnotatedWith(ApiFactory.class)).iterator();

            while(var6.hasNext()) {
                TypeElement element = (TypeElement)var6.next();
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "正在处理: " + element.toString());
                if (CLASS_NAME == null) {
                    CLASS_NAME = ((ApiFactory)element.getAnnotation(ApiFactory.class)).name();
                    if ("".equals(CLASS_NAME)) {
                        CLASS_NAME = "HttpClient";
                    }

                    tb = TypeSpec.classBuilder(CLASS_NAME).addModifiers(new Modifier[]{Modifier.PUBLIC, Modifier.FINAL}).addJavadoc("@API factory created by apt\n", new Object[0]);
                }

                if (CLIENT_PATH == null) {
                    CLIENT_PATH = element.getEnclosingElement().toString();
                }

                Iterator var8 = element.getEnclosedElements().iterator();

                while(var8.hasNext()) {
                    Element e = (Element)var8.next();
                    ExecutableElement executableElement = (ExecutableElement)e;
                    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(e.getSimpleName().toString()).addJavadoc("@created by apt\n", new Object[0]).addModifiers(new Modifier[]{Modifier.PUBLIC, Modifier.STATIC});
                    methodBuilder.returns(TypeName.get(executableElement.getReturnType()));
                    String paramsString = "";

                    VariableElement ep;
                    for(Iterator var13 = executableElement.getParameters().iterator(); var13.hasNext(); paramsString = paramsString + ep.getSimpleName().toString() + ",") {
                        ep = (VariableElement)var13.next();
                        methodBuilder.addParameter(TypeName.get(ep.asType()), ep.getSimpleName().toString(), new Modifier[0]);
                    }

                    if ("".equals(paramsString)) {
                        methodBuilder.addStatement("return $T.getInstance().retrofit.create($T.class).$L().compose($T.io_main())", new Object[]{ClassName.get("com.yaobing.module_middleware.network", "Api", new String[0]), ClassName.get(element), e.getSimpleName().toString(), ClassName.get("com.yaobing.module_middleware.network", "RxSchedulers", new String[0])});
                        tb.addMethod(methodBuilder.build());
                    } else {
                        methodBuilder.addStatement("return $T.getInstance().retrofit.create($T.class).$L($L).compose($T.io_main())", new Object[]{ClassName.get("com.yaobing.module_middleware.network", "Api", new String[0]), ClassName.get(element), e.getSimpleName().toString(), paramsString.substring(0, paramsString.length() - 1), ClassName.get("com.yaobing.module_middleware.network", "RxSchedulers", new String[0])});
                        tb.addMethod(methodBuilder.build());
                    }
                }
            }

            if (CLIENT_PATH == null) {
                return true;
            }

            JavaFile javaFile = JavaFile.builder(CLIENT_PATH, tb.build()).build();
            javaFile.writeTo(processingEnv.getFiler());
        } catch (FilerException var15) {
        } catch (IOException var16) {
            var16.printStackTrace();
        } catch (Exception var17) {
            var17.printStackTrace();
        }
        return true;
    }
}
