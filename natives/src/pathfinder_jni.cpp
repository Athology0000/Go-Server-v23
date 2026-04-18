#include <jni.h>
#include "engine/PathfinderEngine.h"
#include <bit>

extern "C" {

JNIEXPORT jlong JNICALL
Java_org_cobalt_pathfinder_NativePathfinderBridge_createEngine(JNIEnv*, jclass) {
    return (jlong)(new PathfinderEngine());
}

JNIEXPORT void JNICALL
Java_org_cobalt_pathfinder_NativePathfinderBridge_destroyEngine(JNIEnv*, jclass, jlong handle) {
    delete (PathfinderEngine*)handle;
}

JNIEXPORT void JNICALL
Java_org_cobalt_pathfinder_NativePathfinderBridge_setRoute(JNIEnv* env, jclass,
        jlong handle, jdoubleArray waypoints, jboolean loop, jint profile) {
    jsize len = env->GetArrayLength(waypoints);
    jdouble* data = env->GetDoubleArrayElements(waypoints, nullptr);
    ((PathfinderEngine*)handle)->setRoute(data, len / 3, loop, profile);
    env->ReleaseDoubleArrayElements(waypoints, data, JNI_ABORT);
}

JNIEXPORT void JNICALL
Java_org_cobalt_pathfinder_NativePathfinderBridge_setTarget(JNIEnv*, jclass,
        jlong handle, jdouble x, jdouble y, jdouble z) {
    ((PathfinderEngine*)handle)->setTarget(x, y, z);
}

JNIEXPORT void JNICALL
Java_org_cobalt_pathfinder_NativePathfinderBridge_setTargetWithRadius(JNIEnv*, jclass,
        jlong handle, jdouble x, jdouble y, jdouble z, jdouble radius) {
    ((PathfinderEngine*)handle)->setTarget(x, y, z, radius);
}

JNIEXPORT jintArray JNICALL
Java_org_cobalt_pathfinder_NativePathfinderBridge_update(JNIEnv* env, jclass,
        jlong handle, jbyteArray worldBuf, jint bx, jint by, jint bz,
        jdouble px, jdouble py, jdouble pz,
        jfloat yaw, jfloat pitch, jboolean onGround) {

    jbyte* buf = env->GetByteArrayElements(worldBuf, nullptr);

    PathCommand cmd = ((PathfinderEngine*)handle)->update(
        (const uint8_t*)buf, bx, by, bz,
        px, py, pz, yaw, pitch, onGround);

    env->ReleaseByteArrayElements(worldBuf, buf, JNI_ABORT);

    jintArray arr = env->NewIntArray(10);
    jint out[10] = {
        cmd.forward?1:0, cmd.back?1:0, cmd.jump?1:0,
        cmd.sneak?1:0,   cmd.sprint?1:0,
        std::bit_cast<int>(cmd.targetYaw),
        std::bit_cast<int>(cmd.targetPitch),
        (int)cmd.status, (int)cmd.activeAction,
        std::bit_cast<int>(cmd.distanceToTarget)
    };
    env->SetIntArrayRegion(arr, 0, 10, out);
    return arr;
}

JNIEXPORT void JNICALL
Java_org_cobalt_pathfinder_NativePathfinderBridge_stop(JNIEnv*, jclass, jlong handle) {
    ((PathfinderEngine*)handle)->stop();
}

JNIEXPORT jint JNICALL
Java_org_cobalt_pathfinder_NativePathfinderBridge_getStatus(JNIEnv*, jclass, jlong handle) {
    return (jint)((PathfinderEngine*)handle)->getStatus();
}

JNIEXPORT jfloatArray JNICALL
Java_org_cobalt_pathfinder_NativePathfinderBridge_getPathNodes(JNIEnv* env, jclass, jlong handle) {
    std::vector<Vec3i> nodes;
    ((PathfinderEngine*)handle)->getPathNodes(nodes);
    jfloatArray arr = env->NewFloatArray((jsize)(nodes.size() * 3));
    if (nodes.empty()) return arr;
    std::vector<jfloat> data;
    data.reserve(nodes.size() * 3);
    for (const auto& n : nodes) {
        data.push_back((jfloat)(n.x + 0.5));  // block-center X
        data.push_back((jfloat)(n.y));          // feet Y (no offset)
        data.push_back((jfloat)(n.z + 0.5));  // block-center Z
    }
    env->SetFloatArrayRegion(arr, 0, (jsize)data.size(), data.data());
    return arr;
}

} // extern "C"
