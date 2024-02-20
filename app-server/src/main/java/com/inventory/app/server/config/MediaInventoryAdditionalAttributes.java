package com.inventory.app.server.config;

public enum MediaInventoryAdditionalAttributes {
    CONSOLES("consoles"),
    NUMBER_OF_PLAYERS("numberOfPlayers"),
    RELEASE_YEAR("releaseYear"),
    AUTHORS("authors"),
    COPYRIGHT_YEAR("copyrightYear"),
    EDITION("edition"),
    DIRECTORS("directors"),
    ARTISTS("artists"),
    SONG_LIST("songList"),
    EPISODES("episodes"),
    SEASON("season");

     private final String jsonKey;

     MediaInventoryAdditionalAttributes(String jsonKey) {
      this.jsonKey = jsonKey;
     }

     public String getJsonKey() {
      return jsonKey;
     }
}
