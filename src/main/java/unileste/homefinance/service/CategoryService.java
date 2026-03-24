package unileste.homefinance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import unileste.homefinance.domain.entity.Category;
import unileste.homefinance.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        log.info("findAll() - [START]");
        List<Category> categories = categoryRepository.findAll();
        log.info("findAll() - [END] - Found {} categories", categories.size());
        return categories;
    }
}
