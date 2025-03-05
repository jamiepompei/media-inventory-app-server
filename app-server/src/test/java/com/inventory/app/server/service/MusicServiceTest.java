package com.inventory.app.server.service;

import com.inventory.app.server.entity.media.Music;
import com.inventory.app.server.repository.IBaseDao;
import com.inventory.app.server.service.media.MusicService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.reset;

public class MusicServiceTest {

    @Mock
    private IBaseDao<Music> daoMock;

    @InjectMocks
    private MusicService underTest;

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
    public void getAllMusicByArtist() {
        // GIVEN

        // WHEN

        // THEN
    }

    @Test
    public void getAllMusicByGenre(){
        // GIVEN

        // WHEN

        // THEN
    }

    @Test
    public void getAllMusicByCollectionTitle() {
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
    public void getById(){
        // GIVEN

        // WHEN

        // THEN

    }

    @Test
    public void create(){
        // GIVEN

        // WHEN

        // THEN
    }

    @Test
    public void update(){
        // GIVEN

        // WHEN

        // THEN
    }

    @Test
    public void deleteById(){
        // GIVEN

        // WHEN

        // THEN
    }
}
