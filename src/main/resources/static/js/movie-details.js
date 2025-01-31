const BASE_URL = "http://localhost:8080/api/movies"; // URL-ul backend-ului
const IMG_URL = "https://image.tmdb.org/t/p/original";

// Selectăm toate stelutele
const stars = document.querySelectorAll('.star');
const rateText = document.querySelector('.rate-text p');

// Adăugăm evenimentul de hover pentru fiecare stea
stars.forEach(star => {
    star.addEventListener('mouseover', () => {
        resetStars(); // Resetăm orice stare de hover sau selectare
        star.classList.add('hover'); // Adăugăm clasa de hover la steaua curentă
        let index = parseInt(star.getAttribute('data-index'));
        highlightStars(index); // Evidentiem stelutele din stânga
    });

    star.addEventListener('mouseout', () => {
        resetStars(); // Resetăm starea de hover
    });

    star.addEventListener('click', () => {
        let index = parseInt(star.getAttribute('data-index'));
        selectStars(index); // Selectăm stelutele până la index-ul selectat
    });
});

// Resetăm toate stelutele
function resetStars() {
    stars.forEach(star => {
        star.classList.remove('hover'); // Scoatem hover-ul
    });
}

async function clearStars() {
    const userId = document.getElementById('userId').value;
    const movieId = window.location.pathname.split('/').pop();

    try {
        // Call backend to remove rating
        const response = await fetch(`${BASE_URL}/ratings/remove?userId=${userId}&tmdbId=${movieId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('Failed to remove rating');
        }

        // Clear the stars UI only if backend call was successful
        stars.forEach(star => {
            star.classList.remove('selected');
        });


        console.log("Rating removed successfully!");
    } catch (error) {
        console.error("Error removing rating:", error);
    }
}

// Evidentiem stelutele până la index-ul dat
function highlightStars(index) {
    for (let i = 0; i < index; i++) {
        stars[i].classList.add('hover'); // Adăugăm clasa de hover la stelutele din stânga
    }
}

// Selectăm stelutele până la index-ul dat
function selectStars(index) {
    stars.forEach((star, i) => {
        if (i < index) {
            star.classList.add('selected'); // Adăugăm clasa de selecție pentru stelutele din stânga
        } else {
            star.classList.remove('selected'); // Scoatem clasa de selecție pentru stelutele din dreapta
        }
    });

    // Modificăm textul în funcție de ratingul selectat
    if (index > 0) {
        rateText.innerHTML = "Your Rating"; // Când utilizatorul selectează stelute
    } else {
        rateText.innerHTML = "How would you rate this movie?"; // Dacă nu a selectat nimic
    }
}

function selectStars(index) {
    const userId = document.getElementById('userId').value;
    const movieId = window.location.pathname.split('/').pop();

    stars.forEach((star, i) => {
        if (i < index) {
            star.classList.add('selected');
        } else {
            star.classList.remove('selected');
        }
    });

    // Send rating to backend
    if (index > 0) {
        sendRating(userId, movieId, index);
        rateText.innerHTML = "Your Rating";
    } else {
        rateText.innerHTML = "How would you rate this movie?";
    }
}

async function sendRating(userId, movieId, rating) {
    console.log(`Attempting to send rating: userId=${userId}, movieId=${movieId}, rating=${rating}`);

    try {
        const response = await fetch(`${BASE_URL}/ratings/add?userId=${userId}&tmdbId=${movieId}&rating=${rating}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        const responseText = await response.text();
        console.log("Server response:", responseText);

        if (!response.ok) {
            throw new Error(`Failed to add rating: ${responseText}`);
        }

        console.log("Rating added successfully!");
        await checkWatchedStatus();
    } catch (error) {
        console.error("Error adding rating:", error);
        rateText.innerHTML = "Error adding rating. Please try again.";
    }
}

//afisam nr de stelute dat de user pentru film, daca exista
async function fetchUserRating() {
    const userId = document.getElementById('userId').value;
    const movieId = window.location.pathname.split('/').pop();

    try {
        const response = await fetch(`${BASE_URL}/ratings/user?userId=${userId}&movieId=${movieId}`);
        const rating = await response.json();

        if (rating > 0) {
            selectStars(rating);
            rateText.innerHTML = "Your Rating";
        }
    } catch (error) {
        console.error("Error fetching user rating:", error);
    }
}

document.addEventListener("DOMContentLoaded", async () => {
    const path = window.location.pathname;
    const movieId = path.split('/').pop();

    console.log("Movie ID:", movieId);

    await withLoader(async () => {
        await fetchUserRating();
        await fetchCast(movieId);
        await fetchRecommendedMovies(movieId);
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

// Funcționalitate pentru butoanele din pagina de detalii film


//verificam daca e in favourites
async function checkFavoriteStatus() {
    const movieId = window.location.pathname.split('/').pop(); // Extract movie ID from URL
    const userId = document.getElementById('userId').value; // Get user ID from hidden input

    try {

        const response = await fetch(`${BASE_URL}/favourites/check?userId=${userId}&tmdbId=${movieId}`);
        if (!response.ok) {
            throw new Error("Failed to check favorite status");
        }

        const isFavorite = await response.json();


        let text = document.getElementById('favorite-text');
        let button = text.parentElement;
        let icon = document.getElementById('favorite-icon');

        if (isFavorite) {
            button.setAttribute('data-favorite', 'true');
            text.textContent = "Added to Favorite";
            icon.classList.add('fa-solid');
            icon.classList.remove('fa-regular');
        } else {
            button.removeAttribute('data-favorite');
            text.textContent = "Add to Favorite";
            icon.classList.add('fa-regular');
            icon.classList.remove('fa-solid');
        }
    } catch (error) {
        console.error("Error checking favorite status:", error);
    }
}

document.addEventListener('DOMContentLoaded', function () {
    checkFavoriteStatus();
});

//verificam daca filmul e in watchlist
async function checkWatchListStatus() {
    const movieId = window.location.pathname.split('/').pop();
    const userId = document.getElementById('userId').value;

    try {

        const response = await fetch(`${BASE_URL}/watchlist/check?userId=${userId}&tmdbId=${movieId}`);
        if (!response.ok) {
            throw new Error("Failed to check watchlist status");
        }

        const isInWatchList = await response.json();


        let text = document.getElementById('watchlist-text');
        let button = text.parentElement;
        let icon = document.getElementById('watchlist-icon');

        if (isInWatchList) {
            button.setAttribute('data-watchlist', 'true');
            text.textContent = "Added to Watch List";
            if (icon) {
                icon.classList.add('fa-solid');
                icon.classList.remove('fa-regular');
            }
        } else {
            button.removeAttribute('data-watchlist');
            text.textContent = "Add to Watch List";
            if (icon) {
                icon.classList.add('fa-regular');
                icon.classList.remove('fa-solid');
            }
        }
    } catch (error) {
        console.error("Error checking watchlist status:", error);
    }
}

document.addEventListener('DOMContentLoaded', function () {
    checkWatchListStatus();
});


//verificam daca filmul este in watched list
async function checkWatchedStatus() {
    const movieId = window.location.pathname.split('/').pop();
    const userId = document.getElementById('userId').value;

    try {
        const response = await fetch(`${BASE_URL}/watched/check?userId=${userId}&tmdbId=${movieId}`);
        const isWatched = await response.json();

        let text = document.getElementById('watched-text');
        let button = text.parentElement;
        text.textContent = isWatched ? "Mark as Unwatched" : "Mark as Watched";

        if (isWatched) {
            button.setAttribute('data-watched', 'true');
        } else {
            button.removeAttribute('data-watched');
        }
    } catch (error) {
        console.error("Error checking watched status:", error);
    }
}


document.addEventListener('DOMContentLoaded', function() {
    checkWatchedStatus();
});


//adaugam sau stergem din to watch un film
async function toggleWatchList() {
    let button = document.getElementById('watchlist-text').parentElement;
    let text = document.getElementById('watchlist-text');
    let icon = document.getElementById('watchlist-icon');
    const movieId = window.location.pathname.split('/').pop();
    const userId = document.getElementById('userId').value;
    const isInWatchList = button.hasAttribute('data-watchlist');

    try {
        let url, method;
        if (isInWatchList) {

            url = `${BASE_URL}/watchlist/remove?userId=${userId}&tmdbId=${movieId}`;
            method = 'DELETE';
            button.removeAttribute('data-watchlist');
        } else {

            url = `${BASE_URL}/watchlist/add`;
            method = 'POST';
            button.setAttribute('data-watchlist', 'true');
        }

        // API request
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: method === 'POST' ? JSON.stringify({
                userId: userId,
                tmdbId: movieId,
                title: document.title
            }) : null
        });

        if (!response.ok) {
            throw new Error('Failed to update watchlist status');
        }


        text.textContent = isInWatchList ? "Add to Watch List" : "Added to Watch List";
        if (icon) {
            icon.classList.toggle('fa-solid', !isInWatchList);
            icon.classList.toggle('fa-regular', isInWatchList);
        }

        console.log(isInWatchList ?
            "Movie removed from watchlist successfully!" :
            "Movie added to watchlist successfully!");

    } catch (error) {

        text.textContent = isInWatchList ? "Added to Watch List" : "Add to Watch List";
        console.error("Error updating watchlist status:", error);
    }
}


//stergem sau adaugam filmul in lista de watched
async function toggleWatched() {
    let button = document.getElementById('watched-text').parentElement;
    let text = document.getElementById('watched-text');
    const movieId = window.location.pathname.split('/').pop();
    const userId = document.getElementById('userId').value;
    const isWatched = text.textContent === "Mark as Unwatched";

    try {
        let url, method;
        if (isWatched) {
            url = `${BASE_URL}/watched/remove?userId=${userId}&tmdbId=${movieId}`;
            method = 'DELETE';
            button.removeAttribute('data-watched');
        } else {
            //verificam daca e in watchlist
            const checkWatchlistResponse = await fetch(`${BASE_URL}/watchlist/check?userId=${userId}&tmdbId=${movieId}`);
            if (!checkWatchlistResponse.ok) {
                throw new Error('Failed to check watchlist status');
            }
            const isInWatchList = await checkWatchlistResponse.json();
            //daca este il stergem din watchlist
            if(isInWatchList)
            {
              const removeWatchlistResponse = await fetch(`${BASE_URL}/watchlist/remove?userId=${userId}&tmdbId=${movieId}`, {
                  method: 'DELETE',
              });
              if (!removeWatchlistResponse.ok) {
                  throw new Error('Failed to remove movie from watchlist');
              }
              console.log("Movie removed from watchlist before adding to watched list.");
              // Update UI
              const watchlistButton = document.getElementById('watchlist-text');
              watchlistButton.textContent = "Add to Watch List";
              const watchlistButtonParent = watchlistButton.parentElement;
              watchlistButtonParent.removeAttribute('data-watchlist');
            }

            //adaugam la watched movies
            url = `${BASE_URL}/watched/add`;
            method = 'POST';
            button.setAttribute('data-watched', 'true');
        }

        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: method === 'POST' ? JSON.stringify({
                userId: userId,
                tmdbId: movieId,
                title: document.title
            }) : null
        });

        if (!response.ok) {
            throw new Error('Failed to update watched status');
        }

        text.textContent = isWatched ? "Mark as Watched" : "Mark as Unwatched";

        console.log(isWatched ?
            "Film removed from watched list successfully!" :
            "Film added to watched list successfully!");
          if(isWatched) {
          clearStars();
          console.log("Stars reseted!");
          }

    } catch (error) {
        text.textContent = isWatched ? "Mark as Unwatched" : "Mark as Watched";
        console.error("Error updating movie watched status:", error);
    }
}


//adaugam sau stergem din lista de favorite
async function toggleFavorite() {
    let button = document.getElementById('favorite-text').parentElement;
    let text = document.getElementById('favorite-text');
    let icon = document.getElementById('favorite-icon');
    const movieId = window.location.pathname.split('/').pop();
    const userId = document.getElementById('userId').value;
    const isFavorite = button.hasAttribute('data-favorite');
    try {
        let url, method;
        if (isFavorite) {

            url = `${BASE_URL}/favourites/remove?userId=${userId}&tmdbId=${movieId}`;
            method = 'DELETE';
            button.removeAttribute('data-favorite');
        } else {

            url = `${BASE_URL}/favourites/add`;
            method = 'POST';
            button.setAttribute('data-favorite', 'true');
        }

        // API request
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: method === 'POST' ? JSON.stringify({
                userId: userId,
                tmdbId: movieId,
                title: document.title
            }) : null
        });

        if (!response.ok) {
            throw new Error('Failed to update favorites status');
        }

        text.textContent = isFavorite ? "Add to Favorite" : "Added to Favorite";
        icon.classList.toggle('fa-solid', !isFavorite);
        icon.classList.toggle('fa-regular', isFavorite);

        console.log(isFavorite ?
            "Movie removed from favorites list successfully!" :
            "Movie added to favorites list successfully!");

    } catch (error) {

        text.textContent = isFavorite ? "Added to Favorite" : "Add to Favorite";
        console.error("Error updating favorite status:", error);
    }
}


async function fetchCast(Id) {
    try {
        const response = await fetch(`${BASE_URL}/${Id}/movie-cast`);
        const textResponse = await response.text();  // Obținem textul răspunsului pentru debugging
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const actors = JSON.parse(textResponse);  // Parsem manual textul ca JSON
        const container = document.getElementById("cast-carousel");
        const swiperContainer = document.createElement("div");
        swiperContainer.classList.add("swiper");

        const swiperWrapper = document.createElement("div");
        swiperWrapper.classList.add("swiper-wrapper");

        // Încărcăm datele pentru fiecare actor și le adăugăm în swiper
        try {

            actors.forEach((actor) => {
                const actorSlide = document.createElement("div");
                actorSlide.classList.add("swiper-slide");

                // Declari variabila doar o dată
                const actorCard = document.createElement("div");
                actorCard.classList.add("movie-card");
                actorCard.dataset.id = actor.id; // Adaugă ID-ul actorului
                const profileUrl = actor.profile_path ? `${IMG_URL}${actor.profile_path}` : "https://via.placeholder.com/500x750?text=No+Image";
                const name = actor.name || "Unknown Name";
                const character = actor.character || "Unknown Character";

                actorCard.innerHTML = `
                <img src="${profileUrl}" alt="${name}" />
                <div class="movie-info">
                    <h3>${name}</h3>
                    <p>${character}</p>
                </div>
            `;

                //Daca intr-o viata a mea viitoare o sa ma plictisesc atat de rau incat sa-mi zgarii unghiile de pereti,
                // //atunci o sa fac si o pagina pentru fiecare actor, pana atunci nu=)))
                /*actorCard.addEventListener("click", () => {
                    window.location.href = `/movie-details/${movie.id}`;
                });*/

                actorSlide.appendChild(actorCard);
                swiperWrapper.appendChild(actorSlide);
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

    } catch (error) {
        console.error("Error fetching movie's cast:", error);
    }
}

async function fetchRecommendedMovies(Id){
    try {
        const response = await fetch(`${BASE_URL}/${Id}/recommendations`);
        const textResponse = await response.text();  // Obținem textul răspunsului pentru debugging
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const movies = JSON.parse(textResponse);  // Parsem manual textul ca JSON
        const container = document.getElementById("recommendations-carousel");
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

    } catch (error) {
        console.error("Error fetching movie's cast:", error);
    }
}


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