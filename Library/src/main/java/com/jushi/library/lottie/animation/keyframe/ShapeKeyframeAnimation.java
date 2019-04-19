package com.jushi.library.lottie.animation.keyframe;

import android.graphics.Path;


import com.jushi.library.lottie.model.content.ShapeData;
import com.jushi.library.lottie.utils.MiscUtils;
import com.jushi.library.lottie.value.Keyframe;

import java.util.List;

public class ShapeKeyframeAnimation extends BaseKeyframeAnimation<ShapeData, Path> {
  private final ShapeData tempShapeData = new ShapeData();
  private final Path tempPath = new Path();

  public ShapeKeyframeAnimation(List<Keyframe<ShapeData>> keyframes) {
    super(keyframes);
  }

  @Override public Path getValue(Keyframe<ShapeData> keyframe, float keyframeProgress) {
    ShapeData startShapeData = keyframe.startValue;
    ShapeData endShapeData = keyframe.endValue;

    tempShapeData.interpolateBetween(startShapeData, endShapeData, keyframeProgress);
    MiscUtils.getPathFromData(tempShapeData, tempPath);
    return tempPath;
  }
}
