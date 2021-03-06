<html>
<head>
    <meta charset="UTF-8" />
    <title>users</title>
    <link rel="icon" type="image/png" href="${rc.getContextPath()}/favicon/favicon.ico">
    <link rel="stylesheet" href="${rc.getContextPath()}/resources/layout/index.css" type="text/css"></link>
    <link rel="stylesheet" href="${rc.getContextPath()}/resources/layout/userList.css" type="text/css"></link>
    <script src="${rc.getContextPath()}/resources/js/jquery-2.1.3.js" type="text/javascript"></script>
</head>
<body>
    <div class="header">
        <span>
            <a href="${rc.getContextPath()}/stats">Stats</a>
        </span>
        |
        <span>
            <a href="${rc.getContextPath()}/discover">Discover</a>
        </span>
    </div>

    <div class="wrapper">
        <div class="userTable">
            <div class="userTableHeader">
                <div class="userTableColumn userColumn">name</div>
                <div class="userTableColumn audioColumn">stats</div>
            </div>
            <div class="separator"></div>
            <ol class="userTableBody">
                <#list users as user>
                    <li class="userTableRow">
                        <div class="userTableColumn userColumn">
                            <a href="${vkDomainUrl}${user.url}" target="_blank">${user.name}</a>
                        </div>
                        <div class="userTableColumn audioColumn">
                            <a href="${rc.getContextPath()}/audio/${user.vkId}">
                                <#if user_has_next>
                                    ${user.rating?string["0.###"]} / ${user.ratedAudioCount} / ${user.totalAudioCount}
                                <#else>
                                    Random User
                                </#if>
                            </a>
                        </div>
                    </li>
                </#list>
            </ol>
        </div>
    </div>
</body>
</html>