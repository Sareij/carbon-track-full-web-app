package com.carbontrack.service;

import com.carbontrack.dto.ActivityResponse;
import com.carbontrack.exception.InvalidDateRangeException;
import com.carbontrack.exception.ResourceNotFoundException;
import com.carbontrack.model.ActivityLog;
import com.carbontrack.model.Category;
import com.carbontrack.model.User;
import com.carbontrack.repository.ActivityLogRepository;
import com.carbontrack.repository.ActivityLogSearchRepository;
import com.carbontrack.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * Logs activities: validate -> ask the Step 3 engine for CO2e -> save the log WITH the result.
 * The unit is fixed per category because each category has its own payload field
 * (distanceKm, kwh, servings, spendAmount).
 */
@Service
public class ActivityService {

    private static final Map<Category, String> UNIT_BY_CATEGORY = Map.of(
            Category.TRANSPORT, "KM",
            Category.ELECTRICITY, "KWH",
            Category.FOOD, "SERVING",
            Category.SHOPPING, "USD");

    private static final int DEFAULT_RANGE_DAYS = 30;
    private static final int MAX_RANGE_DAYS = 366;

    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogSearchRepository searchRepository;
    private final EmissionCalculationService calculationService;

    public ActivityService(UserRepository userRepository,
                           ActivityLogRepository activityLogRepository,
                           ActivityLogSearchRepository searchRepository,
                           EmissionCalculationService calculationService) {
        this.userRepository = userRepository;
        this.activityLogRepository = activityLogRepository;
        this.searchRepository = searchRepository;
        this.calculationService = calculationService;
    }

    @Transactional
    public ActivityResponse log(String username, Category category, String activityType,
                                BigDecimal quantity, LocalDate date) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LocalDate logDate = (date != null) ? date : LocalDate.now();
        String unit = UNIT_BY_CATEGORY.get(category);

        // The engine finds the factor valid ON THAT DATE, checks the category matches, and multiplies.
        EmissionResult result = calculationService.calculate(category, activityType, unit, quantity, logDate);

        ActivityLog log = new ActivityLog();
        log.setUser(user);
        log.setCategory(category);
        log.setActivityType(result.factor().getActivityType());          // normalised, e.g. CAR_PETROL
        log.setQuantity(quantity.setScale(3, RoundingMode.HALF_UP));
        log.setUnit(unit);
        log.setCo2eKg(result.co2eKg());                                  // stored once, never recalculated
        log.setEmissionFactor(result.factor());                          // audit trail: which rate was used
        log.setLogDate(logDate);

        return ActivityResponse.from(activityLogRepository.save(log));
    }

    @Transactional(readOnly = true)
    public List<ActivityResponse> list(String username, LocalDate from, LocalDate to, Category category) {
        LocalDate end = (to != null) ? to : LocalDate.now();
        LocalDate start = (from != null) ? from : end.minusDays(DEFAULT_RANGE_DAYS);

        if (start.isAfter(end)) {
            throw new InvalidDateRangeException("'from' date must not be after 'to' date");
        }
        if (ChronoUnit.DAYS.between(start, end) > MAX_RANGE_DAYS) {
            throw new InvalidDateRangeException("Date range must not exceed " + MAX_RANGE_DAYS + " days");
        }

        List<ActivityLog> logs = (category == null)
                ? searchRepository.findForUser(username, start, end)
                : searchRepository.findForUserAndCategory(username, category, start, end);
        return logs.stream().map(ActivityResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ActivityResponse getOne(String username, Long id) {
        return ActivityResponse.from(findOwned(username, id));
    }

    @Transactional
    public void delete(String username, Long id) {
        activityLogRepository.delete(findOwned(username, id));
    }

    /** Someone else's log looks exactly like a missing one (404), so ids can't be probed. */
    private ActivityLog findOwned(String username, Long id) {
        return activityLogRepository.findById(id)
                .filter(a -> a.getUser().getUsername().equals(username))
                .orElseThrow(() -> new ResourceNotFoundException("Activity " + id + " not found"));
    }
}
