package com.paymybuddy.test;

import com.paymybuddy.core.security.SecurityUtils;
import com.paymybuddy.feature.customer.Customer;
import com.paymybuddy.feature.customer.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthenticationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CustomerRepository customerRepo;

	@Test
	public void testDisplayLoginForm_WhenNotLoggedIn_ShouldShowLoginPage() throws Exception {
		mockMvc.perform(get("/login"))
				.andExpect(view().name("login"));

		mockMvc.perform(get("/transfer"))
				.andExpect(redirectedUrl("/login"));

		mockMvc.perform(get("/"))
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	public void testDisplayLoginForm_WhenLoggedIn_ShouldRedirectToTransfer() throws Exception {
		var session = new MockHttpSession();
		session.setAttribute("email", "test@example.com");

		mockMvc.perform(get("/login").session(session))
				.andExpect(redirectedUrl("/transfer"));

		mockMvc.perform(get("/").session(session))
				.andExpect(redirectedUrl("/transfer"));
	}

	@Test
	public void testLogin_SuccessfulAuthentication_ShouldRedirectToTransfer() throws Exception {
		customerRepo.save(
				new Customer("test", "test@example.com", SecurityUtils.hashPassword("Test1234&€%£"))
		);

		mockMvc.perform(post("/login")
						.contentType(APPLICATION_FORM_URLENCODED)
						.param("email", "test@example.com")
						.param("password", "Test1234&€%£"))
				.andExpect(redirectedUrl("/transfer"));
	}

	@Test
	public void testLogin_FailedAuthentication_ShouldShowLoginPageWithError() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/login")
						.contentType(APPLICATION_FORM_URLENCODED)
						.param("email", "wrong@example.com")
						.param("password", "wrongpassword"))
				.andExpect(view().name("login"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("error"));
	}

	@Test
	public void testLogout_ShouldInvalidateSessionAndRedirectToLogin() throws Exception {
		var session = new MockHttpSession();
		session.setAttribute("email", "test@example.com");

		mockMvc.perform(get("/login").session(session))
				.andExpect(redirectedUrl("/transfer"));

		mockMvc.perform(get("/logout").session(session))
				.andExpect(redirectedUrl("/login"));

		assert(session.isInvalid());
	}

	@Test
	public void testDisplayRegistrationForm_ShouldShowRegistrationPage() throws Exception {
		mockMvc.perform(get("/register"))
				.andExpect(view().name("register"));
	}

	@Test
	public void testRegister_SuccessfulRegistration_ShouldRedirectToLogin() throws Exception {
		mockMvc.perform(post("/register")
						.contentType(APPLICATION_FORM_URLENCODED)
						.param("username", "newUser")
						.param("email", "newUser@example.com")
						.param("confirmEmail", "newUser@example.com")
						.param("password", "Str0ngP@ssw0rd!")
						.param("confirmPassword", "Str0ngP@ssw0rd!"))
				.andExpect(status().isOk());
	}

	@Test
	public void testRegister_ExistingUser_ShouldShowRegisterPageWithError() throws Exception {
		customerRepo.save(new Customer("existingUser", "existingEmail@example.com", "hashedPassword"));

		mockMvc.perform(post("/register")
						.contentType(APPLICATION_FORM_URLENCODED)
						.param("username", "existingUser")
						.param("email", "existingEmail@example.com")
						.param("confirmEmail", "existingEmail@example.com")
						.param("password", "Str0ngP@ssw0rd!")
						.param("confirmPassword", "Str0ngP@ssw0rd!"))
				.andExpect(view().name("register"))
				.andExpect(model().attributeExists("error"));
	}

	@Test
	public void testRegister_WeakPassword_ShouldShowRegisterPageWithError() throws Exception {
		mockMvc.perform(post("/register")
						.contentType(APPLICATION_FORM_URLENCODED)
						.param("username", "newUser")
						.param("email", "newUser@example.com")
						.param("confirmEmail", "newUser@example.com")
						.param("password", "weak")
						.param("confirmPassword", "weak"))
				.andExpect(view().name("register"))
				.andExpect(model().attributeExists("error"));
	}

	@Test
	public void testRegister_EmailOrPasswordConfirmationMismatch_ShouldShowRegisterPageWithError() throws Exception {
		mockMvc.perform(post("/register")
						.contentType(APPLICATION_FORM_URLENCODED)
						.param("username", "newUser")
						.param("email", "newUser@example.com")
						.param("confirmEmail", "wrongEmail@example.com")
						.param("password", "Str0ngP@ssw0rd!")
						.param("confirmPassword", "Str0ngP@ssw0rd"))
				.andExpect(view().name("register"))
				.andExpect(model().attributeExists("error"));
	}

}
