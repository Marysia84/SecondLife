package com.greensoft.secondlife._1;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zebul on 5/16/17.
 */

public class Peers {

    class PeerNotExistsExeption extends Exception{

    }

    private Map<PeerId, Peer> peers = new HashMap<>();

    public boolean contains(PeerId peerId) {
        return peers.containsKey(peerId);
    }

    public void add(Peer peer) {

        peers.put(peer.getId(), peer);
    }

    public Peer remove(PeerId peerId) throws PeerNotExistsExeption {

        Peer peer = getPeer(peerId);
        peers.remove(peerId);
        return peer;
    }

    public boolean isEmpty() {
        return peers.isEmpty();
    }

    public Peer getPeer(PeerId peerId) throws PeerNotExistsExeption {
        final Peer peer = peers.get(peerId);
        if(peer == null){

            throw new PeerNotExistsExeption();
        }
        return peer;
    }
}
