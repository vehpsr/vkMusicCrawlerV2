package com.gans.vk.service.impl;

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.gans.vk.dao.SongDao;
import com.gans.vk.dao.SongDao.ArtistData;
import com.gans.vk.dao.SongDao.SongData;
import com.gans.vk.dao.SongDao.TopArtistsList;
import com.gans.vk.json.StatNode;
import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;
import com.gans.vk.service.SongService;

public class SongServiceImpl implements SongService {

    @SuppressWarnings("unused")
    private static final Log LOG = LogFactory.getLog(SongServiceImpl.class);

    @Autowired
    private SongDao _songDao;

    @Override
    public List<SongData> getAllUnratedSongs(User target, User user, int limit) {
        return _songDao.getAllUnratedSongs(target, user, limit);
    }

    @Override
    public Song get(long id) {
        return _songDao.get(id);
    }

    @Override
    public StatNode statisticsSongData(User user) {
        StatNode root = new StatNode("Total Songs");
        root.setVal(_songDao.countAll());

        StatNode myTopListNode = getTopArtistsDataFor(user, TopArtistsList.USER_LIST);
        root.addNode(myTopListNode);

        StatNode pplTopListNode = getTopArtistsDataFor(user, TopArtistsList.GLOBAL_LIST);
        root.addNode(pplTopListNode);

        return root;
    }

    private StatNode getTopArtistsDataFor(User user, TopArtistsList listOwner) {
        StatNode statNode = new StatNode(listOwner == TopArtistsList.USER_LIST ? "My top list" : "Ppl top list");
        int sum = 0;
        List<ArtistData> topData = _songDao.getTopArtistsData(user, listOwner);
        for (ArtistData data : topData) {
            StatNode artistNode = new StatNode(data.getArtist());
            artistNode.setVal(MessageFormat.format("{0} - {1}", data.getArtistCount(), data.getData()));
            statNode.addNode(artistNode);
            sum += data.getArtistCount();
        }
        statNode.setVal(sum);
        return statNode;
    }

    public void setSongDao(SongDao songDao) {
        _songDao = songDao;
    }

}
