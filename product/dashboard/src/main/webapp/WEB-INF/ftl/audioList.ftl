<html>
<head>
    <meta charset="UTF-8" />
    <title>audio</title>
    <link rel="icon" type="image/png" href="${rc.getContextPath()}/favicon/favicon.ico">
    <link rel="stylesheet" href="${rc.getContextPath()}/resources/layout/nv.d3.css" type="text/css"></link>
    <link rel="stylesheet" href="${rc.getContextPath()}/resources/layout/index.css" type="text/css"></link>
    <link rel="stylesheet" href="${rc.getContextPath()}/resources/layout/audioList.css" type="text/css"></link>
    <script src="${rc.getContextPath()}/resources/js/jquery-2.1.3.js" type="text/javascript"></script>
    <script src="${rc.getContextPath()}/resources/js/d3.js" type="text/javascript"></script>
    <script src="${rc.getContextPath()}/resources/js/nv.d3.js" type="text/javascript"></script>
    <script src="${rc.getContextPath()}/resources/js/audiojs/audio.js" type="text/javascript"></script>
</head>
<body>

    <script src="${rc.getContextPath()}/resources/js/dashboard/audioList.js" type="text/javascript"></script>

    <div class="sidePanel">
        <div><!-- TODO remove hardcode -->
            <a href="//vk.com/${user.url}" target="_blank">${user.name}</a>
        </div>
        <div>
            <a href="${rc.getContextPath()}/">Home</a>
        </div>
        <div>
            <a href="${rc.getContextPath()}/discover">Discover</a>
        </div>
        <div>
            <a href="#" onclick="scrollToCurrentSong(event);">Focus</a>
        </div>
        <div>
            <a href="#" onclick="createRatingStatsChart(event);">Stats</a>
        </div>
    </div>

    <div class="wrapper">
        <div id="audioPlayer">
            <div class="currentSong">
                <span class="currentArtist"></span>
                &nbsp;&ndash;&nbsp;
                <span class="currentTitle"></span>
            </div>
            <audio></audio> <!-- audio player container -->
        </div>
        <ol class="audioList">
            <#list songs as song>
                <li>
                    <div data-src="${song.url}" class="songWrap">
                        <div class="titleWrap">
                            <span class="artist">${song.artist}</span>
                            -
                            <span class="title">${song.title}</span>
                        </div>
                        <div class="songMeta">
                            <span class="time">${song.time}</span>
                        </div>
                    </div>
                    <@rating song=song />
                </li>
            </#list>
        </ol>
    </div>

    <div id="userStats" style="display:none;" data-action="${rc.getContextPath()}/stats/user/${user.id?c}">
        <div class="close" onclick="$('#userStats svg').empty(); $('#userStats').hide();" >close</div>
        <svg></svg>
    </div>
</body>
</html>

<#macro rating song>
    <form class="stars" action="${rc.getContextPath()}/song/rate/${song.id?c}">
        <input type="radio" name="rating" id="star1_${song.id?c}" value="1">
        <label for="star1_${song.id?c}" class="star1" ><span><span></span></span></label>
        <input type="radio" name="rating" id="star2_${song.id?c}" value="2">
        <label for="star2_${song.id?c}" class="star2" ><span><span></span></span></label>
        <input type="radio" name="rating" id="star3_${song.id?c}" value="3">
        <label for="star3_${song.id?c}" class="star3" ><span><span></span></span></label>
        <input type="radio" name="rating" id="star4_${song.id?c}" value="4">
        <label for="star4_${song.id?c}" class="star4" ><span><span></span></span></label>
        <input type="radio" name="rating" id="star5_${song.id?c}" value="5">
        <label for="star5_${song.id?c}" class="star5" ><span><span></span></span></label>
    </form>
    <#if song.artistRateCount != 0>
        <div class="artistRatingStats artistRate_${(song.artistAvgRating * 2 - 1)?round}">
            <span class="artistRateCount">${song.artistRateCount}</span>
            /
            <span class="artistAvgRating">${song.artistAvgRating?string["0.#"]}</span>
        </div>
    </#if>
</#macro>
