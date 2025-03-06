package dev.dixie.repository;

import dev.dixie.model.entity.ImagerUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    public void saveUser(ImagerUser imagerUser) {
        entityManager.persist(imagerUser);
    }

    public Optional<ImagerUser> findUserByEmail(String email) {
        var query = entityManager.createQuery(
                "SELECT i FROM ImagerUser i WHERE i.email = :email", ImagerUser.class);
        query.setParameter("email", email);
        var imagerUser = query.getSingleResult();
        return Optional.ofNullable(imagerUser);
    }
}
