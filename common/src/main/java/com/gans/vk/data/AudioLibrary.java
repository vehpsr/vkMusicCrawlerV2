package com.gans.vk.data;

import java.util.*;
import java.util.Map.Entry;


public class AudioLibrary {

    public static final AudioLibrary EMPTY = new AudioLibrary("") {
        @Override
        @SuppressWarnings("unchecked")
        <T, E> Map<T, E> getArtistsDataStorage() {
            return (Map<T, E>) Collections.unmodifiableMap(super.getArtistsDataStorage());
        }
    };

    private final Map<String, Integer> _artistsCount = getArtistsDataStorage();
    private String _id;
    /** lazy initialization of total audio library count */
    private int _size = 0;

    public AudioLibrary(String id) {
        _id = id;
    }

    <T, E> Map<T, E> getArtistsDataStorage() {
        return new HashMap<T, E>();
    }

    public void put(String artist) {
        increment(artist, 1);
    }

    public void put(ArtistData entry) {
        increment(entry.getKey(), entry.getValue());
    }

    public void putAll(List<ArtistData> artistData) {
        for (ArtistData entry : artistData) {
            put(entry);
        }
    }

    private void increment(String artist, int amount) {
        Integer count = _artistsCount.get(artist);
        if (count == null) {
            count = 0;
        }
        count += amount;
        _artistsCount.put(artist, count);
    }

    public String getId() {
        return _id;
    }

    public boolean isEmpty() {
        return _artistsCount.isEmpty();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public List<ArtistData> getEntries() {
        List<ArtistData> result = new ArrayList<ArtistData>();
        for (Entry<String, Integer> entry : _artistsCount.entrySet()) {
            result.add(new ArtistData(entry));
        }
        Collections.sort(result);
        return result;
    }

    public Set<String> getUniqueArtists() {
        return _artistsCount.keySet();
    }

    public int getCount(String artist) {
        Integer count = _artistsCount.get(artist);
        return count == null ? 0 : count;
    }

    public int getUniqueEntriesCount() {
        return _artistsCount.size();
    }

    public int getTotalEntriesCount() {
        if (_size == 0 && !_artistsCount.isEmpty()) {
            for (Integer val : _artistsCount.values()) {
                _size += val;
            }
        }
        return _size;
    }
}
