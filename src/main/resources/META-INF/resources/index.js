export default function define(trenzz, rendered) {
  const main = trenzz.module();
  const fileAttachments = new Map([["trenzz.json", new URL("./api/trenzz", import.meta.url)]]);
  main.builtin("FileAttachment", trenzz.fileAttachments(name => fileAttachments.get(name)));
  main.variable(rendered("rawData")).define("rawData", ["FileAttachment"], function (FileAttachment) {
    return (FileAttachment("trenzz.json").json())
  });
  main.variable(rendered("data")).define("data", ["rawData"], function (rawData) {
    return (rawData.hourlySummaries.flatMap(summary => {
      return summary.trends.map(trend => {
          return {
              "date": summary.hour,
              "name": trend.name,
              "category": trend.trendSequence,
              "value": isNaN(trend.tweetVolume)?0:trend.tweetVolume
          }
      })}))
  });

  main.variable(rendered("replay")).define("replay", ["Generators", "viewof replay"], (G, _) => G.input(_));
  main.variable(rendered("chart")).define("chart", ["replay", "d3", "width", "height", "bars", "axis", "labels", "ticker", "keyframes", "duration", "x", "invalidation"], async function* (replay, d3, width, height, bars, axis, labels, ticker, keyframes, duration, x, invalidation) {
    replay;

    const svg = d3.create("svg")
      .attr("viewBox", [0, 0, width, height]);

    const updateBars = bars(svg);
    const updateAxis = axis(svg);
    const updateLabels = labels(svg);
    const updateTicker = ticker(svg);

    yield svg.node();

    for (const keyframe of keyframes) {
      const transition = svg.transition()
        .duration(duration)
        .ease(d3.easeLinear);

      // Extract the top bar’s value.
      x.domain([0, keyframe[1][0].value]);

      updateAxis(keyframe, transition);
      updateBars(keyframe, transition);
      updateLabels(keyframe, transition);
      updateTicker(keyframe, transition);

      invalidation.then(() => svg.interrupt());
      await transition.end();
    }
  }
  );
  main.variable(rendered("duration")).define("duration", function () {
    return (
      250
    )
  });
  main.variable(rendered("n")).define("n", function () {
    return (
      12
    )
  });
  main.variable(rendered("names")).define("names", ["data"], function (data) {
    return (
      new Set(data.map(d => d.name))
    )
  });
  main.variable(rendered("datevalues")).define("datevalues", ["d3", "data"], function (d3, data) {
    return (
      Array.from(d3.rollup(data, ([d]) => d.value, d => d.date, d => d.name))
        .map(([date, data]) => [new Date(date), data])
        .sort(([a], [b]) => d3.ascending(a, b))
    )
  });
  main.variable(rendered("rank")).define("rank", ["names", "d3", "n"], function (names, d3, n) {
    return (
      function rank(value) {
        const data = Array.from(names, name => ({ name, value: value(name) }));
        data.sort((a, b) => d3.descending(a.value, b.value));
        for (let i = 0; i < data.length; ++i) data[i].rank = Math.min(n, i);
        return data;
      }
    )
  });
  main.variable(rendered("k")).define("k", function () {
    return (
      10
    )
  });
  main.variable(rendered("keyframes")).define("keyframes", ["d3", "datevalues", "k", "rank"], function (d3, datevalues, k, rank) {
    const keyframes = [];
    let ka, a, kb, b;
    for ([[ka, a], [kb, b]] of d3.pairs(datevalues)) {
      for (let i = 0; i < k; ++i) {
        const t = i / k;
        keyframes.push([
          new Date(ka * (1 - t) + kb * t),
          rank(name => (a.get(name) || 0) * (1 - t) + (b.get(name) || 0) * t)
        ]);
      }
    }
    keyframes.push([new Date(kb), rank(name => b.get(name) || 0)]);
    return keyframes;
  }
  );
  main.variable(rendered("nameframes")).define("nameframes", ["d3", "keyframes"], function (d3, keyframes) {
    return (
      d3.groups(keyframes.flatMap(([, data]) => data), d => d.name)
    )
  });
  main.variable(rendered("prev")).define("prev", ["nameframes", "d3"], function (nameframes, d3) {
    return (
      new Map(nameframes.flatMap(([, data]) => d3.pairs(data, (a, b) => [b, a])))
    )
  });
  main.variable(rendered("next")).define("next", ["nameframes", "d3"], function (nameframes, d3) {
    return (
      new Map(nameframes.flatMap(([, data]) => d3.pairs(data)))
    )
  });
  main.variable(rendered("bars")).define("bars", ["n", "color", "y", "x", "prev", "next"], function (n, color, y, x, prev, next) {
    return (
      function bars(svg) {
        let bar = svg.append("g")
          .attr("fill-opacity", 0.6)
          .selectAll("rect");

        return ([date, data], transition) => bar = bar
          .data(data.slice(0, n), d => d.name)
          .join(
            enter => enter.append("rect")
              .attr("fill", color)
              .attr("height", y.bandwidth())
              .attr("x", x(0))
              .attr("y", d => y((prev.get(d) || d).rank))
              .attr("width", d => x((prev.get(d) || d).value) - x(0)),
            update => update,
            exit => exit.transition(transition).remove()
              .attr("y", d => y((next.get(d) || d).rank))
              .attr("width", d => x((next.get(d) || d).value) - x(0))
          )
          .call(bar => bar.transition(transition)
            .attr("y", d => y(d.rank))
            .attr("width", d => x(d.value) - x(0)));
      }
    )
  });
  main.variable(rendered("labels")).define("labels", ["n", "x", "prev", "y", "next", "textTween"], function (n, x, prev, y, next, textTween) {
    return (
      function labels(svg) {
        let label = svg.append("g")
          .style("font", "bold 12px var(--sans-serif)")
          .style("font-variant-numeric", "tabular-nums")
          .attr("text-anchor", "end")
          .selectAll("text");

        return ([date, data], transition) => label = label
          .data(data.slice(0, n), d => d.name)
          .join(
            enter => enter.append("text")
              .attr("transform", d => `translate(${x((prev.get(d) || d).value)},${y((prev.get(d) || d).rank)})`)
              .attr("y", y.bandwidth() / 2)
              .attr("x", -6)
              .attr("dy", "-0.25em")
              .text(d => d.name)
              .call(text => text.append("tspan")
                .attr("fill-opacity", 0.7)
                .attr("font-weight", "normal")
                .attr("x", -6)
                .attr("dy", "1.15em")),
            update => update,
            exit => exit.transition(transition).remove()
              .attr("transform", d => `translate(${x((next.get(d) || d).value)},${y((next.get(d) || d).rank)})`)
              .call(g => g.select("tspan").tween("text", d => textTween(d.value, (next.get(d) || d).value)))
          )
          .call(bar => bar.transition(transition)
            .attr("transform", d => `translate(${x(d.value)},${y(d.rank)})`)
            .call(g => g.select("tspan").tween("text", d => textTween((prev.get(d) || d).value, d.value))));
      }
    )
  });
  main.variable(rendered("textTween")).define("textTween", ["d3", "formatNumber"], function (d3, formatNumber) {
    return (
      function textTween(a, b) {
        const i = d3.interpolateNumber(a, b);
        return function (t) {
          this.textContent = formatNumber(i(t));
        };
      }
    )
  });
  main.variable(rendered("formatNumber")).define("formatNumber", ["d3"], function (d3) {
    return (
      d3.format(",d")
    )
  });
  main.variable(rendered("axis")).define("axis", ["margin", "d3", "x", "width", "barSize", "n", "y"], function (margin, d3, x, width, barSize, n, y) {
    return (
      function axis(svg) {
        const g = svg.append("g")
          .attr("transform", `translate(0,${margin.top})`);

        const axis = d3.axisTop(x)
          .ticks(width / 160)
          .tickSizeOuter(0)
          .tickSizeInner(-barSize * (n + y.padding()));

        return (_, transition) => {
          g.transition(transition).call(axis);
          g.select(".tick:first-of-type text").remove();
          g.selectAll(".tick:not(:first-of-type) line").attr("stroke", "white");
          g.select(".domain").remove();
        };
      }
    )
  });
  main.variable(rendered("ticker")).define("ticker", ["barSize", "width", "margin", "n", "formatDate", "keyframes"], function (barSize, width, margin, n, formatDate, keyframes) {
    return (
      function ticker(svg) {
        const now = svg.append("text")
          .style("font", `bold ${barSize}px var(--sans-serif)`)
          .style("font-variant-numeric", "tabular-nums")
          .attr("text-anchor", "end")
          .attr("x", 135)
          .attr("y", 18)
          .attr("dy", "0.32em")
          .text(formatDate(keyframes[0][0]));

        return ([date], transition) => {
          transition.end().then(() => now.text(formatDate(date)));
        };
      }
    )
  });
  main.variable(rendered("formatDate")).define("formatDate", ["d3"], function (d3) {
    return (
      d3.timeFormat("%a %d %B %Y  %I %p")
    )
  });
  main.variable(rendered("color")).define("color", ["d3", "data"], function (d3, data) {
    const scale = d3.scaleOrdinal(d3.schemeTableau10);
    if (data.some(d => d.category !== undefined)) {
      const categoryByName = new Map(data.map(d => [d.name, d.category]))
      scale.domain(categoryByName.values());
      return d => scale(categoryByName.get(d.name));
    }
    return d => scale(d.name);
  }
  );
  main.variable(rendered("x")).define("x", ["d3", "margin", "width"], function (d3, margin, width) {
    return (
      d3.scaleLinear([0, 1], [margin.left, width - margin.right])
    )
  });
  main.variable(rendered("y")).define("y", ["d3", "n", "margin", "barSize"], function (d3, n, margin, barSize) {
    return (
      d3.scaleBand()
        .domain(d3.range(n + 1))
        .rangeRound([margin.top, margin.top + barSize * (n + 1 + 0.1)])
        .padding(0.1)
    )
  });
  main.variable(rendered("height")).define("height", ["margin", "barSize", "n"], function (margin, barSize, n) {
    return (
      margin.top + barSize * n + margin.bottom
    )
  });
  main.variable(rendered("barSize")).define("barSize", function () {
    return (
      24
    )
  });
  main.variable(rendered("margin")).define("margin", function () {
    return (
      { top: 46, right: 6, bottom: 6, left: 6 }
    )
  });
  main.variable(rendered("d3")).define("d3", ["require"], function (require) {
    return (
      require("d3@6")
    )
  });
  main.variable(rendered("viewof replay")).define("viewof replay", ["html"], function (html) {
    return (
      html`<button>Replay`
    )
  });
  return main;
}
