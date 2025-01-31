document.addEventListener('DOMContentLoaded', function () {

    document.getElementById('forgotPasswordLink').addEventListener('click', function (event) {
        event.preventDefault(); // Previne comportamentul implicit
        $('#forgotPasswordModel').modal('show'); // Afișează modalul

        // Elimină fundalul generat doar pentru acest modal
        const backdrops = document.querySelectorAll('.modal-backdrop');
        backdrops.forEach((backdrop) => {
            if (backdrop.parentNode) {
                backdrop.parentNode.removeChild(backdrop);
            }
        });
    });



    const passwordField = document.getElementById('password');
    const confirmPasswordField = document.getElementById('confirmPassword');

    // Funcție pentru a preveni copy, paste și cut
    function disableCopyPasteCut(event) {
        event.preventDefault();
        alert('Copy, paste and cut are disabled for password fields!');
    }

    // Aplicăm funcția pe ambele câmpuri
    passwordField.addEventListener('copy', disableCopyPasteCut);
    passwordField.addEventListener('paste', disableCopyPasteCut);
    passwordField.addEventListener('cut', disableCopyPasteCut);


    confirmPasswordField.addEventListener('copy', disableCopyPasteCut);
    confirmPasswordField.addEventListener('paste', disableCopyPasteCut);
    confirmPasswordField.addEventListener('cut', disableCopyPasteCut);

    const section = document.querySelector('.background > section');
    const form = document.querySelector('.background form');
    const signupButton = document.querySelector('.background button');

    if (!section || !form || !signupButton) {
        console.error('Section, form, or button not found.');
        return;
    }

    section.style.opacity = 0;

    setTimeout(() => {
        section.style.transition = 'opacity 1s ease-in-out';
        section.style.opacity = 1;
    }, 500);

    signupButton.addEventListener('click', function (event) {
        //event.preventDefault();

        const emailInput = document.querySelector('input[type="email"]');
        const passwordInput = document.querySelector('input[type="password"]');
        const confirmPasswordInput = document.querySelector('input[name="confirm-password"]');

        // Validare inputuri
        const isValid =
            emailInput.checkValidity() &&
            passwordInput.checkValidity() &&
            confirmPasswordInput.checkValidity();

        if (!isValid) {
            section.classList.add('shake');

            setTimeout(() => {
                section.classList.remove('shake');
            }, 1000);
        } else {
            console.log('Form is valid.');
        }
    });


    // Asigură funcționalitatea meniului

    const menuToggle = document.querySelector(".menu-toggle");
    const navLinks = document.querySelector(".nav-links");

    menuToggle?.addEventListener("click", () => {
        menuToggle.classList.toggle("open");
        navLinks.classList.toggle("show");
    });

    const navbar = document.querySelector(".navbar");

    window.addEventListener("scroll", () => {
        if (window.scrollY > 50) { // După 50px scroll
            navbar.classList.add("scrolled");
        } else {
            navbar.classList.remove("scrolled");
        }
    });


});

function togglePasswordVisibility(fieldId) {
    // Selectăm input-ul de parolă și iconița aferentă pe baza ID-ului
    const input = document.getElementById(fieldId);
    const eyeIcon = input.previousElementSibling; // Ion-icon-ul este imediat înaintea input-ului

    // Verificăm dacă parola este ascunsă (tipul câmpului este "password")
    if (input.type === "password") {
        input.type = "text"; // Face parola vizibilă
        eyeIcon.setAttribute("name", "eye"); // Schimbă iconița în ochi deschis
    } else {
        input.type = "password"; // Ascunde parola
        eyeIcon.setAttribute("name", "eye-outline"); // Schimbă iconița în ochi închis
    }
}




