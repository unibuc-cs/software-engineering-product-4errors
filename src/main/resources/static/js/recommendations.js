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


  document.addEventListener('DOMContentLoaded', function() {
        const menuToggle = document.querySelector('.menu-toggle');
        const navLinks = document.querySelector('.nav-links');

        menuToggle.addEventListener('click', function() {
            menuToggle.classList.toggle('open');
            navLinks.classList.toggle('show');
        });
    });
