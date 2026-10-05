/* Applies the saved appearance (accent, dark mode, currency symbol) on every page.
   Loaded in <head> so the theme is set before first paint (no flash). */
(function () {
    var settings = {};
    try { settings = JSON.parse(localStorage.getItem('finance-theme') || '{}') || {}; } catch (e) { settings = {}; }
    var root = document.documentElement;
    if (settings.accent) root.style.setProperty('--accent', settings.accent);
    if (settings.dark) root.setAttribute('data-theme', 'dark');
    window.FMTheme = {
        settings: settings,
        apply: function (s) {
            if (s.accent) root.style.setProperty('--accent', s.accent);
            if (s.dark) root.setAttribute('data-theme', 'dark'); else root.removeAttribute('data-theme');
            document.querySelectorAll('.cur').forEach(function (el) { el.textContent = s.currency || '₹'; });
        },
        save: function (s) { try { localStorage.setItem('finance-theme', JSON.stringify(s)); } catch (e) {} }
    };
    document.addEventListener('DOMContentLoaded', function () {
        if (settings.currency) {
            document.querySelectorAll('.cur').forEach(function (el) { el.textContent = settings.currency; });
        }
    });
})();
