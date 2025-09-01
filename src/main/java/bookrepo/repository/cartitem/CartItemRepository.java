package bookrepo.repository.cartitem;

import bookrepo.model.CartItem;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Page<CartItem> findByShoppingCart_User_Id(Long userId, Pageable pageable);

    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.book WHERE ci.shoppingCart.id = :cartId")
    Set<CartItem> findByCartIdWithBook(@Param("cartId") Long cartId);

    Optional<CartItem> findByIdAndShoppingCartId(Long id, Long shoppingCartId);
}

