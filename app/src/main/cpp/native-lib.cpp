#include <jni.h>
#include <string>
#include "dalvik.h"

extern "C"
JNIEXPORT void JNICALL
Java_com_github_hotbugfix_HotFixManager_init(JNIEnv *env, jobject jobj, jint api) {

    // TODO
    void *handle=dlopen("libdvm.so",RTLD_NOW);

    if(handle)
    {
        const char *name = api > 10 ? "_Z20dvmDecodeIndirectRefP6ThreadP8_jobject" :
                           "dvmDecodeIndirectRef";
        dvmDecodeIndirectRef_fnPtr = (dvmDecodeIndirectRef_func) dlsym(handle, name);

        dvmThreadSelf_fnPtr = (dvmThreadSelf_func) dlsym(handle, api > 10 ? "_Z13dvmThreadSelfv"
                                                                          : "dvmThreadSelf");
        jclass clazz = env->FindClass("java/lang/reflect/Method");
        jClassMethod = env->GetMethodID(clazz,"getDeclaringClass","()Ljava/lang/Class;");

    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_github_hotbugfix_HotFixManager_replaceMethod(JNIEnv *env, jobject jobj, jobject src,
                                                      jobject dest) {
    // TODO
    jobject clazz=env->CallObjectMethod(dest,jClassMethod);
    ClassObject *clz= (ClassObject *) dvmDecodeIndirectRef_fnPtr(dvmThreadSelf_fnPtr(), clazz);
    clz->status=CLASS_INITIALIZED;
    Method *meth= (Method *) env->FromReflectedMethod(src);
    Method *target=(Method *)env->FromReflectedMethod(dest);
    meth->clazz=target->clazz;
    meth->accessFlags=target->accessFlags;
    meth->methodIndex=target->methodIndex;
    meth->jniArgInfo=target->jniArgInfo;
    meth->registersSize=target->registersSize;
    meth->outsSize=target->outsSize;
    meth->insns=target->insns;
    meth->insSize=meth->insSize;
    meth->nativeFunc=target->nativeFunc;
}
