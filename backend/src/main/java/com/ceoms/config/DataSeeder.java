package com.ceoms.config;

import com.ceoms.entity.*;
import com.ceoms.entity.enums.RoleType;
import com.ceoms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final EventCategoryRepository categoryRepository;
    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;
    private final BudgetRepository budgetRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Profile("dev")
    CommandLineRunner seedData() {
        return args -> {
            if (roleRepository.count() > 0) {
                log.info("Data already seeded, skipping...");
                return;
            }
            log.info("Seeding initial CEOMS data...");

            Role superAdmin = roleRepository.save(Role.builder().name(RoleType.SUPER_ADMIN).description("College Management").build());
            Role operator = roleRepository.save(Role.builder().name(RoleType.OPERATOR).description("Event Operator").build());
            Role student = roleRepository.save(Role.builder().name(RoleType.STUDENT).description("Student/User").build());

            Department cs = departmentRepository.save(Department.builder().name("Computer Science").code("CS").description("CS Department").headName("Dr. Smith").build());
            Department mech = departmentRepository.save(Department.builder().name("Mechanical Engineering").code("MECH").description("ME Department").headName("Dr. Johnson").build());
            Department civil = departmentRepository.save(Department.builder().name("Civil Engineering").code("CIVIL").description("CE Department").headName("Dr. Williams").build());

            EventCategory tech = categoryRepository.save(EventCategory.builder().name("Technical").description("Technical events and hackathons").build());
            EventCategory cultural = categoryRepository.save(EventCategory.builder().name("Cultural").description("Cultural festivals and performances").build());
            EventCategory sports = categoryRepository.save(EventCategory.builder().name("Sports").description("Sports tournaments").build());

            Venue auditorium = venueRepository.save(Venue.builder().name("Main Auditorium").capacity(500).location("Block A").facilities("Projector, Sound System, AC").build());
            Venue ground = venueRepository.save(Venue.builder().name("Sports Ground").capacity(2000).location("Campus Center").facilities("Open field, Seating").build());

            User admin = userRepository.save(User.builder()
                    .email("admin@ceoms.edu")
                    .password(passwordEncoder.encode("Admin@123"))
                    .firstName("Super").lastName("Admin")
                    .phone("9876543210")
                    .department(cs)
                    .roles(Set.of(superAdmin))
                    .emailVerified(true)
                    .build());

            User op = userRepository.save(User.builder()
                    .email("operator@ceoms.edu")
                    .password(passwordEncoder.encode("Operator@123"))
                    .firstName("Event").lastName("Operator")
                    .phone("9876543211")
                    .department(cs)
                    .roles(Set.of(operator))
                    .emailVerified(true)
                    .build());

            userRepository.save(User.builder()
                    .email("student@ceoms.edu")
                    .password(passwordEncoder.encode("Student@123"))
                    .firstName("John").lastName("Student")
                    .phone("9876543212")
                    .department(cs)
                    .roles(Set.of(student))
                    .emailVerified(true)
                    .build());

            Event event = eventRepository.save(Event.builder()
                    .eventCode("EVT-DEMO001")
                    .title("Annual Tech Fest 2026")
                    .description("College annual technical festival with hackathons, workshops, and competitions.")
                    .category(tech)
                    .department(cs)
                    .organizer(op)
                    .facultyCoordinator("Dr. Smith")
                    .studentCoordinator("Alice Johnson")
                    .venue(auditorium)
                    .startDate(LocalDate.now().plusDays(30))
                    .endDate(LocalDate.now().plusDays(32))
                    .capacity(300)
                    .objectives("Promote technical innovation among students")
                    .status(com.ceoms.entity.enums.EventStatus.APPROVED)
                    .build());

            budgetRepository.save(Budget.builder()
                    .event(event)
                    .estimatedAmount(new BigDecimal("150000"))
                    .approvedAmount(new BigDecimal("150000"))
                    .approved(true)
                    .build());

            log.info("Seed data created successfully!");
            log.info("Admin: admin@ceoms.edu / Admin@123");
            log.info("Operator: operator@ceoms.edu / Operator@123");
            log.info("Student: student@ceoms.edu / Student@123");
        };
    }
}
