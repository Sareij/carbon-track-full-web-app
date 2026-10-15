package com.carbontrack.service;

import com.carbontrack.dto.CreateEmissionFactorRequest;
import com.carbontrack.dto.EmissionFactorResponse;
import com.carbontrack.dto.UpdateEmissionFactorRequest;
import com.carbontrack.exception.ConflictException;
import com.carbontrack.exception.ResourceNotFoundException;
import com.carbontrack.model.Category;
import com.carbontrack.model.EmissionFactor;
import com.carbontrack.repository.EmissionFactorRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Reading factors (everyone) and managing them (admins). */
@Service
public class EmissionFactorService {

    private final EmissionFactorRepository repository;

    public EmissionFactorService(EmissionFactorRepository repository) {
        this.repository = repository;
    }

    /** For dropdowns: only the CURRENT factor per (activityType, unit): active, already effective, newest version. */
    @Transactional(readOnly = true)
    public List<EmissionFactorResponse> listCurrent(Category category) {
        LocalDate today = LocalDate.now();
        Map<String, EmissionFactor> newest = new LinkedHashMap<>();

        for (EmissionFactor f : repository.findByActiveTrueOrderByCategoryAscActivityTypeAsc()) {
            if (f.getEffectiveDate().isAfter(today)) continue;               // not in force yet
            if (category != null && f.getCategory() != category) continue;
            newest.merge(f.getActivityType() + "|" + f.getUnit(), f,
                    (a, b) -> a.getEffectiveDate().isAfter(b.getEffectiveDate()) ? a : b);
        }
        return newest.values().stream().map(EmissionFactorResponse::from).toList();
    }

    /** Admin view: every row, including old versions and inactive ones. */
    @Transactional(readOnly = true)
    public List<EmissionFactorResponse> listAll() {
        Sort sort = Sort.by("category", "activityType", "unit")
                .and(Sort.by(Sort.Direction.DESC, "effectiveDate"));
        return repository.findAll(sort).stream().map(EmissionFactorResponse::from).toList();
    }

    @Transactional
    public EmissionFactorResponse create(CreateEmissionFactorRequest req) {
        String type = req.activityType().trim().toUpperCase(Locale.ROOT);
        String unit = req.unit().trim().toUpperCase(Locale.ROOT);

        // The table is tiny (tens of rows), so checking in memory is fine and keeps repositories untouched.
        for (EmissionFactor existing : repository.findAll()) {
            if (existing.getActivityType().equals(type) && existing.getUnit().equals(unit)) {
                if (existing.getCategory() != req.category()) {
                    throw new ConflictException(type + "/" + unit + " already belongs to category " + existing.getCategory());
                }
                if (existing.getEffectiveDate().equals(req.effectiveDate())) {
                    throw new ConflictException("A factor for " + type + "/" + unit
                            + " effective " + req.effectiveDate() + " already exists");
                }
            }
        }

        EmissionFactor f = new EmissionFactor();
        f.setCategory(req.category());
        f.setActivityType(type);
        f.setUnit(unit);
        f.setKgCo2ePerUnit(req.kgCo2ePerUnit().setScale(6, java.math.RoundingMode.HALF_UP));
        f.setSource(req.source().trim());
        f.setEffectiveDate(req.effectiveDate());
        f.setActive(true);
        return EmissionFactorResponse.from(repository.save(f));
    }

    @Transactional
    public EmissionFactorResponse update(Long id, UpdateEmissionFactorRequest req) {
        EmissionFactor f = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emission factor " + id + " not found"));

        BigDecimal value = req.kgCo2ePerUnit();
        if (value != null) f.setKgCo2ePerUnit(value.setScale(6, java.math.RoundingMode.HALF_UP));
        if (req.source() != null) f.setSource(req.source().trim());
        if (req.active() != null) f.setActive(req.active());

        // activityType / unit / category are deliberately NOT editable: changing them would
        // silently change the meaning of every log that points at this row.
        return EmissionFactorResponse.from(f);   // dirty checking saves it on commit
    }
}
