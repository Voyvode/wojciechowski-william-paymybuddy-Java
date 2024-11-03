package com.paymybuddy.core.security;

import com.paymybuddy.feature.customer.CustomerDTO;
import com.paymybuddy.feature.customer.CustomerService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

	private final CustomerService customerService;

	/**
	 * Displays the login form.
	 *
	 * <p>Redirects to the transfer page if the customer is logged in.
	 *
	 * @param model   the model to hold attributes for the view
	 * @param request the HTTP request object
	 * @return the name of the view to render
	 */
	@GetMapping("/login")
	public String displayLoginForm(Model model, HttpServletRequest request) {
		if (isCustomerLoggedIn(request)) {
			log.info("Customer already logged in, redirecting to transfer page");
			return "redirect:/transfer";
		}

		model.addAttribute("customer", CustomerDTO.builder().build());
		log.info("Displaying login form");
		return "login";
	}

	/**
	 * Authenticates a customer using the provided credentials.
	 *
	 * <p>If authentication is successful, the user is redirected to the transfer page. Otherwise, an error message is
	 * displayed on the login form.
	 *
	 * @param customerDTO the data transfer object containing customer credentials
	 * @param model       the model to hold attributes for the view
	 * @param session     the HTTP session object
	 * @param response    the HTTP response object
	 * @return the name of the view to render
	 */
	@PostMapping("/login")
	public String login(@ModelAttribute CustomerDTO customerDTO, Model model, HttpSession session, HttpServletResponse response) {
		log.info("Authentication attempt for {}", customerDTO.getEmail());

		try {
			var authenticatedCustomer = customerService.authenticate(customerDTO.getEmail(), customerDTO.getPassword());

			if (authenticatedCustomer != null) {
				session.setAttribute("username", authenticatedCustomer.getUsername());
				session.setAttribute("email", authenticatedCustomer.getEmail());

				var sessionCookie = new Cookie("JSESSIONID", session.getId());
				sessionCookie.setMaxAge(30 * 60); // 30 minutes
				sessionCookie.setPath("/");
				sessionCookie.setSecure(true);
				response.addCookie(sessionCookie);

				log.info("{} logged in, redirecting to transfer page", authenticatedCustomer.getEmail());
				return "redirect:/transfer";
			} else {
				model.addAttribute("error", "Invalid email or password");
				log.warn("Authentication failed for {}", customerDTO.getEmail());
				return "login";
			}
		} catch (AuthenticationException e) {
			model.addAttribute("error", "Authentication error: " + e.getMessage());
			log.error("Error during authentication for {}: {}", customerDTO.getEmail(), e.getMessage());
			return "login";
		}
	}

	/**
	 * Logs out the customer.
	 *
	 * <p>Invalidates the current session and clears the session cookie, then redirects to the login page.
	 *
	 * @param session  the HTTP session object
	 * @param response the HTTP response object
	 * @return the name of the view to render
	 */
	@GetMapping("/logout")
	public String logout(HttpSession session, HttpServletResponse response) {
		var disconnectingCustomer = session.getAttribute("email");
		session.invalidate();

		var sessionCookie = new Cookie("JSESSIONID", null);
		sessionCookie.setMaxAge(0);
		sessionCookie.setPath("/");
		response.addCookie(sessionCookie);

		log.info("{} logged out, redirecting to login", disconnectingCustomer);
		return "redirect:/login";
	}

	/**
	 * Checks if a customer is currently logged in.
	 *
	 * @param request the HTTP request object
	 * @return true if the customer is logged in, false otherwise
	 */
	private boolean isCustomerLoggedIn(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		return session != null && session.getAttribute("email") != null;
	}

}
