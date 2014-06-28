package com.jonnyzzz.teamcity.renamer.resolve;

import com.intellij.openapi.util.TextRange;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildRunnerElement;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildRunnersElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Util {
  private Util() {
  }

  private static final Random RANDOM = new Random(System.currentTimeMillis());

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

  public static String uniqueRunnerID(BuildRunnersElement buildRunnersElement) {
    while (true) {
      String result = "RUNNER_" + (1000 + RANDOM.nextInt(1000));
      for (BuildRunnerElement runnerElement : buildRunnersElement.getRunners()) {
        if (result.equals(runnerElement.getBuildRunnerId().getValue())) {
          continue;
        }
      }

      return result;
    }
  }
}
