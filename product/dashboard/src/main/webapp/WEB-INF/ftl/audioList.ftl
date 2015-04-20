<html>
<head>
    <meta charset="UTF-8" />
    <title>audio</title>
    <link rel="icon" type="image/png" href="${rc.getContextPath()}/favicon/favicon.ico">
    <link rel="stylesheet" href="${rc.getContextPath()}/resources/layout/index.css" type="text/css"></link>
    <link rel="stylesheet" href="${rc.getContextPath()}/resources/layout/audioList.css" type="text/css"></link>
    <script src="${rc.getContextPath()}/resources/js/jquery-2.1.3.js" type="text/javascript"></script>
    <script src="${rc.getContextPath()}/resources/js/audiojs/audio.js" type="text/javascript"></script>
</head>
<body>

    <script src="${rc.getContextPath()}/resources/js/dashboard/audioList.js" type="text/javascript"></script>

    <div class="sidePanel">
        <div>
            <a href="${rc.getContextPath()}/">Home</a>
        </div>
        <div>
            <a href="${rc.getContextPath()}/discover">Discover</a>
        </div>
        <div>
            <a href="#" onclick="scrollToCurrentSong(event);">Focus</a>
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
                    <form class="stars" action="${rc.getContextPath()}/song/rate/${song.id?c}">
                        <input type="radio" name="rating" id="star1" value="1">
                        <input type="radio" name="rating" id="star2" value="2">
                        <input type="radio" name="rating" id="star3" value="3">
                        <input type="radio" name="rating" id="star4" value="4">
                        <input type="radio" name="rating" id="star5" value="5">
                    </form>
                    <#if song.artistRateCount != 0>
                        <div class="artistRatingStats artistRate_${(song.artistAvgRating * 2 - 1)?round}">
                            <span class="artistRateCount">${song.artistRateCount}</span>
                            /
                            <span class="artistAvgRating">${song.artistAvgRating?string["0.#"]}</span>
                        </div>
                    </#if>
                </li>
            </#list>
        </ol>
    </div>
</body>
</html>
