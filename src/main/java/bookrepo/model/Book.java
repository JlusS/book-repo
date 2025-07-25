package bookrepo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "books", uniqueConstraints = {
        @UniqueConstraint(columnNames = "isbn")
})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title must not be blank")
    @Size(max = 255)
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Author must not be blank")
    @Size(max = 255)
    @Column(nullable = false)
    private String author;

    @NotBlank(message = "ISBN must not be blank")
    @Size(max = 13)
    @Column(nullable = false, unique = true)
    private String isbn;

    @NotNull(message = "Price must not be null")
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false)
    private BigDecimal price;

    @Size(max = 1000)
    private String description;

    @Size(max = 255)
    private String coverImage;
}
