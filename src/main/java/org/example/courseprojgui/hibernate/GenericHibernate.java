package org.example.courseprojgui.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaQuery;

import java.util.ArrayList;
import java.util.List;

public class GenericHibernate {
    private EntityManagerFactory entityManagerFactory;

    public GenericHibernate(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public <T> void create(T entity) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(entity);
            entityManager.getTransaction().commit();
        }catch (Exception e) {

        } finally {
            if(entityManager != null) {
                entityManager.close();
            }
        }
    }

    public <T> void update(T entity) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(entity);
            entityManager.getTransaction().commit();
        }catch (Exception e) {

        } finally {
            if(entityManager != null) {
                entityManager.close();
            }
        }
    }

    public <T> List<T> getAllRecords(Class<T> className) {
        List<T> listOfRecords = new ArrayList<>();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            CriteriaQuery query = entityManager.getCriteriaBuilder().createQuery();
            query.select(query.from(className));
            Query q = entityManager.createQuery(query);
            listOfRecords = q.getResultList();
        }catch (Exception e) {

        } finally {
            if(entityManager != null) {
                entityManager.close();
            }
        }
        return listOfRecords;
    }
}