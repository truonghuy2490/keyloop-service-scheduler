package com.keyloop.scheduler.application.usecase.appointment.bookappointment;

import java.time.*;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.keyloop.scheduler.application.abstraction.dataaccess.DealershipRepository;
import com.keyloop.scheduler.application.abstraction.dataaccess.TechnicianRepository;

@Component
public class AppointmentScheduleValidator {

        public boolean isDealershipAvailable(
                        DealershipRepository.DealershipDetailsData dealership,
                        Instant startTime,
                        Instant endTime) {
                ZoneId zone = ZoneId.of(dealership.timeZone());

                ZonedDateTime start = startTime.atZone(zone);
                ZonedDateTime end = endTime.atZone(zone);

                if (!isSameBusinessDate(start, end)) {
                        return false;
                }

                LocalDate date = start.toLocalDate();
                LocalTime startLocal = start.toLocalTime();
                LocalTime endLocal = end.toLocalTime();

                // Date override takes precedence over weekly hours.
                var override = dealership.dateOverrides().stream()
                                .filter(item -> item.businessDate().equals(date))
                                .findFirst();

                if (override.isPresent()) {
                        var schedule = override.get();

                        if (schedule.isClosed()) {
                                return false;
                        }

                        return isWithinInterval(
                                        startLocal,
                                        endLocal,
                                        schedule.opensAt(),
                                        schedule.closesAt());
                }

                // Fall back to regular weekly business hours.
                return dealership.workingHours().stream()
                                .filter(item -> item.dayOfWeek() == date.getDayOfWeek())
                                .filter(item -> !item.isClosed())
                                .anyMatch(item -> isWithinInterval(
                                                startLocal,
                                                endLocal,
                                                item.opensAt(),
                                                item.closesAt()));
        }

        public boolean isTechnicianAvailable(
                        TechnicianRepository.TechnicianDetailsData technician,
                        ZoneId dealershipZone,
                        Instant startTime,
                        Instant endTime) {
                ZonedDateTime start = startTime.atZone(dealershipZone);
                ZonedDateTime end = endTime.atZone(dealershipZone);

                if (!isSameBusinessDate(start, end)) {
                        return false;
                }

                LocalDate date = start.toLocalDate();
                LocalTime startLocal = start.toLocalTime();
                LocalTime endLocal = end.toLocalTime();

                // Date override takes precedence over weekly hours.
                var override = technician.dateOverrides().stream()
                                .filter(item -> item.workDate().equals(date))
                                .findFirst();

                if (override.isPresent()) {
                        var schedule = override.get();

                        if (schedule.isUnavailable()) {
                                return false;
                        }

                        return isWithinInterval(
                                        startLocal,
                                        endLocal,
                                        schedule.startsAt(),
                                        schedule.endsAt());
                }

                // A booking must fit entirely within one working interval.
                return technician.workingHours().stream()
                                .filter(item -> item.dayOfWeek() == date.getDayOfWeek())
                                .anyMatch(item -> isWithinInterval(
                                                startLocal,
                                                endLocal,
                                                item.startsAt(),
                                                item.endsAt()));
        }

        private boolean isSameBusinessDate(
                        ZonedDateTime start,
                        ZonedDateTime end) {
                return start.isBefore(end)
                                && start.toLocalDate().equals(end.toLocalDate());
        }

        private boolean isWithinInterval(
                        LocalTime requestedStart,
                        LocalTime requestedEnd,
                        LocalTime availableStart,
                        LocalTime availableEnd) {
                if (Objects.isNull(availableStart)
                                || Objects.isNull(availableEnd)) {
                        return false;
                }

                if (!availableStart.isBefore(availableEnd)) {
                        return false;
                }

                return !requestedStart.isBefore(availableStart)
                                && !requestedEnd.isAfter(availableEnd)
                                && requestedStart.isBefore(requestedEnd);
        }
}
