package com.jonnyzzz.teamcity.renamer.diagram;

import org.jetbrains.annotations.NotNull;

public interface TCEdgesProducer {

  Iterable<TCEdge> getEdges(@NotNull TCDataModel model, @NotNull TCNode node);

}
