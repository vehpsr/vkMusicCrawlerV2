package com.gans.vk.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class AbstractModel implements Model {

    private Long _id;

    @Id
    @Basic
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Override
    public Long getId() {
        return _id;
    }

    protected void setId(Long id) {
        _id = id;
    }

    @Override
    public int hashCode() {
        if (_id == null) {
            return super.hashCode();
        }
        final int prime = 31;
        int result = 1;
        result = prime * result + _id.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (_id == null) {
            return false;
        }
        AbstractModel that = (AbstractModel) obj;
        if (!_id.equals(that._id)) {
            return false;
        }
        return true;
    }

    @Transient
    public boolean isPersistent() {
        return (_id != null);
    }
}
