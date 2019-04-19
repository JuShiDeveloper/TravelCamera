package com.jushi.library.lottie.model.content;


import android.support.annotation.Nullable;

import com.jushi.library.lottie.lottie.LottieDrawable;
import com.jushi.library.lottie.animation.content.Content;
import com.jushi.library.lottie.animation.content.RepeaterContent;
import com.jushi.library.lottie.model.animatable.AnimatableFloatValue;
import com.jushi.library.lottie.model.animatable.AnimatableTransform;
import com.jushi.library.lottie.model.layer.BaseLayer;

public class Repeater implements ContentModel {
  private final String name;
  private final AnimatableFloatValue copies;
  private final AnimatableFloatValue offset;
  private final AnimatableTransform transform;
  private final boolean hidden;

  public Repeater(String name, AnimatableFloatValue copies, AnimatableFloatValue offset,
                  AnimatableTransform transform, boolean hidden) {
    this.name = name;
    this.copies = copies;
    this.offset = offset;
    this.transform = transform;
    this.hidden = hidden;
  }

  public String getName() {
    return name;
  }

  public AnimatableFloatValue getCopies() {
    return copies;
  }

  public AnimatableFloatValue getOffset() {
    return offset;
  }

  public AnimatableTransform getTransform() {
    return transform;
  }

  public boolean isHidden() {
    return hidden;
  }

  @Nullable
  @Override public Content toContent(LottieDrawable drawable, BaseLayer layer) {
    return new RepeaterContent(drawable, layer, this);
  }
}
