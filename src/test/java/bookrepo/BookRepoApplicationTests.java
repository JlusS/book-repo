package bookrepo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class BookRepoApplicationTests {
    @Test
    void contextLoads() {
        System.out.println("Starting context load test...");
    }

}
