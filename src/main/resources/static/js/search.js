const BASE_URL = "http://localhost:8080/api/movies"; // URL-ul backend-ului
const IMG_URL = "https://image.tmdb.org/t/p/original";

const genres = [
    {
        id: 28,
        name: "Action",
    },
    {
        id: 12,
        name: "Adventure",
    },
    {
        id: 16,
        name: "Animation",
    },
    {
        id: 35,
        name: "Comedy",
    },
    {
        id: 80,
        name: "Crime",
    },
    {
        id: 99,
        name: "Documentary",
    },
    {
        id: 18,
        name: "Drama",
    },
    {
        id: 10751,
        name: "Family",
    },
    {
        id: 14,
        name: "Fantasy",
    },
    {
        id: 36,
        name: "History",
    },
    {
        id: 27,
        name: "Horror",
    },
    {
        id: 10402,
        name: "Music",
    },
    {
        id: 9648,
        name: "Mystery",
    },
    {
        id: 10749,
        name: "Romance",
    },
    {
        id: 878,
        name: "Science Fiction",
    },
    {
        id: 10770,
        name: "TV Movie",
    },
    {
        id: 53,
        name: "Thriller",
    },
    {
        id: 10752,
        name: "War",
    },
    {
        id: 37,
        name: "Western",
    },
];
const tagsEl = document.getElementById("tags");
let selectedGenre = [];
let currentPage = 1;
let searchQuery = "";


// Eveniment de selectare a unui gen
function setGenre() {
    tagsEl.innerHTML = ""; // Resetăm conținutul tag-urilor de genuri
    genres.forEach((genre) => {
        const tag = document.createElement("div");
        tag.classList.add("tag");
        tag.id = genre.id;
        tag.innerText = genre.name;

        // Adăugăm evenimentul de click pentru fiecare tag
        tag.addEventListener("click", async () => {
            if (selectedGenre.includes(genre.id)) {
                // Dacă genul este deja selectat, îl eliminăm
                selectedGenre = selectedGenre.filter((id) => id !== genre.id);
            } else {
                // Dacă genul nu este selectat, îl adăugăm
                selectedGenre.push(genre.id);
            }

            // Resetăm query-ul dacă utilizatorul selectează genuri
            if (searchQuery) {
                searchQuery = "";
                document.getElementById("search-bar").value = ""; // Curățăm input-ul
            }

            // Actualizăm selecția vizuală a genurilor
            highlightSelection();

            // Apelăm funcția pentru a reîncărca filmele
            await withLoader(async () => {
                console.log("Displaying movies based on selected genres...");
                await displayMovies(1); // Transmite doar genurile selectate
            });
        });

        tagsEl.append(tag); // Adăugăm tag-ul în containerul de genuri
    });
}



function highlightSelection() {
    // Eliminăm clasa de evidențiere de la toate tag-urile
    document.querySelectorAll(".tag").forEach((tag) => {
        tag.classList.remove("highlight");
    });

    // Dacă există genuri selectate, le evidențiem
    if (selectedGenre.length !== 0) {
        selectedGenre.forEach((id) => {
            const highlightedTag = document.getElementById(id);
            highlightedTag.classList.add("highlight");
        });
    }

    // Actualizăm butonul de Clear (ștergere selecții)
    manageClearBtn();
}

function manageClearBtn() {
    let clearBtn = document.getElementById("clear");
    if (selectedGenre.length === 0) {
        // Dacă nu sunt genuri selectate, eliminăm butonul Clear
        if (clearBtn) {
            clearBtn.remove();
        }
    } else {
        // Dacă există genuri selectate, afișăm butonul Clear
        if (!clearBtn) {
            let clear = document.createElement("div");
            clear.classList.add("tag", "highlight");
            clear.id = "clear";
            clear.innerText = "Clear x";
            clear.addEventListener("click", async () => {
                selectedGenre = [];
                setGenre();

                await withLoader(async () => {
                    console.log("Page loaded. Displaying popular movies...");
                    await displayMovies(1); // Începe cu prima pagină
                });
            });
            tagsEl.append(clear);
        }
    }
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



// Funcție pentru obținerea filmelor populare din API-ul backend
async function fetchMovies(page, query="", genres="") {
    try {
        // Dacă nu există niciun termen de căutare și nici genuri selectate, ar trebui să afișăm filmele populare
        if (!query && !genres) {
            const url = `${BASE_URL}/page?page=${page}`;
            const response = await fetch(url);
            const textResponse = await response.text();
            //console.log(textResponse);  // Afișăm răspunsul pentru debugging

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = JSON.parse(textResponse);
            return data;
        }

        // Dacă există doar un termen de căutare
        if (query && !genres) {
            const url = `${BASE_URL}/search?query=${query}&page=${page}`;
            const response = await fetch(url);
            const textResponse = await response.text();
            //console.log(textResponse);  // Afișăm răspunsul pentru debugging

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = JSON.parse(textResponse);
            return data;
        }

        // Dacă există doar genuri selectate
        if (!query && genres) {
            const url = `${BASE_URL}/genres?genres=${genres}&page=${page}`;
            const response = await fetch(url);
            const textResponse = await response.text();
            //console.log(textResponse);  // Afișăm răspunsul pentru debugging

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = JSON.parse(textResponse);
            return data;
        }

    } catch (error) {
        console.error("Error fetching movies:", error);
        return []; // Returnăm un array gol în caz de eroare
    }
}


// Funcție pentru a afișa filmele populare într-un container
async function displayMovies(page, query="") {
    const moviesContainer = document.getElementById("movies-container"); // Div-ul unde afișăm filmele
    const resultMessage = document.getElementById("results");
    const loadMoreButton = document.getElementById("load-more-button"); // Butonul pentru "Load More"

    resultMessage.innerHTML = ""

    if (!moviesContainer) {
        console.error("Movies container not found in DOM.");
        return;
    }

    //Vreau sa curat continutul paginii doar atunci cand fac o cautare noua pentru prima oara (adica pagina este 1)
    //In rest vreau ca continutul sa se pastreze pe masura ce vreau sa incarc cat mai multe date despre alte filme (Load More Button Functionality)
    if (query!=null && page === 1 ){
        moviesContainer.innerHTML = ""
    }

    // Verificăm dacă există un termen de căutare și genuri selectate
    const genresParam = selectedGenre.join(","); // Genurile selectate
    const movies = await fetchMovies(page, query, genresParam);

    // Dacă nu sunt filme pentru această pagină, afișăm un mesaj
    if (movies.length === 0 && page === 1) {
        resultMessage.innerHTML = "<p class='no-results'>No movies found.</p>";
        if (loadMoreButton) {
            loadMoreButton.style.display = "none"; // Ascunde butonul Load More dacă nu sunt filme
        }
        return;
    }

    movies.forEach((movie) => {
        const imageUrl = movie.poster_path ? `${IMG_URL}${movie.poster_path}` : "https://via.placeholder.com/500x750?text=No+Image";
        const title = movie.title || "Unknown Title";
        const year = movie.release_date ? movie.release_date.split("-")[0] : "Unknown Year";
        const runtime = movie.runtime ? formatRuntime(movie.runtime) : "Unknown Duration";
        const director = movie.directors?.length ? movie.directors.map(director => director.name).join(", ") : "Unknown Director";
        const genres = movie.genres?.length ? movie.genres.map(genre => genre.name).join(", ") : "Unknown Genres";

        const movieCard = document.createElement("div");
        movieCard.classList.add("movie-card");
        movieCard.setAttribute("data-id", movie.id); // Atribuie ID-ul filmului

        movieCard.innerHTML = `
            <img src="${imageUrl}" alt="${title}" />
            <div class="movie-info">
                <h3>${title}</h3>
                <p>${year} | ${runtime} | Directed by: ${director}</p>
                <p>Genres: ${genres}</p>
            </div>
        `;

        // Adaugă un eveniment de click pentru redirecționare
        movieCard.addEventListener("click", () => {
            window.location.href = `movie-details/${movie.id}`;
        });

        // Adăugăm filmele noi în container, fără a șterge filmele anterioare
        moviesContainer.appendChild(movieCard);
    });
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

// Eveniment pe bara de căutare
document.getElementById("search-bar").addEventListener("keydown", async (event) => {
    if (event.key === "Enter") { // Verificăm dacă utilizatorul apasă Enter
        event.preventDefault(); // Previne trimiterea formularului sau alte acțiuni implicite
        searchQuery = event.target.value.trim(); // Obținem termenul de căutare

        // Resetăm genurile dacă utilizatorul introduce un query
        if (selectedGenre.length > 0) {
            selectedGenre = [];
            setGenre(); // Reinitializează tag-urile fără selecții
        }

        await withLoader(async () => {
            console.log("Displaying movies based on search query...");
            await displayMovies(1, searchQuery); // Începe cu prima pagină și caută după query
        });
    }
});


// Eveniment pe formularul de căutare
document.getElementById("search-form").addEventListener("submit", async (event) => {
    event.preventDefault(); // Previne reîncărcarea paginii
    searchQuery = document.getElementById("search-bar").value.trim(); // Obținem termenul de căutare

    // Resetăm genurile dacă utilizatorul introduce un query
    if (selectedGenre.length > 0) {
        selectedGenre = [];
        setGenre(); // Reinitializează tag-urile fără selecții
    }

    await withLoader(async () => {
        console.log("Displaying movies based on search query...");
        await displayMovies(1, searchQuery); // Începe cu prima pagină și caută după query
    });
});


const loadMoreButton = document.getElementById("load-more-button");

if (loadMoreButton) {
    loadMoreButton.addEventListener("click", async () => {
        // Ascundem butonul și afișăm loader-ul
        loadMoreButton.classList.add("hidden");
        showLoader();

        currentPage++; // Incrementăm pagina curentă
        try {
            await displayMovies(currentPage,searchQuery); // Afișăm filmele pentru pagina curentă
        } catch (error) {
            console.error("Error loading more movies:", error);
        } finally {
            // Afișăm din nou butonul și ascundem loader-ul
            hideLoader();
            loadMoreButton.classList.remove("hidden");
        }
    });
}

window.onload = async () => {
    await withLoader(async () => {
        console.log("Page loaded. Displaying popular movies...");
        setGenre();
        await displayMovies(currentPage); // Începe cu prima pagină
    });
};