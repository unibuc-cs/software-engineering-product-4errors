const BASE_URL = "http://localhost:8080/api/movies"; // URL-ul backend-ului
const IMG_URL = "https://image.tmdb.org/t/p/original";

// Obține elementele DOM
const landingPage = document.getElementById("landing-page");
const movieTitle = document.getElementById("movie-title");
const movieGenres = document.getElementById("movie-genres");
const movieDescription = document.getElementById("movie-description");
const circle = document.getElementById("rating-progress");
const ratingText = document.getElementById("rating-text");
const movieYear = document.getElementById("movie-year");
const movieDuration = document.getElementById("movie-duration");
const movieDirector = document.getElementById("movie-director");
const seeMoreButton = document.getElementById("see-more");

// Adaugă un event listener pentru butonul "See More"
seeMoreButton.addEventListener("click", () => {
    const movieId = seeMoreButton.dataset.id; // Preia ID-ul filmului
    if (movieId) {
        window.location.href = `/movie-details/${movieId}`; // Navighează către pagina detaliilor
    }
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

function showLoader() {
    document.getElementById("loader").classList.remove("hidden");
}

function hideLoader() {
    document.getElementById("loader").classList.add("hidden");
}

function formatRuntime(minutes) {
    if (!minutes || isNaN(minutes)) {
        return "Unknown Duration";
    }

    const hours = Math.floor(minutes / 60); // Calculăm orele
    const remainingMinutes = minutes % 60; // Calculăm minutele rămase

    // Dacă remainingMinutes este 0, afișăm doar orele
    if (remainingMinutes === 0) {
        return `${hours}h`;
    }

    // Altfel, afișăm orele și minutele
    return `${hours}h ${remainingMinutes}min`;
}


// Funcție pentru a actualiza UI-ul cu datele filmului
function updateMovieUI(data) {
    const movieDetails = data;  // Datele sunt deja populate complet în obiectul `data`
    seeMoreButton.dataset.id = movieDetails.id; // Setează ID-ul în butonul "See More"
    console.log(seeMoreButton.dataset.id);
    // Setează background-ul cu posterul filmului
    landingPage.style.backgroundImage = `url(${IMG_URL + movieDetails.backdrop_path})`;

    // Setează titlul, genurile și descrierea filmului
    movieTitle.textContent = movieDetails.title;
    movieGenres.innerHTML = movieDetails.genres.length
        ? movieDetails.genres.map((genre) => `<div class="genre">${genre.name}</div>`).join('')
        : "No genres available";
    movieDescription.textContent = movieDetails.overview;

    // Setează rating-ul filmului
    const voteAverage = movieDetails.vote_average;
    const maxRating = 10;
    const strokeDasharray = 440;
    const offset = strokeDasharray - strokeDasharray * (voteAverage / maxRating);
    circle.style.strokeDashoffset = offset;
    ratingText.textContent = voteAverage.toFixed(2);

    // Setează culoarea rating-ului
    circle.style.stroke = voteAverage >= 7.5 ? "#182D09" : voteAverage >= 5 ? "#e09f3e" : "#390007";

    // Setează anul, durata și regizorul
    movieYear.textContent = movieDetails.release_date ? movieDetails.release_date.split("-")[0] : "Unknown Year";
    movieDuration.textContent = movieDetails.runtime ? `${formatRuntime(movieDetails.runtime)}` : "Unknown Duration";

    // Regizorul
    movieDirector.textContent = `Directed by: ${movieDetails.directors?.length ? movieDetails.directors.map(director => director.name).join(", ") : "Unknown Director"}`;

}

// Funcție pentru filmele populare
async function fetchPopularMovies() {
    //showLoader(); // Afișează loader-ul
    try {
        const response = await fetch(`${BASE_URL}/popular`);
        const textResponse = await response.text();  // Obținem textul răspunsului pentru debugging
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = JSON.parse(textResponse);  // Parsem manual textul ca JSON
        createSwiper("popular-carousel", data); // Filmele populare sunt returnate de API
    } catch (error) {
        console.error("Error fetching popular movies:", error);
    } finally {
        //hideLoader(); // Ascunde loader-ul
    }
}

async function fetchTopRatedMovies() {
    //showLoader(); // Afișează loader-ul
    try {
        const response = await fetch(`${BASE_URL}/top-rated`);
        const textResponse = await response.text();  // Obținem textul răspunsului pentru debugging
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = JSON.parse(textResponse);  // Parsem manual textul ca JSON
        createSwiper("top-rated-carousel", data); // Filmele populare sunt returnate de API
    } catch (error) {
        console.error("Error fetching popular movies:", error);
    } finally {
        //hideLoader(); // Ascunde loader-ul
    }
}

// Funcție pentru a crea un Swiper pentru filme
async function createSwiper(containerId, movies) {
    const container = document.getElementById(containerId);
    const swiperContainer = document.createElement("div");
    swiperContainer.classList.add("swiper");

    const swiperWrapper = document.createElement("div");
    swiperWrapper.classList.add("swiper-wrapper");

    // Încărcăm datele pentru fiecare film și le adăugăm în swiper
    try {

        movies.forEach((movie) => {
            const movieSlide = document.createElement("div");
            movieSlide.classList.add("swiper-slide");

            // Declari variabila doar o dată
            const movieCard = document.createElement("div");
            movieCard.classList.add("movie-card");
            movieCard.dataset.id = movie.id; // Adaugă ID-ul filmului
            const imageUrl = movie.poster_path ? `${IMG_URL}${movie.poster_path}` : "https://via.placeholder.com/500x750?text=No+Image";
            const title = movie.title || "Unknown Title";
            const year = movie.release_date ? movie.release_date.split("-")[0] : "Unknown Year";
            const runtime = movie.runtime ? formatRuntime(movie.runtime) : "Unknown Duration";
            const director = movie.directors?.length ? movie.directors.map(director => director.name).join(", ") : "Unknown Director";
            const genres = movie.genres?.length ? movie.genres.map(genre => genre.name).join(", ") : "Unknown Genres";

            movieCard.innerHTML = `
                <img src="${imageUrl}" alt="${title}" />
                <div class="movie-info">
                    <h3>${title}</h3>
                    <p>${year} | ${runtime} | Directed by: ${director}</p>
                    <p>Genres: ${genres}</p>
                </div>
            `;

            movieCard.addEventListener("click", () => {
                window.location.href = `/movie-details/${movie.id}`;
            });

            movieSlide.appendChild(movieCard);
            swiperWrapper.appendChild(movieSlide);
        });

        swiperContainer.appendChild(swiperWrapper);
        container.appendChild(swiperContainer);

        new Swiper(".swiper", {
            slidesPerView: "auto",
            spaceBetween: 20,
            autoplay: { delay: 2500, disableOnInteraction: false },
            loop: true,
            centeredSlides: false,
        });
    } catch (error) {
        console.error("Error creating swiper:", error);
    }
}


// Funcție pentru a obține un film aleatoriu din cele populare
async function getRandomPopularMovie() {
    //showLoader(); // Afișează loader-ul
    try {
        const response = await fetch(`${BASE_URL}/popular`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        if (data && data.length > 0) {
            const randomMovie = data[Math.floor(Math.random() * data.length)];
            updateMovieUI(randomMovie);
        } else {
            console.error("No popular movies found in response.");
        }
    } catch (error) {
        console.error("Error fetching popular movies:", error);
    } finally {
        //hideLoader(); //ascunde loaderul dupa ce s-au incarcat datele
    }
}



window.onload = async () => {
    await withLoader(async () => {
        console.log("Page loaded. Displaying movies...");
        await getRandomPopularMovie();
        // Reîncărcăm un film aleatoriu la fiecare 15 secunde
        await setInterval(getRandomPopularMovie, 10000);

        // Inițializăm filmele
        await fetchPopularMovies();
        await fetchTopRatedMovies();
    });
};

// Wrapper pentru funcții cu loader
async function withLoader(task) {
    showLoader();
    try {
        await task();
    } catch (e) {
        console.error("Error during task execution:", e);
    } finally {
        hideLoader();
    }
}
