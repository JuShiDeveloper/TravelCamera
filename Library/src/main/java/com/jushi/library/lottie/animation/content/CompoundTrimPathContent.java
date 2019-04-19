package com.jushi.library.lottie.animation.content;

import android.graphics.Path;


import com.jushi.library.lottie.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CompoundTrimPathContent {
    private List<TrimPathContent> contents = new ArrayList<>();

    void addTrimPath(TrimPathContent trimPath) {
        contents.add(trimPath);
    }

    public void apply(Path path) {
        for (int i = contents.size() - 1; i >= 0; i--) {
            Utils.applyTrimPathIfNeeded(path, contents.get(i));
        }
    }
}
