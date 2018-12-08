package khovalink.persistence;

/**
 * A personalized exception for {@code Link} issues.
 *
 * @author flo
 */
public class LinkException extends Exception {

    private static final long serialVersionUID = -3454079092237339487L;

    public LinkException(final String message) {
        super(message);
    }
}
