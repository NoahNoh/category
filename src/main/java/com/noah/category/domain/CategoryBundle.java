package com.noah.category.domain;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table( name="CategoryBundle" )
public class CategoryBundle implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id", length=11)
	private int id;

	@Column(name="mainCategoryId", length=11)
	private int mainCategoryId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="mainCategoryId", insertable = false, updatable = false)
	@Fetch(FetchMode.JOIN)
	@ToString.Exclude
	private Category mainCategory;

	@Column(name="subCategoryId", length=11)
	private int subCategoryId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="subCategoryId", insertable = false, updatable = false)
	@Fetch(FetchMode.JOIN)
	private Category subCategory;

	@Column(name="createdAt", nullable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date createdAt;

	@Column(name="updatedAt", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP")
	private Date updatedAt;

	@Builder
	public CategoryBundle(int mainCategoryId, int subCategoryId){
		this.mainCategoryId = mainCategoryId;
		this.subCategoryId = subCategoryId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		CategoryBundle that = (CategoryBundle) o;

		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return 314318069;
	}
}