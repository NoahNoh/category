package com.noah.category.service;

import com.noah.category.dao.dsl.CategoryDslRepository;
import com.noah.category.exception.NoahException;
import com.noah.category.dao.dsl.CategoryDslRepository;
import com.noah.category.dto.ResultMsg;
import com.noah.category.dto.req.CategoryEditReqDTO;
import com.noah.category.dto.req.CategoryReqDTO;
import com.noah.category.dto.res.CategoryResDTO;
import com.noah.category.exception.NoahException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = { NoahException.class, Exception.class })
public class CategoryService {
	private final CategoryDslRepository categoryDslRepository;

	public CategoryService(CategoryDslRepository categoryDslRepository) {
		this.categoryDslRepository = categoryDslRepository;
	}

	/**
	 * Category: 정보 등록
	 * @param info  등록정보
	 * @return  등록완료 메시지
	 */
	public ResultMsg saveCategory(CategoryReqDTO info){
		this.categoryDslRepository.saveCategory(info);
		return this.setMsg("카테고리 정보가 등록 되었습니다.");
	}

	/**
	 * Category: 목록 조회
	 * @param categoryName  검색: 카테고리 명
	 * @return  카테고리 목록
	 */
	public List<CategoryResDTO> listCategory(String categoryName) {
		return this.categoryDslRepository.listCategoryBundle(categoryName);
	}

	/**
	 * Category: 단일 조회
	 * @param id  조회할 카테고리 ID
	 * @return  특정 카테고리 상세정보 조회
	 */
	public CategoryResDTO findCategory(int id) {
		return this.categoryDslRepository.findCategoryBundle(id);
	}

	/**
	 * Category: 정보 변경
	 * @param id  변경 요청할 Category ID
	 * @param info  변경 요청정보
	 * @return  변경완료 메시지
	 */
	public ResultMsg editCategory(int id, CategoryEditReqDTO info){
		this.categoryDslRepository.editCategory(id, info);
		return this.setMsg("카테고리 정보가 변경 되었습니다.");
	}

	/**
	 * Category: 정보 제거
	 * @param id  제거 요청할 Category ID
	 * @return  제거완료 메시지
	 */
	public ResultMsg deleteCategory(int id) {
		this.categoryDslRepository.deleteCategory(id);
		return this.setMsg("카테고리 정보가 삭제 되었습니다.");
	}

	/**
	 * 공통: 반환 메시지 세팅
	 * @param info  세팅할 메시지 정보
	 * @return  반환 메시지
	 */
	private ResultMsg setMsg(String info) {
		return ResultMsg.builder().info(info).build();
	}
}