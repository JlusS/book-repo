package bookrepo.dto;

public record BookSearchParameters(String[] authors,
                                   String[] titles,
                                   String[] isbns,
                                   String[] descriptions,
                                   String[] coverImages,
                                   String[] prices) {
}
