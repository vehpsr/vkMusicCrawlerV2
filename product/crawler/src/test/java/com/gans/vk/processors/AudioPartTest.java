package com.gans.vk.processors;

import static com.gans.vk.processors.VkUserAudioResponseProcessor.AudioPart.ARTIST;
import static com.gans.vk.processors.VkUserAudioResponseProcessor.AudioPart.TITLE;
import static com.gans.vk.processors.VkUserAudioResponseProcessor.AudioPart.URL;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import com.gans.vk.processors.VkUserAudioResponseProcessor.AudioPart;

public class AudioPartTest {

    @Test
    public void testUrlTransformation() {
        String google = "google.com";
        List<String> urls = Arrays.asList(
                "google.com",
                "google.com?",
                "google.com?someparam=value",
                "google.com?someparam=value?somethingelse"
                );
        for (String url : urls) {
            assertThat(url, URL.transform(url), is(google));
        }
    }

    @Test
    public void testArtistAndTitleNormalization() {
        testArtistAndTitleNormalization("ABCdef", "abcdef");
        testArtistAndTitleNormalization("a¶b♪c", "a?b?c");
        testArtistAndTitleNormalization("abc абв 123 !@#$%?(){}[]<>.,+=-_*", "abc абв 123 !@#$%?(){}[]<>.,+=-_*");
        testArtistAndTitleNormalization(" ¶ ♪ ", "");
        testArtistAndTitleNormalization("&#123;abc", "abc");
        testArtistAndTitleNormalization("a&#123;bc", "a&#123;bc");
        testArtistAndTitleNormalization("&#123;&12;abc", "abc");
        testArtistAndTitleNormalization(" &#123; &12; abc", "abc");
        testArtistAndTitleNormalization(" :!&#123; ?{}[]()' &12; abc", "abc");

        String veryLongString = UUID.randomUUID().toString() + UUID.randomUUID().toString();
        veryLongString += veryLongString + veryLongString + veryLongString;

        for (AudioPart part : new AudioPart[] {ARTIST, TITLE}) {
            String result = part.transform(veryLongString);
            assertThat(part.name() + " " + result.length(),
                    result.length() < veryLongString.length(), is(true));
            assertThat(part.name() + " - " + veryLongString + " - " + result,
                    veryLongString.startsWith(result), is(true));
        }
    }

    private void testArtistAndTitleNormalization(String input, String expected) {
        assertThat(ARTIST.transform(input), is(expected));
        assertThat(TITLE.transform(input), is(expected));
    }
}
