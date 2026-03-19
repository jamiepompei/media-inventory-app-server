package com.inventory.app.server.factory;

import com.inventory.app.server.service.media.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.ObjectProvider;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ServiceFactoryTest {
    private ServiceFactory serviceFactory;
    private ObjectProvider<BookService> bookServiceProvider;
    private ObjectProvider<MovieService> movieServiceProvider;
    private ObjectProvider<VideoGameService> videoGameServiceProvider;
    private ObjectProvider<MusicService> musicServiceProvider;
    private ObjectProvider<TelevisionService> televisionServiceProvider;

    @Before
    public void setUp() {
        bookServiceProvider = mock(ObjectProvider.class);
        movieServiceProvider = mock(ObjectProvider.class);
        videoGameServiceProvider = mock(ObjectProvider.class);
        musicServiceProvider = mock(ObjectProvider.class);
        televisionServiceProvider = mock(ObjectProvider.class);

        serviceFactory = new ServiceFactory(
                movieServiceProvider,
                bookServiceProvider,
                videoGameServiceProvider,
                musicServiceProvider,
                televisionServiceProvider
        );
    }

    @Test
    public void testGetService_ValidEntityType_shouldGetBookService() {
        // GIVEN
        BookService bookService = mock(BookService.class);
        when(bookServiceProvider.getIfAvailable()).thenReturn(bookService);

        // WHEN
        BaseService<?> result = serviceFactory.getService("book");

        // THEN
        assertNotNull(result);
        assertEquals(bookService, result);
        verify(bookServiceProvider, times(1)).getIfAvailable();
    }

    @Test
    public void testGetService_ValidEntityType_shouldGetMovieService() {
        // GIVEN
        MovieService movieService = mock(MovieService.class);
        when(movieServiceProvider.getIfAvailable()).thenReturn(movieService);

        // WHEN
        BaseService<?> result = serviceFactory.getService("movie");

        // THEN
        assertNotNull(result);
        assertEquals(movieService, result);
        verify(movieServiceProvider, times(1)).getIfAvailable();
    }

    @Test
    public void testGetService_ValidEntityType_shouldGetTelevisionShowService() {
        // GIVEN
        TelevisionService televisionService = mock(TelevisionService.class);
        when(televisionServiceProvider.getIfAvailable()).thenReturn(televisionService);

        // WHEN
        BaseService<?> result = serviceFactory.getService("televisionShow");

        // THEN
        assertNotNull(result);
        assertEquals(televisionService, result);
        verify(televisionServiceProvider, times(1)).getIfAvailable();
    }

    @Test
    public void testGetService_ValidEntityType_shouldGetVideoGameService() {
        // GIVEN
        VideoGameService videoGameService = mock(VideoGameService.class);
        when(videoGameServiceProvider.getIfAvailable()).thenReturn(videoGameService);

        // WHEN
        BaseService<?> result = serviceFactory.getService("videoGame");

        // THEN
        assertNotNull(result);
        assertEquals(videoGameService, result);
        verify(videoGameServiceProvider, times(1)).getIfAvailable();
    }

    @Test
    public void testGetService_ValidEntityType_shouldGetMusicService() {
        // GIVEN
        MusicService musicService = mock(MusicService.class);
        when(musicServiceProvider.getIfAvailable()).thenReturn(musicService);

        // WHEN
        BaseService<?> result = serviceFactory.getService("music");

        // THEN
        assertNotNull(result);
        assertEquals(musicService, result);
        verify(musicServiceProvider, times(1)).getIfAvailable();
    }

    @Test
    public void testGetService_InvalidEntityType() {
        // GIVEN
        String invalidEntityType = "invalidType";

        // WHEN & THEN
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            serviceFactory.getService(invalidEntityType);
        });
        assertEquals("No service found for entity type " + invalidEntityType, exception.getMessage());
    }

    @Test
    public void testGetService_NullProvider() {
        // GIVEN
        when(bookServiceProvider.getIfAvailable()).thenReturn(null);

        // WHEN
        BaseService<?> result = serviceFactory.getService("book");

        // THEN
        assertNull(result);
        verify(bookServiceProvider, times(1)).getIfAvailable();
    }

}