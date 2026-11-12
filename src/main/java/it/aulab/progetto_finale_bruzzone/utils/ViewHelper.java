package it.aulab.progetto_finale_bruzzone.utils;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import it.aulab.progetto_finale_bruzzone.dtos.ArticleDto;
import it.aulab.progetto_finale_bruzzone.models.Article;
import it.aulab.progetto_finale_bruzzone.models.Category;
import it.aulab.progetto_finale_bruzzone.models.Image;

/**
 * Funzioni di supporto per i template Thymeleaf, richiamabili come ${@ui.metodo(...)}.
 */
@Component("ui")
public class ViewHelper {

    private static final Locale IT = Locale.ITALIAN;
    private static final DateTimeFormatter LONG_DATE = DateTimeFormatter.ofPattern("d MMMM yyyy", IT);
    private static final DateTimeFormatter MASTHEAD_DATE = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", IT);

    // ---- Immagini di copertina -------------------------------------------------

    public String cover(Article article) {
        return article == null ? coverFor(null, null, null) : coverFor(article.getImage(), article.getCategory(), article.getId());
    }

    public String cover(ArticleDto article) {
        return article == null ? coverFor(null, null, null) : coverFor(article.getImage(), article.getCategory(), article.getId());
    }

    /** Copertina generata, usata anche come fallback se l'immagine caricata non si carica. */
    public String generatedCover(Article article) {
        return article == null ? generated(null, null) : generated(article.getCategory(), article.getId());
    }

    public String generatedCover(ArticleDto article) {
        return article == null ? generated(null, null) : generated(article.getCategory(), article.getId());
    }

    public boolean hasUploadedImage(Image image) {
        return image != null && image.getPath() != null && !image.getPath().isBlank();
    }

    private String coverFor(Image image, Category category, Long id) {
        if (hasUploadedImage(image)) {
            return image.getPath();
        }
        return generated(category, id);
    }

    private String generated(Category category, Long id) {
        String slug = category == null ? "generale" : slug(category.getName());
        return "/covers/" + slug + "/" + (id == null ? 0 : id) + ".svg";
    }

    public static String slug(String text) {
        if (text == null || text.isBlank()) {
            return "generale";
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        String slug = normalized.toLowerCase(IT).replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        return slug.isEmpty() ? "generale" : slug;
    }

    // ---- Testo -----------------------------------------------------------------

    public String date(LocalDate date) {
        return date == null ? "" : date.format(LONG_DATE);
    }

    public String today() {
        String formatted = LocalDate.now().format(MASTHEAD_DATE);
        return formatted.substring(0, 1).toUpperCase(IT) + formatted.substring(1);
    }

    public int readingMinutes(String body) {
        if (body == null || body.isBlank()) {
            return 1;
        }
        int words = body.trim().split("\\s+").length;
        return Math.max(1, Math.round(words / 200f));
    }

    public List<String> paragraphs(String body) {
        if (body == null) {
            return List.of();
        }
        return Arrays.stream(body.split("\\r?\\n\\s*\\r?\\n|\\r?\\n"))
                .map(String::trim)
                .filter(p -> !p.isEmpty())
                .toList();
    }

    public String excerpt(String body, int maxChars) {
        if (body == null) {
            return "";
        }
        String flat = body.replaceAll("\\s+", " ").trim();
        if (flat.length() <= maxChars) {
            return flat;
        }
        int cut = flat.lastIndexOf(' ', maxChars);
        return flat.substring(0, cut > 0 ? cut : maxChars) + "…";
    }

    public String roleLabel(String authority) {
        if (authority == null) {
            return "";
        }
        return switch (authority) {
            case "ROLE_ADMIN" -> "Admin";
            case "ROLE_REVISOR" -> "Revisore";
            case "ROLE_WRITER" -> "Redattore";
            default -> "Lettore";
        };
    }

    public String initials(String name) {
        if (name == null || name.isBlank()) {
            return "?";
        }
        String[] parts = name.trim().split("\\s+");
        String first = parts[0].substring(0, 1);
        String last = parts.length > 1 ? parts[parts.length - 1].substring(0, 1) : "";
        return (first + last).toUpperCase(IT);
    }
}
