package org.kumoricon.registration.model;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class AutoSuggestRepository {
    protected final NamedParameterJdbcTemplate jdbcTemplate;

    public AutoSuggestRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected MapSqlParameterSource searchStringToQueryTerms(String search) {
        String[] terms = search.trim().split(" ", 2);

        MapSqlParameterSource params = new MapSqlParameterSource();

        for (int i = 0; i < terms.length; i++) {
            params.addValue("term" + i, terms[i]);
        }
        return params;
    }

    protected List<String> queryForTerms(String sql, MapSqlParameterSource parameters) {
        List<String> results;
        try {
            results = jdbcTemplate.queryForList(sql, parameters, String.class);
        } catch (EmptyResultDataAccessException e) {
            results = new ArrayList<>();
        }
        return results;
    }

    @SafeVarargs
    final protected TreeSet<String> combineResults(List<String>... searchResults) {
        TreeSet<String> output = new TreeSet<>();
        for (List<String> result : searchResults) {
            result.forEach(s -> output.add(s.strip()));      // We have at least one person that goes by a single name.
                                                             // This is here so their name doesn't get an extra trailing
                                                             // space from the concatenation that happens in the
                                                             // SQL query
        }
        return output;
    }
}
