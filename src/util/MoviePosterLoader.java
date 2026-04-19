package util;

import model.Movie;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.LinkedHashSet;
import java.util.Set;

public final class MoviePosterLoader {

    private static final String POSTER_DIRECTORY = "/assets/movies/";

    private MoviePosterLoader() {
    }

    public static BufferedImage loadPoster(Movie movie) {
        if (movie == null) {
            return null;
        }

        for (String fileName : buildCandidateFileNames(movie)) {
            String resourcePath = POSTER_DIRECTORY + fileName;
            try (InputStream is = MoviePosterLoader.class.getResourceAsStream(resourcePath)) {
                if (is == null) {
                    continue;
                }
                BufferedImage img = ImageIO.read(is);
                if (img != null) {
                    return img;
                }
            } catch (IOException e) {
                System.err.println("Unable to load poster: " + resourcePath + " - " + e.getMessage());
            }
        }

        System.err.println("No poster found for movie: " + movie.getTitle());
        return null;
    }

    private static Set<String> buildCandidateFileNames(Movie movie) {
        Set<String> candidates = new LinkedHashSet<>();
        candidates.add(movie.getMovieID() + ".png");
        candidates.add(movie.getMovieID() + ".jpg");
        candidates.add(movie.getMovieID() + ".jpeg");

        String title = movie.getTitle();
        if (title != null && !title.isBlank()) {
            candidates.add(title.trim() + ".png");
            candidates.add(title.trim() + ".jpg");
            candidates.add(toSafeFileName(title) + ".png");
            candidates.add(toSafeFileName(title) + ".jpg");
        }

        return candidates;
    }

    private static String toSafeFileName(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");

        return normalized
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "");
    }
}