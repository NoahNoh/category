package com.noah.category.dto.req;

import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * [Category] 등록 요청 DTO_210811
 */
@Data
public class CategoryReqDTO implements Serializable {
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
     * 서브 카테고리 ID
     */
    @Digits(integer = 11, fraction = 0)
    private Integer subCategoryId;
}