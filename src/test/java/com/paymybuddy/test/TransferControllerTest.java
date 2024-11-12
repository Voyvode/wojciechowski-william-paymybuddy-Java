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
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TransferControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CustomerRepository customerRepo;

	private MockHttpSession session;

	@BeforeEach
	public void setup() {
		session = new MockHttpSession();
		var sender = new Customer("sender", "sender@example.com", SecurityUtils.hashPassword("password123"));
		var receiver = new Customer("receiver", "buddy@example.com", SecurityUtils.hashPassword("password456"));
		customerRepo.saveAll(Arrays.asList(sender, receiver));

		session.setAttribute("email", "sender@example.com");
		session.setAttribute("username", "sender");
	}

	@Test
	public void testDisplayTransferPage_WhenLoggedIn_ShouldShowTransferPage() throws Exception {
		mockMvc.perform(get("/transfer").session(session))
				.andExpect(view().name("transfer"));
	}

	@Test
	public void testDisplayTransferPage_WhenNotLoggedIn_ShouldRedirectToLogin() throws Exception {
		mockMvc.perform(get("/transfer"))
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	public void testCreateTransfer_SuccessfulTransfer_ShouldRedirectWithSuccessMessage() throws Exception {
		mockMvc.perform(post("/transfer")
						.session(session)
						.contentType(APPLICATION_FORM_URLENCODED)
						.param("receiverUsername", "buddyUser")
						.param("amount", "100")
						.param("description", "Paiement test"))
				.andExpect(redirectedUrl("/transfer"))
				.andExpect(flash().attributeExists("successMessage"));
	}

	@Test
	public void testCreateTransfer_InvalidTransfer_ShouldRedirectWithErrorMessage() throws Exception {
		mockMvc.perform(post("/transfer")
						.session(session)
						.contentType(APPLICATION_FORM_URLENCODED)
						.param("receiverUsername", "nonexistentUser")
						.param("amount", "100")
						.param("description", "Invalid transfer test"))
				.andExpect(redirectedUrl("/transfer"))
				.andExpect(flash().attributeExists("errorMessage"));
	}

	@Test
	public void testCreateTransfer_ValidationErrors_ShouldRedirectWithErrorMessage() throws Exception {
		mockMvc.perform(post("/transfer")
						.session(session)
						.contentType(APPLICATION_FORM_URLENCODED)
						.param("receiverUsername", "buddyUser")
						.param("amount", "") // Erreur de validation
						.param("description", "Transfer sans montant"))
				.andExpect(redirectedUrl("/transfer"))
				.andExpect(flash().attributeExists("errorMessage"));
	}

}
