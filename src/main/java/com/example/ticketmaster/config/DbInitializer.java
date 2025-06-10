package com.example.ticketmaster.config;

import com.example.ticketmaster.entity.Role;
import com.example.ticketmaster.entity.User;
import com.example.ticketmaster.repository.RoleRepository;
import com.example.ticketmaster.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DbInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DbInitializer(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createRoleIfNotExist("ADMIN", "Administrator with full access");
        createRoleIfNotExist("ORGANIZER", "Event organizer");
        createRoleIfNotExist("CLIENT", "Regular client");

        if (!userRepository.existsByUsername("admin")) {
            User admin = new User(
                    "admin",
                    "admin@example.com",
                    passwordEncoder.encode("admin"),
                    "Admin",
                    "User"
            );

            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Role not found: ADMIN"));

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            admin.setRoles(roles);

            userRepository.save(admin);
        }

        // Create demo organizer if it doesn't exist
        if (!userRepository.existsByUsername("organizer")) {
            User organizer = new User(
                    "organizer",
                    "organizer@example.com",
                    passwordEncoder.encode("organizer"),
                    "Demo",
                    "Organizer"
            );

            Role organizerRole = roleRepository.findByName("ORGANIZER")
                    .orElseThrow(() -> new RuntimeException("Role not found: ORGANIZER"));

            Set<Role> roles = new HashSet<>();
            roles.add(organizerRole);
            organizer.setRoles(roles);

            userRepository.save(organizer);
        }

        // Create demo client if it doesn't exist
        if (!userRepository.existsByUsername("client")) {
            User client = new User(
                    "client",
                    "client@example.com",
                    passwordEncoder.encode("client"),
                    "Demo",
                    "Client"
            );

            Role clientRole = roleRepository.findByName("CLIENT")
                    .orElseThrow(() -> new RuntimeException("Role not found: CLIENT"));

            Set<Role> roles = new HashSet<>();
            roles.add(clientRole);
            client.setRoles(roles);

            userRepository.save(client);
        }
    }

    private void createRoleIfNotExist(String roleName, String description) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = new Role(roleName, description);
            roleRepository.save(role);
        }
    }
}