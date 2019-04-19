package com.jushi.library.lottie.model.content;


import android.support.annotation.Nullable;

import com.jushi.library.lottie.lottie.LottieDrawable;
import com.jushi.library.lottie.animation.content.Content;
import com.jushi.library.lottie.model.layer.BaseLayer;

public interface ContentModel {
  @Nullable
  Content toContent(LottieDrawable drawable, BaseLayer layer);
}
