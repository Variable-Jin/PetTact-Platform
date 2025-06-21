package com.pettact.api.board.Category.dto;

import com.pettact.api.board.Category.BoardCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<BoardCategory, Long> {

    boolean existsByBoardCategoryTitle(String boardCategoryTitle);

}
