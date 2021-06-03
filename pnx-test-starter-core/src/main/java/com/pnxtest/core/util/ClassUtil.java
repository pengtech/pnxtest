/*
 *  Copyright (c) 2020-2021
 *  This file is part of PnxTest framework.
 *
 *  PnxTest is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero Public License version 3 as
 *  published by the Free Software Foundation
 *
 *  PnxTest is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero Public License for more details.
 *
 *  You should have received a copy of the GNU Affero Public License
 *  along with PnxTest.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  For more information, please contact the author at this address:
 *  chen.baker@gmail.com
 *
 */

package com.pnxtest.core.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ClassUtil {
    private static final String CLASS_SUFFIX = ".class";
    private static final String PROTOCOL_JAR = "jar";

    private ClassUtil(){}


    public static Object convertValueByType( Class<?> clazz, String value ) {
        if(StringUtil.isBlank(value) || StringUtil.isEmpty(value)) return null;

        try {

            if (Boolean.class == clazz || Boolean.TYPE == clazz) return Boolean.parseBoolean(value);
            if (Byte.class == clazz || Byte.TYPE == clazz) return Byte.parseByte(value);
            if (Short.class == clazz || Short.TYPE == clazz) return Short.parseShort(value);
            if (Integer.class == clazz || Integer.TYPE == clazz) return Integer.parseInt(value);
            if (Long.class == clazz || Long.TYPE == clazz) return Long.parseLong(value);
            if (Float.class == clazz || Float.TYPE == clazz) return Float.parseFloat(value);
            if (Double.class == clazz || Double.TYPE == clazz) return Double.parseDouble(value);
            if (Character.class == clazz || Character.TYPE == clazz) return new Character(value.charAt(0));
        }catch (NumberFormatException e){
            return value;
        }
        return value;
    }

    public static String primitive2Wrapper(Class<?> type){
        String ret = type.getName();
        switch (type.getName()){
            case "byte":
                ret = Byte.class.getName();
                break;
            case "int":
                ret = Integer.class.getName();
                break;
            case "short":
                ret = Short.class.getName();
                break;
            case "char":
                ret = Character.class.getName();
                break;
            case "long":
                ret = Long.class.getName();
                break;
            case "float":
                ret = Float.class.getName();
                break;
            case "double":
                ret = Double.class.getName();
                break;
            case "boolean":
                ret = Boolean.class.getName();
                break;
            default:
                break;
        }

        return ret;
    }

    public static Set<Method> getAllMethodsWithAnnotation(String packageName, Class<? extends Annotation> annotation) {
        Set<Method> methods = new HashSet<Method>();
        try {
            for (Class<?> cls : getClasses(packageName)) {
                methods.addAll(getAllMethodsWithAnnotation(cls, annotation));
            }
        } catch (SecurityException e) {
            //ignore
        } catch (IOException e) {
            //ignore
        }

        return methods;
    }

    public static Set<Method> getAllMethodsWithAnnotation(Class<?> cls, Class<? extends Annotation> annotation) {
        Set<Method> methods = new HashSet<Method>();
        try {
            for (Method method : cls.getDeclaredMethods()) {
                if (hasAnnotation(method, annotation)) {
                    methods.add(method);
                }
            }
            for (Method method : cls.getMethods()) {
                if (hasAnnotation(method, annotation)) {
                    methods.add(method);
                }
            }
        } catch (SecurityException e) {
            System.err.println("ClassUtil.getAllMethodsWithAnnotation: " + e.getMessage());
        }
        return methods;
    }

    public static Set<Method> getAllMethodsWithAnnotation(Class<?> cls, Class<? extends Annotation>[] annotations) {
        Set<Method> methods = new HashSet<Method>();
        for(Class<? extends Annotation> ma:annotations){
            try {
                methods.addAll(getAllMethodsWithAnnotation(cls, ma));
            }catch (Exception e){
                //
            }
        }
        return methods;
    }

    public static boolean hasAnnotation(Method method, Class<? extends Annotation> annotation) {
        if (method.isAnnotationPresent(annotation))
            return true;
        Class<?>[] interfaces = method.getDeclaringClass().getInterfaces();
        for (Class<?> iface : interfaces) {
            try {
                if (iface.getMethod(method.getName(), method.getParameterTypes()).isAnnotationPresent(annotation))
                    return true;
            } catch (NoSuchMethodException e) {
                // Ignore
            } catch (SecurityException e) {
                // Ignore
            }

        }
        return false;
    }

    public static <T extends Annotation> T getAnnotation(Method method, Class<T> annotation) {
        if (method.isAnnotationPresent(annotation))
            return method.getAnnotation(annotation);
        Class<?>[] interfaces = method.getDeclaringClass().getInterfaces();
        for (Class<?> iface : interfaces) {
            try {
                Method iMethod = iface.getMethod(method.getName(), method.getParameterTypes());
                if (iMethod.isAnnotationPresent(annotation))
                    return iMethod.getAnnotation(annotation);
            } catch (NoSuchMethodException e) {
                // Ignore...
            } catch (SecurityException e) {
                // Ignore...
            }

        }
        return null;
    }

    public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotation) {
        if (clazz.isAnnotationPresent(annotation))
            return clazz.getAnnotation(annotation);
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> iface : interfaces) {
            try {
                if (iface.isAnnotationPresent(annotation))
                    return iface.getAnnotation(annotation);
            } catch (SecurityException e) {
                //Ignore...
            }

        }
        return null;
    }

    public static List<Class<?>> getClasses(String pkg) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = pkg.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<Class<?>> classes = new ArrayList<Class<?>>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equalsIgnoreCase(PROTOCOL_JAR)) {
                try {
                    classes.addAll(getClassesFromJar(resource, pkg));
                } catch (IOException e) {
                    //System.err.println("Unable to get classes from jar: " + resource);
                }
            } else {
                try {
                    classes.addAll(getClasses(new File(resource.toURI()), pkg));
                } catch (URISyntaxException e) {
                    // ignore it
                }
            }
        }
        return classes;
    }

    private static List<Class<?>> getClassesFromJar(URL jar, String pkg) throws IOException {
        List<Class<?>> classes = new ArrayList<Class<?>>();

        String jarFileName = URLDecoder.decode(jar.getFile(), "UTF-8");
        jarFileName = jarFileName.substring(5, jarFileName.indexOf("!"));
        JarFile jf = new JarFile(jarFileName);
        Enumeration<JarEntry> jarEntries = jf.entries();

        while (jarEntries.hasMoreElements()) {
            String entryName = jarEntries.nextElement().getName().replace("/", ".");
            if (entryName.startsWith(pkg) && entryName.endsWith(CLASS_SUFFIX)) {
                entryName = entryName.substring(0, entryName.lastIndexOf('.'));
                try {
                    classes.add(Class.forName(entryName));
                } catch (Throwable e) {
                    System.err.println("Unable to get class " + entryName + " from jar " + jarFileName);
                }
            }
        }
        jf.close();

        return classes;
    }


    private static List<Class<?>> getClasses(File directory, String packageName) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(getClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(CLASS_SUFFIX)) {
                String clsName = packageName + '.' + file.getName().substring(0, file.getName().lastIndexOf("."));
                try {
                    classes.add(Class.forName(clsName));
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    // ignore it
                }
            }
        }
        return classes;
    }

    public static Method getMethod(Class<?> clazz, String name) throws NoSuchMethodException {
        Method[] methods = clazz.getMethods();
        for (Method m : methods) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        if (null != clazz.getSuperclass()) {
            getMethod(clazz.getSuperclass(), name);
        }
        throw new NoSuchMethodException();
    }

    public static Field[] getAllFields(Class<?> clazz, Class<?> uptoParent) {
        Collection<Field> fields = new HashSet<Field>(Arrays.asList(clazz.getDeclaredFields()));
        if ((clazz.getSuperclass() != null) && !clazz.getSuperclass().equals(uptoParent)) {
            fields.addAll(Arrays.asList(getAllFields(clazz.getSuperclass(), uptoParent)));
        }
        return fields.toArray(new Field[] {});
    }

    public static void extractInterfaces(Set<Class<?>> iSet, Class<?> clazz) {
        if (Object.class.equals(clazz)) {
            return;
        }
        Class<?>[] classes = clazz.getInterfaces();
        iSet.addAll(Arrays.asList(classes));
        extractInterfaces(iSet, clazz.getSuperclass());
    }



    public static Class<?> getRawType(Type type) {
        if (type instanceof Class<?>) {
            // type is a normal class.
            return (Class<?>) type;

        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            return (Class<?>) rawType;
        } else if (type instanceof GenericArrayType) {
            final GenericArrayType genericArrayType = (GenericArrayType) type;
            final Class<?> componentRawType = getRawType(genericArrayType.getGenericComponentType());
            return Array.newInstance(componentRawType, 0).getClass();
        } else if (type instanceof TypeVariable) {
            final TypeVariable typeVar = (TypeVariable) type;
            if ((typeVar.getBounds() != null) && (typeVar.getBounds().length > 0)) {
                return getRawType(typeVar.getBounds()[0]);
            }
        }
        throw new RuntimeException("Unable to determine base class from Type");
    }




    public static Field getField(String fieldName, Class<?> clazz) throws NoSuchFieldException {
        try {
            return clazz.getField(fieldName);
        } catch (NoSuchFieldException e) {
            Field[] fields = getAllFields(clazz, Object.class);
            for (Field f : fields) {
                if (f.getName().equalsIgnoreCase(fieldName)) {
                    return f;
                }
            }
            throw e;
        }
    }

    public static Object getField(String fieldName, Object classObj) {
        try {
            Field field = getField(fieldName, classObj.getClass());
            field.setAccessible(true);
            return field.get(classObj);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final Set<Class<?>> WRAPPER_TYPES = new HashSet<Class<?>>(
            Arrays.asList(Boolean.class, Character.class, Byte.class, Short.class, Integer.class,
                    Long.class, Float.class, Double.class, Void.class, String.class));

    public static boolean isWrapperType(Class<?> clazz) {
        return WRAPPER_TYPES.contains(clazz);
    }

    public static boolean isPrimitiveOrWrapperType(Class<?> clazz) {
        return clazz.isPrimitive() || isWrapperType(clazz);
    }

}
