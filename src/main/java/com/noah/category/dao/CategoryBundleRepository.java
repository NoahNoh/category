package com.noah.category.dao;

import com.noah.category.domain.CategoryBundle;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryBundleRepository extends CrudRepository<CategoryBundle, Integer> {
	<S extends CategoryBundle> S save(S info);
}