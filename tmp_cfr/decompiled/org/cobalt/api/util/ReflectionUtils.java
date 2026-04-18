/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\u001b\n\u0002\b\u0005\n\u0002\u0010$\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J%\u0010\b\u001a\u00028\u0000\"\u0004\b\u0000\u0010\u00042\u0006\u0010\u0005\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u0006H\u0007\u00a2\u0006\u0004\b\b\u0010\tJ)\u0010\f\u001a\u00020\u000b2\u0006\u0010\u0005\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u00062\b\u0010\n\u001a\u0004\u0018\u00010\u0001H\u0007\u00a2\u0006\u0004\b\f\u0010\rJ#\u0010\b\u001a\u00020\u00102\n\u0010\u000f\u001a\u0006\u0012\u0002\b\u00030\u000e2\u0006\u0010\u0007\u001a\u00020\u0006H\u0003\u00a2\u0006\u0004\b\b\u0010\u0011JO\u0010\u0016\u001a\u00028\u0000\"\u0004\b\u0000\u0010\u00042\u0006\u0010\u0005\u001a\u00020\u00012\u0006\u0010\u0012\u001a\u00020\u00062\u0010\u0010\u0014\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u000e0\u00132\u0016\u0010\u0015\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010\u00010\u0013\"\u0004\u0018\u00010\u0001H\u0007\u00a2\u0006\u0004\b\u0016\u0010\u0017J5\u0010\u0019\u001a\u00020\u00182\n\u0010\u000f\u001a\u0006\u0012\u0002\b\u00030\u000e2\u0006\u0010\u0012\u001a\u00020\u00062\u0010\u0010\u0014\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u000e0\u0013H\u0003\u00a2\u0006\u0004\b\u0019\u0010\u001aJ)\u0010\u001b\u001a\u00028\u0000\"\u0004\b\u0000\u0010\u00042\n\u0010\u000f\u001a\u0006\u0012\u0002\b\u00030\u000e2\u0006\u0010\u0007\u001a\u00020\u0006H\u0007\u00a2\u0006\u0004\b\u001b\u0010\u001cJ-\u0010\u001d\u001a\u00020\u000b2\n\u0010\u000f\u001a\u0006\u0012\u0002\b\u00030\u000e2\u0006\u0010\u0007\u001a\u00020\u00062\b\u0010\n\u001a\u0004\u0018\u00010\u0001H\u0007\u00a2\u0006\u0004\b\u001d\u0010\u001eJM\u0010\u001f\u001a\u00028\u0000\"\u0004\b\u0000\u0010\u00042\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00028\u00000\u000e2\u0010\u0010\u0014\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u000e0\u00132\u0016\u0010\u0015\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010\u00010\u0013\"\u0004\u0018\u00010\u0001H\u0007\u00a2\u0006\u0004\b\u001f\u0010 J!\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00100!2\n\u0010\u000f\u001a\u0006\u0012\u0002\b\u00030\u000eH\u0007\u00a2\u0006\u0004\b\"\u0010#J!\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00180!2\n\u0010\u000f\u001a\u0006\u0012\u0002\b\u00030\u000eH\u0007\u00a2\u0006\u0004\b$\u0010#J9\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00100!\"\b\b\u0000\u0010&*\u00020%2\n\u0010\u000f\u001a\u0006\u0012\u0002\b\u00030\u000e2\f\u0010'\u001a\b\u0012\u0004\u0012\u00028\u00000\u000eH\u0007\u00a2\u0006\u0004\b(\u0010)J9\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00180!\"\b\b\u0000\u0010&*\u00020%2\n\u0010\u000f\u001a\u0006\u0012\u0002\b\u00030\u000e2\f\u0010'\u001a\b\u0012\u0004\u0012\u00028\u00000\u000eH\u0007\u00a2\u0006\u0004\b*\u0010)J-\u0010-\u001a\u00020\u000b2\u0006\u0010\u0005\u001a\u00020\u00012\u0014\u0010,\u001a\u0010\u0012\u0004\u0012\u00020\u0006\u0012\u0006\u0012\u0004\u0018\u00010\u00010+H\u0007\u00a2\u0006\u0004\b-\u0010.J3\u00100\u001a\u0010\u0012\u0004\u0012\u00020\u0006\u0012\u0006\u0012\u0004\u0018\u00010\u00010+2\u0006\u0010\u0005\u001a\u00020\u00012\f\u0010/\u001a\b\u0012\u0004\u0012\u00020\u00060!H\u0007\u00a2\u0006\u0004\b0\u00101J(\u00102\u001a\u00028\u0000\"\u0006\b\u0000\u0010\u0004\u0018\u00012\u0006\u0010\u0005\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u0006H\u0087\b\u00a2\u0006\u0004\b2\u0010\tJ,\u00103\u001a\u00028\u0000\"\u0006\b\u0000\u0010\u0004\u0018\u00012\n\u0010\u000f\u001a\u0006\u0012\u0002\b\u00030\u000e2\u0006\u0010\u0007\u001a\u00020\u0006H\u0087\b\u00a2\u0006\u0004\b3\u0010\u001cJR\u00104\u001a\u00028\u0000\"\u0006\b\u0000\u0010\u0004\u0018\u00012\u0006\u0010\u0005\u001a\u00020\u00012\u0006\u0010\u0012\u001a\u00020\u00062\u0010\u0010\u0014\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u000e0\u00132\u0016\u0010\u0015\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010\u00010\u0013\"\u0004\u0018\u00010\u0001H\u0087\b\u00a2\u0006\u0004\b4\u0010\u0017J\u001d\u00106\u001a\u00028\u0000\"\b\b\u0000\u0010\u0004*\u000205*\u00028\u0000H\u0007\u00a2\u0006\u0004\b6\u00107\u00a8\u00068"}, d2={"Lorg/cobalt/api/util/ReflectionUtils;", "", "<init>", "()V", "T", "instance", "", "fieldName", "getField", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", "value", "", "setField", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)V", "Ljava/lang/Class;", "clazz", "Ljava/lang/reflect/Field;", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/reflect/Field;", "methodName", "", "paramTypes", "args", "invokeMethod", "(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Class;[Ljava/lang/Object;)Ljava/lang/Object;", "Ljava/lang/reflect/Method;", "getMethod", "(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", "getStaticField", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Object;", "setStaticField", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Object;)V", "createInstance", "(Ljava/lang/Class;[Ljava/lang/Class;[Ljava/lang/Object;)Ljava/lang/Object;", "", "getAllFields", "(Ljava/lang/Class;)Ljava/util/List;", "getAllMethods", "", "A", "annotation", "getFieldsWithAnnotation", "(Ljava/lang/Class;Ljava/lang/Class;)Ljava/util/List;", "getMethodsWithAnnotation", "", "fieldValues", "setFields", "(Ljava/lang/Object;Ljava/util/Map;)V", "fieldNames", "getFields", "(Ljava/lang/Object;Ljava/util/List;)Ljava/util/Map;", "getFieldTypeSafe", "getStaticFieldTypeSafe", "invokeMethodTypeSafe", "Ljava/lang/reflect/AccessibleObject;", "makeAccessible", "(Ljava/lang/reflect/AccessibleObject;)Ljava/lang/reflect/AccessibleObject;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nReflectionUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ReflectionUtils.kt\norg/cobalt/api/util/ReflectionUtils\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,161:1\n777#2:162\n873#2,2:163\n777#2:165\n873#2,2:166\n1300#2,2:168\n1315#2,4:170\n*S KotlinDebug\n*F\n+ 1 ReflectionUtils.kt\norg/cobalt/api/util/ReflectionUtils\n*L\n117#1:162\n117#1:163,2\n122#1:165\n122#1:166,2\n134#1:168,2\n134#1:170,4\n*E\n"})
public final class ReflectionUtils {
    @NotNull
    public static final ReflectionUtils INSTANCE = new ReflectionUtils();

    private ReflectionUtils() {
    }

    @JvmStatic
    public static final <T> T getField(@NotNull Object instance, @NotNull String fieldName) {
        Intrinsics.checkNotNullParameter((Object)instance, (String)"instance");
        Intrinsics.checkNotNullParameter((Object)fieldName, (String)"fieldName");
        Field field = ReflectionUtils.getField(instance.getClass(), fieldName);
        ReflectionUtils.makeAccessible((AccessibleObject)field);
        return (T)field.get(instance);
    }

    @JvmStatic
    public static final void setField(@NotNull Object instance, @NotNull String fieldName, @Nullable Object value) {
        Intrinsics.checkNotNullParameter((Object)instance, (String)"instance");
        Intrinsics.checkNotNullParameter((Object)fieldName, (String)"fieldName");
        Field field = ReflectionUtils.getField(instance.getClass(), fieldName);
        ReflectionUtils.makeAccessible((AccessibleObject)field);
        field.set(instance, value);
    }

    @JvmStatic
    private static final Field getField(Class<?> clazz, String fieldName) {
        for (Class<?> current = clazz; current != null; current = current.getSuperclass()) {
            try {
                Field field = current.getDeclaredField(fieldName);
                Intrinsics.checkNotNullExpressionValue((Object)field, (String)"getDeclaredField(...)");
                return field;
            }
            catch (NoSuchFieldException noSuchFieldException) {
                continue;
            }
        }
        throw new RuntimeException("Field " + fieldName + " not found in class " + clazz);
    }

    @JvmStatic
    public static final <T> T invokeMethod(@NotNull Object instance, @NotNull String methodName, @NotNull Class<?>[] paramTypes, Object ... args) {
        Intrinsics.checkNotNullParameter((Object)instance, (String)"instance");
        Intrinsics.checkNotNullParameter((Object)methodName, (String)"methodName");
        Intrinsics.checkNotNullParameter(paramTypes, (String)"paramTypes");
        Intrinsics.checkNotNullParameter((Object)args, (String)"args");
        Method method = ReflectionUtils.getMethod(instance.getClass(), methodName, paramTypes);
        ReflectionUtils.makeAccessible((AccessibleObject)method);
        return (T)method.invoke(instance, Arrays.copyOf(args, args.length));
    }

    @JvmStatic
    private static final Method getMethod(Class<?> clazz, String methodName, Class<?>[] paramTypes) {
        for (Class<?> current = clazz; current != null; current = current.getSuperclass()) {
            try {
                Method method = current.getDeclaredMethod(methodName, Arrays.copyOf(paramTypes, paramTypes.length));
                Intrinsics.checkNotNullExpressionValue((Object)method, (String)"getDeclaredMethod(...)");
                return method;
            }
            catch (NoSuchMethodException noSuchMethodException) {
                continue;
            }
        }
        throw new RuntimeException("Method " + methodName + " not found in class " + clazz);
    }

    @JvmStatic
    public static final <T> T getStaticField(@NotNull Class<?> clazz, @NotNull String fieldName) {
        Intrinsics.checkNotNullParameter(clazz, (String)"clazz");
        Intrinsics.checkNotNullParameter((Object)fieldName, (String)"fieldName");
        Field field = ReflectionUtils.getField(clazz, fieldName);
        ReflectionUtils.makeAccessible((AccessibleObject)field);
        return (T)field.get(null);
    }

    @JvmStatic
    public static final void setStaticField(@NotNull Class<?> clazz, @NotNull String fieldName, @Nullable Object value) {
        Intrinsics.checkNotNullParameter(clazz, (String)"clazz");
        Intrinsics.checkNotNullParameter((Object)fieldName, (String)"fieldName");
        Field field = ReflectionUtils.getField(clazz, fieldName);
        ReflectionUtils.makeAccessible((AccessibleObject)field);
        field.set(null, value);
    }

    @JvmStatic
    public static final <T> T createInstance(@NotNull Class<T> clazz, @NotNull Class<?>[] paramTypes, Object ... args) {
        Intrinsics.checkNotNullParameter(clazz, (String)"clazz");
        Intrinsics.checkNotNullParameter(paramTypes, (String)"paramTypes");
        Intrinsics.checkNotNullParameter((Object)args, (String)"args");
        Constructor<T> constructor = clazz.getDeclaredConstructor(Arrays.copyOf(paramTypes, paramTypes.length));
        Intrinsics.checkNotNullExpressionValue(constructor, (String)"getDeclaredConstructor(...)");
        Constructor<T> constructor2 = constructor;
        ReflectionUtils.makeAccessible((AccessibleObject)constructor2);
        return constructor2.newInstance(Arrays.copyOf(args, args.length));
    }

    @JvmStatic
    @NotNull
    public static final List<Field> getAllFields(@NotNull Class<?> clazz) {
        Intrinsics.checkNotNullParameter(clazz, (String)"clazz");
        List fields = new ArrayList();
        for (Class<?> current = clazz; current != null; current = current.getSuperclass()) {
            Collection collection = fields;
            Field[] fieldArray = current.getDeclaredFields();
            Intrinsics.checkNotNullExpressionValue((Object)fieldArray, (String)"getDeclaredFields(...)");
            CollectionsKt.addAll((Collection)collection, (Object[])fieldArray);
        }
        return fields;
    }

    @JvmStatic
    @NotNull
    public static final List<Method> getAllMethods(@NotNull Class<?> clazz) {
        Intrinsics.checkNotNullParameter(clazz, (String)"clazz");
        List methods = new ArrayList();
        for (Class<?> current = clazz; current != null; current = current.getSuperclass()) {
            Collection collection = methods;
            Method[] methodArray = current.getDeclaredMethods();
            Intrinsics.checkNotNullExpressionValue((Object)methodArray, (String)"getDeclaredMethods(...)");
            CollectionsKt.addAll((Collection)collection, (Object[])methodArray);
        }
        return methods;
    }

    /*
     * WARNING - void declaration
     */
    @JvmStatic
    @NotNull
    public static final <A extends Annotation> List<Field> getFieldsWithAnnotation(@NotNull Class<?> clazz, @NotNull Class<A> annotation) {
        void $this$filterTo$iv$iv;
        Intrinsics.checkNotNullParameter(clazz, (String)"clazz");
        Intrinsics.checkNotNullParameter(annotation, (String)"annotation");
        Iterable $this$filter$iv = ReflectionUtils.getAllFields(clazz);
        boolean $i$f$filter = false;
        Iterable iterable = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            Field it = (Field)element$iv$iv;
            boolean bl = false;
            if (!it.isAnnotationPresent(annotation)) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        return (List)destination$iv$iv;
    }

    /*
     * WARNING - void declaration
     */
    @JvmStatic
    @NotNull
    public static final <A extends Annotation> List<Method> getMethodsWithAnnotation(@NotNull Class<?> clazz, @NotNull Class<A> annotation) {
        void $this$filterTo$iv$iv;
        Intrinsics.checkNotNullParameter(clazz, (String)"clazz");
        Intrinsics.checkNotNullParameter(annotation, (String)"annotation");
        Iterable $this$filter$iv = ReflectionUtils.getAllMethods(clazz);
        boolean $i$f$filter = false;
        Iterable iterable = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            Method it = (Method)element$iv$iv;
            boolean bl = false;
            if (!it.isAnnotationPresent(annotation)) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        return (List)destination$iv$iv;
    }

    @JvmStatic
    public static final void setFields(@NotNull Object instance, @NotNull Map<String, ? extends Object> fieldValues) {
        Intrinsics.checkNotNullParameter((Object)instance, (String)"instance");
        Intrinsics.checkNotNullParameter(fieldValues, (String)"fieldValues");
        for (Map.Entry<String, ? extends Object> entry : fieldValues.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();
            ReflectionUtils.setField(instance, name, value);
        }
    }

    /*
     * WARNING - void declaration
     */
    @JvmStatic
    @NotNull
    public static final Map<String, Object> getFields(@NotNull Object instance, @NotNull List<String> fieldNames) {
        void $this$associateWithTo$iv$iv;
        Intrinsics.checkNotNullParameter((Object)instance, (String)"instance");
        Intrinsics.checkNotNullParameter(fieldNames, (String)"fieldNames");
        Iterable $this$associateWith$iv = fieldNames;
        boolean $i$f$associateWith = false;
        LinkedHashMap result$iv = new LinkedHashMap(RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateWith$iv, (int)10)), (int)16));
        Iterable iterable = $this$associateWith$iv;
        Map destination$iv$iv = result$iv;
        boolean $i$f$associateWithTo = false;
        for (Object element$iv$iv : $this$associateWithTo$iv$iv) {
            void it;
            String string = (String)element$iv$iv;
            Object t = element$iv$iv;
            Map map = destination$iv$iv;
            boolean bl = false;
            Object t2 = ReflectionUtils.getField(instance, (String)it);
            map.put(t, t2);
        }
        return destination$iv$iv;
    }

    @JvmStatic
    public static final /* synthetic */ <T> T getFieldTypeSafe(Object instance, String fieldName) {
        Intrinsics.checkNotNullParameter((Object)instance, (String)"instance");
        Intrinsics.checkNotNullParameter((Object)fieldName, (String)"fieldName");
        boolean $i$f$getFieldTypeSafe = false;
        return ReflectionUtils.getField(instance, fieldName);
    }

    @JvmStatic
    public static final /* synthetic */ <T> T getStaticFieldTypeSafe(Class<?> clazz, String fieldName) {
        Intrinsics.checkNotNullParameter(clazz, (String)"clazz");
        Intrinsics.checkNotNullParameter((Object)fieldName, (String)"fieldName");
        boolean $i$f$getStaticFieldTypeSafe = false;
        return ReflectionUtils.getStaticField(clazz, fieldName);
    }

    @JvmStatic
    public static final /* synthetic */ <T> T invokeMethodTypeSafe(Object instance, String methodName, Class<?>[] paramTypes, Object ... args) {
        Intrinsics.checkNotNullParameter((Object)instance, (String)"instance");
        Intrinsics.checkNotNullParameter((Object)methodName, (String)"methodName");
        Intrinsics.checkNotNullParameter(paramTypes, (String)"paramTypes");
        Intrinsics.checkNotNullParameter((Object)args, (String)"args");
        boolean $i$f$invokeMethodTypeSafe = false;
        return ReflectionUtils.invokeMethod(instance, methodName, paramTypes, Arrays.copyOf(args, args.length));
    }

    @JvmStatic
    @NotNull
    public static final <T extends AccessibleObject> T makeAccessible(@NotNull T $this$makeAccessible) {
        Intrinsics.checkNotNullParameter($this$makeAccessible, (String)"<this>");
        $this$makeAccessible.setAccessible(true);
        return $this$makeAccessible;
    }
}

