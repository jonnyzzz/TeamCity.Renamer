package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.diagram.DiagramRelationshipInfo;
import com.intellij.diagram.DiagramRelationshipInfoAdapter;
import com.intellij.diagram.presentation.DiagramLineType;

import java.awt.*;

public class TCRelationships {
  static DiagramRelationshipInfo SNAPSHOT = new DiagramRelationshipInfoAdapter("SNAPSHOT") {
    @Override
    public Shape getStartArrow() {
      return STANDARD;
    }
  };
  static DiagramRelationshipInfo ARTIFACT = new DiagramRelationshipInfoAdapter("ARTIFACT") {
    @Override
    public Shape getStartArrow() {
      return STANDARD;
    }

    @Override
    public DiagramLineType getLineType() {
      return DiagramLineType.DASHED;
    }
  };
}
