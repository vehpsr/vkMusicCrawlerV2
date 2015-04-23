package com.gans.vk.model.impl;

import java.text.MessageFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.gans.vk.model.AbstractModel;

@Entity
@Table(name="Groups")
public class Group extends AbstractModel {

    public enum GroupStatus {
        NOT_FOUND, PARSER_ERROR
    }

    public static final int NAME_MAX_LEN = 80;
    public static final int URL_MAX_LEN = 40;
    public static final int VK_ID_MAX_LEN = 20;

    @Column(length = NAME_MAX_LEN)
    private String _name;

    @Column(length = URL_MAX_LEN)
    private String _url;

    @Column(length = VK_ID_MAX_LEN)
    private String _vkId;

    private int _paginationStart;

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getUrl() {
        return _url;
    }

    public void setUrl(String url) {
        _url = url;
    }

    public String getVkId() {
        return _vkId;
    }

    public void setVkId(String vkId) {
        _vkId = vkId;
    }

    public int getPaginationStart() {
        return _paginationStart;
    }

    public void setPaginationStart(int paginationStart) {
        _paginationStart = paginationStart;
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0}: {1} {2} {3}, offset: {4}", getId(), _name, _url, _vkId, _paginationStart);
    }

}
