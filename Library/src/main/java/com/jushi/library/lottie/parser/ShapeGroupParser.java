package com.jushi.library.lottie.parser;

import android.util.JsonReader;


import com.jushi.library.lottie.lottie.LottieComposition;
import com.jushi.library.lottie.model.content.ContentModel;
import com.jushi.library.lottie.model.content.ShapeGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class ShapeGroupParser {

  private ShapeGroupParser() {}

  static ShapeGroup parse(
      JsonReader reader, LottieComposition composition) throws IOException {
    String name = null;
    boolean hidden = false;
    List<ContentModel> items = new ArrayList<>();

    while (reader.hasNext()) {
      switch (reader.nextName()) {
        case "nm":
          name = reader.nextString();
          break;
        case "hd":
          hidden = reader.nextBoolean();
          break;
        case "it":
          reader.beginArray();
          while (reader.hasNext()) {
            ContentModel newItem = ContentModelParser.parse(reader, composition);
            if (newItem != null) {
              items.add(newItem);
            }
          }
          reader.endArray();
          break;
        default:
          reader.skipValue();
      }
    }

    return new ShapeGroup(name, items, hidden);
  }
}
