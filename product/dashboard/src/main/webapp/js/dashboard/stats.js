// set up statistic graph
function createRatingStatsChart(e) {
    e.preventDefault();
    e.stopPropagation();

    $("#userStats").show();

    $.ajax({
        url: $("#userStats").data("action"),
        method: 'GET',
        beforeSend: function(xhr) {
            xhr.setRequestHeader("Accept", "application/json");
            xhr.setRequestHeader("Content-Type", "application/json");
        }
    }).done(function(data) {
        var avgRatingData = [];
        avgRatingData[0] = data[0];

        var songsCountData = [];
        songsCountData[0] = data[1];

        addStatisticsChart(avgRatingData, "avgRatingData");
        addStatisticsChart(songsCountData, "songsCountData");
    });
}

function addStatisticsChart(data, selector) {
    nv.addGraph(function() {
        var chart = nv.models.lineChart()
            .margin({top: 10, right: 15, bottom: 25, left: 25})
            .forceY([1, 5])
            .showLegend(false);

        chart.yAxis.axisLabel(data[0].key);

        chart.xAxis.tickFormat(function(d) {
            return d3.time.format('%d/%m')(new Date(d))
        });

        d3.select('#userStats #' + selector + ' svg')
            .datum(data)
            .call(chart);

        nv.utils.windowResize(function() {
            chart.update()
        });
        return chart;
    });
}
