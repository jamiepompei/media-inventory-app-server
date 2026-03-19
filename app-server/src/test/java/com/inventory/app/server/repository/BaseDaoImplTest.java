package com.inventory.app.server.repository;

import com.inventory.app.server.entity.media.Book;
import com.inventory.app.server.entity.media.Media;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class BaseDaoImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<Book> criteriaQuery;

    @Mock
    private Root<Book> root;

    @Mock
    private TypedQuery<Book> typedQuery;

    private BaseDaoImpl<Book> baseDaoImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        baseDaoImpl = new BaseDaoImpl<Book>(entityManager);
        baseDaoImpl.setClazz(Book.class);
    }

    @Test
    public void testFindOne() {
        // GIVEN
        long id = 1L;
        String username = "testUser";
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Book.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Book.class)).thenReturn(root);
        when(criteriaQuery.select(root)).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(new Book());

        // WHEN
        Object result = baseDaoImpl.findOne(id, username);

        // THEN
        assertNotNull(result);
        verify(entityManager, times(1)).createQuery(criteriaQuery);
    }

    @Test
    public void testFindById() {
        // GIVEN
        long id = 1L;
        Book expectedEntity = new Book();

        when(entityManager.find(Book.class, id)).thenReturn(expectedEntity);

        // WHEN
        Media result = baseDaoImpl.findById(id);

        // THEN
        assertEquals(expectedEntity, result);
        verify(entityManager, times(1)).find(Book.class, id);
    }

    @Test
    public void testCreateOrUpdate() {
        // GIVEN
        Book entity = new Book();
        when(entityManager.merge(entity)).thenReturn(entity);

        // WHEN
        Media result = baseDaoImpl.createOrUpdate(entity);

        // THEN
        assertEquals(entity, result);
        verify(entityManager, times(1)).merge(entity);
    }

    @Test
    public void testDelete() {
        // GIVEN
        Book entity = new Book();

        // WHEN
        baseDaoImpl.delete(entity);

        // THEN
        verify(entityManager, times(1)).remove(entity);
    }

    @Test
    public void testDeleteById() {
        // GIVEN
        long id = 1L;
        String username = "testUser";
        Book entity = new Book();
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Book.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Book.class)).thenReturn(root);
        when(criteriaQuery.select(root)).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(entity);

        // WHEN
        baseDaoImpl.deleteById(id, username);

        // THEN
        verify(entityManager, times(1)).remove(entity);
    }

    @Test
    public void testFindAll() {
        // GIVEN
        Book entity = new Book();
        List<Book> expectedList = Collections.singletonList(entity);
        when(entityManager.createQuery("from com.inventory.app.server.entity.media.Book", Book.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedList);

        // WHEN
        List<Book> result = baseDaoImpl.findAll();

        // THEN
        assertEquals(expectedList, result);
       verify(entityManager, times(1)).createQuery("from com.inventory.app.server.entity.media.Book", Book.class);
    }
}