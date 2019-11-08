package ch.romix.schirizettel.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GenerationHint {
  public final boolean perfect;
  public final Set<String> unusedCSVFields;
  public final Set<String> unusedTemplateFields;

  public GenerationHint(Set<String> unusedCSVFields, Set<String> unusedTemplateFields) {
    perfect = unusedCSVFields.isEmpty() && unusedTemplateFields.isEmpty();
    this.unusedCSVFields = unusedCSVFields;
    this.unusedTemplateFields = unusedTemplateFields;
  }

  public String getHints() {
    List<String> hints = new ArrayList<>();
    if (!unusedCSVFields.isEmpty()) {
      hints.add(
          "Folgende Felder sind in der CSV-Datei vorhanden, werden in der Vorlage aber nicht verwendet:\n"
              + unusedCSVFields.stream().sorted().collect(Collectors.joining(", ")));
    }
    if (!unusedTemplateFields.isEmpty()) {
      hints.add("Folgende Felder sind in der Vorlage vorhanden, werden aber von der CSV-Datei nicht gesetzt:\n"
          + unusedTemplateFields.stream().sorted().collect(Collectors.joining(", ")));
    }
    return String.join("\n\n", hints);
  }
}
