package com.gans.vk.json;

import java.util.ArrayList;
import java.util.List;

public class StatNode {

    private final String _key;
    private final List<StatNode> _nodes = new ArrayList<>();
    private Integer _val;

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

    public Integer getVal() {
        return _val;
    }

    public void setVal(Integer val) {
        _val = val;
    }

}
