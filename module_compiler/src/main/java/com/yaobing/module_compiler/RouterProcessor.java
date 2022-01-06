package com.yaobing.module_compiler;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import com.yaobing.module_apt.ContractFactory;
import com.yaobing.module_apt.Router;
import com.yaobing.module_apt.helper.RouterActivityModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({ "com.yaobing.module_apt.Router"})
@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {

    private void addActivityModel(List<RouterActivityModel> mRouterActivityModels, String viewCode, TypeElement element) {

        RouterActivityModel viewCodeRouterActivityModel = new RouterActivityModel();
        viewCodeRouterActivityModel.setElement(element);
        viewCodeRouterActivityModel.setActionName(viewCode);

        viewCodeRouterActivityModel.setNeedBind(false);
        mRouterActivityModels.add(viewCodeRouterActivityModel);

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        String CLASS_NAME = "IntentRouter";
        String PACKAGE_NAME =null;

        TypeSpec.Builder tb = classBuilder(CLASS_NAME)
                .addModifiers(PUBLIC, FINAL)
                .addSuperinterface(ClassName.get("com.yaobing.module_middleware.interfaces","IRouter"))
                .addJavadoc("@API intent router created by apt\n" +
                        "支持组件化多模块\n" +
                        "add by yaobing\n");



        CodeBlock.Builder staticBuilderGo = CodeBlock.builder();
        ClassName routerManagerClassName = ClassName.get("com.yaobing.module_middleware.router", "RouterManager");

        MethodSpec.Builder methodBuilder1 = MethodSpec.methodBuilder("go")
                .addJavadoc("@created by apt \n")
                .addModifiers(PUBLIC, STATIC)
                .addParameter(ClassName.get("android.content", "Context"), "context")
                .addParameter(String.class, "name")
                .addParameter(ClassName.get("android.os", "Bundle"), "extra")
                ;

        List<ClassName> mList = new ArrayList<>();
        CodeBlock.Builder blockBuilderGo = CodeBlock.builder();
        ClassName mIntentClassName = ClassName.get("android.content", "Intent");

        blockBuilderGo.add("$T intent =" +
                        "new $T();\n",
                mIntentClassName,
                mIntentClassName
        );

        blockBuilderGo.beginControlFlow("if(extra != null)");
        blockBuilderGo.addStatement("\tintent.putExtras(extra)");
        blockBuilderGo.endControlFlow();

        blockBuilderGo.beginControlFlow(" switch (name)");//括号开始

        List<RouterActivityModel> mRouterActivityModels = new ArrayList<>();
        try {
            for (TypeElement element : ElementFilter.typesIn(roundEnvironment.getElementsAnnotatedWith(Router.class))) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "正在处理: " + element.toString());
                ClassName currentType = ClassName.get(element);
                if (mList.contains(currentType)) continue;
                mList.add(currentType);
                RouterActivityModel mRouterActivityModel = new RouterActivityModel();
                mRouterActivityModel.setElement(element);
                mRouterActivityModel.setActionName(element.getAnnotation(Router.class).value());

                mRouterActivityModel.setNeedBind(false);
                mRouterActivityModels.add(mRouterActivityModel);

                String viewCode = element.getAnnotation(Router.class).viewCode();
                if(!"unknown".equals(viewCode)){

                    if(viewCode.contains(",")){
                        String[] viewCodes = viewCode.split(",");
                        for(String vc:viewCodes){
                            addActivityModel(mRouterActivityModels, vc, element);
                        }
                    }
                    else{
                        addActivityModel(mRouterActivityModels, viewCode, element);
                    }


                }

                if(PACKAGE_NAME == null) {
//                    String temp = element.getQualifiedName().toString();
//                    temp = temp.substring(0, temp.lastIndexOf("."));
//                    PACKAGE_NAME = temp.substring(0, temp.lastIndexOf("."));
                    String temp = element.getEnclosingElement().toString();
                    if(temp.contains(".ui")){
                        PACKAGE_NAME = temp.substring(0, temp.lastIndexOf(".ui"));
                    }
                    else{
                        PACKAGE_NAME = temp;
                    }
//                    PACKAGE_NAME = element.getEnclosingElement().toString();
//                    mAbstractProcessor.mMessager.printMessage(Diagnostic.Kind.NOTE, "PACKAGE_NAME: " + PACKAGE_NAME);
                }
            }

            if(mRouterActivityModels.size() ==0){
                return true;
            }

            for (RouterActivityModel item : mRouterActivityModels) {
                blockBuilderGo.add("\tcase $S: \n", item.getActionName());
                blockBuilderGo.addStatement("\t\tintent.setClass(context, $T.class)", item.getElement());
                blockBuilderGo.addStatement("\t\tbreak");

                staticBuilderGo.addStatement("$T.getInstance().register(\"$L\", $T.class)",
                        routerManagerClassName,
                        item.getActionName(),
                        item.getElement().asType()
                );
            }
            blockBuilderGo.add("default: \n");

            blockBuilderGo.addStatement("\t\t$T routerManager = $T.getInstance()", routerManagerClassName, routerManagerClassName);
            blockBuilderGo.addStatement("\t\tClass destinationClass = routerManager.getDestination(name)");
            blockBuilderGo.addStatement("\t\tif(destinationClass == null) return");
            blockBuilderGo.addStatement("\t\tintent.setClass(context, destinationClass)");
            blockBuilderGo.addStatement("\t\tbreak");

            blockBuilderGo.endControlFlow();
            blockBuilderGo.addStatement("context.startActivity(intent)");
            methodBuilder1.addCode(blockBuilderGo.build());

            tb.addStaticBlock(staticBuilderGo.build());
            tb.addMethod(methodBuilder1.build());

            tb.addMethod(MethodSpec.methodBuilder("go")
                    .addJavadoc("@created by apt")
                    .addModifiers(PUBLIC, STATIC)
                    .addParameter(ClassName.get("android.content", "Context"), "context")
                    .addParameter(String.class, "name")
                    .addCode("go(context, name, null);\n").build());

            tb.addMethod(MethodSpec.methodBuilder("setup")
                    .addJavadoc("@created by apt")
                    .addModifiers(PUBLIC, STATIC).build());

            //2021.02.25 插件化逻辑,插件化装载类
            MethodSpec.Builder setupMethod2 = MethodSpec.methodBuilder("getRoutes")
                    .addJavadoc("@created by apt for plugin getRoutes\n")
                    .addModifiers(PUBLIC, STATIC)
                    .returns(Map.class);
            CodeBlock.Builder blockBuilderSetupMethod2= CodeBlock.builder();
            blockBuilderSetupMethod2.addStatement("$T<String, Class> routes = new $L<>()",
                    ClassName.get("java.util", "Map"),
                    ClassName.get("java.util", "HashMap"));
            for (RouterActivityModel item : mRouterActivityModels) {
                blockBuilderSetupMethod2.addStatement("routes.put(\"$L\", $T.class)", item.getActionName(), item.getElement().asType());
            }
            blockBuilderSetupMethod2.addStatement("return routes");
            setupMethod2.addCode(blockBuilderSetupMethod2.build());
            tb.addMethod(setupMethod2.build());


            //2021.02.24 插件化逻辑
            MethodSpec.Builder proxyMethod = MethodSpec.methodBuilder("proxyGo")
                    .addJavadoc("@created by apt for plugin\n")
                    .addModifiers(PUBLIC, STATIC)
                    .addParameter(ClassName.get("android.content", "Context"), "context")
                    .addParameter(String.class, "name")
                    .addParameter(ClassName.get("android.content", "Intent"), "intent");

            CodeBlock.Builder blockBuilderProxyGo = CodeBlock.builder();
            blockBuilderProxyGo.addStatement("intent.setAction(\"PROXY_VIEW_ACTION\")");
            blockBuilderProxyGo.addStatement("$T routerManager = $T.getInstance()", routerManagerClassName, routerManagerClassName);
            blockBuilderProxyGo.addStatement("Class destinationClass = routerManager.getDestination(name)");
            blockBuilderProxyGo.addStatement("if(destinationClass == null) return");
            blockBuilderProxyGo.addStatement("intent.putExtra(\"REMOTE_CLASS\", destinationClass.getName())");
            blockBuilderProxyGo.addStatement("context.startActivity(intent)");
            proxyMethod.addCode(blockBuilderProxyGo.build());

            tb.addMethod(proxyMethod.build());
            //2021.02.24 插件化逻辑 end

            JavaFile javaFile = JavaFile.builder(PACKAGE_NAME+".a", tb.build()).build();// 生成源代码
            javaFile.writeTo(processingEnv.getFiler());// 在 app module/build/generated/source/apt 生成一份源代码
        } catch (FilerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}

