package com.noah.category.dto.req;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * [Category] 수정 요청 DTO_210811
 */
@Data
@NoArgsConstructor
public class CategoryEditReqDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 카테고리 명
     */
    @NotNull
    @Size(min=1,max=100)
    private String categoryName;

    /**
     * 카테고리 내용
     */
    @NotNull
    private String categoryDesc;

    /**
     * 서브 카테고리 정보
     */
    private SubCategoryEditReqDTO subCategory;
}