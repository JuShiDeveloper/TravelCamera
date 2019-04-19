package com.jushi.library.lottie.model.animatable;

import android.graphics.Path;


import com.jushi.library.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.jushi.library.lottie.animation.keyframe.ShapeKeyframeAnimation;
import com.jushi.library.lottie.model.content.ShapeData;
import com.jushi.library.lottie.value.Keyframe;

import java.util.List;

public class AnimatableShapeValue extends BaseAnimatableValue<ShapeData, Path> {

  public AnimatableShapeValue(List<Keyframe<ShapeData>> keyframes) {
    super(keyframes);
  }

  @Override public BaseKeyframeAnimation<ShapeData, Path> createAnimation() {
    return new ShapeKeyframeAnimation(keyframes);
  }
}
