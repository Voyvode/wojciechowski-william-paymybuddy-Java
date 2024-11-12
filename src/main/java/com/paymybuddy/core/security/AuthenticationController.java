package com.paymybuddy.core.security;

import com.paymybuddy.feature.customer.CustomerDTO;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * AuthController handles customer authentication.
 *
 * <p>This class provides methods to display the login form, authenticate a customer, manage logout, and check if
 * a customer is already logged in.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

	private final AuthenticationService authService;

	/**
	 * Handles the root URL redirection.
	 *
	 * <p>If the customer is authenticated, redirects to the transfer page. Otherwise, redirects to the login page.
	 *
	 * @param request the HTTP request object
	 * @return the redirect URL
	 */
	@GetMapping("/")
	public String home(HttpServletRequest request) {
		if (isCustomerLoggedIn(request)) {
			return "redirect:/transfer";
		}
		return "redirect:/login";
	}

	/**
	 * Displays the login form.
	 *
	 * <p>Redirects to the transfer page if the customer is logged in.
	 *
	 * @param request the HTTP request object
	 * @param model   the model to hold attributes for the view
	 * @return the login view to render or, if logged in, a redirect to transfer
	 */
	@GetMapping("/login")
	public String displayLoginForm(HttpServletRequest request, Model model) {
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
	 * <p>If authentication is successful, the customer is redirected to the transfer page. Otherwise, an error
	 * message is displayed on the login form.
	 *
	 * @param dto      the data transfer object containing customer credentials
	 * @param session  the HTTP session object
	 * @param response the HTTP response object
	 * @param model    the model to hold attributes for the view
	 * @return the login view to render or, if logged in, a redirect to transfer
	 */
	@PostMapping("/login")
	public String login(@ModelAttribute("customer") CustomerDTO dto, HttpSession session, HttpServletResponse response, Model model) {
		log.info("Authentication attempt for {}", dto.getEmail());

		try {
			var authenticatedCustomer = authService.authenticate(dto.getEmail(), dto.getPassword());
			session.setAttribute("username", authenticatedCustomer.getUsername());
			session.setAttribute("email", authenticatedCustomer.getEmail());

			var sessionCookie = new Cookie("JSESSIONID", session.getId());
			sessionCookie.setMaxAge(30 * 60); // 30 minutes
			sessionCookie.setPath("/");
			sessionCookie.setHttpOnly(true);
			sessionCookie.setSecure(true);
			response.addCookie(sessionCookie);

			log.info("{} ({}) logged in successfully", authenticatedCustomer.getUsername(), authenticatedCustomer.getEmail());
			return "redirect:/transfer";

		} catch (AuthenticationException e) {
			model.addAttribute("error", "Identification incorrecte. Réessayez.");
			log.error("Authentication error for {}: {}", dto.getEmail(), e.getMessage());
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
	 * @return a redirect to login
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
	 * Displays the registration form.
	 *
	 * @param model the model holding attributes for the view
	 * @return the register view to render
	 */
	@GetMapping("/register")
	public String displayRegistrationForm(Model model) {
		model.addAttribute("newCustomer", CustomerDTO.builder().build());
		return "register";
	}

	/**
	 * Processes the registration of a new customer.
	 *
	 * <p>If registration is successful, the customer is redirected to the login page to confirm credentials.
	 * Otherwise, an error message is displayed on the registration form.
	 *
	 * @param dto                the new customer information
	 * @param result             the binding result for validation
	 * @param redirectAttributes the redirect attributes for flash messages
	 * @return a redirect to login or, in case of error, the register view to render
	 */
	@PostMapping("/register")
	public String register(@ModelAttribute @Validated CustomerDTO dto, BindingResult result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			log.error("New registration attempt with errors: {}", result.getAllErrors());
			return "register";
		}

		try {
			authService.register(dto.getUsername(), dto.getEmail(), dto.getPassword());
			log.info("New customer '{}' registered with {}", dto.getUsername(), dto.getEmail());
			redirectAttributes.addFlashAttribute("message", "Registration successful, now please login");
			return "redirect:/login";

		} catch (EntityExistsException e) {
			log.warn("Username '{}' or email '{}' already used", dto.getUsername(), dto.getEmail());
			result.rejectValue("username", "error.user", "Username or email already in use");
			return "register";

		} catch (IllegalArgumentException e) {
			log.warn("Invalid password for registration attempt: {}", dto.getEmail());
			result.rejectValue("password", "error.password", e.getMessage());
			return "register";
		}
	}

	/**
	 * Checks if a customer is currently logged in.
	 *
	 * @param request the HTTP request object
	 * @return true if the customer is logged in, false otherwise
	 */
	private boolean isCustomerLoggedIn(HttpServletRequest request) {
		var session = request.getSession(false);
		return session != null && session.getAttribute("email") != null;
	}

}
