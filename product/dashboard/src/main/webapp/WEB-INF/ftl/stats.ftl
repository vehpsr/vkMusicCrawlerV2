<html>
<head>
    <meta charset="UTF-8" />
    <title>stats</title>
    <link rel="icon" type="image/png" href="${rc.getContextPath()}/favicon/favicon.ico">
    <link rel="stylesheet" href="${rc.getContextPath()}/resources/layout/nv.d3.css" type="text/css"></link>
    <link rel="stylesheet" href="${rc.getContextPath()}/resources/layout/index.css" type="text/css"></link>
    <link rel="stylesheet" href="${rc.getContextPath()}/resources/layout/stats.css" type="text/css"></link>
    <script src="${rc.getContextPath()}/resources/js/jquery-2.1.3.js" type="text/javascript"></script>
    <script src="${rc.getContextPath()}/resources/js/d3.js" type="text/javascript"></script>
    <script src="${rc.getContextPath()}/resources/js/nv.d3.js" type="text/javascript"></script>
    <script src="${rc.getContextPath()}/resources/js/nv.d3.patch.js" type="text/javascript"></script>
    <script src="${rc.getContextPath()}/resources/js/dashboard/stats.js" type="text/javascript"></script>
</head>
<body>

    <div id="contstants" style="display:none" data-context-path="${rc.getContextPath()}"></div>

    <div class="header">
        <span>
            <a href="${rc.getContextPath()}/">Home</a>
        </span>
        |
        <span>
            <a href="${rc.getContextPath()}/discover">Discover</a>
        </span>
    </div>

    <div id="ratingDiagram" class="metricDiagram ratingDiagram">
        <svg></svg>
        <script type="text/javascript">
            setUpRatingChart();
        </script>
    </div>

    <div id="systemStats" class="systemData">
        <ol>
            <#list systemStats as entry>
                <li>${entry.key} - ${entry.value}</li>
            </#list>
        </ol>
    </div>

    <div>
        <div id="treeDemo" style="width: 500px;"></div>
        <script type="text/javascript">
            setUpTreeDemo();
        </script>
    </div>
</body>
</html>