package com.jushi.library.lottie.model.animatable;


import com.jushi.library.lottie.animation.keyframe.TextKeyframeAnimation;
import com.jushi.library.lottie.model.DocumentData;
import com.jushi.library.lottie.value.Keyframe;

import java.util.List;

public class AnimatableTextFrame extends BaseAnimatableValue<DocumentData, DocumentData> {

  public AnimatableTextFrame(List<Keyframe<DocumentData>> keyframes) {
    super(keyframes);
  }

  @Override public TextKeyframeAnimation createAnimation() {
    return new TextKeyframeAnimation(keyframes);
  }
}
