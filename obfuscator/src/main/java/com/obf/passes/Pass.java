package com.obf.passes;

import org.objectweb.asm.tree.ClassNode;
import java.util.Map;

public interface Pass {
    /**
     * Transform all classes in-place.
     * @param classes mutable map of internal class name → ClassNode
     */
    void apply(Map<String, ClassNode> classes);
}
