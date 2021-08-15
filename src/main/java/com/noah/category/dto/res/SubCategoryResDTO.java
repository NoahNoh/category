package com.noah.category.dto.res;

import com.noah.category.domain.CategoryBundle;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * [Notice] 목록 정보 DTO_210712
 */
@Data
@NoArgsConstructor
public class SubCategoryResDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 카테고리 ID
	 */
	private int categoryId;

	/**
	 * 카테고리 명
	 */
	private String categoryName;

	/**
	 * 카테고리 설명
	 */
	private String categoryDesc;

	/**
	 * 생성일자
	 */
	private Date createdAt;

	/**
	 * 수정일자
	 */
	private Date updatedAt;

	@Builder
	public SubCategoryResDTO(CategoryBundle subInfo) {
		this.categoryId = subInfo.getSubCategory().getId();
		this.categoryName = subInfo.getSubCategory().getCategoryName();
		this.categoryDesc = subInfo.getSubCategory().getCategoryDesc();
		this.createdAt = subInfo.getSubCategory().getCreatedAt();
		this.updatedAt = subInfo.getSubCategory().getUpdatedAt();
	}
}