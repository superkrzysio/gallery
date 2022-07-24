package kw.tools.gallery.persistence;

import kw.tools.gallery.models.Gallery;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class GallerySearchSpecification implements Specification<Gallery>
{
    private final List<GallerySearchCriteria> criteria = new ArrayList<>();

    public void addCriteria(GallerySearchCriteria criteria)
    {
        this.criteria.add(criteria);
    }

    public void addCriteria(List<GallerySearchCriteria> criteria)
    {
        this.criteria.addAll(criteria);
    }

    @Override
    public Predicate toPredicate(Root<Gallery> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder)
    {
        List<Predicate> predicates = new ArrayList<>();
        for (GallerySearchCriteria criteria : this.criteria)
        {
            List<Predicate> subpredicates = new ArrayList<>();
            if (criteria.repositoryId != null)
            {
                subpredicates.add(criteriaBuilder.equal(root.get("repositoryId"), criteria.repositoryId));
            }
            switch (criteria.rating)
            {
                case ZERO:
                    subpredicates.add(criteriaBuilder.equal(root.get("rating"), 0));
                    break;
                case POSITIVE:
                    subpredicates.add(criteriaBuilder.greaterThan(root.get("rating"), 0));
            }
            predicates.add(criteriaBuilder.and(subpredicates.toArray(new Predicate[0])));
        }
        return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
    }
}
