package com.game.repository;

import com.game.entity.Player;
import com.game.entity.PlayerSearchCriteria;
import org.springframework.data.domain.*;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
public class PlayerCriteriaRepository {


    private final EntityManager entityLocalManager;

    private final CriteriaBuilder criteriaBuilder;

    public PlayerCriteriaRepository(LocalContainerEntityManagerFactoryBean entityManager) {
        this.entityLocalManager = entityManager.getNativeEntityManagerFactory().createEntityManager();
        this.criteriaBuilder = entityLocalManager.getCriteriaBuilder();
    }

    // getAll -> return all players with pagination and filtering and sorting
    public Page<Player> findAllWithFilters(PlayerSearchCriteria playerSearchCriteria){
        CriteriaQuery<Player> criteriaQuery = criteriaBuilder.createQuery(Player.class);
        Root<Player> playerRoot = criteriaQuery.from(Player.class);
        Predicate predicate = getPredicate(playerSearchCriteria, playerRoot);
        criteriaQuery.where(predicate);
        criteriaQuery.orderBy(
                criteriaBuilder.asc(playerRoot.get(playerSearchCriteria.getOrder().getFieldName())));

        TypedQuery<Player> typedQuery = entityLocalManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(playerSearchCriteria.getPageNumber() * playerSearchCriteria.getPageSize());
        typedQuery.setMaxResults(playerSearchCriteria.getPageSize());

        Pageable pageable = getPageable(playerSearchCriteria);

        long playersCount = getPlayersCount(predicate);

        return new PageImpl<>(typedQuery.getResultList(),
                pageable,
                playersCount);
    }

    private Predicate getPredicate(PlayerSearchCriteria params, Root<Player> playerRoot) {
        List<Predicate> predicates = new ArrayList<>();

        //name criteria
        if(Objects.nonNull(params.getName())){
            predicates.add(
                    criteriaBuilder.like(playerRoot.get("name"),
                            "%" + params.getName() + "%")
            );
        }
        //title criteria
        if(Objects.nonNull(params.getTitle())){
            predicates.add(
                    criteriaBuilder.like(playerRoot.get("title"),
                            "%" + params.getTitle() + "%")
            );
        }
        //birthdate criteria for after
        if(Objects.nonNull(params.getAfter())){
            System.out.println(playerRoot.get("birthday"));
            predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(playerRoot.get("birthday"),
                            new Date(params.getAfter()))
            );
        }
        if(Objects.nonNull(params.getBefore())){
            predicates.add(
                    criteriaBuilder.lessThanOrEqualTo(playerRoot.get("birthday"),
                            new Date(params.getBefore()))
            );
        }

        //race criteria
        if(Objects.nonNull(params.getRace())){
            predicates.add(
                    criteriaBuilder.equal(playerRoot.get("race"),
                            params.getRace())
            );
        }
        //profession criteria
        if(Objects.nonNull(params.getProfession())){
            predicates.add(
                    criteriaBuilder.equal(playerRoot.get("profession"),
                            params.getProfession())
            );
        }
        //banned criteria
        if(Objects.nonNull(params.getBanned())){
            predicates.add(
                    criteriaBuilder.equal(playerRoot.get("banned"),
                            params.getBanned())
            );
        }
        //experience criteria
        if(Objects.nonNull(params.getMinExperience())){
            predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(playerRoot.get("experience"),
                            params.getMinExperience())
            );
        }
        if(Objects.nonNull(params.getMaxExperience())){
            predicates.add(
                    criteriaBuilder.lessThanOrEqualTo(playerRoot.get("experience"),
                            params.getMaxExperience())
            );
        }
        // level criteria
        if(Objects.nonNull(params.getMinLevel())){
            predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(playerRoot.get("level"),
                            params.getMinLevel())
            );
        }
        if(Objects.nonNull(params.getMaxLevel())){
            predicates.add(
                    criteriaBuilder.lessThanOrEqualTo(playerRoot.get("level"),
                            params.getMaxLevel())
            );
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private Pageable getPageable(PlayerSearchCriteria playerSearchCriteria) {
        Sort sort = Sort.by(Sort.Direction.ASC, playerSearchCriteria.getOrder().getFieldName());
        return PageRequest.of(playerSearchCriteria.getPageNumber(),playerSearchCriteria.getPageSize(), sort);
    }

    private long getPlayersCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Player> countRoot = countQuery.from(Player.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityLocalManager.createQuery(countQuery).getSingleResult();
    }

    // to return total players count according to applied filters
    public Integer findAllWithFiltersCount(PlayerSearchCriteria playerSearchCriteria) {

        CriteriaQuery<Player> criteriaQuery = criteriaBuilder.createQuery(Player.class);
        Root<Player> playerRoot = criteriaQuery.from(Player.class);
        Predicate predicate = getPredicate(playerSearchCriteria, playerRoot);
        criteriaQuery.where(predicate);
        TypedQuery<Player> typedQuery = entityLocalManager.createQuery(criteriaQuery);

        return typedQuery.getResultList().size();

    }
}
