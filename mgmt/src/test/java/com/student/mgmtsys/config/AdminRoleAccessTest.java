package com.student.mgmtsys.config;

import com.student.mgmtsys.controller.AdminController;
import com.student.mgmtsys.entity.Role;
import com.student.mgmtsys.service.AdminService;
import com.student.mgmtsys.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@TestPropertySource(properties = {
        "jwt.secret=test-secret-must-be-at-least-32-bytes-long-1234567890",
        "jwt.expiration-ms=86400000"
})
class AdminRoleAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private AdminService adminService;

    @Test
    void adminEndpoint_withAdminToken_isAllowed() throws Exception {
        when(adminService.getAllCourses()).thenReturn(List.of());
        String token = jwtService.generateToken("admin", Role.ADMIN);

        mockMvc.perform(get("/api/admin/courses").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void adminEndpoint_withNoToken_isUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/courses"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminEndpoint_withStudentRoleToken_isForbidden() throws Exception {
        String token = jwtService.generateToken("someone", Role.STUDENT);

        mockMvc.perform(get("/api/admin/courses").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
