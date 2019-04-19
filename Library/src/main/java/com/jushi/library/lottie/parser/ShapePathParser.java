package com.jushi.library.lottie.parser;

import android.util.JsonReader;


import com.jushi.library.lottie.lottie.LottieComposition;
import com.jushi.library.lottie.model.animatable.AnimatableShapeValue;
import com.jushi.library.lottie.model.content.ShapePath;

import java.io.IOException;

class ShapePathParser {

  private ShapePathParser() {}

  static ShapePath parse(
      JsonReader reader, LottieComposition composition) throws IOException {
    String name = null;
    int ind = 0;
    AnimatableShapeValue shape = null;
    boolean hidden = false;

    while (reader.hasNext()) {
      switch (reader.nextName()) {
        case "nm":
          name = reader.nextString();
          break;
        case "ind":
          ind = reader.nextInt();
          break;
        case "ks":
          shape = AnimatableValueParser.parseShapeData(reader, composition);
          break;
        case "hd":
          hidden = reader.nextBoolean();
          break;
        default:
          reader.skipValue();
      }
    }

    return new ShapePath(name, ind, shape, hidden);
  }
}
