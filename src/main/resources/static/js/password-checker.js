const passwordInput = document.getElementById('password');
const submitButton = document.getElementById('submit');
const criteria = {
	length: document.getElementById('length'),
	uppercase: document.getElementById('uppercase'),
	lowercase: document.getElementById('lowercase'),
	digit: document.getElementById('digit'),
	special: document.getElementById('special'),
};

passwordInput.addEventListener('input', () => {
	const password = passwordInput.value;

	const validations = [
		updateCriteria(criteria.length, password.length >= 8),
		updateCriteria(criteria.uppercase, /[A-Z]/.test(password)),
		updateCriteria(criteria.lowercase, /[a-z]/.test(password)),
		updateCriteria(criteria.digit, /\d/.test(password)),
		updateCriteria(criteria.special, /[!@#$%^&*(),.?":{}|<>]/.test(password)),
	];

	// Permettre l’inscription lorsque tous les critères sont remplis
	const allValid = validations.every(Boolean);
	submitButton.disabled = !allValid;
});

function updateCriteria(element, isValid) {
	if (isValid) {
		element.classList.remove('invalid');
		element.classList.add('valid');
	} else {
		element.classList.remove('valid');
		element.classList.add('invalid');
	}
	return isValid;
}