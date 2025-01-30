document.addEventListener('DOMContentLoaded', async () => {
    try {
        const response = await fetch('/api/dashboard');
        const data = await response.json();

        const chartOptions = {
            responsive: true,
            maintainAspectRatio: false, // Permite scalarea la dimensiunea containerului
        };

        // 1️⃣ General stats
        new Chart(document.getElementById('userStatsChart'), {
            type: 'bar',
            data: {
                labels: ["Total Movies", "Total Hours", "Average Rating"],
                datasets: [{
                    label: "User Stats",
                    data: [data.totalMoviesWatched, data.totalHoursWatched, data.averageRating],
                    backgroundColor: ["#3498db", "#2ecc71", "#f1c40f"]
                }]
            },
            options: chartOptions
        });

        // 2️⃣ Monthly activity
        new Chart(document.getElementById('monthlyActivityChart'), {
            type: 'line',
            data: {
                labels: Object.keys(data.moviesPerMonth),
                datasets: [{
                    label: "Movies Watched per Month",
                    data: Object.values(data.moviesPerMonth),
                    borderColor: "#e74c3c",
                    fill: false
                }]
            },
            options: chartOptions
        });

        // 3️⃣ Genre distribution
        new Chart(document.getElementById('genresChart'), {
            type: 'pie',
            data: {
                labels: Object.keys(data.genresWatched),
                datasets: [{
                    label: "Genre Distribution",
                    data: Object.values(data.genresWatched),
                    backgroundColor: [
                        "#1abc9c", "#9b59b6", "#34495e", "#f39c12", "#d35400",
                        "#c0392b", "#2980b9", "#8e44ad", "#2c3e50", "#16a085",
                        "#27ae60", "#f1c40f", "#e67e22"
                    ]
                }]
            },
            options: chartOptions
        });

    } catch (error) {
        console.error("Error fetching dashboard data:", error);
    }
});


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
