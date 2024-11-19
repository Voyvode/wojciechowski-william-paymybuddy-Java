package com.paymybuddy.test;

import com.paymybuddy.core.security.SecurityUtils;
import com.paymybuddy.feature.customer.Customer;
import com.paymybuddy.feature.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CustomerControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CustomerRepository customerRepo;

	private MockHttpSession session;

	@BeforeEach
	public void setup() {
		session = new MockHttpSession();
		customerRepo.save(
				new Customer("testuser", "test@example.com", SecurityUtils.hashPassword("password123"))
		);
		session.setAttribute("email", "test@example.com");
		session.setAttribute("username", "testuser");
	}

	@Test
	public void testDisplayProfile_WhenLoggedIn_ShouldShowProfilePage() throws Exception {
		mockMvc.perform(get("/profile").session(session))
				.andExpect(view().name("profile"));
	}

	@Test
	public void testDisplayProfile_WhenNotLoggedIn_ShouldRedirectToLogin() throws Exception {
		mockMvc.perform(get("/profile"))
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	public void testChangePassword_SuccessfulChange_ShouldRedirectToProfile() throws Exception {
		mockMvc.perform(post("/profile/change-password")
						.session(session)
						.contentType(APPLICATION_FORM_URLENCODED)
						.param("oldPassword", "password123")
						.param("newPassword", "newSecurePa$$123"))
				.andExpect(redirectedUrl("/profile"));
	}

	@Test
	public void testChangePassword_FailedChange_ShouldRedirectWithErrorMessage() throws Exception {
		mockMvc.perform(post("/profile/change-password")
						.session(session)
						.contentType(APPLICATION_FORM_URLENCODED)
						.param("oldPassword", "wrongPassword") // Mot de passe incorrect
						.param("newPassword", "newSecurePa$$123"))
				.andExpect(redirectedUrl("/profile"))
				.andExpect(flash().attributeExists("error"));
	}

	@Test
	public void testDisplayAdd_WhenLoggedIn_ShouldShowAddPage() throws Exception {
		mockMvc.perform(get("/add").session(session))
				.andExpect(view().name("add"));
	}

	@Test
	public void testDisplayAdd_WhenNotLoggedIn_ShouldRedirectToLogin() throws Exception {
		mockMvc.perform(get("/add"))
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	public void testAddBuddy_SuccessfulAdd_ShouldRedirectToTransfer() throws Exception {
		customerRepo.save(
				new Customer("buddyuser", "buddy@example.com", SecurityUtils.hashPassword("buddyPass123"))
		);

		mockMvc.perform(post("/add")
						.session(session)
						.contentType(APPLICATION_FORM_URLENCODED)
						.param("buddyEmail", "buddy@example.com"))
				.andExpect(redirectedUrl("/transfer"));
	}

	@Test
	public void testAddBuddy_FailedAdd_ShouldShowErrorMessage() throws Exception {
		mockMvc.perform(post("/add")
						.session(session)
						.contentType(APPLICATION_FORM_URLENCODED)
						.param("buddyEmail", "nonexistent@example.com"))
				.andExpect(redirectedUrl("/transfer"))
				.andExpect(MockMvcResultMatchers.flash().attributeExists("error"));
	}

}