package gift.repository;

import gift.model.Wishlist;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByUserUsername(String username);
    List<Wishlist> findByUserUsernameAndHiddenFalse(String username); // 숨김 상태가 false인 항목만 가져오는 메서드 추가
    Page<Wishlist> findByUserUsernameAndHiddenFalse(String username, Pageable pageable); // 페이징 처리된 메서드 추가
}
