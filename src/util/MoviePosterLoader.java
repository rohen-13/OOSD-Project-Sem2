package util;

import model.Movie;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.util.LinkedHashSet;
import java.util.Set;

public final class MoviePosterLoader {

    private static final String POSTER_DIRECTORY = "assets/movies";

    private MoviePosterLoader() {
    }

    public static BufferedImage loadPoster(Movie movie) {
        if (movie == null) {
            return null;
        }

        for (String fileName : buildCandidateFileNames(movie)) {
            File file = new File(POSTER_DIRECTORY, fileName);
            if (!file.isFile()) {
                continue;
            }

            try {
                return ImageIO.read(file);
            } catch (IOException e) {
                System.err.println("Unable to load poster: " + file.getAbsolutePath() + " - " + e.getMessage());
            }
        }

        return null;
    }

    private static Set<String> buildCandidateFileNames(Movie movie) {
        Set<String> candidates = new LinkedHashSet<>();
        candidates.add(movie.getMovieID() + ".png");

        String title = movie.getTitle();
        if (title != null && !title.isBlank()) {
            candidates.add(title.trim() + ".png");
            candidates.add(toSafeFileName(title) + ".png");
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
