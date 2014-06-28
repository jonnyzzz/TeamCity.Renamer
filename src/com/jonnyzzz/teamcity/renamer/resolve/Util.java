package com.jonnyzzz.teamcity.renamer.resolve;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Util {
  private Util() {
  }

  @NotNull
  public static List<TextRange> getRefRanges(@NotNull String text) {
    List<TextRange> refRanges = new ArrayList<>();
    for(int idx = 0; idx < text.length(); idx++) {

      if (text.charAt(idx) == '%') {
        idx++;
        final int refStart = idx;

        while (idx < text.length() && text.charAt(idx) != '%') idx++;
        if (idx >= text.length()) break;

        final int refEnd = idx;

        if (refStart < refEnd) {
          refRanges.add(new TextRange(refStart, refEnd));
        }
      }
    }
    return refRanges;
  }
}
