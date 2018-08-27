package com.github.hotbugfix;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class Patch {
    private static final String PATCH_CLASSES = "Patch-Classes";
    private static final String ENTRY_NAME = "META-INF/PATCH.MF";

    private Context context;
    private File mFile;
    private Map<String, List<String>> mClassMap;

    public Patch(File file, Context context) {
        super();
        this.mFile = file;
        this.context = context;
        init();
    }

    private void init() {
        JarFile jarFile = null;
        InputStream inputStream = null;
        mClassMap = new HashMap<>();
        List<String> list = new ArrayList<>();

        try {
            jarFile = new JarFile(mFile);
            JarEntry jarEntry = jarFile.getJarEntry(ENTRY_NAME);
            inputStream = jarFile.getInputStream(jarEntry);
            Manifest manifest = new Manifest(inputStream);
            Attributes main = manifest.getMainAttributes();
            Attributes.Name attrName;
            for (Iterator<?> ite = main.keySet().iterator(); ite.hasNext();) {
                attrName = (Attributes.Name) ite.next();
                if (attrName != null) {
                    String name = attrName.toString();
                    if (name.endsWith("Classes")) {
                        list = Arrays.asList(main.getValue(name).split(","));
                        if (name.equalsIgnoreCase(PATCH_CLASSES)) {
                            mClassMap.put(name, list);
                        } else {
                            mClassMap.put(name.trim().substring(0,name.length()-8),list);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                jarFile.close();
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Set<String> getPatchNames() {
        return mClassMap.keySet();
    }

    public List<String> getClasses(String name) {
        return mClassMap.get(name);
    }

    public File getmFile() {
        return mFile;
    }
}
