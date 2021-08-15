package com.noah.category.dao.dsl;

import com.noah.category.dao.CategoryBundleRepository;
import com.noah.category.dao.CategoryRepository;
import com.noah.category.domain.Category;
import com.noah.category.domain.CategoryBundle;
import com.noah.category.domain.QCategory;
import com.noah.category.domain.QCategoryBundle;
import com.noah.category.dto.ReqEventCd;
import com.noah.category.dto.ResultCode;
import com.noah.category.dto.req.CategoryEditReqDTO;
import com.noah.category.dto.req.CategoryReqDTO;
import com.noah.category.dto.req.SubCategoryEditReqDTO;
import com.noah.category.dto.res.CategoryResDTO;
import com.noah.category.exception.NoahException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAUpdateClause;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Transactional(rollbackFor = { NoahException.class, Exception.class })
public class CategoryDslRepository {

    @PersistenceContext private EntityManager entityManager;
    private final QCategory qCategory;
    private final QCategoryBundle qCategoryBundle;
    private final CategoryRepository categoryRepository;
    private final CategoryBundleRepository categoryBundleRepository;

    public CategoryDslRepository(EntityManager entityManager, CategoryRepository categoryRepository, CategoryBundleRepository categoryBundleRepository) {
        this.entityManager = entityManager;
        this.categoryRepository = categoryRepository;
        this.categoryBundleRepository = categoryBundleRepository;
        this.qCategory = QCategory.category;
        this.qCategoryBundle = QCategoryBundle.categoryBundle;
    }

    /**
     * Category: 카테고리 등록
     * @param reqInfo  카테고리 등록정보
     */
    public void saveCategory(CategoryReqDTO reqInfo) {
        // 1.카테고리 대표정보 등록
        Category saveCategoryInfo = Optional.ofNullable(this.categoryRepository.save(
            Category.builder()
                    .info(reqInfo)
                    .build()
        )).orElseThrow(() -> new NoahException(ResultCode.NOAH_0001, "카테고리 정보 등록 도중 에러가 발생했습니다."));

        // 2.카테고리 번들정보 등록
        if(!ObjectUtils.isEmpty(reqInfo.getSubCategoryId())) {
            this.isCategory(reqInfo.getSubCategoryId());
            this.saveCategoryBundle(saveCategoryInfo.getId(), reqInfo.getSubCategoryId());
        }
    }

    /**
     * CategoryBundle: 카테고리 단일정보 조회 (+서브 카테고리 정보)
     * @param id  조회할 카테고리 ID
     * @return  카테고리 단일정보 (+서브 카테고리 정보)
     */
    public CategoryResDTO findCategoryBundle(int id) {
        JPAQuery<Category> query = new JPAQuery<>(entityManager);
        Category fetchOne = Optional.ofNullable(query.select(qCategory)
                .from(qCategory)
                .where(qCategory.id.eq(id))
                .fetchOne()
        ).orElseThrow(() -> new NoahException(ResultCode.NOAH_0001, "유효하지 않은 카테고리 정보 입니다."));

        return CategoryResDTO.builder()
                .mainInfo(fetchOne)
                .subInfo(this.findSubCategory(id))
                .build();
    }

    /**
     * Category: 목록 조회
     * @param categoryName  검색: 카테고리 명
     * @return  카테고리 전체목록
     */
    public List<CategoryResDTO> listCategoryBundle(String categoryName) {
        JPAQuery<List<Category>> query = new JPAQuery<>(entityManager);
        BooleanBuilder builders = this.eqSearch(categoryName);

        List<Category> fetch = Optional.ofNullable(query
                .select(qCategory)
                .from(qCategory)
                .leftJoin(qCategoryBundle).on(qCategoryBundle.mainCategoryId.eq(qCategory.id))
                .leftJoin(qCategoryBundle).on(qCategoryBundle.subCategoryId.eq(qCategory.id))
                .where(builders)
                .orderBy(qCategory.id.asc())
                .fetch()
        ).orElse(Collections.emptyList());

        return fetch.stream()
                .map(n -> CategoryResDTO.builder()
                        .mainInfo(n)
                        .subInfo(this.findSubCategory(n.getId()))
                        .build())
                .collect(Collectors.toList());
    }
    private List<CategoryBundle> findSubCategory(int mainCategoryId) {
        JPAQuery<List<CategoryBundle>> query = new JPAQuery<>(entityManager);
        return Optional.ofNullable(query
                .select(qCategoryBundle)
                .from(qCategoryBundle)
                .leftJoin(qCategory).on(qCategory.id.eq(qCategoryBundle.mainCategoryId))
                .leftJoin(qCategory).on(qCategory.id.eq(qCategoryBundle.subCategoryId))
                .where(qCategoryBundle.mainCategoryId.eq(mainCategoryId))
                .orderBy(qCategoryBundle.id.asc())
                .fetch()
        ).orElse(Collections.emptyList());
    }
    private BooleanBuilder eqSearch(String categoryName) {
        if(ObjectUtils.isEmpty(categoryName)) {
            return null;
        }

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCategory.categoryName.like("%" + categoryName + "%"));

        return builder;
    }

    /**
     * Category: 정보 변경
     * @param id  변경 요청할 Category ID
     * @param info  변경 요청정보
     * @throws NoahException  Update 에러
     */
    public void editCategory(int id, CategoryEditReqDTO info) throws NoahException{
        // 1.카테고리 정보 유효성 체크
        this.isCategory(id);

        // 2.카테고리 대표정보 Update
        JPAUpdateClause update = new JPAUpdateClause(entityManager, qCategory);
        OptionalLong.of( update
                    .set(qCategory.categoryName, info.getCategoryName())
                    .set(qCategory.categoryDesc, info.getCategoryDesc())
                    .where(qCategory.id.eq(id))
                .execute()
        ).orElseThrow(() -> new NoahException(ResultCode.NOAH_0001, "메인 카테고리 정보 편집 도중 에러가 발생했습니다."));

        // 3.서브 카테고리 정보가 있는 경우
        if(!ObjectUtils.isEmpty(info.getSubCategory())) {
            SubCategoryEditReqDTO subCategoryInfo = info.getSubCategory();

            // 3-1. 서브 카테고리 유효성 체크
            this.isCategory(subCategoryInfo.getSubCategoryId());

            // 3-2. 서브 카테고리 이벤트 분기
            switch (subCategoryInfo.getReqEventCd()) {
                case "C": // C: 추가
                    this.saveCategoryBundle(id, subCategoryInfo.getSubCategoryId());
                    break;
                case "D": // D: 제거
                    this.deleteSubCategoryBundle(id, subCategoryInfo.getSubCategoryId());
                    break;
                default:
                    this.isReqEventCd(subCategoryInfo.getReqEventCd());
                    break;
            }
        }
    }
    private void isCategory(int id) {
        Optional.ofNullable(this.findCategory(id))
                .orElseThrow(() -> new NoahException(ResultCode.NOAH_0001, "유효하지 않은 카테고리 정보 입니다."));
    }
    private Category findCategory(int id) {
        JPAQuery<Category> query = new JPAQuery<>(entityManager);
        return query.select(qCategory)
                .from(qCategory)
                .where(qCategory.id.eq(id))
                .fetchOne();
    }
    private void isReqEventCd(String code) {
        if(!ReqEventCd.isReqEventCd(code)) {
            throw new NoahException(ResultCode.NOAH_0001, "올바르지 않은 이벤트 코드 입니다.");
        }
    }
    private void saveCategoryBundle(int mainCategoryId, int subCategoryId) {
        if(mainCategoryId == subCategoryId) {
            throw new NoahException(ResultCode.NOAH_0001, "메인 카테고리와 서브 카테고리는 같을 수 없습니다.");
        }

        this.isDuplicateBundle(mainCategoryId, subCategoryId);

        this.categoryBundleRepository.save(
                CategoryBundle.builder()
                        .mainCategoryId(mainCategoryId)
                        .subCategoryId(subCategoryId)
                        .build()
        );
    }
    private void isDuplicateBundle(int mainCategoryId, int subCategoryId) {
        if(this.isCategoryBundle(mainCategoryId, subCategoryId)) {
            throw new NoahException(ResultCode.NOAH_0001, "이미 등록된 서브 카테고리 입니다.");
        }
    }
    private boolean isBundle(int mainCategoryId, int subCategoryId) {
       if(!this.isCategoryBundle(mainCategoryId, subCategoryId)) {
            log.warn("이미 제거된 서브 카테고리 입니다.");
            return false;
       }
       return true;
    }
    private boolean isCategoryBundle(int mainCategoryId, int subCategoryId) {
        JPAQuery<CategoryBundle> query = new JPAQuery<>(entityManager);
        Optional<CategoryBundle> isCategoryBundle =
                Optional.ofNullable(query.select(qCategoryBundle)
                        .from(qCategoryBundle)
                        .where(
                                qCategoryBundle.mainCategoryId.eq(mainCategoryId),
                                qCategoryBundle.subCategoryId.eq(subCategoryId)
                        )
                        .fetchOne()
                );
        return isCategoryBundle.isPresent();
    }

    /**
     * Main Category: 정보 제거
     * @param id  제거 요청할 Category ID
     */
    public void deleteCategory(int id) {
        this.isCategory(id);

        JPADeleteClause deleteClauseBundle = new JPADeleteClause(entityManager, qCategoryBundle);
        OptionalLong.of(deleteClauseBundle.where(
                        qCategoryBundle.mainCategoryId.eq(id).or(qCategoryBundle.subCategoryId.eq(id))
                ).execute())
                .orElseThrow(() -> new NoahException(ResultCode.NOAH_0001, "번들정보 삭제 도중 예외가 발생하였습니다."));

        JPADeleteClause deleteClause = new JPADeleteClause(entityManager, qCategory);
        OptionalLong.of(deleteClause.where(qCategory.id.eq(id)).execute())
                .orElseThrow(() -> new NoahException(ResultCode.NOAH_0001, "삭제 도중 예외가 발생하였습니다."));
    }

    /**
     * Sub Category: 정보 제거
     * @param mainCategoryId  제거 요청할 메인 카테고리 ID
     * @param subCategoryId  제거 요청할 서브 카테고리 ID
     */
    public void deleteSubCategoryBundle(int mainCategoryId, int subCategoryId) {
        if(this.isBundle(mainCategoryId, subCategoryId)) {
            JPADeleteClause deleteClause = new JPADeleteClause(entityManager, qCategoryBundle);
            OptionalLong.of(deleteClause.where(
                            qCategoryBundle.mainCategoryId.eq(mainCategoryId),
                            qCategoryBundle.subCategoryId.eq(subCategoryId)
                    ).execute())
                    .orElseThrow(() -> new NoahException(ResultCode.NOAH_0001, "삭제 도중 예외가 발생하였습니다."));
        }
    }
}