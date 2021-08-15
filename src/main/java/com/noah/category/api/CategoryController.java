package com.noah.category.api;

import com.noah.category.dto.ResultCode;
import com.noah.category.dto.ResultMsg;
import com.noah.category.dto.req.CategoryEditReqDTO;
import com.noah.category.dto.req.CategoryReqDTO;
import com.noah.category.dto.res.CategoryResDTO;
import com.noah.category.exception.NoahException;
import com.noah.category.service.CategoryService;
import com.noah.category.util.NoahUtils;
import com.noah.category.util.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequestMapping(path="category")
@RestController
public class CategoryController {
	private final CategoryService categoryService;
	private final NoahUtils noahUtils;
	private final ValidatorUtils validatorUtils;

	public CategoryController(CategoryService categoryService, NoahUtils noahUtils, ValidatorUtils validatorUtils) {
		this.categoryService = categoryService;
		this.noahUtils = noahUtils;
		this.validatorUtils = validatorUtils;
	}

	@PostMapping
	public ResultMsg saveCategory(@RequestBody @Valid CategoryReqDTO info, BindingResult result, Errors errors) throws NoahException {
		if(result.hasErrors()) {
			throw new NoahException(ResultCode.NOAH_0001, "등록할 카테고리 등록정보 형태가 올바르지 않습니다.");
		}
		this.validateSaveInfo(info, errors);

		log.info(noahUtils.setDefaultLogMessage(info));
		return this.categoryService.saveCategory(info);
	}
	private void validateSaveInfo(CategoryReqDTO info, Errors errors) throws NoahException {
		validatorUtils.validate(info, errors);
		if(errors.hasErrors()) {
			//errors.getAllErrors().forEach(error -> log.error("[error] "+ error));
			throw new NoahException(ResultCode.NOAH_0001, "등록할 카테고리 등록정보 형식이 올바르지 않습니다.");
		}
	}

	@GetMapping
	public List<CategoryResDTO> listCategory(@RequestParam(required = false) String categoryName) {
		log.info(noahUtils.setDefaultLogMessage(""));
		return this.categoryService.listCategory(categoryName);
	}

	@GetMapping("{id}")
	public CategoryResDTO findCategory(@PathVariable int id) {
		log.info(noahUtils.setDefaultLogMessage(id));
		return this.categoryService.findCategory(id);
	}

	@PutMapping("{id}")
	public ResultMsg editCategory(@PathVariable int id, @RequestBody @Valid CategoryEditReqDTO info, BindingResult result, Errors errors) throws NoahException {
		if(result.hasErrors()) {
			throw new NoahException(ResultCode.NOAH_0001, "변경할 카테고리 형태가 올바르지 않습니다.");
		}
		this.validateEditInfo(info, errors);

		log.info(noahUtils.setDefaultLogMessage(info));
		return this.categoryService.editCategory(id, info);
	}
	private void validateEditInfo(CategoryEditReqDTO info, Errors errors) throws NoahException {
		validatorUtils.validate(info, errors);
		if(errors.hasErrors()) {
			//errors.getAllErrors().forEach(error -> log.error("[error] "+ error));
			throw new NoahException(ResultCode.NOAH_0001, "변경할 메인 카테고리 대표정보 형식이 올바르지 않습니다.");
		}

		if(info.getSubCategory() != null){
			validatorUtils.validate(info.getSubCategory(), errors);
			if(errors.hasErrors()) {
				//errors.getAllErrors().forEach(error -> log.error("[error] "+ error));
				throw new NoahException(ResultCode.NOAH_0001, "변경할 서브 카테고리 정보 형식이 올바르지 않습니다.");
			}
		}
	}

	@DeleteMapping("{id}")
	public ResultMsg deleteCategory(@PathVariable int id) throws NoahException {
		log.info(noahUtils.setDefaultLogMessage(id));
		return this.categoryService.deleteCategory(id);
	}
}