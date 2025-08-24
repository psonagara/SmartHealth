package com.ps.schedule;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ps.dto.request.AGRequest;
import com.ps.entity.AGPreference;
import com.ps.enu.AGMode;
import com.ps.repo.AGPreferenceRepository;
import com.ps.service.IAvailabilityService;
import com.ps.util.AvailabilityUtil;

import jakarta.transaction.Transactional;

/**
 * Scheduled component responsible for automatically generating availability slots for doctors.
 * <p>
 * This component is triggered periodically based on a cron expression, fetching active
 * {@link AGPreference} records that are configured for automatic or continuous generation modes.
 * It then calculates the target date for slot creation and generates new slots via the
 * {@link IAvailabilityService} if necessary.
 * </p>
 *
 * <p><b>Key Responsibilities:</b></p>
 * <ul>
 *   <li>Run at scheduled intervals to check for slot generation needs.</li>
 *   <li>Skip slot generation if already generated up to the target date.</li>
 *   <li>Support multiple generation modes like {@code AUTO} and {@code CUSTOM_CONTINUOUS}.</li>
 *   <li>Ensure transactional execution for data consistency.</li>
 * </ul>
 *
 * @see AGPreferenceRepository
 * @see IAvailabilityService
 * @see AGPreference
 * @see AGMode
 */
@Component
public class AvailabilityAutoGenerator {

	private static final Logger LOG = LoggerFactory.getLogger(AvailabilityAutoGenerator.class);
	
	@Autowired
	private AGPreferenceRepository agPreferenceRepository;
	
	@Autowired
	private IAvailabilityService availabilityService;

	/**
     * Scheduled job that generates availability slots for doctors based on stored preferences.
     * <p>
     * This method:
     * <ol>
     *   <li>Retrieves active preferences in modes {@link AGMode#AUTO} or {@link AGMode#CUSTOM_CONTINUOUS}.</li>
     *   <li>Calculates the target generation date based on {@code daysAhead} and {@code startDate}.</li>
     *   <li>Skips processing if slots are already generated until the target date.</li>
     *   <li>Generates new slots by preparing an {@link AGRequest} and delegating to
     *       {@link IAvailabilityService#generateAvailabilitySlots(AGRequest, com.example.Doctor)}.</li>
     * </ol>
     * </p>
     *
     * <p>Runs at the 1AM every day, as per the cron expression {@code 0 0 1 * * *}.</p>
     *
     * @see AvailabilityUtil#prepareAGRequest(int, java.time.LocalDate, Object, AGMode, boolean)
     */
	@Transactional
	@Scheduled(cron = "0 0 1 * * *")
	public void generateAvailabilitySlots() {
		LOG.info("Started Schedular AvailabilityAutoGenerator.generateAvailabilitySlots");

		long start = System.currentTimeMillis();
		List<AGPreference> preferences = agPreferenceRepository.findByModeInAndIsActive(List.of(AGMode.AUTO, AGMode.CUSTOM_CONTINUOUS), true);
		
		for (AGPreference preference : preferences) {
			int daysAhead = preference.getDaysAhead();
			LocalDate lastGeneratedOn = preference.getLastGeneratedOn();
			LocalDate today = LocalDate.now();
			LocalDate target = today.plusDays(daysAhead);
			if (ChronoUnit.DAYS.between(preference.getStartDate(), today) < 0)
				target = preference.getStartDate().plusDays(daysAhead);
			if (ChronoUnit.DAYS.between(lastGeneratedOn, target) <= 0) {
				LOG.info("Availability Slots already generated till targeted date, doctorId:{}, targetDate:{}, lastGeneratedOn:{}", preference.getDoctor().getId(), target, lastGeneratedOn);
			} else {
				if (lastGeneratedOn.isBefore(today))
					lastGeneratedOn = today;
				daysAhead = (int) ChronoUnit.DAYS.between(lastGeneratedOn, target);
				AGRequest request = AvailabilityUtil.prepareAGRequest(daysAhead, lastGeneratedOn, preference.getSlotInputs(), AGMode.SCHEDULED, preference.getSkipHoliday());
				availabilityService.generateAvailabilitySlots(request, preference.getDoctor());
			}
		}
		
		LOG.info("Ended Schedular AvailabilityAutoGenerator.generateAvailabilitySlots, Time Taken:{}ms", (System.currentTimeMillis() - start));
	}
}