package ru.hse.vectorizer.integration.domain;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.hse.vectorizer.domain.JdbcVectorRepository;
import ru.hse.vectorizer.domain.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.hse.vectorizer.utils.Constants.LOGIN_SYMBOLS;

@SpringBootTest(classes = {TestJdbcConfiguration.class, JdbcVectorRepository.class})
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class JdbcVectorRepositoryIT {

    @Autowired
    private JdbcVectorRepository repository;

    String login = new Random().ints(10, 0, LOGIN_SYMBOLS.length())
            .mapToObj(index -> LOGIN_SYMBOLS.charAt(index % LOGIN_SYMBOLS.length()))
            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
            .toString();

    @Test
    public void givenNewVector_whenSave_thenCreate() {
        Vector toSave = new Vector("test", 1, 4, 2);
        Optional<Vector> result = repository.save(login, toSave);
        assertEquals(toSave, result.get());
    }

    @Test
    public void givenVectorsOfDifferentUsers_whenRead_thenDoNotShowOthersVectors() {
        Vector toSave = new Vector("test", 1, 4, 2);
        repository.save(login, toSave);

        toSave = new Vector("other", 1, 4, 2);
        repository.save(login, toSave);

        Iterable<Vector> allVectors = repository.findAll("a");
        List<Vector> retrievedVectors = new ArrayList<>();
        allVectors.forEach(retrievedVectors::add);
        assertEquals(retrievedVectors.size(), 0);
    }

    @Test
    public void givenExistingVector_whenSave_thenUpdate() {
        Vector toSave = new Vector("test", 1, 4, 2);
        repository.save(login, toSave);
        Vector toUpdate = new Vector("test", 0, 5, -2);
        var result = repository.save(login, toUpdate);
        assertEquals(toUpdate, result.get());
    }

    @Test
    public void givenExistingVector_whenFindByName_thenFind() {
        Vector toSave = new Vector("test2", 1, 4, 2);
        repository.save(login, toSave);
        var result = repository.findByName(login, "test2");
        assertEquals(toSave, result.get());
    }

    @Test
    public void givenNotExistingVector_whenFindByName_thenEmpty() {
        var result = repository.findByName(login, "test2");
        assertTrue(result.isEmpty());
    }

    @Test
    public void givenSeveralVectors_whenFindAll_thenReturnAll() {
        List<Vector> vectorsToSave = new ArrayList<>();
        vectorsToSave.add(new Vector("vector1", 1, 2, 3));
        vectorsToSave.add(new Vector("vector2", 4, 5, 6));
        vectorsToSave.add(new Vector("vector3", 7, 8, 9));

        for (Vector vector : vectorsToSave) {
            repository.save(login, vector);
        }

        Iterable<Vector> allVectors = repository.findAll(login);
        List<Vector> retrievedVectors = new ArrayList<>();
        allVectors.forEach(retrievedVectors::add);

        for (Vector vector : vectorsToSave) {
            assertTrue(retrievedVectors.contains(vector));
        }

        assertEquals(vectorsToSave.size(), retrievedVectors.size());
    }

    @Test
    public void givenExistingVector_whenSaveAndDelete_thenVectorDeleted() {
        Vector toSave = new Vector("vectorToDelete", 1, 2, 3);
        repository.save(login, toSave);

        boolean deleteResult = repository.deleteByName(login, "vectorToDelete");
        assertTrue(deleteResult);

        Optional<Vector> findResult = repository.findByName(login, "vectorToDelete");
        assertTrue(findResult.isEmpty());
    }

    @Test
    public void givenNotExistingVector_whenDelete_thenVectorNotDeleted() {
        boolean deleteResult = repository.deleteByName(login, "nonExistentVector");
        assertFalse(deleteResult);
    }

    @Test
    public void givenPageOfVectors_whenFindPage_thenReturnCorrectPage() {
        Vector vector1 = new Vector("vector1", 1, 2, 3);
        Vector vector2 = new Vector("vector2", 4, 5, 6);
        Vector vector3 = new Vector("vector3", 7, 8, 9);
        Vector vector4 = new Vector("vector4", 5, 2, 1);

        repository.save(login, vector1);
        repository.save(login, vector2);
        repository.save(login, vector3);
        repository.save(login, vector4);

        int pageSize = 2;
        int pageNumber = 1;
        Iterable<Vector> page = repository.findPage(login, pageSize, pageNumber);

        List<Vector> pageList = new ArrayList<>();
        page.forEach(pageList::add);

        assertEquals(pageSize, pageList.size());
        assertTrue(pageList.contains(vector1));
        assertTrue(pageList.contains(vector2));
        assertFalse(pageList.contains(vector3));
        assertFalse(pageList.contains(vector4));

        pageNumber = 2;
        page = repository.findPage(login, pageSize, pageNumber);

        pageList.clear();
        page.forEach(pageList::add);

        assertEquals(pageSize, pageList.size());
        assertFalse(pageList.contains(vector1));
        assertFalse(pageList.contains(vector2));
        assertTrue(pageList.contains(vector3));
        assertTrue(pageList.contains(vector4));
    }

    @Test
    public void givenIncorrectPageNumber_whenFindPage_thenEmpty() {
        Vector vector1 = new Vector("vector1", 1, 2, 3);
        Vector vector2 = new Vector("vector2", 4, 5, 6);
        Vector vector3 = new Vector("vector3", 7, 8, 9);
        Vector vector4 = new Vector("vector4", 5, 2, 1);

        repository.save(login, vector1);
        repository.save(login, vector2);
        repository.save(login, vector3);
        repository.save(login, vector4);

        Iterable<Vector> page = repository.findPage(login, 2, 3);
        List<Vector> pageList = new ArrayList<>();
        page.forEach(pageList::add);
        assertTrue(pageList.isEmpty());
    }

    @Test
    public void givenEmptyRepository_whenGetNumberOfRows_thenZero() {
        int numberOfRows = repository.numberOfRows(login);
        assertEquals(0, numberOfRows);
    }

    @Test
    public void givenOneVectorSaved_whenGetNumberOfRows_thenOne() {
        Vector vector = new Vector("vector1", 1, 2, 3);
        repository.save(login, vector);

        int numberOfRows = repository.numberOfRows(login);
        assertEquals(1, numberOfRows);
    }

    @Test
    public void givenMultipleVectorsSaved_whenGetNumberOfRows_thenCorrectCount() {
        Vector vector1 = new Vector("vector1", 1, 2, 3);
        Vector vector2 = new Vector("vector2", 4, 5, 6);
        Vector vector3 = new Vector("vector3", 7, 8, 9);

        repository.save(login, vector1);
        repository.save(login, vector2);
        repository.save(login, vector3);

        int numberOfRows = repository.numberOfRows(login);
        assertEquals(3, numberOfRows);
    }

    @Test
    public void givenVectorsDeleted_whenGetNumberOfRows_thenCorrectCount() {
        Vector vector1 = new Vector("vector1", 1, 2, 3);
        Vector vector2 = new Vector("vector2", 4, 5, 6);
        Vector vector3 = new Vector("vector3", 7, 8, 9);

        repository.save(login, vector1);
        repository.save(login, vector2);
        repository.save(login, vector3);

        repository.deleteByName(login, "vector1");

        int numberOfRows = repository.numberOfRows(login);
        assertEquals(2, numberOfRows);
    }
}
