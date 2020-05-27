package com.xlh.study.butterknife.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.xlh.study.butterknife.annotation.BindView;
import com.xlh.study.butterknife.annotation.OnClick;
import com.xlh.study.butterknife.compiler.utils.Constants;
import com.xlh.study.butterknife.compiler.utils.EmptyUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

// 用来生成 META-INF/services/javax.annotation.processing.Processor 文件
@AutoService(Processor.class)
// 允许/支持的注解类型，让注解处理器处理
@SupportedAnnotationTypes({Constants.BINDVIEW_ANNOTATION_TYPES, Constants.ONCLICK_ANNOTATION_TYPES})
// 指定JDK编译版本
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class SimpleButterKnifeProcessor extends AbstractProcessor {
    // Elements中包含用于操作Element的工作方法
    private Elements elementUtils;
    // Filer用来创建新的源文件，class文件以及辅助文件
    private Filer filer;
    // 打印使用
    private Messager messager;
    // Types包含用于操作TypeMirror的工具方法
    private Types typeUtils;


    /**
     * key:类节点
     * value:被@BindView注解的属性集合
     */
    private Map<TypeElement, List<VariableElement>> tempBindViewMap = new HashMap<>();

    /**
     * key:类节点
     * value:被@OnClick注解的方法集合
     */
    private Map<TypeElement, List<ExecutableElement>> tempOnClickMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        // 初始化
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
        messager.printMessage(Diagnostic.Kind.NOTE,
                "注解处理器初始化完成，开始处理注解------------------------------->");
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotationsSet, RoundEnvironment roundEnv) {

        // 属性上使用了@BindView注解
        if (!EmptyUtils.isEmpty(annotationsSet)) {
            // 获取所有被@BindView注解的属性元素集合
            Set<? extends Element> bindViewElements = roundEnv.getElementsAnnotatedWith(BindView.class);
            // 获取所有被@OnClick注解的方法元素集合
            Set<? extends Element> onClickElements = roundEnv.getElementsAnnotatedWith(OnClick.class);

            // 判断集合是否为空
            if (!EmptyUtils.isEmpty(bindViewElements) || !EmptyUtils.isEmpty(onClickElements)) {
                // 收集信息，存储Map赋值
                valueOfMap(bindViewElements, onClickElements);

                try {
                    // 生成类文件
                    createJavaFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;

        }

        return false;
    }

    /**
     * 收集信息，存储Map赋值
     *
     * @param bindViewElements
     * @param onClickElements
     */
    private void valueOfMap(Set<? extends Element> bindViewElements, Set<? extends Element> onClickElements) {
        // 判断所有被@BindView注解的属性元素集合是否为空
        if (!EmptyUtils.isEmpty(bindViewElements)) {
            for (Element bindViewElement : bindViewElements) {
                messager.printMessage(Diagnostic.Kind.NOTE, "@BindView --->" + bindViewElement.getSimpleName());
                if (bindViewElement.getKind() == ElementKind.FIELD) {
                    VariableElement fieldElement = (VariableElement) bindViewElement;
                    // 属性节点，上一个（父节点），类节点
                    TypeElement typeElement = (TypeElement) fieldElement.getEnclosingElement();
                    // 判断map集合中是否包含了key(类节点)
                    if (tempBindViewMap.containsKey(typeElement)) {
                        // 如果map集合中包含了key(类节点)
                        tempBindViewMap.get(typeElement).add(fieldElement);
                    } else {
                        // 如果不包含，则添加
                        List<VariableElement> fields = new ArrayList<>();
                        fields.add(fieldElement);
                        tempBindViewMap.put(typeElement, fields);
                    }
                }
            }
        }

        // 判断所有被@OnClick注解的属性元素集合是否为空
        if (!EmptyUtils.isEmpty(onClickElements)) {
            for (Element onClickElement : onClickElements) {
                messager.printMessage(Diagnostic.Kind.NOTE, "@OnClick --->" + onClickElement.getSimpleName());
                if (onClickElement.getKind() == ElementKind.METHOD) {
                    ExecutableElement methodElement = (ExecutableElement) onClickElement;
                    // 属性节点，上一个（父节点），类节点
                    TypeElement typeElement = (TypeElement) methodElement.getEnclosingElement();
                    // 判断map集合中是否包含了key(类节点)
                    if (tempOnClickMap.containsKey(typeElement)) {
                        // 如果map集合中包含了key(类节点)
                        tempOnClickMap.get(typeElement).add(methodElement);
                    } else {
                        // 如果不包含，则添加
                        List<ExecutableElement> methods = new ArrayList<>();
                        methods.add(methodElement);
                        tempOnClickMap.put(typeElement, methods);
                    }
                }
            }
        }
    }

    /**
     * 生成类文件
     */
    private void createJavaFile() throws IOException {

        // 判断是否有需要生成的类文件
        if (!EmptyUtils.isEmpty(tempBindViewMap)) {
            // 获取接口的类型
            TypeElement viewBinderType = elementUtils.getTypeElement(Constants.VIEWBINDER);
            TypeElement clickListenerType = elementUtils.getTypeElement(Constants.CLICKLISTENER);
            TypeElement viewType = elementUtils.getTypeElement(Constants.VIEW);

            for (Map.Entry<TypeElement, List<VariableElement>> bindViewEntry : tempBindViewMap.entrySet()) {
                // 类名
                ClassName className = ClassName.get(bindViewEntry.getKey());
                // 实现接口泛型---->implements ViewBinder<MainActivity>
                ParameterizedTypeName typeName = ParameterizedTypeName.get(ClassName.get(viewBinderType), ClassName.get(bindViewEntry.getKey()));

                // 方法参数体---->final MainActivity target
                ParameterSpec parameterSpec = ParameterSpec
                        .builder(ClassName.get(bindViewEntry.getKey()), // MainActivity
                                Constants.TARGET_PARAMETER_NAME) // 方法参数名target
                        // 修饰符
                        .addModifiers(Modifier.FINAL)
                        .build();

                /**
                 * bind方法体
                 * @Override
                 * public void bind(final MainActivity target) {
                 */
                MethodSpec.Builder methodBuilder = MethodSpec
                        // // bind方法名
                        .methodBuilder(Constants.BIND_METHOD_NAME)
                        // 添加@Override注解
                        .addAnnotation(Override.class)
                        // 添加方法修饰符
                        .addModifiers(Modifier.PUBLIC)
                        // 添加方法参数
                        .addParameter(parameterSpec);

                for (VariableElement fieldElement : bindViewEntry.getValue()) {
                    // 获取属性名
                    String fieldName = fieldElement.getSimpleName().toString();
                    // 获取@BindView注解的值,如R.id.xx
                    int annotationValue = fieldElement.getAnnotation(BindView.class).value();
                    // target.tv = target.findViewById(R.id.tv);
                    String methodContent = "$N." + fieldName + " = $N.findViewById($L)";
                    // 加入方法内容
                    methodBuilder
                            .addStatement(methodContent,
                                    Constants.TARGET_PARAMETER_NAME,
                                    Constants.TARGET_PARAMETER_NAME,
                                    annotationValue);
                }

                if (!EmptyUtils.isEmpty(tempOnClickMap)) {
                    for (Map.Entry<TypeElement, List<ExecutableElement>> onClickEntry : tempOnClickMap.entrySet()) {
                        // 类名
                        if (className.equals(ClassName.get(onClickEntry.getKey()))) {
                            for (ExecutableElement methodElement : onClickEntry.getValue()) {
                                // 获取方法名
                                String methodName = methodElement.getSimpleName().toString();
                                // 获取@OnClick注解的值
                                int annotationValue = methodElement.getAnnotation(OnClick.class).value();

                                methodBuilder.beginControlFlow("$N.findViewById($L).setOnClickListener(new $T()",
                                        Constants.TARGET_PARAMETER_NAME, annotationValue, ClassName.get(clickListenerType))
                                        .beginControlFlow("public void doClick($T v)", ClassName.get(viewType))
                                        .addStatement("$N." + methodName + "(v)", Constants.TARGET_PARAMETER_NAME)
                                        .endControlFlow()
                                        .endControlFlow(")")
                                        .build();

                            }
                        }
                    }
                }

                // 生成必须是同包
                JavaFile.builder(className.packageName(),  // 包名
                        TypeSpec.classBuilder(className.simpleName() + "$ViewBinder") // 类名
                                .addSuperinterface(typeName) // 实现ViewBinder接口（有泛型）
                                .addModifiers(Modifier.PUBLIC) // 类修饰符
                                .addMethod(methodBuilder.build()) // 加入方法体
                                .build()) // 类构建完成
                        .build() // JavaFile构建
                        .writeTo(filer); // 文件生成器开始生成类文件

            }

        }

    }


}
