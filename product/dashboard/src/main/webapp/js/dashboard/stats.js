// set up statistic graph
$(function () {
    $.ajax({
        url: $("#contstants").data("contextPath") + "/stats/rating",
        method: 'GET',
        beforeSend: function(xhr) {
            xhr.setRequestHeader("Accept", "application/json");
            xhr.setRequestHeader("Content-Type", "application/json");
        }
    }).done(function(data) {
        addStatisticsChart({values:[{x: 13, y: 23}, {x: 33, y: 23}]}, "#avgRatingDiagram", d3.format('.02f'));
        addStatisticsChart({values:[{x: 15, y: 25}, {x: 25, y: 15}]}, "#songsCountDiagram");
    });
});

function addStatisticsChart(data, selector, yAxisFormat) {
    nv.addGraph(function() {
        var chart = nv.models.lineChart()
            //.margin({top: 30, right: 15, bottom: 25, left: 25})
            //.forceY([1, 5])
            .showLegend(false);

        if (yAxisFormat) {
            chart.yAxis.tickFormat(yAxisFormat);
        }

        chart.xAxis.tickFormat(function(d) {
            return d3.time.format('%d/%m')(new Date(d))
        });

        d3.select(selector + ' svg')
            .datum(new Array(data))
            .call(chart);

        /*nv.utils.windowResize(function() {
            chart.update()
        });*/
        return chart;
    });
}
