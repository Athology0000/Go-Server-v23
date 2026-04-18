/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.pathfinder.pathfinder.heap;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.Arrays;
import java.util.NoSuchElementException;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0016\n\u0002\b\u0002\n\u0002\u0010\u0013\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\r\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0007\u0010\bJ\r\u0010\t\u001a\u00020\u0002\u00a2\u0006\u0004\b\t\u0010\nJ\r\u0010\f\u001a\u00020\u000b\u00a2\u0006\u0004\b\f\u0010\rJ\r\u0010\u000f\u001a\u00020\u000e\u00a2\u0006\u0004\b\u000f\u0010\u0010J\r\u0010\u0012\u001a\u00020\u0011\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0015\u0010\u0015\u001a\u00020\u00062\u0006\u0010\u0014\u001a\u00020\u000e\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u0015\u0010\u0017\u001a\u00020\u00112\u0006\u0010\u0014\u001a\u00020\u000e\u00a2\u0006\u0004\b\u0017\u0010\u0018J\u001d\u0010\u001a\u001a\u00020\u000b2\u0006\u0010\u0014\u001a\u00020\u000e2\u0006\u0010\u0019\u001a\u00020\u0011\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\r\u0010\u001c\u001a\u00020\u000e\u00a2\u0006\u0004\b\u001c\u0010\u0010J\u000f\u0010\u001d\u001a\u00020\u000bH\u0002\u00a2\u0006\u0004\b\u001d\u0010\rJ\u0017\u0010\u001f\u001a\u00020\u000b2\u0006\u0010\u001e\u001a\u00020\u0002H\u0002\u00a2\u0006\u0004\b\u001f\u0010\u0005J\u0017\u0010 \u001a\u00020\u000b2\u0006\u0010\u001e\u001a\u00020\u0002H\u0002\u00a2\u0006\u0004\b \u0010\u0005R\u0014\u0010\"\u001a\u00020!8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\"\u0010#R\u0016\u0010%\u001a\u00020$8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b%\u0010&R\u0016\u0010(\u001a\u00020'8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b(\u0010)R\u0016\u0010\t\u001a\u00020\u00028\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\t\u0010*\u00a8\u0006+"}, d2={"Lorg/cobalt/api/pathfinder/pathfinder/heap/PrimitiveMinHeap;", "", "", "initialCapacity", "<init>", "(I)V", "", "isEmpty", "()Z", "size", "()I", "", "clear", "()V", "", "peekMin", "()J", "", "peekMinCost", "()D", "packedNode", "contains", "(J)Z", "getCost", "(J)D", "cost", "insertOrUpdate", "(JD)V", "extractMin", "ensureCapacity", "index", "siftUp", "siftDown", "Lit/unimi/dsi/fastutil/longs/Long2IntOpenHashMap;", "nodeToIndexMap", "Lit/unimi/dsi/fastutil/longs/Long2IntOpenHashMap;", "", "nodes", "[J", "", "costs", "[D", "I", "cobalt"})
@SourceDebugExtension(value={"SMAP\nPrimitiveMinHeap.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PrimitiveMinHeap.kt\norg/cobalt/api/pathfinder/pathfinder/heap/PrimitiveMinHeap\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,142:1\n1#2:143\n*E\n"})
public final class PrimitiveMinHeap {
    @NotNull
    private final Long2IntOpenHashMap nodeToIndexMap;
    @NotNull
    private long[] nodes;
    @NotNull
    private double[] costs;
    private int size;

    /*
     * WARNING - void declaration
     */
    public PrimitiveMinHeap(int initialCapacity) {
        void $this$nodeToIndexMap_u24lambda_u240;
        Long2IntOpenHashMap long2IntOpenHashMap;
        Long2IntOpenHashMap long2IntOpenHashMap2 = long2IntOpenHashMap = new Long2IntOpenHashMap(initialCapacity);
        PrimitiveMinHeap primitiveMinHeap = this;
        boolean bl = false;
        $this$nodeToIndexMap_u24lambda_u240.defaultReturnValue(-1);
        primitiveMinHeap.nodeToIndexMap = long2IntOpenHashMap;
        this.nodes = new long[initialCapacity + 1];
        this.costs = new double[initialCapacity + 1];
    }

    public final boolean isEmpty() {
        return this.size == 0;
    }

    public final int size() {
        return this.size;
    }

    public final void clear() {
        this.size = 0;
        this.nodeToIndexMap.clear();
    }

    public final long peekMin() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.nodes[1];
    }

    public final double peekMinCost() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.costs[1];
    }

    public final boolean contains(long packedNode) {
        return this.nodeToIndexMap.containsKey(packedNode);
    }

    public final double getCost(long packedNode) {
        int index = this.nodeToIndexMap.get(packedNode);
        return index == -1 ? Double.MAX_VALUE : this.costs[index];
    }

    public final void insertOrUpdate(long packedNode, double cost) {
        int existingIndex = this.nodeToIndexMap.get(packedNode);
        if (existingIndex != -1) {
            if (cost < this.costs[existingIndex]) {
                this.costs[existingIndex] = cost;
                this.siftUp(existingIndex);
            }
        } else {
            this.ensureCapacity();
            int n = this.size;
            this.size = n + 1;
            this.nodes[this.size] = packedNode;
            this.costs[this.size] = cost;
            this.nodeToIndexMap.put(packedNode, this.size);
            this.siftUp(this.size);
        }
    }

    public final long extractMin() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        long minNode = this.nodes[1];
        this.nodeToIndexMap.remove(minNode);
        long lastNode = this.nodes[this.size];
        double lastCost = this.costs[this.size];
        this.nodes[1] = lastNode;
        this.costs[1] = lastCost;
        int n = this.size;
        this.size = n + -1;
        if (this.size > 0) {
            this.nodeToIndexMap.put(lastNode, 1);
            this.siftDown(1);
        }
        return minNode;
    }

    private final void ensureCapacity() {
        if (this.size >= this.nodes.length - 1) {
            int newCap = this.nodes.length * 2;
            long[] lArray = Arrays.copyOf(this.nodes, newCap);
            Intrinsics.checkNotNullExpressionValue((Object)lArray, (String)"copyOf(...)");
            this.nodes = lArray;
            double[] dArray = Arrays.copyOf(this.costs, newCap);
            Intrinsics.checkNotNullExpressionValue((Object)dArray, (String)"copyOf(...)");
            this.costs = dArray;
        }
    }

    private final void siftUp(int index) {
        int parentIndex;
        double parentCost;
        int current = index;
        long nodeToMove = this.nodes[current];
        double costToMove = this.costs[current];
        while (current > 1 && costToMove < (parentCost = this.costs[parentIndex = current >> 1])) {
            this.nodes[current] = this.nodes[parentIndex];
            this.costs[current] = parentCost;
            this.nodeToIndexMap.put(this.nodes[current], current);
            current = parentIndex;
        }
        this.nodes[current] = nodeToMove;
        this.costs[current] = costToMove;
        this.nodeToIndexMap.put(nodeToMove, current);
    }

    private final void siftDown(int index) {
        int current = index;
        long nodeToMove = this.nodes[current];
        double costToMove = this.costs[current];
        int half = this.size >> 1;
        while (current <= half) {
            int childIndex = current << 1;
            double childCost = this.costs[childIndex];
            int rightIndex = childIndex + 1;
            if (rightIndex <= this.size && this.costs[rightIndex] < childCost) {
                childIndex = rightIndex;
                childCost = this.costs[rightIndex];
            }
            if (!(costToMove > childCost)) break;
            this.nodes[current] = this.nodes[childIndex];
            this.costs[current] = childCost;
            this.nodeToIndexMap.put(this.nodes[current], current);
            current = childIndex;
        }
        this.nodes[current] = nodeToMove;
        this.costs[current] = costToMove;
        this.nodeToIndexMap.put(nodeToMove, current);
    }
}

