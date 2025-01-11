function showAvatarCarousel() {
    const carousel = document.getElementById("avatar-carousel");

    if (carousel.style.display === "block") {
        // Dacă este vizibil, îl închide
        carousel.style.display = "none";
    } else {
        // Dacă nu este vizibil, îl deschide
        carousel.style.display = "block";
    }
}

let selectedAvatar = null;

function selectAvatar(element) {
    const avatars = document.querySelectorAll(".avatar-option");
    avatars.forEach(avatar => avatar.style.borderColor = "transparent");

    element.style.borderColor = "#1e90ff";
    selectedAvatar = element.src;

    document.getElementById("save-avatar-btn").style.display = "block";
}

function saveAvatar() {
    if (selectedAvatar) {
        const csrfToken = document.querySelector('meta[name="csrf-token"]').getAttribute('content');

        const avatarFileName = selectedAvatar.split('/').pop();

        fetch("/profile/update-avatar", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-CSRF-TOKEN": csrfToken
            },
            body: JSON.stringify({ avatar: "/images/" + avatarFileName })
        })
            .then(response => {
                if (response.ok) {
                    document.getElementById("current-avatar").src = "/images/" + avatarFileName;
                    alert("Avatar actualizat cu succes!");
                    document.getElementById("avatar-carousel").style.display = "none";
                } else {
                    alert("Eroare la actualizarea avatarului.");
                }
            });
    }
}

document.addEventListener("DOMContentLoaded", () => {
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

