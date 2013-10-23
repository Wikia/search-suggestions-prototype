$(function() {
  function plot(name, series) {
    var plotWrap = $('<div class="col-lg-4 plot-wrapper">').appendTo('.plot-container');
    var plotRap = $('<div style="min-height: 200px;">').appendTo(plotWrap);
    var pairs = [];
    for ( var i in series ) {
      pairs.push([i,series[i]]);
    }
    var plot = $.plot( plotRap, [ pairs ] );
    plotWrap.append("<b>" + name + "</b>");
    return plot;
  }

  $.get("../api/metrics/")
    .done(function(data) {
      for ( var i in data ) {
        var seriesMap = data[i];
        for ( plotName in seriesMap ) {
          var values = seriesMap[plotName];
          plot(plotName, values);
        }
      }
    })
})
