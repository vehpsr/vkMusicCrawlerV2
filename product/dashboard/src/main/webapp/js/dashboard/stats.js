function setUpRatingChart() {
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
};

function addStatisticsChart(data) {
    nv.addGraph(function() {
        var colors = ['rgb(169,68,66)','rgb(212,156,94)','rgb(225,215,92)','rgb(164,184,83)','rgb(60,118,61)'];
        var chart = nv.models.multiBarChart()
            .reduceXTicks(false)
            .rotateLabels(0)
            .showControls(false)
            .stacked(true)
            .color(colors)
            .groupSpacing(0.1);

        chart.yAxis.tickFormat(d3.format('d'));

        chart.xAxis.tickFormat(function(d) {
            return d3.time.format('%d/%m')(new Date(d))
        });

        chart.tooltipContent(function(ratingValue, date, count, e, graph) {
            var avgRating = data.key;
            var ratingFormat = d3.format('.1f');

            var hoverDate = e.series.values[e.pointIndex].hoverDate;
            return    '<p>' + count + ' of <b><span style="color:' + colors[ratingValue-1] + '">(' + ratingValue + ')</span></b> at ' + date + '</p>' +
                '<p>avg rating: ' + ratingFormat(avgRating[e.point.x]) + '</p>';
        });

        d3.select('#ratingDiagram svg')
            .datum(data.value)
            .call(chart);

        nv.utils.windowResize(chart.update);

        return chart;
    });
}

function setUpSystemStatsTable() {
    $.ajax({
        url: $("#contstants").data("contextPath") + "/stats/system",
        method: 'GET',
        beforeSend: function(xhr) {
            xhr.setRequestHeader("Accept", "application/json");
            xhr.setRequestHeader("Content-Type", "application/json");
        }
    }).done(function(data) {
        addSystemStatsTable(data);
    });
}

function addSystemStatsTable(data) {
    nv.addGraph(function() {
        var chart = nv.models.indentedTree()
        .columns([ {
            key : 'key',
            label : 'Name',
            width : '75%',
            type : 'text'
        }, {
            key : 'val',
            label : 'Stat',
            width : '25%',
            type : 'text'
        } ]);

        d3.select('#systemStats').datum(data).call(chart);

        return chart;
    });
}
