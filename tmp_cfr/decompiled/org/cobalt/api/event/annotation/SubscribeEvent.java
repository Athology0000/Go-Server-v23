/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 */
package org.cobalt.api.event.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import kotlin.Metadata;

@Retention(value=RetentionPolicy.RUNTIME)
@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u001b\n\u0002\u0010\b\n\u0002\b\u0005\b\u0086\u0002\u0018\u00002\u00020\u0001B\u0017\u0012\u000e\b\u0002\u0010\u0003\u001a\u00020\u0002B\u0004\b\u0003\u0010\u0004\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\u0006\u001a\u0004\b\u0003\u0010\u0006\u00a8\u0006\u0007"}, d2={"Lorg/cobalt/api/event/annotation/SubscribeEvent;", "", "", "priority", "<init>", "(I)V", "()I", "cobalt"})
public @interface SubscribeEvent {
    public int priority() default 2;
}

