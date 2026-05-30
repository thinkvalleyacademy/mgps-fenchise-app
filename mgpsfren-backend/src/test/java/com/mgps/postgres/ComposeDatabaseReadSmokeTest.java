package com.mgps.postgres;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ComposeDatabaseReadSmokeTest {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres123";

    @Test
    void shouldReadLegacyComposeDatabaseTables() throws Exception {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                 "select name, max_students, max_staff, is_active " +
                 "from subscription_plans order by name")) {

            List<String> planNames = new ArrayList<>();
            while (resultSet.next()) {
                planNames.add(resultSet.getString("name"));
            }

            assertThat(planNames).containsExactly("BASIC", "ENTERPRISE", "PROFESSIONAL");
        }
    }

    @Test
    void shouldReadSchoolRegistryCountsFromComposeDatabase() throws Exception {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                 "select " +
                 "(select count(*) from subscription_plans) as subscription_plans, " +
                 "(select count(*) from schools) as schools, " +
                 "(select count(*) from master_users) as master_users")) {

            assertThat(resultSet.next()).isTrue();
            assertThat(resultSet.getLong("subscription_plans")).isEqualTo(3L);
            assertThat(resultSet.getLong("schools")).isEqualTo(0L);
            assertThat(resultSet.getLong("master_users")).isEqualTo(0L);
        }
    }
}
