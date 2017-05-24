package com.greensoft.secondlife._1;

/**
 * Created by zebul on 5/16/17.
 */

//TODO: add equality test
public class PeerId {

    private String id;
    public PeerId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PeerId peerId = (PeerId) o;

        return id.equals(peerId.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
