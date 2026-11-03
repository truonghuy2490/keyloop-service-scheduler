
package com.keyloop.scheduler.integration.appointment;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.keyloop.scheduler.application.common.result.Result;
import com.keyloop.scheduler.application.usecase.appointment.bookappointment.Handler;
import com.keyloop.scheduler.application.usecase.appointment.bookappointment.Request;
import com.keyloop.scheduler.application.usecase.appointment.bookappointment.Response;
import com.keyloop.scheduler.integration.config.PostgreSQLContainerConfiguration;
import com.keyloop.scheduler.integration.fixture.BookingSeedData;

@SpringBootTest
@ActiveProfiles("test")
@Import(PostgreSQLContainerConfiguration.class)
class BookAppointmentConcurrencyIT {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("bookAppointmentHandler")
    private Handler bookingHandler;

    // =========================================================
    // SEED DATA
    // =========================================================

    private static final UUID DEALERSHIP_1 = BookingSeedData.DEALERSHIP_1;

    private static final UUID JOHN_SMITH = BookingSeedData.JOHN_SMITH;

    private static final UUID DAVID_WILSON = BookingSeedData.DAVID_WILSON;

    private static final UUID CUSTOMER_1 = BookingSeedData.CUSTOMER_1;

    private static final UUID CUSTOMER_2 = BookingSeedData.CUSTOMER_2;

    private static final UUID VEHICLE_1 = BookingSeedData.VEHICLE_1;

    private static final UUID VEHICLE_4 = BookingSeedData.VEHICLE_4;

    private static final UUID WHEEL_ALIGNMENT = BookingSeedData.WHEEL_ALIGNMENT;

    private static final UUID BAY_1 = UUID.fromString(
            "20000000-0000-0000-0000-000000000001");

    private static final UUID BAY_2 = UUID.fromString(
            "20000000-0000-0000-0000-000000000002");

    private static final int DURATION_MINUTES = 60;

    // Test dates are isolated from existing seeded appointments.
    private static final OffsetDateTime SAME_TECH_TIME = OffsetDateTime.parse("2026-11-18T09:00:00+07:00");

    private static final OffsetDateTime DIFFERENT_TECH_TIME = OffsetDateTime.parse("2026-11-26T09:00:00+07:00");

    private static final OffsetDateTime BACK_TO_BACK_TIME = OffsetDateTime.parse("2026-11-24T09:00:00+07:00");

    private static final OffsetDateTime SEEDED_CONFLICT_TIME = OffsetDateTime.parse("2026-11-05T09:00:00+07:00");

    private static final OffsetDateTime OUTSIDE_HOURS_TIME = OffsetDateTime.parse("2026-11-23T14:30:00+07:00");

    // IDs of appointments successfully created by tests.
    private final List<UUID> createdAppointmentIds = new ArrayList<>();

    // test 0
    @Test
    void shouldUseIsolatedTestDatabase() throws Exception {

        try (Connection connection = dataSource.getConnection()) {

            System.out.println(
                    "JDBC URL: " +
                            connection.getMetaData().getURL());

            System.out.println(
                    "Database: " +
                            connection.getCatalog());

            assertEquals(
                    "scheduler_test",
                    connection.getCatalog());
        }
    }

    // =========================================================
    // TEST 1: POSTGRESQL CONNECTION
    // =========================================================

    @Test
    void shouldConnectToPostgreSQLContainer() throws Exception {

        try (Connection connection = dataSource.getConnection()) {

            assertNotNull(connection);
            assertFalse(connection.isClosed());

            assertEquals(
                    "scheduler_test",
                    connection.getCatalog());
        }
    }

    // =========================================================
    // TEST 2: VERIFY SEED DATA
    // =========================================================

    @Test
    void shouldLoadDealershipOneSeedData() {

        Integer technicianCount = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM technician
                        WHERE dealership_id = ?
                          AND id IN (?, ?)
                          AND active = TRUE
                        """,
                Integer.class,
                DEALERSHIP_1,
                JOHN_SMITH,
                DAVID_WILSON);

        assertEquals(2, technicianCount);

        Integer qualificationCount = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM technician_qualification
                        WHERE technician_id IN (?, ?)
                          AND service_type_id = ?
                        """,
                Integer.class,
                JOHN_SMITH,
                DAVID_WILSON,
                WHEEL_ALIGNMENT);

        assertEquals(2, qualificationCount);

        Integer activeBayCount = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM service_bay
                        WHERE dealership_id = ?
                          AND active = TRUE
                        """,
                Integer.class,
                DEALERSHIP_1);

        assertEquals(2, activeBayCount);
    }

    // =========================================================
    // TEST 3: CONCURRENT BOOKING - SAME TECHNICIAN
    // =========================================================

    @Test
    void shouldPreventDoubleBookingForSameTechnician()
            throws Exception {

        OffsetDateTime start = SAME_TECH_TIME;
        OffsetDateTime end = start.plusMinutes(DURATION_MINUTES);

        assertEquals(
                0,
                countAppointmentsForTechnician(JOHN_SMITH, start, end));

        Request requestA = createRequest(
                CUSTOMER_1,
                VEHICLE_1,
                JOHN_SMITH,
                start);

        Request requestB = createRequest(
                CUSTOMER_2,
                VEHICLE_4,
                JOHN_SMITH,
                start);

        List<Result<Response>> results = executeConcurrently(requestA, requestB);

        trackSuccessfulAppointments(results);

        long successCount = results.stream()
                .filter(Result::isSuccess)
                .count();

        long failureCount = results.stream()
                .filter(Result::isFailure)
                .count();

        assertEquals(
                1,
                successCount,
                "Only one booking should succeed for John Smith");

        assertEquals(
                1,
                failureCount,
                "One booking must be rejected");

        assertEquals(
                1,
                countAppointmentsForTechnician(JOHN_SMITH, start, end),
                "Database must contain exactly one booking");
    }

    // =========================================================
    // TEST 4: CONCURRENT BOOKING - DIFFERENT TECHNICIANS
    // =========================================================

    @Test
    void shouldAllowConcurrentBookingForDifferentTechnicians()
            throws Exception {

        OffsetDateTime start = DIFFERENT_TECH_TIME;
        OffsetDateTime end = start.plusMinutes(DURATION_MINUTES);

        Request johnRequest = createRequest(
                CUSTOMER_1,
                VEHICLE_1,
                JOHN_SMITH,
                start);

        Request davidRequest = createRequest(
                CUSTOMER_2,
                VEHICLE_4,
                DAVID_WILSON,
                start);

        List<Result<Response>> results = executeConcurrently(johnRequest, davidRequest);

        trackSuccessfulAppointments(results);

        assertTrue(
                results.stream().allMatch(Result::isSuccess),
                () -> "Both bookings should succeed.\n"
                        + "John: " + describeResult(results.get(0)) + "\n"
                        + "David: " + describeResult(results.get(1)));

        Result.Success<Response> johnSuccess = (Result.Success<Response>) results.get(0);

        Result.Success<Response> davidSuccess = (Result.Success<Response>) results.get(1);

        UUID johnBayId = johnSuccess.value().serviceBay().id();
        UUID davidBayId = davidSuccess.value().serviceBay().id();

        assertNotEquals(
                johnBayId,
                davidBayId,
                "Concurrent bookings must use different service bays");

        assertEquals(
                1,
                countAppointmentsForTechnician(JOHN_SMITH, start, end));

        assertEquals(
                1,
                countAppointmentsForTechnician(DAVID_WILSON, start, end));

        assertEquals(
                1,
                countAppointmentsForBay(johnBayId, start, end));

        assertEquals(
                1,
                countAppointmentsForBay(davidBayId, start, end));
    }

    // =========================================================
    // TEST 5: BACK-TO-BACK APPOINTMENTS
    // =========================================================

    @Test
    void shouldAllowBackToBackAppointmentsForSameTechnician()
            throws Exception {

        OffsetDateTime firstStart = BACK_TO_BACK_TIME;
        OffsetDateTime secondStart = firstStart.plusMinutes(60);

        Request firstRequest = createRequest(
                CUSTOMER_1,
                VEHICLE_1,
                JOHN_SMITH,
                firstStart);

        Request secondRequest = createRequest(
                CUSTOMER_2,
                VEHICLE_4,
                JOHN_SMITH,
                secondStart);

        Result<Response> firstResult = book(firstRequest);
        trackSuccessfulAppointment(firstResult);

        assertTrue(
                firstResult.isSuccess(),
                () -> "First appointment should succeed. "
                        + describeResult(firstResult));

        Result<Response> secondResult = book(secondRequest);
        trackSuccessfulAppointment(secondResult);

        assertTrue(
                secondResult.isSuccess(),
                () -> "Second appointment should succeed. "
                        + describeResult(secondResult));

        assertEquals(
                1,
                countAppointmentsForTechnician(
                        JOHN_SMITH,
                        firstStart,
                        firstStart.plusMinutes(60)));

        assertEquals(
                1,
                countAppointmentsForTechnician(
                        JOHN_SMITH,
                        secondStart,
                        secondStart.plusMinutes(60)));
    }

    // =========================================================
    // TEST 6: EXISTING APPOINTMENT CONFLICT
    // =========================================================

    @Test
    void shouldRejectBookingWhenTechnicianAlreadyHasAppointment()
            throws Exception {

        OffsetDateTime start = SEEDED_CONFLICT_TIME;
        OffsetDateTime end = start.plusMinutes(DURATION_MINUTES);

        int beforeCount = countAppointmentsForTechnician(
                JOHN_SMITH,
                start,
                end);

        assertEquals(
                1,
                beforeCount,
                "Seed data must contain John's existing appointment");

        Request request = createRequest(
                CUSTOMER_2,
                VEHICLE_4,
                JOHN_SMITH,
                start);

        Result<Response> result = book(request);

        trackSuccessfulAppointment(result);

        assertTrue(
                result.isFailure(),
                "Overlapping booking must be rejected");

        assertEquals(
                beforeCount,
                countAppointmentsForTechnician(JOHN_SMITH, start, end),
                "Appointment count must remain unchanged");
    }

    // =========================================================
    // TEST 7: OUTSIDE WORKING HOURS
    // =========================================================

    @Test
    void shouldRejectBookingOutsideTechnicianWorkingHours()
            throws Exception {

        OffsetDateTime start = OUTSIDE_HOURS_TIME;
        OffsetDateTime end = start.plusMinutes(DURATION_MINUTES);

        Request request = createRequest(
                CUSTOMER_1,
                VEHICLE_1,
                JOHN_SMITH,
                start);

        Result<Response> result = book(request);

        trackSuccessfulAppointment(result);

        assertTrue(
                result.isFailure(),
                "Appointment must fail outside technician working hours");

        assertEquals(
                0,
                countAppointmentsForTechnician(JOHN_SMITH, start, end));
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private Request createRequest(
            UUID customerId,
            UUID vehicleId,
            UUID technicianId,
            OffsetDateTime startTime) {

        return new Request(
                customerId.toString(),
                vehicleId.toString(),
                DEALERSHIP_1.toString(),
                WHEEL_ALIGNMENT.toString(),
                technicianId.toString(),
                startTime);
    }

    private Result<Response> book(Request request) throws Exception {

        return bookingHandler.handle(request)
                .get(30, TimeUnit.SECONDS);
    }

    private List<Result<Response>> executeConcurrently(
            Request requestA,
            Request requestB) throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch readyGate = new CountDownLatch(2);
        CountDownLatch startGate = new CountDownLatch(1);

        try {

            Callable<Result<Response>> taskA = () -> {
                readyGate.countDown();
                startGate.await();
                return book(requestA);
            };

            Callable<Result<Response>> taskB = () -> {
                readyGate.countDown();
                startGate.await();
                return book(requestB);
            };

            Future<Result<Response>> futureA = executor.submit(taskA);

            Future<Result<Response>> futureB = executor.submit(taskB);

            assertTrue(
                    readyGate.await(10, TimeUnit.SECONDS),
                    "Both booking threads must be ready");

            startGate.countDown();

            Result<Response> resultA = futureA.get(40, TimeUnit.SECONDS);

            Result<Response> resultB = futureB.get(40, TimeUnit.SECONDS);

            return List.of(resultA, resultB);

        } finally {
            startGate.countDown();
            executor.shutdownNow();
        }
    }

    private int countAppointmentsForTechnician(
            UUID technicianId,
            OffsetDateTime start,
            OffsetDateTime end) {

        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM appointment
                        WHERE technician_id = ?
                          AND start_time < ?
                          AND end_time > ?
                          AND status = 'CONFIRMED'
                        """,
                Integer.class,
                technicianId,
                Timestamp.from(end.toInstant()),
                Timestamp.from(start.toInstant()));

        return count == null ? 0 : count;
    }

    private int countAppointmentsForBay(
            UUID bayId,
            OffsetDateTime start,
            OffsetDateTime end) {

        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM appointment
                        WHERE service_bay_id = ?
                          AND start_time < ?
                          AND end_time > ?
                          AND status = 'CONFIRMED'
                        """,
                Integer.class,
                bayId,
                Timestamp.from(end.toInstant()),
                Timestamp.from(start.toInstant()));

        return count == null ? 0 : count;
    }

    // =========================================================
    // TRACK SUCCESSFUL APPOINTMENTS
    // =========================================================

    private void trackSuccessfulAppointments(
            List<Result<Response>> results) {

        results.forEach(this::trackSuccessfulAppointment);
    }

    private void trackSuccessfulAppointment(
            Result<Response> result) {

        if (result instanceof Result.Success<Response> success) {

            UUID appointmentId = success.value().id();

            createdAppointmentIds.add(appointmentId);
        }
    }

    // =========================================================
    // CLEANUP
    // =========================================================

    @AfterEach
    void cleanupCreatedAppointments() {

        for (UUID appointmentId : createdAppointmentIds) {

            jdbcTemplate.update(
                    "DELETE FROM appointment WHERE id = ?",
                    appointmentId);
        }

        createdAppointmentIds.clear();
    }

    private String describeResult(Result<Response> result) {

        if (result instanceof Result.Success<Response> success) {
            return "SUCCESS: appointmentId=" + success.value().id()
                    + ", technicianId=" + success.value().technician().id()
                    + ", bayId=" + success.value().serviceBay().id();
        }

        if (result instanceof Result.Failure<Response> failure) {
            return "FAILURE: " + failure.errorDetails();
        }

        return "UNKNOWN RESULT";
    }
}
