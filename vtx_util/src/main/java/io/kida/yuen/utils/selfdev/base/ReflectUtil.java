package io.kida.yuen.utils.selfdev.base;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.esotericsoftware.reflectasm.MethodAccess;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Verticle;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_util
 * @File: ReflectUtil.java
 * @ClassName: ReflectUtil
 * @Description:反射工具类
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/11
 */
@Slf4j
public class ReflectUtil {

    private static final String GETTER = "get";
    private static final String SETTER = "set";
    private static final String UTF_8 = "UTF-8";

    private static final Class<?>[] baseClazzArr = {String.class, int.class, Long.class, Double.class, double.class,
        Date.class, Integer.class, boolean.class, Boolean.class, Object.class};

    /**
     * 
     * @MethodName: getFields
     * @Description: 获取指定类的所有字段,排除static,final字段
     * @author yuanzhenhui
     * @param clazz
     * @return List<Field>
     * @date 2023-04-17 03:24:43
     */
    public static List<Field> getFields(Class<?> clazz) {
        List<Field> fieldResult = new CopyOnWriteArrayList<>();
        while (clazz != Object.class) {
            Field[] fields = clazz.getDeclaredFields();
            Arrays.stream(fields)
                .filter(field -> !Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers()))
                .forEach(field -> {
                    field.setAccessible(true);
                    fieldResult.add(field);
                });
            clazz = clazz.getSuperclass();
        }
        return fieldResult;
    }

    /**
     * 
     * @MethodName: getFieldNames
     * @Description: 获取指定类的所有字段名称,排除static,final字段
     * @author yuanzhenhui
     * @param clazz
     * @return List<String>
     * @date 2023-04-17 03:32:05
     */
    public static List<String> getFieldNames(Class<?> clazz) {
        List<Field> fields = getFields(clazz);
        return fields.stream().map(Field::getName).collect(Collectors.toList());
    }

    /**
     * 
     * @MethodName: getSuperClassGenricType
     * @Description: 通过反射, 获得定义 Class 时声明的父类的泛型参数的类型 如: public EmployeeDao extends BaseDao<Employee, String>
     * @author yuanzhenhui
     * @param clazz
     * @param index
     * @return Class<?>
     * @date 2023-04-17 03:35:44
     */
    public static Class<?> getSuperClassGenricType(Class<?> clazz, int index) {
        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType)genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class<?>)params[index];
    }

    /**
     * 
     * @MethodName: getSuperGenericType
     * @Description: 通过反射, 获得 Class 定义中声明的父类的泛型参数类型 如: public EmployeeDao extends BaseDao<Employee, String>
     * @author yuanzhenhui
     * @param <T>
     * @param clazz
     * @return Class<T>
     * @date 2023-04-17 03:36:04
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getSuperGenericType(Class<?> clazz) {
        return (Class<T>)getSuperClassGenricType(clazz, 0);
    }

    /**
     * 
     * @MethodName: getStringValueByAsm
     * @Description: 通过ASM反射获取字段的字符串值
     * @author yuanzhenhui
     * @param <T>
     * @param t
     * @param fieldName
     * @return String
     * @date 2023-04-17 03:36:23
     */
    public static <T> String getStringValueByAsm(T t, String fieldName) {
        String reStr = null;
        Object getValue = getValueByAsm(t, fieldName);
        if (null != getValue) {
            reStr = String.valueOf(getValue);
        }
        return reStr;
    }

    /**
     * 
     * @MethodName: getClasses
     * @Description: 根据包名获取包下面所有的类名
     * @author yuanzhenhui
     * @param scanPath
     * @param recursive
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     *             CopyOnWriteArraySet<Class<?>>
     * @date 2023-04-17 03:51:49
     */
    public static CopyOnWriteArraySet<Class<?>> getClasses(String scanPath, boolean recursive)
        throws IOException, ClassNotFoundException {
        CopyOnWriteArraySet<Class<?>> classes = new CopyOnWriteArraySet<>();

        if (StringUtil.isNotEmpty(scanPath)) {
            String[] scanPathArr = scanPath.split(",");
            for (String packageName : scanPathArr) {
                String packageDirName = packageName.replace('.', '/');
                Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
                while (dirs.hasMoreElements()) {
                    URL url = dirs.nextElement();
                    String protocol = url.getProtocol();
                    if ("file".equals(protocol)) {
                        findClassesInPackageByFile(packageName, URLDecoder.decode(url.getFile(), UTF_8), recursive,
                            classes);
                    } else if ("jar".equals(protocol)) {
                        JarFile jar = ((JarURLConnection)url.openConnection()).getJarFile();
                        findClassesInPackageByJar(packageName, jar.entries(), packageDirName, recursive, classes);
                    }
                }
            }
        }

        return classes;
    }

    /**
     * 
     * @MethodName: getVerticleClasses
     * @Description: 获取Verticle类
     * @author yuanzhenhui
     * @param scanPath
     * @param recursive
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     *             CopyOnWriteArraySet<Class<Verticle>>
     * @date 2023-04-17 03:51:59
     */
    @SuppressWarnings("unchecked")
    public static CopyOnWriteArraySet<Class<Verticle>> getVerticleClasses(String scanPath, boolean recursive)
        throws IOException, ClassNotFoundException {
        CopyOnWriteArraySet<Class<?>> classes = new CopyOnWriteArraySet<>();
        CopyOnWriteArraySet<Class<Verticle>> vcRe = null;
        String packageName = scanPath;
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
        while (dirs.hasMoreElements()) {
            URL url = dirs.nextElement();
            String protocol = url.getProtocol();
            if ("file".equals(protocol)) {
                findClassesInPackageByFile(packageName, URLDecoder.decode(url.getFile(), UTF_8), recursive, classes);
            } else if ("jar".equals(protocol)) {
                JarFile jar = ((JarURLConnection)url.openConnection()).getJarFile();
                findClassesInPackageByJar(packageName, jar.entries(), packageDirName, recursive, classes);
            }
        }

        if (!classes.isEmpty()) {
            vcRe = new CopyOnWriteArraySet<>(classes.parallelStream().filter(AbstractVerticle.class::isAssignableFrom)
                .map(clazz -> (Class<Verticle>)clazz).collect(Collectors.toList()));
        }
        return vcRe;
    }

    /**
     * 
     * @MethodName: findClassesInPackageByJar
     * @Description: 以jar的形式来获取包下的所有Class
     * @author yuanzhenhui
     * @param packageName
     * @param entries
     * @param packageDirName
     * @param recursive
     * @param classes
     * @throws ClassNotFoundException
     *             void
     * @date 2023-04-17 03:52:12
     */
    private static void findClassesInPackageByJar(String packageName, Enumeration<JarEntry> entries,
        String packageDirName, boolean recursive, CopyOnWriteArraySet<Class<?>> classes) throws ClassNotFoundException {
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.charAt(0) == '/') {
                name = name.substring(1);
            }
            if (name.startsWith(packageDirName)) {
                int idx = name.lastIndexOf('/');
                if (idx != -1) {
                    packageName = name.substring(0, idx).replace('/', '.');
                }
                if (((idx != -1) || recursive) && (name.endsWith(".class") && !entry.isDirectory())) {
                    classes.add(
                        Class.forName(packageName + '.' + name.substring(packageName.length() + 1, name.length() - 6)));
                }
            }
        }
    }

    /**
     * 
     * @MethodName: findClassesInPackageByFile
     * @Description: 以文件的形式来获取包下的所有Class
     * @author yuanzhenhui
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     *            void
     * @date 2023-04-17 03:52:28
     */
    private static void findClassesInPackageByFile(String packageName, String packagePath, boolean recursive,
        CopyOnWriteArraySet<Class<?>> classes) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            log.warn("no packagename path : [{}] has files", packageName);
            return;
        }
        File[] dirfiles =
            dir.listFiles(file -> (recursive && file.isDirectory()) || (file.getName().endsWith(".class")));

        if (null != dirfiles) {
            Arrays.asList(dirfiles).stream().forEach(file -> {
                if (file.isDirectory()) {
                    findClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive,
                        classes);
                } else {
                    try {
                        classes.add(Thread.currentThread().getContextClassLoader()
                            .loadClass(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                    } catch (ClassNotFoundException e) {
                        log.error("func[ReflectUtil.findClassesInPackageByFile] Exception [{} - {}] stackTrace[{}] ",
                            e.getCause(), e.getMessage(), e.getStackTrace());
                    }
                }
            });
        }
    }

    /**
     * 
     * @MethodName: convertJson2Pojo
     * @Description: 字符串转换成pojo对象
     * @author yuanzhenhui
     * @param <T>
     * @param bodyStr
     * @param clazz
     * @return T
     * @date 2023-04-17 03:52:51
     */
    public static <T> T convertJson2Pojo(String bodyStr, Class<T> clazz) {
        return Json.decodeValue(bodyStr, clazz);
    }

    /**
     * 
     * @MethodName: convertString2Json
     * @Description: 字符串转JOOQ Json
     * @author yuanzhenhui
     * @param bodyStr
     * @return JsonObject
     * @date 2023-04-17 03:53:01
     */
    public static JsonObject convertString2Json(String bodyStr) {
        JsonObject json = JsonObject.mapFrom(Json.decodeValue(bodyStr));
        return JsonObject.mapFrom(
            json.stream().collect(Collectors.toMap(e -> StringUtil.camel2Underline(e.getKey()), e -> e.getValue())));
    }

    /**
     * 
     * @MethodName: getValueByAsm
     * @Description: 通过ASM获取实体中的字段的值
     * @author yuanzhenhui
     * @param <T>
     * @param t
     * @param fieldName
     * @return Object
     * @date 2023-04-17 03:53:11
     */
    public static <T> Object getValueByAsm(T t, String fieldName) {
        return getValueByAsm(MethodAccess.get(t.getClass()), t, fieldName);
    }

    /**
     * 
     * @MethodName: getValueByAsm
     * @Description: 通过ASM获取实体中的字段的值(在外层已经做好MethodAccess预设)
     * @author yuanzhenhui
     * @param <T>
     * @param ma
     * @param t
     * @param fieldName
     * @return Object
     * @date 2023-04-17 03:53:22
     */
    public static <T> Object getValueByAsm(MethodAccess ma, T t, String fieldName) {
        return ma.invoke(t, GETTER + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
    }

    /**
     * 
     * @MethodName: setValueByAsm
     * @Description: 通过ASM赋值到实体中
     * @author yuanzhenhui
     * @param <T>
     * @param t
     * @param fieldName
     * @param obj
     *            void
     * @date 2023-04-17 03:56:06
     */
    public static <T> void setValueByAsm(T t, String fieldName, Object obj) {
        setValueByAsm(MethodAccess.get(t.getClass()), t, fieldName, obj);
    }

    /**
     * 
     * @MethodName: setValueByAsm
     * @Description: 通过ASM赋值到实体中在外层已经做好MethodAccess预设()
     * @author yuanzhenhui
     * @param <T>
     * @param ma
     * @param t
     * @param fieldName
     * @param obj
     *            void
     * @date 2023-04-17 03:56:15
     */
    public static <T> void setValueByAsm(MethodAccess ma, T t, String fieldName, Object obj) {
        ma.invoke(t, SETTER + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), obj);
    }

    /**
     * 
     * @MethodName: listCopyAsm
     * @Description: 使用ASM对list结合赋值
     * @author yuanzhenhui
     * @param <T>
     * @param sourceList
     * @param targetClazz
     * @return List<T>
     * @date 2023-04-17 03:56:24
     */
    public static <T> List<T> listCopyAsm(List<?> sourceList, Class<T> targetClazz) {
        List<T> reList = new ArrayList<>();
        if (null != sourceList && !sourceList.isEmpty()) {
            MethodAccess targetMa = MethodAccess.get(targetClazz);
            MethodAccess sourceMa = MethodAccess.get(sourceList.get(0).getClass());
            reList = sourceList.stream().map(source -> objectCopyAsm(targetMa, sourceMa, source, targetClazz))
                .collect(Collectors.toList());
        }
        return reList;
    }

    /**
     * 
     * @MethodName: objectCopyAsm
     * @Description: 通过ASM方式对类进行复制
     * @author yuanzhenhui
     * @param <T>
     * @param sourceObject
     * @param targetClazz
     * @return T
     * @date 2023-04-17 03:56:45
     */
    public static <T> T objectCopyAsm(Object sourceObject, Class<T> targetClazz) {
        MethodAccess targetMa = MethodAccess.get(targetClazz);
        MethodAccess sourceMa = MethodAccess.get(sourceObject.getClass());
        return objectCopyAsm(targetMa, sourceMa, sourceObject, targetClazz);
    }

    /**
     * 
     * @MethodName: objectCopyAsm
     * @Description: 通过ASM方式对类进行复制
     * @author yuanzhenhui
     * @param <T>
     * @param targetMa
     * @param sourceMa
     * @param sourceObject
     * @param targetClazz
     * @return T
     * @date 2023-04-17 03:57:00
     */
    public static <T> T objectCopyAsm(MethodAccess targetMa, MethodAccess sourceMa, Object sourceObject,
        Class<T> targetClazz) {
        T targetObject = null;
        try {
            targetObject = targetClazz.newInstance();
            // 首先判断源实体是否为空
            if (null != sourceObject) {
                // 获取源实体类
                List<Field> fieldList = getAllFields(sourceObject);
                if (!fieldList.isEmpty()) {
                    Field[] fields = new Field[fieldList.size()];
                    for (int i = 0; i < fieldList.size(); i++) {
                        fields[i] = fieldList.get(i);
                    }
                    transFromFieldByAsm(targetMa, sourceMa, sourceObject, targetObject, fields);
                }
            } else {
                log.debug("func[ReflectUtil.objectCopyASM] - Object is empty");
            }
        } catch (Exception e) {
            log.error("func[ReflectUtil.objectCopyASM] Exception [{} - {}] stackTrace[{}] ", e.getCause(),
                e.getMessage(), e.getStackTrace());
        }
        return targetObject;
    }

    /**
     * 
     * @MethodName: objectDataAppendAsm
     * @Description: 使用ASM反射对对象数据追加
     * @author yuanzhenhui
     * @param <T>
     * @param sourceObject
     * @param targetObject
     *            void
     * @date 2023-04-17 03:58:14
     */
    public static <T> void objectDataAppendAsm(T sourceObject, T targetObject) {
        transFromFieldByAsm(sourceObject, targetObject, sourceObject.getClass().getDeclaredFields());
    }

    /**
     * 
     * @MethodName: objectDataAppendAsm
     * @Description: 使用ASM反射对对象数据追加
     * @author yuanzhenhui
     * @param <T>
     * @param targetMa
     * @param sourceMa
     * @param sourceObject
     * @param targetObject
     *            void
     * @date 2023-04-17 03:58:27
     */
    public static <T> void objectDataAppendAsm(MethodAccess targetMa, MethodAccess sourceMa, T sourceObject,
        T targetObject) {
        transFromFieldByAsm(targetMa, sourceMa, sourceObject, targetObject,
            sourceObject.getClass().getDeclaredFields());
    }

    /**
     * 
     * @MethodName: transFromFieldByAsm
     * @Description: ASM传输数据
     * @author yuanzhenhui
     * @param <T>
     * @param sourceObject
     * @param targetObject
     * @param fields
     *            void
     * @date 2023-04-17 04:02:19
     */
    public static <T> void transFromFieldByAsm(Object sourceObject, T targetObject, Field[] fields) {
        MethodAccess targetMa = MethodAccess.get(targetObject.getClass());
        MethodAccess sourceMa = MethodAccess.get(sourceObject.getClass());
        transFromFieldByAsm(targetMa, sourceMa, sourceObject, targetObject, fields);
    }

    /**
     * 
     * @MethodName: transFromFieldByAsm
     * @Description: ASM传输数据
     * @author yuanzhenhui
     * @param <T>
     * @param targetMa
     * @param sourceMa
     * @param sourceObject
     * @param targetObject
     * @param fields
     *            void
     * @date 2023-04-17 04:02:32
     */
    public static <T> void transFromFieldByAsm(MethodAccess targetMa, MethodAccess sourceMa, Object sourceObject,
        T targetObject, Field[] fields) {
        String methodName = null;
        String fieldName = null;
        Object getValue = null;

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                // 过滤static或final字段
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                fieldName = field.getName();
                methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                getValue = sourceMa.invoke(sourceObject, GETTER + methodName);
                if (null != getValue) {
                    targetMa.invoke(targetObject, SETTER + methodName, getValue);
                }
            } catch (Exception e) {
                log.error("func[ReflectUtil.transFromFieldByASM] Exception [{} - {}] stackTrace[{}] ", e.getCause(),
                    e.getMessage(), e.getStackTrace());
                continue;
            }
        }
    }

    /**
     * 
     * @MethodName: getAllFields
     * @Description: 通过迭代获取子类与父类的所有字段
     * @author yuanzhenhui
     * @param <T>
     * @param t
     * @return List<Field>
     * @date 2023-04-17 04:03:17
     */
    public static <T> List<Field> getAllFields(T t) {
        Class<?> clazz = t.getClass();
        // 通过迭代的循环获取子类及父类中所有字段
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            // 当父类为null的时候说明到达了最上层的父类(Object类).
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            // 得到父类,然后赋给自己
            clazz = clazz.getSuperclass();
        }

        List<Field> reList = new ArrayList<>();
        if (!fieldList.isEmpty()) {
            for (Field field : fieldList) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                reList.add(field);
            }
        }
        return reList;
    }

    /**
     * 
     * @MethodName: map2Bean
     * @Description: 将map数据转成bean
     * @author yuanzhenhui
     * @param <T>
     * @param map
     * @param targetClazz
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     *             T
     * @date 2023-04-17 04:04:13
     */
    public static <T> T map2Bean(Map<String, Object> map, Class<T> targetClazz)
        throws IllegalAccessException, InstantiationException {
        MethodAccess clazzMa = MethodAccess.get(targetClazz);
        T t = targetClazz.newInstance();
        map.entrySet().stream().forEach(entry -> setValueByAsm(clazzMa, t, entry.getKey(), entry.getValue()));
        return t;
    }

    /**
     * 
     * @MethodName: bean2Map
     * @Description: bean转成map集合
     * @author yuanzhenhui
     * @param bean
     * @param propNames
     * @return Map<String,Object>
     * @date 2023-04-17 04:04:51
     */
    public static Map<String, Object> bean2Map(Object bean, String... propNames) {
        Map<String, Object> rtn = null;
        if (null == propNames) {
            List<Field> fieldList = getAllFields(bean);
            rtn = fieldList.stream()
                .collect(Collectors.toMap(Field::getName, field -> getValueByAsm(bean, field.getName())));
        } else {
            rtn = Arrays.asList(propNames).stream()
                .collect(Collectors.toMap(propName -> propName, propName -> getValueByAsm(bean, propName)));
        }
        return rtn;
    }

    /**
     * 
     * @MethodName: invokePrivateMethod
     * @Description: 执行私有方法
     * @author yuanzhenhui
     * @param object
     * @param methodName
     * @param params
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     *             Object
     * @date 2023-04-17 04:08:39
     */
    public static Object invokePrivateMethod(Object object, String methodName, Object... params)
        throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?>[] types = new Class[params.length];
        IntStream.range(0, params.length).forEach(i -> types[i] = params[i].getClass());

        Class<?> clazz = object.getClass();
        Method method = null;
        for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            method = superClass.getDeclaredMethod(methodName, types);
            break;
        }

        if (method == null) {
            throw new NoSuchMethodException("No Such Method:" + clazz.getSimpleName() + methodName);
        }

        boolean accessible = method.isAccessible();
        method.setAccessible(true);
        Object result = method.invoke(object, params);
        method.setAccessible(accessible);
        return result;
    }

    /**
     * 
     * @MethodName: getConstValue
     * @Description: 获取常量值
     * @author yuanzhenhui
     * @param <T>
     * @param clazz
     * @param constName
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     *             T
     * @date 2023-04-17 04:09:18
     */
    @SuppressWarnings("unchecked")
    public static <T> T getConstValue(Class<?> clazz, String constName)
        throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = clazz.getDeclaredField(constName);
        if (field != null) {
            field.setAccessible(true);
            Object object = field.get(null);
            if (object != null) {
                return (T)object;
            }
            return null;
        }
        return null;
    }

    /**
     * 
     * @MethodName: getPrivateProperty
     * @Description: 获取私有属性Value
     * @author yuanzhenhui
     * @param bean
     * @param name
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     *             Object
     * @date 2023-04-17 04:10:11
     */
    public static Object getPrivateProperty(Object bean, String name)
        throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = bean.getClass().getDeclaredField(name);
        if (field != null) {
            field.setAccessible(true);
            return field.get(bean);
        } else {
            throw new RuntimeException("The field [ " + field + "] in [" + bean.getClass().getName() + "] not exists");
        }
    }

    /**
     * 
     * @MethodName: setPrivateProperty
     * @Description: 设置私有属性Value
     * @author yuanzhenhui
     * @param bean
     * @param name
     * @param value
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     *             void
     * @date 2023-04-17 04:10:27
     */
    public static void setPrivateProperty(Object bean, String name, Object value)
        throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = bean.getClass().getDeclaredField(name);
        if (field != null) {
            field.setAccessible(true);
            field.set(bean, value);
        } else {
            throw new RuntimeException("The field [ " + field + "] in [" + bean.getClass().getName() + "] not exists");
        }
    }

    /**
     * 
     * @MethodName: getFieldType
     * @Description: 获取字段类型
     * @author yuanzhenhui
     * @param clazz
     * @param name
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     *             Class<?>
     * @date 2023-04-17 04:10:39
     */
    public static Class<?> getFieldType(Class<?> clazz, String name) throws NoSuchFieldException, SecurityException {
        Field field = clazz.getDeclaredField(name);
        if (field != null) {
            return field.getType();
        } else {
            throw new RuntimeException("Cannot locate field " + name + " on " + clazz);
        }
    }

    /**
     * 
     * @MethodName: transSqlField2EntityField
     * @Description: sql字段转换为实体字段
     * @author yuanzhenhui
     * @param <T>
     * @param map
     * @param clazz
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     *             T
     * @date 2023-04-17 04:10:51
     */
    public static <T> T transSqlField2EntityField(Map<String, Object> map, Class<T> clazz)
        throws IllegalAccessException, InstantiationException {
        MethodAccess clazzMa = MethodAccess.get(clazz);
        T t = clazz.newInstance();
        map.entrySet().stream().filter(entry -> null != entry.getValue()).forEach(
            entry -> setValueByAsm(clazzMa, t, StringUtil.underline2Camel(entry.getKey(), true), entry.getValue()));
        return t;
    }

    /**
     * 
     * @MethodName: getClass
     * @Description: 通过字符组成类型数组
     * @author yuanzhenhui
     * @param str
     * @param value
     * @param valueList
     * @return Class<?>[]
     * @date 2023-04-17 04:11:11
     */
    public static Class<?>[] getClass(String str, String value, List<Object> valueList) {
        if (StringUtil.isNotBlank(str)) {
            String[] s = str.split(",");
            if (null != s) {
                List<Class<?>> list = new ArrayList<>();
                String[] valueS = (StringUtil.isNotBlank(value)) ? value.split(";") : null;
                Class<?>[] c = new Class[s.length];
                IntStream.range(0, s.length).forEach(i -> {
                    String str1 = s[i];
                    String v = (null != valueS && valueS.length > i) ? valueS[i] : null;
                    if (StringUtil.isNotBlank(str1)) {
                        try {
                            for (Class<?> clazz : Arrays.asList(baseClazzArr)) {
                                if (clazz.getSimpleName().equals(str1)) {
                                    c[i] = clazz;
                                    list.add(clazz);
                                    if (null != v) {
                                        if (clazz.equals(Date.class)) {
                                            valueList.add(DateUtil.string2Date(v, DateUtil.yyyy_MM_dd_EN));
                                        } else if (clazz.getSimpleName().toLowerCase()
                                            .equals(boolean.class.getSimpleName())) {
                                            valueList.add((v.equals("true")) ? true : false);
                                        } else {
                                            valueList.add(stringCast2AnyType(clazz, v));
                                        }
                                    }
                                } else {
                                    Class<?> clazzObj = Class.forName(str1);
                                    c[i] = clazzObj;
                                    list.add(clazzObj);
                                    if (null != v) {
                                        valueList.add(clazzObj.newInstance());
                                    }
                                }
                            }
                        } catch (Exception e) {
                            log.error("func[ReflectUtil.getClass] Exception [{} - {}] stackTrace[{}] ", e.getCause(),
                                e.getMessage(), e.getStackTrace());
                        }
                    }
                });
                return c;
            }
        }
        return null;
    }

    /**
     * 
     * @MethodName: stringCast2AnyType
     * @Description: 将字符串转换为任意类型
     * @author yuanzhenhui
     * @param <T>
     * @param cls
     * @param value
     * @return
     * @throws Exception
     *             T
     * @date 2023-04-17 04:20:02
     */
    public static <T> T stringCast2AnyType(Class<T> cls, String value) throws Exception {
        return (T)cls.getConstructor(String.class).newInstance(value);
    }

}
