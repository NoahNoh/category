package com.noah.category.domain;

import com.noah.category.dto.req.CategoryReqDTO;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table( name="Category" )
public class Category implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id", length=11)
	private int id;

	@Column(name="categoryName", length=100)
	private String categoryName;

	@Column(name="categoryDesc", columnDefinition="TEXT")
	private String categoryDesc;

	@Column(name="createdAt", nullable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date createdAt;

	@Column(name="updatedAt", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP")
	private Date updatedAt;

	@OneToMany(mappedBy = "subCategory", cascade = CascadeType.ALL)
	@ToString.Exclude
	private List<CategoryBundle> subCategory = new ArrayList<>();

	@Builder
	public Category(CategoryReqDTO info) {
		this.categoryName = info.getCategoryName();
		this.categoryDesc = info.getCategoryDesc();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		Category category = (Category) o;

		return Objects.equals(id, category.id);
	}

	@Override
	public int hashCode() {
		return 1596826009;
	}
}