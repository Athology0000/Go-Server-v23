/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.event;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.cobalt.api.event.Event;
import org.cobalt.api.event.EventBus;
import org.cobalt.api.event.annotation.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010!\n\u0000\n\u0002\b\u0005*\u0001(\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001+B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0004\u001a\u00020\u0001H\u0007\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0017\u0010\b\u001a\u00020\u00052\u0006\u0010\u0004\u001a\u00020\u0001H\u0007\u00a2\u0006\u0004\b\b\u0010\u0007J\u0017\u0010\u000b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\tH\u0007\u00a2\u0006\u0004\b\u000b\u0010\fJ%\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\t0\u00102\u0006\u0010\r\u001a\u00020\u00012\u0006\u0010\u000f\u001a\u00020\u000eH\u0002\u00a2\u0006\u0004\b\u0011\u0010\u0012J;\u0010\u0018\u001a\u00020\u0005\"\b\b\u0000\u0010\u0013*\u00020\t2\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00028\u00000\u00142\u0012\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00020\u00050\u0016H\u0007\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u0017\u0010\u001a\u001a\u00020\u00052\u0006\u0010\n\u001a\u00020\tH\u0003\u00a2\u0006\u0004\b\u001a\u0010\u001bR*\u0010\u001f\u001a\u0018\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001e0\u001d0\u001c8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001f\u0010 RT\u0010$\u001aB\u0012\f\u0012\n \"*\u0004\u0018\u00010\u00010\u0001\u0012\f\u0012\n \"*\u0004\u0018\u00010#0# \"* \u0012\f\u0012\n \"*\u0004\u0018\u00010\u00010\u0001\u0012\f\u0012\n \"*\u0004\u0018\u00010#0#\u0018\u00010!0!8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b$\u0010%R:\u0010'\u001a(\u0012\f\u0012\n\u0012\u0006\b\u0001\u0012\u00020\t0\u0014\u0012\u0016\u0012\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00050\u00160&0\u001c8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b'\u0010 R\u0014\u0010)\u001a\u00020(8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b)\u0010*\u00a8\u0006,"}, d2={"Lorg/cobalt/api/event/EventBus;", "", "<init>", "()V", "obj", "", "register", "(Ljava/lang/Object;)V", "unregister", "Lorg/cobalt/api/event/Event;", "event", "post", "(Lorg/cobalt/api/event/Event;)Lorg/cobalt/api/event/Event;", "instance", "Ljava/lang/reflect/Method;", "method", "Ljava/util/function/Consumer;", "createInvoker", "(Ljava/lang/Object;Ljava/lang/reflect/Method;)Ljava/util/function/Consumer;", "T", "Ljava/lang/Class;", "eventClass", "Lkotlin/Function1;", "listener", "registerEvent", "(Ljava/lang/Class;Lkotlin/jvm/functions/Function1;)V", "handleDynamic", "(Lorg/cobalt/api/event/Event;)V", "Ljava/util/concurrent/ConcurrentHashMap;", "", "Lorg/cobalt/api/event/EventBus$ListenerData;", "listeners", "Ljava/util/concurrent/ConcurrentHashMap;", "Ljava/util/concurrent/ConcurrentHashMap$KeySetView;", "kotlin.jvm.PlatformType", "", "registered", "Ljava/util/concurrent/ConcurrentHashMap$KeySetView;", "", "dynamicRunnable", "org/cobalt/api/event/EventBus$classCache$1", "classCache", "Lorg/cobalt/api/event/EventBus$classCache$1;", "ListenerData", "cobalt"})
@SourceDebugExtension(value={"SMAP\nEventBus.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EventBus.kt\norg/cobalt/api/event/EventBus\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 5 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,138:1\n14060#2,2:139\n1915#3,2:141\n1915#3:143\n1915#3,2:144\n1916#3:146\n1915#3,2:155\n540#4:147\n525#4,6:148\n221#5:154\n222#5:157\n*S KotlinDebug\n*F\n+ 1 EventBus.kt\norg/cobalt/api/event/EventBus\n*L\n22#1:139,2\n50#1:141,2\n80#1:143\n81#1:144,2\n80#1:146\n133#1:155,2\n131#1:147\n131#1:148,6\n132#1:154\n132#1:157\n*E\n"})
public final class EventBus {
    @NotNull
    public static final EventBus INSTANCE = new EventBus();
    @NotNull
    private static final ConcurrentHashMap<Class<?>, List<ListenerData>> listeners = new ConcurrentHashMap();
    private static final ConcurrentHashMap.KeySetView<Object, Boolean> registered = ConcurrentHashMap.newKeySet();
    @NotNull
    private static final ConcurrentHashMap<Class<? extends Event>, List<Function1<Event, Unit>>> dynamicRunnable = new ConcurrentHashMap();
    @NotNull
    private static final classCache.1 classCache = new ClassValue<List<? extends Class<?>>>(){

        protected List<Class<?>> computeValue(Class<?> type) {
            Intrinsics.checkNotNullParameter(type, (String)"type");
            Set classes = new LinkedHashSet<E>();
            for (Class<?> c = type; c != null; c = c.getSuperclass()) {
                classes.add(c);
                Class<?>[] classArray = c.getInterfaces();
                Intrinsics.checkNotNullExpressionValue(classArray, (String)"getInterfaces(...)");
                Object[] $this$forEach$iv = classArray;
                boolean $i$f$forEach = false;
                for (Object element$iv : $this$forEach$iv) {
                    Class it = (Class)element$iv;
                    boolean bl = false;
                    Intrinsics.checkNotNull((Object)it);
                    classes.add(it);
                }
            }
            return CollectionsKt.toList((Iterable)classes);
        }
    };

    private EventBus() {
    }

    @JvmStatic
    public static final void register(@NotNull Object obj) {
        Intrinsics.checkNotNullParameter((Object)obj, (String)"obj");
        if (!registered.add(obj)) {
            return;
        }
        Method[] methodArray = obj.getClass().getDeclaredMethods();
        Intrinsics.checkNotNullExpressionValue((Object)methodArray, (String)"getDeclaredMethods(...)");
        Object[] $this$forEach$iv = methodArray;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            Method method = (Method)element$iv;
            boolean bl = false;
            if (!method.isAnnotationPresent(SubscribeEvent.class)) continue;
            Class<?>[] params = method.getParameterTypes();
            if (!(params.length == 1 && Event.class.isAssignableFrom(params[0]))) {
                boolean $i$a$-require-EventBus$register$1$22 = false;
                String $i$a$-require-EventBus$register$1$22 = "Invalid Method";
                throw new IllegalArgumentException($i$a$-require-EventBus$register$1$22.toString());
            }
            method.setAccessible(true);
            int priority = method.getAnnotation(SubscribeEvent.class).priority();
            Class<?> eventType = params[0];
            Intrinsics.checkNotNull((Object)method);
            Consumer<Event> consumer = INSTANCE.createInvoker(obj, method);
            listeners.compute(eventType, (arg_0, arg_1) -> EventBus.register$lambda$0$2((arg_0, arg_1) -> EventBus.register$lambda$0$1(obj, consumer, priority, arg_0, arg_1), arg_0, arg_1));
        }
    }

    @JvmStatic
    public static final void unregister(@NotNull Object obj) {
        Intrinsics.checkNotNullParameter((Object)obj, (String)"obj");
        if (!registered.remove(obj)) {
            return;
        }
        Set set = listeners.keySet();
        Intrinsics.checkNotNullExpressionValue((Object)set, (String)"<get-keys>(...)");
        Iterable $this$forEach$iv = set;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            Class key = (Class)element$iv;
            boolean bl = false;
            listeners.compute(key, (arg_0, arg_1) -> EventBus.unregister$lambda$0$1((arg_0, arg_1) -> EventBus.unregister$lambda$0$0(obj, arg_0, arg_1), arg_0, arg_1));
        }
    }

    @JvmStatic
    @NotNull
    public static final Event post(@NotNull Event event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        Class<?> eventClass = event.getClass();
        Object t = classCache.get(eventClass);
        Intrinsics.checkNotNullExpressionValue(t, (String)"get(...)");
        Iterable $this$forEach$iv = (Iterable)t;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            Class clazz = (Class)element$iv;
            boolean bl = false;
            List<ListenerData> list = listeners.get(clazz);
            if (list == null) continue;
            Iterable $this$forEach$iv2 = list;
            boolean $i$f$forEach2 = false;
            for (Object element$iv2 : $this$forEach$iv2) {
                ListenerData data = (ListenerData)element$iv2;
                boolean bl2 = false;
                data.getInvoker().accept(event);
            }
        }
        EventBus.handleDynamic(event);
        return event;
    }

    private final Consumer<Event> createInvoker(Object instance, Method method) {
        Consumer consumer;
        try {
            method.setAccessible(true);
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
            MethodHandle methodHandle = lookup.unreflect(method);
            MethodHandle boundHandle = methodHandle.bindTo(instance);
            CallSite callSite = LambdaMetafactory.metafactory(lookup, "accept", MethodType.methodType(Consumer.class), MethodType.methodType(Void.TYPE, Object.class), boundHandle, MethodType.methodType(Void.TYPE, method.getParameterTypes()[0]));
            consumer = callSite.getTarget().invokeExact();
        }
        catch (Throwable throwable) {
            consumer = arg_0 -> EventBus.createInvoker$lambda$0(method, instance, arg_0);
        }
        return consumer;
    }

    @JvmStatic
    public static final <T extends Event> void registerEvent(@NotNull Class<T> eventClass, @NotNull Function1<? super T, Unit> listener) {
        Intrinsics.checkNotNullParameter(eventClass, (String)"eventClass");
        Intrinsics.checkNotNullParameter(listener, (String)"listener");
        dynamicRunnable.computeIfAbsent(eventClass, arg_0 -> EventBus.registerEvent$lambda$1(EventBus::registerEvent$lambda$0, arg_0)).add(arg_0 -> EventBus.registerEvent$lambda$2(listener, arg_0));
    }

    /*
     * WARNING - void declaration
     */
    @JvmStatic
    private static final void handleDynamic(Event event) {
        void $this$filterTo$iv$iv;
        Map $this$filter$iv = dynamicRunnable;
        boolean $i$f$filter = false;
        Object object = $this$filter$iv;
        Map destination$iv$iv = new LinkedHashMap();
        boolean $i$f$filterTo = false;
        Iterator iterator = $this$filterTo$iv$iv.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry element$iv$iv;
            Map.Entry entry = element$iv$iv = iterator.next();
            boolean bl = false;
            Class clazz = (Class)entry.getKey();
            if (!clazz.isAssignableFrom(event.getClass())) continue;
            destination$iv$iv.put(element$iv$iv.getKey(), element$iv$iv.getValue());
        }
        Map $this$forEach$iv = destination$iv$iv;
        boolean $i$f$forEach = false;
        object = $this$forEach$iv.entrySet().iterator();
        while (object.hasNext()) {
            Map.Entry element$iv;
            Map.Entry entry = element$iv = (Map.Entry)object.next();
            boolean bl = false;
            List listeners = (List)entry.getValue();
            Iterable $this$forEach$iv2 = listeners;
            boolean $i$f$forEach2 = false;
            for (Object element$iv2 : $this$forEach$iv2) {
                Function1 it = (Function1)element$iv2;
                boolean bl2 = false;
                it.invoke((Object)event);
            }
        }
    }

    private static final List register$lambda$0$1(Object $obj, Consumer $consumer, int $priority, Class clazz, List list) {
        Intrinsics.checkNotNullParameter((Object)clazz, (String)"<unused var>");
        List list2 = list;
        ArrayList<ListenerData> newList = new ArrayList<ListenerData>(list2 != null ? (Collection)list2 : (Collection)CollectionsKt.emptyList());
        newList.add(new ListenerData($obj, $consumer, $priority));
        CollectionsKt.sort((List)newList);
        return Collections.unmodifiableList((List)newList);
    }

    private static final List register$lambda$0$2(Function2 $tmp0, Object p0, Object p1) {
        return (List)$tmp0.invoke(p0, p1);
    }

    private static final boolean unregister$lambda$0$0$0(Object $obj, ListenerData it) {
        return it.getInstance() == $obj;
    }

    private static final boolean unregister$lambda$0$0$1(Function1 $tmp0, Object p0) {
        return (Boolean)$tmp0.invoke(p0);
    }

    private static final List unregister$lambda$0$0(Object $obj, Class clazz, List list) {
        Intrinsics.checkNotNullParameter((Object)clazz, (String)"<unused var>");
        List list2 = list;
        if (list2 == null) {
            return null;
        }
        ArrayList<Object> newList = new ArrayList<Object>(list2);
        return newList.removeIf(arg_0 -> EventBus.unregister$lambda$0$0$1(arg_0 -> EventBus.unregister$lambda$0$0$0($obj, arg_0), arg_0)) ? (newList.isEmpty() ? null : Collections.unmodifiableList((List)newList)) : list;
    }

    private static final List unregister$lambda$0$1(Function2 $tmp0, Object p0, Object p1) {
        return (List)$tmp0.invoke(p0, p1);
    }

    private static final void createInvoker$lambda$0(Method $method, Object $instance, Event evt) {
        Intrinsics.checkNotNullParameter((Object)evt, (String)"evt");
        Object[] objectArray = new Object[]{evt};
        $method.invoke($instance, objectArray);
    }

    private static final List registerEvent$lambda$0(Class it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return new ArrayList();
    }

    private static final List registerEvent$lambda$1(Function1 $tmp0, Object p0) {
        return (List)$tmp0.invoke(p0);
    }

    private static final Unit registerEvent$lambda$2(Function1 $listener, Event event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        $listener.invoke((Object)event);
        return Unit.INSTANCE;
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u000f\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\t\b\u0082\b\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B%\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u0012\u0006\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0004\b\t\u0010\nJ\u0019\u0010\f\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\u0000H\u0096\u0082\u0004\u00a2\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000e\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0016\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u0010\u0010\u0012\u001a\u00020\u0007H\u00c6\u0003\u00a2\u0006\u0004\b\u0012\u0010\u0013J4\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\b\b\u0002\u0010\b\u001a\u00020\u0007H\u00c6\u0001\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u001b\u0010\u0017\u001a\u00020\u00162\b\u0010\u000b\u001a\u0004\u0018\u00010\u0002H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u0017\u0010\u0018J\u0011\u0010\u0019\u001a\u00020\u0007H\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u0019\u0010\u0013J\u0011\u0010\u001b\u001a\u00020\u001aH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001b\u0010\u001cR\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010\u001d\u001a\u0004\b\u001e\u0010\u000fR\u001d\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001f\u001a\u0004\b \u0010\u0011R\u0017\u0010\b\u001a\u00020\u00078\u0006\u00a2\u0006\f\n\u0004\b\b\u0010!\u001a\u0004\b\"\u0010\u0013\u00a8\u0006#"}, d2={"Lorg/cobalt/api/event/EventBus$ListenerData;", "", "", "instance", "Ljava/util/function/Consumer;", "Lorg/cobalt/api/event/Event;", "invoker", "", "priority", "<init>", "(Ljava/lang/Object;Ljava/util/function/Consumer;I)V", "other", "compareTo", "(Lorg/cobalt/api/event/EventBus$ListenerData;)I", "component1", "()Ljava/lang/Object;", "component2", "()Ljava/util/function/Consumer;", "component3", "()I", "copy", "(Ljava/lang/Object;Ljava/util/function/Consumer;I)Lorg/cobalt/api/event/EventBus$ListenerData;", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "Ljava/lang/Object;", "getInstance", "Ljava/util/function/Consumer;", "getInvoker", "I", "getPriority", "cobalt"})
    private static final class ListenerData
    implements Comparable<ListenerData> {
        @NotNull
        private final Object instance;
        @NotNull
        private final Consumer<Event> invoker;
        private final int priority;

        public ListenerData(@NotNull Object instance, @NotNull Consumer<Event> invoker, int priority) {
            Intrinsics.checkNotNullParameter((Object)instance, (String)"instance");
            Intrinsics.checkNotNullParameter(invoker, (String)"invoker");
            this.instance = instance;
            this.invoker = invoker;
            this.priority = priority;
        }

        @NotNull
        public final Object getInstance() {
            return this.instance;
        }

        @NotNull
        public final Consumer<Event> getInvoker() {
            return this.invoker;
        }

        public final int getPriority() {
            return this.priority;
        }

        @Override
        public int compareTo(@NotNull ListenerData other) {
            Intrinsics.checkNotNullParameter((Object)other, (String)"other");
            return Intrinsics.compare((int)other.priority, (int)this.priority);
        }

        @NotNull
        public final Object component1() {
            return this.instance;
        }

        @NotNull
        public final Consumer<Event> component2() {
            return this.invoker;
        }

        public final int component3() {
            return this.priority;
        }

        @NotNull
        public final ListenerData copy(@NotNull Object instance, @NotNull Consumer<Event> invoker, int priority) {
            Intrinsics.checkNotNullParameter((Object)instance, (String)"instance");
            Intrinsics.checkNotNullParameter(invoker, (String)"invoker");
            return new ListenerData(instance, invoker, priority);
        }

        public static /* synthetic */ ListenerData copy$default(ListenerData listenerData, Object object, Consumer consumer, int n, int n2, Object object2) {
            if ((n2 & 1) != 0) {
                object = listenerData.instance;
            }
            if ((n2 & 2) != 0) {
                consumer = listenerData.invoker;
            }
            if ((n2 & 4) != 0) {
                n = listenerData.priority;
            }
            return listenerData.copy(object, consumer, n);
        }

        @NotNull
        public String toString() {
            return "ListenerData(instance=" + this.instance + ", invoker=" + this.invoker + ", priority=" + this.priority + ")";
        }

        public int hashCode() {
            int result = this.instance.hashCode();
            result = result * 31 + this.invoker.hashCode();
            result = result * 31 + Integer.hashCode(this.priority);
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ListenerData)) {
                return false;
            }
            ListenerData listenerData = (ListenerData)other;
            if (!Intrinsics.areEqual((Object)this.instance, (Object)listenerData.instance)) {
                return false;
            }
            if (!Intrinsics.areEqual(this.invoker, listenerData.invoker)) {
                return false;
            }
            return this.priority == listenerData.priority;
        }
    }
}

