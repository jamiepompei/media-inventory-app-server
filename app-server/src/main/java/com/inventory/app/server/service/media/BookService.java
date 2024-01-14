package com.inventory.app.server.service.media;

import com.inventory.app.server.entity.media.Book;
import com.inventory.app.server.repository.IGenericExtendedDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class BookService {
    private IGenericExtendedDao<Book, Long> dao;

    @Autowired
    public void setDao(IGenericExtendedDao<Book, Long> daoToSet) {
        dao = daoToSet;
        dao.setClazz(Book.class);
    }

    @Transactional
    public List<Book> getAllBooksByCollectionTitle(String collectionTitle) {
      return  dao.findByAttributeContainsText("title", collectionTitle);
    }

    @Transactional
    public List<Book> getAllBooksByAuthor(String author){
        return dao.findByAttributeContainsText("author", author);
    }

    @Transactional
    public List<Book> getAllBooksByGenre(String genre){
        return dao.findByAttributeContainsText("genre", genre);
    }
}
