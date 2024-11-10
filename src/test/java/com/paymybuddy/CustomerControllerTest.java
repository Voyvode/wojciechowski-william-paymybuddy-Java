package com.paymybuddy;

import com.paymybuddy.core.security.AuthenticationService;
import com.paymybuddy.feature.customer.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private CustomerService customerService;

	private MockHttpSession session;

	@BeforeEach
	void setUp() {
		session = new MockHttpSession();
		session.setAttribute("username", "testUser");
		session.setAttribute("email", "test@example.com");
	}

	@Test
	void testDisplayRegistrationForm() throws Exception {
		mockMvc.perform(get("/register"))
				.andExpect(status().isOk())
				.andExpect(view().name("register"))
				.andExpect(model().attributeExists("newCustomer"));
	}

	@Test
	void testRegisterSuccess() throws Exception {
		mockMvc.perform(post("/register")
						.param("username", "test")
						.param("email", "test@mail.com")
						.param("password", "password123"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"))
				.andExpect(flash().attributeExists("message"));
	}

	@Test
	void testRegisterDuplicateUser() throws Exception {
		authenticationService.register("castor", "twin@brothers.com", "password123");

		mockMvc.perform(post("/register")
						.param("username", "pollux")
						.param("email", "twin@brothers.com")
						.param("password", "waspsrod321"))
				.andExpect(status().isOk())
				.andExpect(view().name("register"))
				.andExpect(model().attributeHasFieldErrors("newCustomer", "username"));
	}

	@Test
	void testDisplayProfile() throws Exception {
		mockMvc.perform(get("/profile").session(session))
				.andExpect(status().isOk())
				.andExpect(view().name("profile"))
				.andExpect(model().attribute("username", "testUser"))
				.andExpect(model().attribute("email", "test@example.com"));
	}

	@Test
	void testDisplayProfileNotLoggedIn() throws Exception {
		mockMvc.perform(get("/profile"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	void testChangePassword() throws Exception {
		authenticationService.register("testUser", "test@example.com", "oldPassword");

		mockMvc.perform(post("/profile")
						.session(session)
						.param("newPassword", "newPassword123"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/profile"))
				.andExpect(flash().attributeExists("message"));
	}

	@Test
	void testChangePasswordNotLoggedIn() throws Exception {
		mockMvc.perform(post("/profile")
						.param("newPassword", "newPassword123"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	void testDisplayAdd() throws Exception {
		mockMvc.perform(get("/add").session(session))
				.andExpect(status().isOk())
				.andExpect(view().name("add"))
				.andExpect(model().attribute("username", "testUser"))
				.andExpect(model().attribute("email", "test@example.com"));
	}

	@Test
	void testAddBuddy() throws Exception {
		authenticationService.register("testUser", "test@example.com", "password123");
		authenticationService.register("buddy", "buddy@example.com", "password123");

		mockMvc.perform(post("/add")
						.session(session)
						.param("buddyEmail", "buddy@example.com"))
				.andExpect(status().isOk())
				.andExpect(view().name("transfer"));
	}
}
