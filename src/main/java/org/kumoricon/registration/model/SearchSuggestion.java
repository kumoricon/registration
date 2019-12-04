package org.kumoricon.registration.model;

import java.util.Set;

/**
 * Represents a list of suggestions being sent back to the jQuery-Autocomplete library
 */
public class SearchSuggestion {
    private String query;
    private Set<String> suggestions;

    public SearchSuggestion(String query, Set<String> suggestions) {
        this.query = query;
        this.suggestions = suggestions;
    }

    public String getQuery() {
        return query;
    }

    public Set<String> getSuggestions() {
        return suggestions;
    }
}
