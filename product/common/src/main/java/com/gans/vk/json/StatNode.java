package com.gans.vk.json;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class StatNode {

    private final String _key;
    private final List<StatNode> _nodes = new ArrayList<>();
    private String _val;

    public StatNode(String key) {
        _key = key;
    }

    public String getKey() {
        return _key;
    }

    public List<StatNode> getValues() {
        return _nodes;
    }

    public void addNode(StatNode node) {
        _nodes.add(node);
    }

    public String getVal() {
        return _val;
    }

    public void setVal(int val) {
        _val = MessageFormat.format("{0}", val);;
    }

    public void setVal(String val) {
        _val = val;
    }

}
