package com.noah.category.dto.res;

import com.noah.category.domain.Category;
import com.noah.category.domain.CategoryBundle;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * [Notice] 목록 정보 DTO_210712
 */
@Data
@NoArgsConstructor
public class CategoryResDTO implements Serializable {
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
	 * 서브 카테고리 정보
	 */
	private List<SubCategoryResDTO> subCategory;

	/**
	 * 생성일자
	 */
	private Date createdAt;

	/**
	 * 수정일자
	 */
	private Date updatedAt;

	@Builder
	public CategoryResDTO(Category mainInfo, List<CategoryBundle> subInfo) {
		this.categoryId = mainInfo.getId();
		this.categoryName = mainInfo.getCategoryName();
		this.categoryDesc = mainInfo.getCategoryDesc();
		this.createdAt = mainInfo.getCreatedAt();
		this.updatedAt = mainInfo.getUpdatedAt();

		if(!ObjectUtils.isEmpty(subInfo)) {
			this.subCategory = subInfo.stream().map(
					n -> SubCategoryResDTO.builder().subInfo(n).build()
			).collect(Collectors.toList());
		}
	}
}