package com.github.hotbugfix;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;

import java.io.File;
import java.util.List;

public class PatchManager {
    private Context context;
    private HotFixManager hotFixManager;
    private File src;

    public PatchManager(Context context) {
        super();
        this.context = context;
        hotFixManager = new HotFixManager(context);
    }

    public void loadPatch(String path) {
        src = new File(path);
        Patch patch = new Patch(src, context);
        ClassLoader classLoader = context.getClassLoader();
        List<String> list;
        for (String name:patch.getPatchNames()) {
            list = patch.getClasses(name);
            hotFixManager.fix(src, classLoader, list);
        }
    }
}
