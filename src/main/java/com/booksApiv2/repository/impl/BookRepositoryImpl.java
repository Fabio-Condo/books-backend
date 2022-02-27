package com.booksApiv2.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import com.booksApiv2.domain.Book;
import com.booksApiv2.repository.BookRepositoryQuery;
import com.booksApiv2.repository.filter.BookFilter;
import com.booksApiv2.repository.projection.BookSummary;

public class BookRepositoryImpl implements BookRepositoryQuery {

	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	@PersistenceContext
	private EntityManager manager;

	@Override
	public Page<BookSummary> summary(BookFilter bookFilter, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<BookSummary> criteria = builder.createQuery(BookSummary.class);
		Root<Book> root = criteria.from(Book.class);
		criteria.orderBy(builder.asc(root.get("id")));

		criteria.select(builder.construct(BookSummary.class, root.get("id"), root.get("name"), root.get("bookImageUrl"),
				root.get("publicationDate"), root.get("publishingCompany"), root.get("summary"),
				root.get("author").get("name")));

		Predicate[] predicates = cerateRestrictions(bookFilter, builder, root);
		criteria.where(predicates);

		TypedQuery<BookSummary> query = manager.createQuery(criteria);
		addRestrictionsPagination(query, pageable);

		return new PageImpl<>(query.getResultList(), pageable, total(bookFilter));
	}

	@Override
	public Page<Book> bookFilter(BookFilter bookFilter, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Book> criteria = builder.createQuery(Book.class);
		Root<Book> root = criteria.from(Book.class);

		Predicate[] predicates = cerateRestrictions(bookFilter, builder, root);
		criteria.where(predicates);

		TypedQuery<Book> query = manager.createQuery(criteria);
		addRestrictionsPagination(query, pageable);

		return new PageImpl<>(query.getResultList(), pageable, total(bookFilter));
	}

	private Predicate[] cerateRestrictions(BookFilter bookFilter, CriteriaBuilder builder, Root<Book> root) { 
		List<Predicate> predicates = new ArrayList<>();
		if (!ObjectUtils.isEmpty(bookFilter.getName())) {
			predicates.add(builder.like(builder.lower(root.get("name")), "%" + bookFilter.getName().toLowerCase() + "%"));
		}
		if (bookFilter.getPublicationDateBegin() != null) {
			predicates.add(builder.greaterThanOrEqualTo(root.get("publicationDate"), bookFilter.getPublicationDateBegin()));
		}
		if (bookFilter.getPublicationDateEnd() != null) {
			predicates.add(builder.lessThanOrEqualTo(root.get("publicationDate"), bookFilter.getPublicationDateEnd()));
		}
		return predicates.toArray(new Predicate[predicates.size()]);
	}

	private void addRestrictionsPagination(TypedQuery<?> query, Pageable pageable) { 
		int currentPage = pageable.getPageNumber();
		int totalRecordsByPage = pageable.getPageSize();
		int firstPageRecord = currentPage * totalRecordsByPage;

		query.setFirstResult(firstPageRecord);
		query.setMaxResults(totalRecordsByPage);

		LOGGER.info("Current page: " + currentPage + " Total records by page: " + totalRecordsByPage + " First record page " + firstPageRecord);
	}

	private Long total(BookFilter bookFilter) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Book> root = criteria.from(Book.class);

		Predicate[] predicates = cerateRestrictions(bookFilter, builder, root);
		criteria.where(predicates);

		criteria.select(builder.count(root));
		return manager.createQuery(criteria).getSingleResult();
	}

}
