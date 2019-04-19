package com.jushi.library.lottie.model.animatable;


import com.jushi.library.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.jushi.library.lottie.value.Keyframe;

import java.util.List;

public interface AnimatableValue<K, A> {
  List<Keyframe<K>> getKeyframes();
  boolean isStatic();
  BaseKeyframeAnimation<K, A> createAnimation();
}
