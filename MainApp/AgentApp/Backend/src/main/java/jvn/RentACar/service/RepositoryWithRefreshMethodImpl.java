package jvn.RentACar.service;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;

public class RepositoryWithRefreshMethodImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
        implements RepositoryWithRefreshMethod<T, ID> {

    private final EntityManager entityManager;

    public RepositoryWithRefreshMethodImpl(JpaEntityInformation entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void refresh(T t) {
        entityManager.refresh(t);
    }
}
