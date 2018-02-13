package com.carrdinal.network;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Peer {

    public final String address;
    public final int    port;

    public Peer(String address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public int hashCode(){
        return new HashCodeBuilder(101, 73)
                .append(address)
                .append(port)
                .toHashCode();
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Peer)) return false;
        if(o == this) return true;
        Peer other = (Peer) o;
        return new EqualsBuilder()
                .append(this.address, other.address)
                .append(this.port, other.port)
                .isEquals();
    }

    @Override
    public String toString() {
        return String.format("[%s:%s]", address, port);
    }

}
