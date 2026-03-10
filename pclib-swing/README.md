# PCLib Swing

Swing UI helpers and ready-to-use chart components.

**Java version:** Java 8

## Maven

```xml
<dependency>
  <groupId>lu.kbra</groupId>
  <artifactId>pclib-swing</artifactId>
</dependency>
````

## What it contains

This module provides Swing components such as:

* `JColumnChart`
* `JLineGraph`
* `JRadarChart`
* `JLabelBuilder`

## Example

```java
import lu.kbra.pclib.swing.JLabelBuilder;

final JFrame frame = new JFrame("Line Graph");
frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
frame.getContentPane().setLayout(new BorderLayout());

final JColumnChart graph = new JColumnChart(Arrays.asList("a", "baaaaa", "c", "d"));

final Map<String, Double> values = new HashMap<>();
graph.titleEntries.forEach(c -> values.put(c, 1.0));
graph.createSeries("Entry 1").setValues(values);

final Map<String, Double> values2 = new HashMap<>();
graph.titleEntries.forEach(c -> values2.put(c, PCUtils.randomDoubleRange(0, 5)));
graph.createSeries("Entry 2").setValues(values2).setFillColor(new Color(128, 0, 0, 255)).setBorderColor(Color.RED);

final Map<String, Double> values3 = new HashMap<>();
graph.titleEntries.forEach(c -> values3.put(c, -2.0));
graph.createSeries("Entry 3").setValues(values3).setFillColor(new Color(0, 128, 0, 255)).setBorderColor(Color.GREEN);

graph.useMinorAxisSteps = true;
graph.minorAxisStep = 0.1;

graph.overrideMaxValue = false;
graph.maxValue = 10;

graph.overrideMinValue = false;
graph.minValue = 0;

frame.getContentPane().add(graph, BorderLayout.CENTER);

frame.getContentPane().add(graph.createLegend(false, true), BorderLayout.SOUTH);
frame.getContentPane().add(graph.createLegend(true, true), BorderLayout.EAST);

Arrays
    .stream(frame.getContentPane().getComponents())
    .filter(v -> v instanceof JLineGraphLegend)
    .forEach(e -> e.setBackground(Color.LIGHT_GRAY));

frame.setSize(600, 600);
frame.setVisible(true);
```