// set up audio player
$(function() {
    // Setup the player to autoplay the next track
    var a = audiojs.createAll({
        trackEnded : function() {
            var next = $('ol li.playing').next();
            if (!next.length)
                next = $('ol li').first();
            next.addClass('playing').siblings().removeClass('playing');
            audio.load($('.songWrap', next).attr('data-src'));
            audio.play();
        }
    });

    // Load in the first track
    var audio = a[0];
    var first = $('ol .songWrap').attr('data-src');
    $('ol li').first().addClass('playing');
    audio.load(first);
    updateCurrentTrackInfo();
    // Load track or pause if already playing
    $('ol li').click(function(e) {
        e.preventDefault();
        if ($(this).hasClass('playing')) {
            if ($('#audiojs_wrapper0').hasClass('playing')) {
                audio.pause();
            } else {
                audio.play();
            }
        } else {
            $(this).addClass('playing').siblings().removeClass('playing');
            audio.load($('.songWrap', this).attr('data-src'));
            audio.play();
            updateCurrentTrackInfo();
        }
    });

    function updateCurrentTrackInfo() {
        var artistMaxLen = 30;
        var titleMaxLen = 40;
        var separator = " - ";
        var artist = $('ol li.playing .songWrap .artist').text();
        var title = $('ol li.playing .songWrap .title').text();
        while (artist.length + title.length + separator.length > artistMaxLen + titleMaxLen) {
            if (artist.length > artistMaxLen) {
                artist = artist.substring(0, artistMaxLen);
                continue;
            }
            if (title.length + separator.length > titleMaxLen) {
                title = title.substring(0, titleMaxLen - separator.length);
                continue;
            }
            break;
        }
        console.log(artist);
        console.log(title);
        $('#audioPlayer .currentSong').text(artist + separator + title);
    }
});

//set up rating system
$(function() {
    $('ol li .stars').click(function(e) {
        var songId = $(this).parent('li').data('songId');
        var url = $(this).attr('action');
        var rating = $(this).find('input[type="radio"]:checked').val();
        $.ajax({
            url: url + "/" + songId,
            method: 'POST',
            data: JSON.stringify({value: rating}),
            beforeSend: function(xhr) {
                xhr.setRequestHeader("Accept", "application/json");
                xhr.setRequestHeader("Content-Type", "application/json");
            },
            success: function(d) {
                console.log(d);
            }

        });

        e.stopPropagation();
    });

    // deselect all radio buttons
    $('input[type="radio"]').prop('checked', false);
});
