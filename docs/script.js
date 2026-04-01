/* ── Language Toggle ── */
(function () {
    const STORAGE_KEY = 'garp-lang';

    function setLang(lang) {
        document.body.classList.remove('lang-en', 'lang-sk');
        document.body.classList.add('lang-' + lang);
        document.querySelectorAll('.lang-btn').forEach(function (btn) {
            btn.classList.toggle('active', btn.dataset.lang === lang);
        });
        localStorage.setItem(STORAGE_KEY, lang);
    }

    document.querySelectorAll('.lang-btn').forEach(function (btn) {
        btn.addEventListener('click', function () {
            setLang(btn.dataset.lang);
        });
    });

    var saved = localStorage.getItem(STORAGE_KEY);
    if (!saved) {
        var browserLang = (navigator.language || '').toLowerCase();
        saved = browserLang.startsWith('sk') ? 'sk' : 'en';
    }
    setLang(saved);
})();

/* ── Screenshot Carousel ── */
(function () {
    var track = document.querySelector('.carousel-track');
    if (!track) return;

    var slides = track.querySelectorAll('.carousel-slide');
    var total = slides.length;
    var current = 0;

    var prevBtn = document.querySelector('.carousel-btn.prev');
    var nextBtn = document.querySelector('.carousel-btn.next');
    var dotsContainer = document.querySelector('.carousel-dots');

    function updateDots() {
        if (!dotsContainer) return;
        dotsContainer.querySelectorAll('.carousel-dot').forEach(function (dot, i) {
            dot.classList.toggle('active', i === current);
        });
    }

    function goTo(index) {
        if (index < 0) index = total - 1;
        if (index >= total) index = 0;
        current = index;
        track.style.transform = 'translateX(-' + (current * 100) + '%)';
        updateDots();
    }

    if (prevBtn) prevBtn.addEventListener('click', function () { goTo(current - 1); });
    if (nextBtn) nextBtn.addEventListener('click', function () { goTo(current + 1); });

    if (dotsContainer && total > 0) {
        for (var i = 0; i < total; i++) {
            var dot = document.createElement('button');
            dot.className = 'carousel-dot' + (i === 0 ? ' active' : '');
            dot.setAttribute('aria-label', 'Slide ' + (i + 1));
            dot.dataset.index = i;
            dot.addEventListener('click', function () { goTo(parseInt(this.dataset.index)); });
            dotsContainer.appendChild(dot);
        }
    }

    /* Auto-advance every 5s */
    var autoPlay = setInterval(function () { goTo(current + 1); }, 5000);
    var container = document.querySelector('.carousel-container');
    if (container) {
        container.addEventListener('mouseenter', function () { clearInterval(autoPlay); });
        container.addEventListener('mouseleave', function () {
            autoPlay = setInterval(function () { goTo(current + 1); }, 5000);
        });
    }
})();
