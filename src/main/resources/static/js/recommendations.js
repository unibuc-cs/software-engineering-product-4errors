document.addEventListener('DOMContentLoaded', () => {
    const swiper = new Swiper('.swiper', {
        loop: true,
        slidesPerView: 1,
        spaceBetween: 20,
        navigation: {
            nextEl: '.swiper-button-next',
            prevEl: '.swiper-button-prev',
        },
        pagination: {
            el: '.swiper-pagination',
            clickable: true,
        },
        breakpoints: {

            400: {
                slidesPerView: 1,
            },
            600: {
                slidesPerView: 3,
            },
            1024: {
                slidesPerView: 4,
            },
        },
    });

    // Add click event listeners to all movie cards
    const movieCards = document.querySelectorAll('.movie-card');
    movieCards.forEach(card => {
        card.addEventListener('click', () => {
            const movieId = card.getAttribute('data-movie-id');
            if (movieId) {
                window.location.href = `/movie-details/${movieId}`;
            }
        });
    });

});

// Asigură funcționalitatea meniului
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



document.addEventListener('DOMContentLoaded', async () => {
    const goToRandomMovieBtn = document.querySelector("#surpriseMeBtn2");

    if (goToRandomMovieBtn) {
        goToRandomMovieBtn.addEventListener('click', async () => {
            console.log("The 'Surprise Me' button has been pressed!");

            try {
                // We make a request to the server to get a random movie
                const response = await fetch('/surprise', { method: 'GET', cache: 'no-store' });

                if (!response.ok) {
                    throw new Error(`HTTP error: ${response.status}`);
                }

                const data = await response.json();
                console.log("Surprise movie received:", data);

                if (data.url && data.url.trim() !== "") {
                    console.log("Redirect to:", data.url);
                    window.location.href = data.url;
                } else {
                    console.warn("No surprise movie found!");
                    alert("No surprise movie found! Try again!");
                }
            } catch (error) {
                console.error("Error fetching the surprise movie:", error);
               /* alert("A problem occurred. Check the console for details.");*/
            }
        });
    } else {
        console.error("The 'Surprise Me' button was NOT found in the DOM!");
    }
});
