package com.sololeveling.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages API calls to the OSRS Hiscores and Wiki
 */
@Slf4j
public class OsrsApiManager {
    private static final String HISCORE_ENDPOINT = "https://secure.runescape.com/m=hiscore_oldschool/index_lite.ws?player=";
    private static final String WIKI_API_ENDPOINT = "https://oldschool.runescape.wiki/api.php";
    private static final String USER_AGENT = "SoloLevelingPlugin/1.0";

    private final Gson gson = new Gson();

    /**
     * Get a player's hiscore data
     *
     * @param username the player's username
     * @return a map of skills to their level and experience
     */
    public Map<Skill, PlayerSkillData> getPlayerHiscores(String username) {
        Map<Skill, PlayerSkillData> skillData = new HashMap<>();

        try {
            URL url = new URL(HISCORE_ENDPOINT + username.replace(" ", "%20"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                int lineNumber = 0;

                // First line is overall
                reader.readLine();
                lineNumber++;

                // Parse skill data - format for each line is: rank,level,xp
                for (Skill skill : Skill.values()) {
                    if (skill == Skill.OVERALL) {
                        continue;
                    }

                    line = reader.readLine();
                    if (line == null) {
                        break;
                    }

                    lineNumber++;
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        int rank = parseInt(parts[0], -1);
                        int level = parseInt(parts[1], 1);
                        long xp = parseLong(parts[2], 0);

                        skillData.put(skill, new PlayerSkillData(rank, level, xp));
                    }

                    // OSRS Hiscores has a specific order, different from Skill enum
                    // If we reach the end of skills, break
                    if (lineNumber > 23) {
                        break;
                    }
                }

                reader.close();
            } else {
                log.warn("Failed to fetch hiscore data for player: {}, response code: {}", username, responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            log.error("Error fetching hiscore data", e);
        }

        return skillData;
    }

    /**
     * Search the OSRS Wiki for information
     *
     * @param searchTerm the term to search for
     * @return wiki search results
     */
    public WikiSearchResult searchWiki(String searchTerm) {
        WikiSearchResult result = new WikiSearchResult();

        try {
            String urlStr = WIKI_API_ENDPOINT + "?action=opensearch&search=" +
                            searchTerm.replace(" ", "%20") +
                            "&limit=5&namespace=0&format=json";

            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse the response (opensearch format)
                String[] searchResult = gson.fromJson(response.toString(), String[].class);
                if (searchResult.length >= 4) {
                    result.setSearchTerm(searchTerm);
                    result.setUrl("https://oldschool.runescape.wiki/w/" + searchResult[1].replace(" ", "_"));
                    result.setFound(searchResult[1].length() > 0);
                }
            } else {
                log.warn("Failed to search wiki, response code: {}", responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            log.error("Error searching wiki", e);
        }

        return result;
    }

    /**
     * Get detailed information about an item, monster, or other game entity
     *
     * @param entityName the name of the entity to look up
     * @return detailed wiki information
     */
    public WikiEntityInfo getWikiEntityInfo(String entityName) {
        WikiEntityInfo info = new WikiEntityInfo();
        info.setName(entityName);

        try {
            String urlStr = WIKI_API_ENDPOINT + "?action=query&prop=extracts&exintro&explaintext&titles=" +
                            entityName.replace(" ", "%20") +
                            "&format=json";

            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse the JSON response
                JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);
                JsonObject query = jsonObject.getAsJsonObject("query");
                if (query != null) {
                    JsonObject pages = query.getAsJsonObject("pages");
                    if (pages != null && pages.entrySet().size() > 0) {
                        // Get the first page (there should only be one)
                        JsonObject page = pages.entrySet().iterator().next().getValue().getAsJsonObject();

                        if (page != null) {
                            // Extract the description and other info
                            info.setDescription(page.has("extract") ? page.get("extract").getAsString() : "No description available");
                            info.setWikiUrl("https://oldschool.runescape.wiki/w/" + entityName.replace(" ", "_"));
                            info.setFound(true);
                        }
                    }
                }
            } else {
                log.warn("Failed to get wiki entity info, response code: {}", responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            log.error("Error getting wiki entity info", e);
        }

        return info;
    }

    /**
     * Get a wiki URL for a specific entity
     *
     * @param entityName the name of the entity
     * @return the wiki URL
     */
    public String getWikiUrl(String entityName) {
        return "https://oldschool.runescape.wiki/w/" + entityName.replace(" ", "_");
    }

    /**
     * Safely parse an integer
     */
    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Safely parse a long
     */
    private long parseLong(String value, long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Represents a player's skill data from the hiscores
     */
    public static class PlayerSkillData {
        private final int rank;
        private final int level;
        private final long experience;

        public PlayerSkillData(int rank, int level, long experience) {
            this.rank = rank;
            this.level = level;
            this.experience = experience;
        }

        public int getRank() {
            return rank;
        }

        public int getLevel() {
            return level;
        }

        public long getExperience() {
            return experience;
        }
    }

    /**
     * Represents a wiki search result
     */
    public static class WikiSearchResult {
        private String searchTerm;
        private String url;
        private boolean found;

        public String getSearchTerm() {
            return searchTerm;
        }

        public void setSearchTerm(String searchTerm) {
            this.searchTerm = searchTerm;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isFound() {
            return found;
        }

        public void setFound(boolean found) {
            this.found = found;
        }
    }

    /**
     * Represents detailed information about a game entity from the wiki
     */
    public static class WikiEntityInfo {
        private String name;
        private String description;
        private String wikiUrl;
        private boolean found;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getWikiUrl() {
            return wikiUrl;
        }

        public void setWikiUrl(String wikiUrl) {
            this.wikiUrl = wikiUrl;
        }

        public boolean isFound() {
            return found;
        }

        public void setFound(boolean found) {
            this.found = found;
        }
    }
}
