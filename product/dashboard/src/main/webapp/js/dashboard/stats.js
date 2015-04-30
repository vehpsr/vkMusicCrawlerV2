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
        addStatisticsChart(data);
    });
});

function addStatisticsChart(data) {
    nv.addGraph(function() {
        var chart = nv.models.multiBarChart()
            .reduceXTicks(true)
            .rotateLabels(0)
            .showControls(false)
            .stacked(true)
            .color(['rgb(169,68,66)','rgb(212,156,94)','rgb(225,215,92)','rgb(164,184,83)','rgb(60,118,61)'])
            .groupSpacing(0.1);

        chart.yAxis.tickFormat(d3.format('d'));

        chart.xAxis.tickFormat(function(d) {
            return d3.time.format('%d/%m')(new Date(d))
        });

        d3.select('#ratingDiagram svg')
            .datum(data.value)
            .call(chart);

        nv.utils.windowResize(chart.update);

        return chart;
    });
}
