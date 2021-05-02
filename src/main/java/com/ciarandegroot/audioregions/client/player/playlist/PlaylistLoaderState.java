package com.ciarandegroot.audioregions.client.player.playlist;

abstract class PlaylistLoaderState {
    // for both enter and update, if state changes, return new state, else return null
    abstract PlaylistLoaderState enter();
    abstract PlaylistLoaderState update();
    abstract void cancel();
}
