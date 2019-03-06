package eu.captech.digitalization.commons.basic.file;

import eu.captech.digitalization.commons.basic.function.Predicate;

import java.nio.file.Path;
import java.util.regex.Pattern;

public class RegexPathPredicate implements Predicate<Path> {
    private final Pattern pattern;

    public RegexPathPredicate(String regex) {
        pattern = Pattern.compile(regex);
    }

    @Override
    public boolean test(Path path) {
        return pattern.matcher(path.toString()).find();
    }
}
