package khovalink.persistence;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

/**
 * Static class dealing about link's persistence in file.
 *
 * @author flo
 */
public class LinkDAO {

    private static final File DIRECTORY = new File("LinkDB");
    private static final String LINK_NAME_FORMAT = "[\\w+-._]+.link";

    private static LinkDAO instance;

    private final ObservableList<String> names = FXCollections.observableArrayList();

    /**
     * Instanciates a new {@code LinkDAO}.
     *
     * @throws IOException If there is an invalid file name.
     */
    private LinkDAO() throws IOException {
        if (exists()) {
            for (final String fileName : DIRECTORY.list()) {
                if (!fileName.matches(LINK_NAME_FORMAT)) {
                    throw new IOException("Invalid file name : \"" + fileName + "\".");
                }
                names.add(fileName);
            }
        }
    }

    /**
     * Tells if the database directory exist.
     *
     * @return A {@code boolean} set to {@code true} if the directory exist and
     * to {@code false} otherwise.
     */
    private boolean exists() {
        return DIRECTORY.exists();
    }

    /**
     * Tells if the database contains a link with a specified name.
     *
     * @param name The name of the link.
     *
     * @return A {@code boolean} set to {@code true} if a link is found and to
     * {@code false} otherwise.
     */
    public boolean isPresent(final String name) {
        return names.stream().anyMatch(nam -> nam.equalsIgnoreCase(name + ".link"));
    }

    /**
     * Adds a link to the database.
     *
     * @param link The {@code Link} to add.
     *
     * @return {@code true} if the {@code Link} has been added correctly.
     *
     * @throws IOException If an error occurs when writing the link.
     */
    public boolean save(final Link link) throws IOException {
        if (!exists()) {
            DIRECTORY.mkdir();
        }

        try (final ObjectOutputStream oos
                = new ObjectOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(DIRECTORY + File.separator + link.getName() + ".link")))) {
            oos.writeObject(link);
        } catch (final IOException ex) {
            throw ex;
        }

        return names.add(link.getName() + ".link");
    }

    /**
     * Gets a link from the database.
     *
     * @param name The name of the link to get.
     *
     * @return The link.
     *
     * @throws ClassNotFoundException If the class {@code Link} cannot be found.
     * @throws FileNotFoundException If the database directory doesn't exist or
     * if there is no file with this name in database.
     * @throws IOException If an error occurs when reading the link.
     */
    public Link find(final String name) throws ClassNotFoundException, FileNotFoundException, IOException {
        final Link link;

        if (!exists()) {
            throw new FileNotFoundException("The database doesn't exists.");
        }

        final Optional<File> file = Arrays.stream(DIRECTORY.listFiles())
                .filter(fil -> fil.getName().equals(name))
                .findFirst();

        if (!file.isPresent()) {
            throw new FileNotFoundException("Can't find the file \"" + name + ".link\" in database.");
        }

        try (final ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(
                        new FileInputStream(file.get())))) {
            link = (Link) ois.readObject();
        }

        return link;
    }

    /**
     * Returns a {@code SortedList<String>} containing links's names in the
     * database.
     *
     * @return An {@code SortedList<String>} representing links's name.
     */
    public SortedList<String> findAll() {
        return names.sorted((str1, str2) -> str1.compareToIgnoreCase(str2));
    }

    /**
     * Removes a link from database.
     *
     *
     * @param name The name of the link to delete.
     *
     * @return {@code true} if the {@code Link} has been deleted correctly.
     *
     * @throws FileNotFoundException If the database directory doesn't exist or
     * if there is no file with this name in database.
     * @throws IOException If an error occurred when deleting the file.
     */
    public boolean delete(final String name) throws FileNotFoundException, IOException {
        if (!exists()) {
            throw new FileNotFoundException("The database doesn't exists.");
        }

        final Optional<File> file = Arrays.stream(DIRECTORY.listFiles())
                .filter(fil -> fil.getName().equals(name))
                .findFirst();

        if (!file.isPresent()) {
            throw new FileNotFoundException("Can't find the file \"" + name + ".link\" in database.");
        }

        if (!file.get().delete()) {
            throw new IOException("Can't delete the file \"" + name + ".link\" in database.");
        }

        return names.remove(name);
    }

    public static LinkDAO getInstance() throws IOException {
        return instance == null ? instance = new LinkDAO() : instance;
    }
}
