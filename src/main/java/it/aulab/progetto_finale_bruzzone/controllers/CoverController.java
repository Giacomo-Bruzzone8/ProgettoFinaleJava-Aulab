package it.aulab.progetto_finale_bruzzone.controllers;

import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Genera copertine SVG in stile editoriale per gli articoli senza immagine.
 * Ogni categoria ha la sua palette, l'id dell'articolo decide la composizione:
 * la stessa URL produce sempre la stessa immagine.
 */
@RestController
public class CoverController {

    private record Palette(String paper, String ink, String accent, String label) {}

    private static final Map<String, Palette> PALETTES = Map.of(
            "politica", new Palette("#efe6da", "#2b2522", "#a3271f", "Politica"),
            "economia", new Palette("#e7e9de", "#1f2a24", "#3c6e57", "Economia"),
            "food-drink", new Palette("#f2e6d3", "#33261c", "#c06a2b", "Food & Drink"),
            "sport", new Palette("#e3e8ec", "#1b2430", "#2c5f8a", "Sport"),
            "intrattenimento", new Palette("#ece3e6", "#2a2026", "#8a3b5c", "Intrattenimento"),
            "tech", new Palette("#e2e8e6", "#1a2224", "#1f7a74", "Tech"));

    private static final Palette DEFAULT = new Palette("#ebe6dc", "#262320", "#6d655a", "Chronicle");

    private static final int W = 1200;
    private static final int H = 750;

    @GetMapping(value = "/covers/{category}/{id}.svg", produces = "image/svg+xml")
    public ResponseEntity<String> cover(@PathVariable String category, @PathVariable long id) {
        Palette p = PALETTES.getOrDefault(category, DEFAULT);
        Random rnd = new Random(id * 7919L + category.hashCode());

        StringBuilder svg = new StringBuilder(4096);
        svg.append("<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 ").append(W).append(' ').append(H)
           .append("' preserveAspectRatio='xMidYMid slice'>");
        svg.append("<rect width='100%' height='100%' fill='").append(p.paper()).append("'/>");

        switch ((int) (Math.abs(id) % 4)) {
            case 0 -> horizon(svg, p, rnd);
            case 1 -> halftone(svg, p, rnd);
            case 2 -> stripes(svg, p, rnd);
            default -> arcs(svg, p, rnd);
        }

        grain(svg, p, rnd);

        svg.append("<g font-family='Georgia, serif' fill='").append(p.ink()).append("'>")
           .append("<rect x='48' y='48' width='").append(36 + p.label().length() * 15).append("' height='44' fill='")
           .append(p.paper()).append("'/>")
           .append("<text x='66' y='78' font-size='22' letter-spacing='4'>")
           .append(p.label().toUpperCase(Locale.ITALIAN).replace("&", "&amp;")).append("</text>")
           .append("</g>");
        svg.append("</svg>");

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("image/svg+xml"))
                .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS))
                .body(svg.toString());
    }

    /** Un grande disco sopra linee orizzontali, come un sole sul mare. */
    private void horizon(StringBuilder s, Palette p, Random rnd) {
        int cx = 300 + rnd.nextInt(600);
        int cy = 260 + rnd.nextInt(120);
        int r = 150 + rnd.nextInt(90);
        s.append(circle(cx, cy, r, p.accent(), 1));
        int horizon = cy + r / 3;
        s.append("<rect x='0' y='").append(horizon).append("' width='").append(W).append("' height='")
         .append(H - horizon).append("' fill='").append(p.paper()).append("'/>");
        for (int y = horizon, gap = 6; y < H; y += gap, gap += 2) {
            s.append("<rect x='0' y='").append(y).append("' width='").append(W).append("' height='3' fill='")
             .append(p.ink()).append("'/>");
        }
    }

    /** Retino a punti con dimensione che cresce verso un fuoco. */
    private void halftone(StringBuilder s, Palette p, Random rnd) {
        double fx = 0.25 + rnd.nextDouble() * 0.5;
        double fy = 0.3 + rnd.nextDouble() * 0.4;
        int step = 30;
        double maxDist = Math.hypot(W, H) * 0.55;
        for (int y = step / 2; y < H; y += step) {
            for (int x = step / 2; x < W; x += step) {
                double d = Math.hypot(x - fx * W, y - fy * H) / maxDist;
                double r = Math.max(0, (1 - d) * step * 0.48);
                if (r > 1.2) {
                    s.append(circle(x, y, r, d < 0.35 ? p.accent() : p.ink(), 1));
                }
            }
        }
    }

    /** Fasce diagonali in un riquadro con un disco in primo piano. */
    private void stripes(StringBuilder s, Palette p, Random rnd) {
        int bx = 120 + rnd.nextInt(200);
        int by = 120;
        int bw = W - bx - 120 - rnd.nextInt(150);
        int bh = H - 240;
        s.append("<defs><clipPath id='c'><rect x='").append(bx).append("' y='").append(by).append("' width='")
         .append(bw).append("' height='").append(bh).append("'/></clipPath></defs>");
        s.append("<g clip-path='url(#c)'>");
        for (int i = -H; i < W + H; i += 34) {
            s.append("<line x1='").append(i).append("' y1='0' x2='").append(i + H).append("' y2='").append(H)
             .append("' stroke='").append(p.ink()).append("' stroke-width='12'/>");
        }
        s.append("</g>");
        int r = 110 + rnd.nextInt(70);
        s.append(circle(bx + bw - r / 2, by + bh - r / 2, r, p.accent(), 1));
    }

    /** Archi concentrici che partono da un angolo. */
    private void arcs(StringBuilder s, Palette p, Random rnd) {
        int cx = rnd.nextBoolean() ? 0 : W;
        int cy = H;
        int rings = 9 + rnd.nextInt(4);
        for (int i = rings; i > 0; i--) {
            String fill = (i % 2 == 0) ? p.ink() : p.paper();
            if (i == 3) {
                fill = p.accent();
            }
            s.append(circle(cx, cy, i * 78, fill, 1));
        }
        int r = 50 + rnd.nextInt(30);
        s.append(circle(cx == 0 ? W - 220 : 220, 200 + rnd.nextInt(60), r, p.accent(), 1));
    }

    /** Leggera grana tipografica per evitare l'effetto "vettoriale piatto". */
    private void grain(StringBuilder s, Palette p, Random rnd) {
        s.append("<g fill='").append(p.ink()).append("' opacity='0.08'>");
        for (int i = 0; i < 220; i++) {
            s.append("<rect x='").append(rnd.nextInt(W)).append("' y='").append(rnd.nextInt(H))
             .append("' width='2' height='2'/>");
        }
        s.append("</g>");
    }

    private static String circle(double cx, double cy, double r, String fill, double opacity) {
        return String.format(Locale.ROOT, "<circle cx='%.1f' cy='%.1f' r='%.1f' fill='%s' opacity='%.2f'/>",
                cx, cy, r, fill, opacity);
    }
}
