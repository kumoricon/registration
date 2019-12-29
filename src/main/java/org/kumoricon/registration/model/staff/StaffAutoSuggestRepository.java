package org.kumoricon.registration.model.staff;

import org.kumoricon.registration.model.AutoSuggestRepository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
public class StaffAutoSuggestRepository extends AutoSuggestRepository {
    public StaffAutoSuggestRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    /**
     * Returns names and legal names that begin with the word(s) in the search query.
     * @param search May contain one or more words separated by spaces
     * @return Sorted set of names
     */
    @Transactional(readOnly = true)
    public Set<String> findNamesLike(String search) {
        if (search == null || search.isBlank()) {
            return new TreeSet<>();
        }
        MapSqlParameterSource searchTerm = searchStringToQueryTerms(search);

        final String nameSingleSQL = "SELECT first_name || ' ' || last_name as name from staff WHERE deleted = FALSE AND first_name ILIKE :term0 || '%' OR last_name ILIKE :term0 || '%' LIMIT 15";
        final String nameMultiSQL = "SELECT first_name || ' ' || last_name as name from staff WHERE deleted = FALSE AND first_name ILIKE :term0 || '%' AND last_name ILIKE :term1 || '%' LIMIT 15";
        final String legalSingleSQL = "SELECT legal_first_name || ' ' || legal_last_name as name from staff WHERE deleted = FALSE AND legal_first_name ILIKE :term0 || '%' OR legal_last_name ILIKE :term0 || '%' LIMIT 15";
        final String legalMultiSQL = "SELECT legal_first_name || ' ' || legal_last_name as name from staff WHERE deleted = FALSE AND legal_first_name ILIKE :term0 || '%' AND legal_last_name ILIKE :term1 || '%' LIMIT 15";

        List<String> names;
        List<String> legalNames;
        if (searchTerm.getParameterNames() != null && searchTerm.getParameterNames().length > 1) {
            names = queryForTerms(nameMultiSQL, searchTerm);
            legalNames = queryForTerms(legalMultiSQL, searchTerm);
        } else {
            names = queryForTerms(nameSingleSQL, searchTerm);
            legalNames = queryForTerms(legalSingleSQL, searchTerm);
        }

        return combineResults(names, legalNames);
    }

}
