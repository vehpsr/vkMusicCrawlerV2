// set up audio player
$(function() {
    // Setup the player to autoplay the next track
    var a = audiojs.createAll({
        trackEnded : function() {
            var next = $('ol li.playing').next();
            if (!next.length)
                next = $('ol li').first();
            next.addClass('playing').siblings().removeClass('playing');
            audio.load($('a', next).attr('data-src'));
            audio.play();
        }
    });

    // Load in the first track
    var audio = a[0];
    first = $('ol a').attr('data-src');
    $('ol li').first().addClass('playing');
    audio.load(first);
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
            audio.load($('a', this).attr('data-src'));
            audio.play();
        }
    });
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
});
