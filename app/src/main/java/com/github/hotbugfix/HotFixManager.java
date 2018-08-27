package com.github.hotbugfix;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.alipay.euler.andfix.annotation.MethodReplace;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;

public class HotFixManager {
    static {
        System.loadLibrary("native-lib");
    }

    public static native void init(int api);

    public static native void replaceMethod(Method src, Method dest);

    private Context context;
    private File optFile;

    public HotFixManager(Context context) {
        super();
        this.context = context;
        init(Build.VERSION.SDK_INT);
    }

    public void fix(File file, final ClassLoader classLoader, List<String> list) {
        optFile = new File(context.getFilesDir(), file.getName());
        if (optFile.exists()) {
            optFile.delete();
        }
        try {
            final DexFile dexFile = DexFile.loadDex(file.getAbsolutePath(), optFile.getAbsolutePath(), Context.MODE_PRIVATE);
            ClassLoader classLoader1 = new ClassLoader() {
                @Override
                protected Class<?> findClass(String name) throws ClassNotFoundException {
                    Class cls = dexFile.loadClass(name,this);
                    if (cls == null) {
                        cls = Class.forName(name);
                    }
                    return cls;
                }
            };

            Enumeration<String> entry = dexFile.entries();
            while (entry.hasMoreElements()) {
                String key = entry.nextElement();
                if (!list.contains(key)) {
                    continue;
                }
                Class realClass = dexFile.loadClass(key, classLoader1);
                if (realClass!=null) {
                    fix(realClass, classLoader);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fix(Class realClass, ClassLoader classLoader) {
        Method[] methods = realClass.getMethods();
        for (Method needMethod:methods) {
            MethodReplace methodReplace = needMethod.getAnnotation(MethodReplace.class);
            if (methodReplace == null) {
                continue;
            }
            Log.e("github","找到替换方法:"+methodReplace.toString()+"; class对象:"+realClass.toString());
            String cls = methodReplace.clazz();
            String methodName = methodReplace.method();
            replaceMethod(classLoader, cls, methodName, realClass, needMethod);
        }
    }

    private void replaceMethod(ClassLoader classLoader, String cls, String methodName, Class realClass, Method needMethod) {
        try {
            Class srcClass = Class.forName(cls);
            if (srcClass!=null) {
                Method src = srcClass.getDeclaredMethod(methodName, needMethod.getParameterTypes());
                replaceMethod(src, needMethod);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
