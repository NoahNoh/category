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
     * Category: ???????????? ??????
     * @param reqInfo  ???????????? ????????????
     */
    public void saveCategory(CategoryReqDTO reqInfo) {
        // 1.???????????? ???????????? ??????
        Category saveCategoryInfo = Optional.ofNullable(this.categoryRepository.save(
            Category.builder()
                    .info(reqInfo)
                    .build()
        )).orElseThrow(() -> new NoahException(ResultCode.NOAH_0001, "???????????? ?????? ?????? ?????? ????????? ??????????????????."));

        // 2.???????????? ???????????? ??????
        if(!ObjectUtils.isEmpty(reqInfo.getSubCategoryId())) {
            this.isCategory(reqInfo.getSubCategoryId());
            this.saveCategoryBundle(saveCategoryInfo.getId(), reqInfo.getSubCategoryId());
        }
    }

    /**
     * CategoryBundle: ???????????? ???????????? ?????? (+?????? ???????????? ??????)
     * @param id  ????????? ???????????? ID
     * @return  ???????????? ???????????? (+?????? ???????????? ??????)
     */
    public CategoryResDTO findCategoryBundle(int id) {
        JPAQuery<Category> query = new JPAQuery<>(entityManager);
        Category fetchOne = Optional.ofNullable(query.select(qCategory)
                .from(qCategory)
                .where(qCategory.id.eq(id))
                .fetchOne()
        ).orElseThrow(() -> new NoahException(ResultCode.NOAH_0001, "???????????? ?????? ???????????? ?????? ?????????."));

        return CategoryResDTO.builder()
                .mainInfo(fetchOne)
                .subInfo(this.findSubCategory(id))
                .build();
    }

    /**
     * Category: ?????? ??????
     * @param categoryName  ??????: ???????????? ???
     * @return  ???????????? ????????????
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
     * Category: ?????? ??????
     * @param id  ?????? ????????? Category ID
     * @param info  ?????? ????????????
     * @throws NoahException  Update ??????
     */
    public void editCategory(int id, CategoryEditReqDTO info) throws NoahException{
        // 1.???????????? ?????? ????????? ??????
        this.isCategory(id);

        // 2.???????????? ???????????? Update
        JPAUpdateClause update = new JPAUpdateClause(entityManager, qCategory);
        OptionalLong.of( update
                    .set(qCategory.categoryName, info.getCategoryName())
                    .set(qCategory.categoryDesc, info.getCategoryDesc())
                    .where(qCategory.id.eq(id))
                .execute()
        ).orElseThrow(() -> new NoahException(ResultCode.NOAH_0001, "?????? ???????????? ?????? ?????? ?????? ????????? ??????????????????."));

        // 3.?????? ???????????? ????????? ?????? ??????
        if(!ObjectUtils.isEmpty(info.getSubCategory())) {
            SubCategoryEditReqDTO subCategoryInfo = info.getSubCategory();

            // 3-1. ?????? ???????????? ????????? ??????
            this.isCategory(subCategoryInfo.getSubCategoryId());

            // 3-2. ?????? ???????????? ????????? ??????
            switch (subCategoryInfo.getReqEventCd()) {
                case "C": // C: ??????
                    this.saveCategoryBundle(id, subCategoryInfo.getSubCategoryId());
                    break;
                case "D": // D: ??????
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
                .orElseThrow(() -> new NoahException(ResultCode.NOAH_0001, "???????????? ?????? ???????????? ?????? ?????????."));
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
            throw new NoahException(ResultCode.NOAH_0001, "???????????? ?????? ????????? ?????? ?????????.");
        }
    }
    private void saveCategoryBundle(int mainCategoryId, int subCategoryId) {
        if(mainCategoryId == subCategoryId) {
            throw new NoahException(ResultCode.NOAH_0001, "?????? ??????????????? ?????? ??????????????? ?????? ??? ????????????.");
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
            throw new NoahException(ResultCode.NOAH_0001, "?????? ????????? ?????? ???????????? ?????????.");
        }
    }
    private boolean isBundle(int mainCategoryId, int subCategoryId) {
       if(!this.isCategoryBundle(mainCategoryId, subCategoryId)) {
            log.warn("?????? ????????? ?????? ???????????? ?????????.");
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
     * Main Category: ?????? ??????
     * @param id  ?????? ????????? Category ID
     */
    public void deleteCategory(int id) {
        this.isCategory(id);

        JPADeleteClause deleteClauseBundle = new JPADeleteClause(entityManager, qCategoryBundle);
        OptionalLong.of(deleteClauseBundle.where(
                        qCategoryBundle.mainCategoryId.eq(id).or(qCategoryBundle.subCategoryId.eq(id))
                ).execute())
                .orElseThrow(() -> new NoahException(ResultCode.NOAH_0001, "???????????? ?????? ?????? ????????? ?????????????????????."));

        JPADeleteClause deleteClause = new JPADeleteClause(entityManager, qCategory);
        OptionalLong.of(deleteClause.where(qCategory.id.eq(id)).execute())
                .orElseThrow(() -> new NoahException(ResultCode.NOAH_0001, "?????? ?????? ????????? ?????????????????????."));
    }

    /**
     * Sub Category: ?????? ??????
     * @param mainCategoryId  ?????? ????????? ?????? ???????????? ID
     * @param subCategoryId  ?????? ????????? ?????? ???????????? ID
     */
    public void deleteSubCategoryBundle(int mainCategoryId, int subCategoryId) {
        if(this.isBundle(mainCategoryId, subCategoryId)) {
            JPADeleteClause deleteClause = new JPADeleteClause(entityManager, qCategoryBundle);
            OptionalLong.of(deleteClause.where(
                            qCategoryBundle.mainCategoryId.eq(mainCategoryId),
                            qCategoryBundle.subCategoryId.eq(subCategoryId)
                    ).execute())
                    .orElseThrow(() -> new NoahException(ResultCode.NOAH_0001, "?????? ?????? ????????? ?????????????????????."));
        }
    }
}