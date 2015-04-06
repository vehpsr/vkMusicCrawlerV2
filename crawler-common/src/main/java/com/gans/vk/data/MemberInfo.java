package com.gans.vk.data;

public class MemberInfo {

    private String _name;
    private String _url;
    private String _id;

    public String name() {
        return _name;
    }
    public MemberInfo name(String name) {
        _name = name;
        return this;
    }
    public String url() {
        return _url;
    }
    public MemberInfo url(String url) {
        _url = url;
        return this;
    }
    public String id() {
        return _id;
    }
    public MemberInfo id(String id) {
        _id = id;
        return this;
    }

}
