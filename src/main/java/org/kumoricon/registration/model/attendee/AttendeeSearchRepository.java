package org.kumoricon.registration.model.attendee;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AttendeeSearchRepository {
    private final JdbcTemplate jdbcTemplate;

    public AttendeeSearchRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Searches for Attendees that contain all the given words in the firstName,
     * lastName, legalFirstName, legalLastName or fanName fields, case insensitive
     * @param searchWords Words to search for
     * @return Matching Attendees
     */
//    public List<Attendee> searchFor(String[] searchWords) {
//
//        CriteriaBuilder builder = em.getCriteriaBuilder();
//        CriteriaQuery<Attendee> query = builder.createQuery(Attendee.class);
//        Root<Attendee> root = query.from(Attendee.class);
//
//        List<Predicate> predicates = new ArrayList<>();
//
//        for (String word : searchWords) {
//            predicates.add(buildOrPredicatesForWord(builder, root, word));
//        }
//
//        Predicate allPredicates = builder.and(predicates.toArray(new Predicate[predicates.size()]));
//        query.where(allPredicates);
//
//        return em.createQuery(query.select(root)).setMaxResults(100).getResultList();
//    }

    /**
     * Creates a query predicate to search for the given word
     * @param builder Current CriteriaBuilder
     * @param root Root Attendee class
     * @param word Word to search for
     * @return Predicate word in firstName OR lastName OR fanName
     */
//    private Predicate buildOrPredicatesForWord(CriteriaBuilder builder, Root<Attendee> root, String word) {
//        Predicate hasFirstName = builder.like(root.get("firstName"), "%" + word + "%");
//        Predicate hasLastName = builder.like(root.get("lastName"), "%" + word + "%");
//        Predicate hasFanName = builder.like(root.get("fanName"), "%" + word + "%");
//        Predicate hasLegalFirstName = builder.like(root.get("legalFirstName"), "%" + word + "%");
//        Predicate hasLegalLastName = builder.like(root.get("legalLastName"), "%" + word + "%");
//        return builder.or(hasFirstName, hasLastName, hasFanName, hasLegalFirstName, hasLegalLastName);
//    }
}
