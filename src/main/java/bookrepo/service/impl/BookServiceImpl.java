package bookrepo.service.impl;

import bookrepo.dto.book.BookDto;
import bookrepo.dto.book.BookDtoWithoutCategoryIds;
import bookrepo.dto.book.BookSearchParameters;
import bookrepo.dto.book.CreateBookRequestDto;
import bookrepo.exception.EntityNotFoundException;
import bookrepo.mapper.BookMapper;
import bookrepo.model.Book;
import bookrepo.model.Category;
import bookrepo.repository.book.BookRepository;
import bookrepo.repository.book.BookSpecificationBuilder;
import bookrepo.repository.category.CategoryRepository;
import bookrepo.service.BookService;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;
    private final CategoryRepository categoryRepository;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(requestDto.getCategoryIds()));
        book.setCategories(categories);

        bookRepository.save(book);
        return bookMapper.toDto(book);
    }


    @Override
    public Page<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(bookMapper::toDto);
    }

    @Override
    public BookDto getById(Long id) {
        return bookMapper.toDto(bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id: " + id)
        ));
    }

    @Override
    public BookDto update(Long id, CreateBookRequestDto requestDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find book by id: " + id));

        bookMapper.updateModelFromDto(requestDto, book);
        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(requestDto.getCategoryIds()));
        book.setCategories(categories);

        bookRepository.save(book);
        return bookMapper.toDto(book);
    }


    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDto> search(BookSearchParameters params) {
        return bookRepository.findAll(bookSpecificationBuilder.build(params))
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public List<BookDtoWithoutCategoryIds> findAllByCategoryId(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Category not found with id: " + id));
        List<Book> books = bookRepository.findAllByCategories_Id(id);
        return books.stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }
}
