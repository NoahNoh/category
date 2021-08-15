package com.noah.category.dto.req;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class SubCategoryEditReqDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 편집 이벤트 코드
     */
    @NotNull
    private String reqEventCd;

    /**
     * 서브 카테고리 ID
     */
    @Positive
    private int subCategoryId;
}
