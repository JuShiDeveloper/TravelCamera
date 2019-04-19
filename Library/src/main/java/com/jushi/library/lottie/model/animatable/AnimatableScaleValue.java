package com.jushi.library.lottie.model.animatable;


import com.jushi.library.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.jushi.library.lottie.animation.keyframe.ScaleKeyframeAnimation;
import com.jushi.library.lottie.value.Keyframe;
import com.jushi.library.lottie.value.ScaleXY;

import java.util.List;

public class AnimatableScaleValue extends BaseAnimatableValue<ScaleXY, ScaleXY> {

  AnimatableScaleValue() {
    this(new ScaleXY(1f, 1f));
  }

  public AnimatableScaleValue(ScaleXY value) {
    super(value);
  }

  public AnimatableScaleValue(List<Keyframe<ScaleXY>> keyframes) {
    super(keyframes);
  }

  @Override public BaseKeyframeAnimation<ScaleXY, ScaleXY> createAnimation() {
    return new ScaleKeyframeAnimation(keyframes);
  }
}
