package com.inventory.app.server.service;

import com.inventory.app.server.entity.media.TelevisionShow;
import com.inventory.app.server.repository.IBaseDao;
import com.inventory.app.server.service.media.TelevisionService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.reset;

public class TelevisionServiceTest {
    @Mock
    private IBaseDao<TelevisionShow> daoMock;

    @InjectMocks
    private TelevisionService underTest;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @After
    public void tearDown() {
        // Reset mocks and clear any invocations
        reset(daoMock);
    }

    @Test
    public void setDao() {
        // GIVEN

        // WHEN

        // THEN
    }

    @Test
    public void getAllBooksByCollectionTitle() {
        // GIVEN

        // WHEN

        // THEN
    }

    @Test
    public void getAllTelevisionShowsByEpisode() {
        // GIVEN

        // WHEN

        // THEN
    }

    @Test
    public void getAllTelevisionShowsByGenre() {
        // GIVEN

        // WHEN

        // THEN
    }

    @Test
    public void getAllByUsername() {
        // GIVEN

        // WHEN

        // THEN
    }

    @Test
    public void getById() {
        // GIVEN

        // WHEN

        // THEN
    }

    @Test
    public void create() {
        // GIVEN

        // WHEN

        // THEN
    }

    @Test
    public void update() {
        // GIVEN

        // WHEN

        // THEN
    }

    @Test
    public void deleteById() {
        // GIVEN

        // WHEN

        // THEN
    }
}