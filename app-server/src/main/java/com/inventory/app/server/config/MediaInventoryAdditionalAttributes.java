package com.inventory.app.server.config;

public enum MediaInventoryAdditionalAttributes {
   CONSOLES("consoles"),
   NUMBER_OF_PLAYERS("numberOfPlayers"),
    RELEASE_DATE("releaseDate"),
    AUTHORS("authors"),
    COPYRIGHT_YEAR("copyright_year"),
    EDITION("edition"),
    DIRECTORS("directors"),
    ARTISTS("artists"),
    SONG_LIST("songList"),
    WRITERS("writers"),
    SEASON("season");

     private final String jsonKey;

     MediaInventoryAdditionalAttributes(String jsonKey) {
      this.jsonKey = jsonKey;
     }

     public String getJsonKey() {
      return jsonKey;
     }
}
